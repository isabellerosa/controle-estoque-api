package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
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

    @Autowired
    public StockServiceImpl(StockRepository stockRepository, StoreRepository storeRepository, ItemRepository itemRepository) {
        this.stockRepository = stockRepository;
        this.storeRepository = storeRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public StockItemDTO addStockItem(StockItemDTO stockDTO) {
        String storeId = stockDTO.getStore().getPublicId();
        String itemId = stockDTO.getItem().getPublicId();

        StoreEntity storeEntity = findStore(storeId);
        ItemEntity itemEntity = findItem(itemId);

        if (storeEntity == null || itemEntity == null) {
            ErrorMessage error = ErrorMessage.INVALID_ENTRY;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }

        if (findStockItem(storeEntity, itemEntity) != null) {
            throw new CustomException(ErrorMessage.DUPLICATED_DATA.getMessage(),
                    ErrorMessage.DUPLICATED_DATA.getStatusCode().value());
        }

        ModelMapper mapper = new ModelMapper();

        StockEntity stockEntity = new StockEntity();
        stockEntity.setStore(storeEntity);
        stockEntity.setItem(itemEntity);
        stockEntity.setQuantity(stockDTO.getQuantity());

        StockEntity savedEntity = stockRepository.save(stockEntity);

        StockItemDTO saved = mapper.map(savedEntity, StockItemDTO.class);

        return saved;
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
    }
}
