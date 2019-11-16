package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rosa.isabelle.inventorycontrol.dto.ItemDTO;
import rosa.isabelle.inventorycontrol.dto.StockDTO;
import rosa.isabelle.inventorycontrol.dto.StockItemDTO;
import rosa.isabelle.inventorycontrol.dto.StoreDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.exception.ErrorMessage;
import rosa.isabelle.inventorycontrol.model.entity.ItemEntity;
import rosa.isabelle.inventorycontrol.model.entity.StockEntity;
import rosa.isabelle.inventorycontrol.model.entity.StoreEntity;
import rosa.isabelle.inventorycontrol.repository.ItemRepository;
import rosa.isabelle.inventorycontrol.repository.StockRepository;
import rosa.isabelle.inventorycontrol.repository.StoreRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private StockRepository stockRepository;
    private StoreRepository storeRepository;
    private ItemRepository itemRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(StockServiceImpl.class);


    @Autowired
    public StockServiceImpl(StockRepository stockRepository, StoreRepository storeRepository, ItemRepository itemRepository) {
        this.stockRepository = stockRepository;
        this.storeRepository = storeRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public StockItemDTO addStockItem(StockItemDTO stockDTO) {
        LOGGER.debug("Starting service addStockItem with name: {} and store: {}",
                stockDTO.getItem().getName(), stockDTO.getStore().getPublicId());

        try {
            String storeId = stockDTO.getStore().getPublicId();
            String itemId = stockDTO.getItem().getPublicId();

            StoreEntity store = findStore(storeId);
            ItemEntity item = findItem(itemId);

            if (store == null || item == null) {
                ErrorMessage error = ErrorMessage.INVALID_ENTRY;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            if (findStockItem(store, item) != null) {
                throw new CustomException(ErrorMessage.DUPLICATED_DATA.getMessage(),
                        ErrorMessage.DUPLICATED_DATA.getStatusCode().value());
            }

            ModelMapper mapper = new ModelMapper();

            StockEntity newStockItem = new StockEntity();
            newStockItem.setStore(store);
            newStockItem.setItem(item);
            newStockItem.setQuantity(stockDTO.getQuantity());

            LOGGER.debug("Trying to insert stockItem on database");
            StockEntity createdStockItem = stockRepository.save(newStockItem);

            return mapper.map(createdStockItem, StockItemDTO.class);
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        } catch (Exception exception) {
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    private StockEntity findStockItem(StoreEntity store, ItemEntity item) {
        return stockRepository.findByStoreAndItem(store, item);
    }

    private ItemEntity findItem(String itemId) {
        return itemRepository.findByPublicId(itemId);
    }

    private StoreEntity findStore(String storeId) {
        return storeRepository.findByPublicId(storeId);
    }

    @Override
    public StockDTO getStock(String storeId, int page, int size) {
        LOGGER.debug("Starting service getStock with storeId: {}", storeId);

        try {
            StoreEntity store = findStore(storeId);

            if (store == null) {
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());
            }

            ModelMapper modelMapper = new ModelMapper();
            final int DEFAULT_SIZE = 15;
            final int FIRST_PAGE = 0;

            Pageable pageable = PageRequest.of(
                    Math.max(page, FIRST_PAGE),
                    size > 0 ? size : DEFAULT_SIZE);

            LOGGER.debug("Trying to find stockItems on database");
            Page<StockEntity> stocks = stockRepository.findByStore(store, pageable);

            List<StockItemDTO> items = new ArrayList<>();
            stocks.getContent().forEach(stock -> {
                StockItemDTO stockItemDTO = new StockItemDTO();
                ItemDTO itemDTO = modelMapper.map(stock.getItem(), ItemDTO.class);
                stockItemDTO.setItem(itemDTO);
                stockItemDTO.setQuantity(stock.getQuantity());

                items.add(stockItemDTO);
            });

            StoreDTO storeDTO = modelMapper.map(store, StoreDTO.class);
            StockDTO returnedStocks = new StockDTO();
            returnedStocks.setStore(storeDTO);
            returnedStocks.setItems(items);

            return returnedStocks;
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        } catch (Exception exception) {
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public StockItemDTO editStockItem(String storeId, String itemId, StockItemDTO requestStockItemDTO) {
        LOGGER.debug("Starting service editStockItem with itemId: {} and storeId: {}", itemId, storeId);

        try {
            StoreEntity store = findStore(storeId);
            ItemEntity item = findItem(itemId);

            if (store == null || item == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            StockEntity originalStockItem = findStockItem(store, item);

            if (originalStockItem == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            ModelMapper modelMapper = new ModelMapper();

            StockEntity editedStockItem = modelMapper.map(originalStockItem, StockEntity.class);
            editedStockItem.setQuantity(requestStockItemDTO.getQuantity());

            LOGGER.debug("Trying to update stockItem on database");
            StockEntity updatedStockItem = stockRepository.save(editedStockItem);

            return modelMapper.map(updatedStockItem, StockItemDTO.class);
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        } catch (Exception exception) {
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public StockItemDTO removeStockItem(String storeId, String itemId) {
        LOGGER.debug("Starting service removeStockItem with storeId: {} and itemId: {}", storeId, itemId);

        try {
            StoreEntity storeEntity = findStore(storeId);
            ItemEntity itemEntity = findItem(itemId);

            if (storeEntity == null || itemEntity == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            StockEntity item = findStockItem(storeEntity, itemEntity);

            if (item == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            LOGGER.debug("Trying to delete stockItem from database");
            stockRepository.delete(item);

            ModelMapper mapper = new ModelMapper();

            return mapper.map(item, StockItemDTO.class);
        } catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        } catch (Exception exception) {
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

}