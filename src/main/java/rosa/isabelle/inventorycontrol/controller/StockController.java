package rosa.isabelle.inventorycontrol.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rosa.isabelle.inventorycontrol.dto.StockDTO;
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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    StockResponseModel getStocks(@PathVariable("storeId") String storeId,
                                       @RequestParam(name = "page", defaultValue = "1") int page,
                                       @RequestParam(name = "size", defaultValue = "15") int size){
        try {
            ModelMapper mapper = new ModelMapper();

            StockDTO stock = stockService.findStocks(storeId, page-1, size);

            StockResponseModel returnedStock = mapper.map(stock, StockResponseModel.class);

            return returnedStock;
        }catch (Exception exception){
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
