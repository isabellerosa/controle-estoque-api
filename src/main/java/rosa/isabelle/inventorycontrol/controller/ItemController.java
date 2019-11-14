package rosa.isabelle.inventorycontrol.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rosa.isabelle.inventorycontrol.dto.ItemDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.model.request.ItemRequestModel;
import rosa.isabelle.inventorycontrol.model.response.ItemResponseModel;
import rosa.isabelle.inventorycontrol.service.ItemService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("{sellerId}/item")
public class ItemController {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponseModel createItem(@RequestBody ItemRequestModel itemRequest,
                                        @PathVariable("sellerId") String sellerId){
        try {
            ModelMapper mapper = new ModelMapper();

            ItemDTO requestDTO = mapper.map(itemRequest, ItemDTO.class);
            requestDTO.setSellerId(sellerId);

            ItemDTO savedItem = itemService.registerItem(requestDTO);

            ItemResponseModel responseModel = mapper.map(savedItem, ItemResponseModel.class);

            return responseModel;

        }catch (CustomException customException){
            customException.printStackTrace();
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemResponseModel> getItems(@PathVariable("sellerId") String sellerId,
                                            @RequestParam(name = "page", defaultValue = "1") int page,
                                            @RequestParam(name = "size", defaultValue = "15") int size){
        try {
            ModelMapper mapper = new ModelMapper();

            List<ItemDTO> items = itemService.getItems(sellerId, page-1, size);

            Type type = new TypeToken<List<ItemResponseModel>>() {}.getType();

            List<ItemResponseModel> returnedItems = mapper.map(items, type);

            return returnedItems;
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
