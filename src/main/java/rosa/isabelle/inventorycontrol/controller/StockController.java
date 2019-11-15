package rosa.isabelle.inventorycontrol.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rosa.isabelle.inventorycontrol.dto.ItemDTO;
import rosa.isabelle.inventorycontrol.dto.StockDTO;
import rosa.isabelle.inventorycontrol.dto.StockItemDTO;
import rosa.isabelle.inventorycontrol.dto.StoreDTO;
import rosa.isabelle.inventorycontrol.model.request.StockRequestModel;
import rosa.isabelle.inventorycontrol.model.response.StockItemModel;
import rosa.isabelle.inventorycontrol.model.response.StockResponseModel;
import rosa.isabelle.inventorycontrol.service.StockService;

@RestController
@RequestMapping("{ownerId}/store/{storeId}/stock")
public class StockController {

    private StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    StockItemModel addStock(@RequestBody StockRequestModel stockRequest, @PathVariable("storeId") String storeId){
        ModelMapper modelMapper = new ModelMapper();

        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setPublicId(storeId);

        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setPublicId(stockRequest.getItem());

        StockItemDTO stockItem = modelMapper.map(stockRequest, StockItemDTO.class);
        stockItem.setItem(itemDTO);
        stockItem.setStore(storeDTO);

        StockItemDTO savedStock = stockService.addStockItem(stockItem);

        StockItemModel returnSaved = modelMapper.map(savedStock, StockItemModel.class);

        return returnSaved;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    StockResponseModel getStocks(@PathVariable("storeId") String storeId,
                                       @RequestParam(name = "page", defaultValue = "1") int page,
                                       @RequestParam(name = "size", defaultValue = "15") int size){
        try {
            ModelMapper mapper = new ModelMapper();

            StockDTO stock = stockService.getStock(storeId, page-1, size);

            StockResponseModel returnedStock = mapper.map(stock, StockResponseModel.class);

            return returnedStock;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{itemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    StockItemModel deleteItem(@PathVariable("itemId") String itemId, @PathVariable("storeId") String storeId){
        ModelMapper modelMapper = new ModelMapper();

        StockItemDTO deletedItem = stockService.removeStockItem(storeId, itemId);

        StockItemModel returnDeleted = modelMapper.map(deletedItem, StockItemModel.class);
        return returnDeleted;
    }
}
