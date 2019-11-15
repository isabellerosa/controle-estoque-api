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
    public StockDTO findStocks(String storeId, int page, int size) {

        StoreEntity store = storeRepository.findByPublicId(storeId);

        if(store == null){
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
