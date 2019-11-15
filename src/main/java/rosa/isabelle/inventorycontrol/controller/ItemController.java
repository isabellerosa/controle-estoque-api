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

            ItemResponseModel createdItemResponse = mapper.map(savedItem, ItemResponseModel.class);

            return createdItemResponse;

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

            List<ItemDTO> itemsPage = itemService.findItems(sellerId, page-1, size);

            Type type = new TypeToken<List<ItemResponseModel>>() {}.getType();

            List<ItemResponseModel> itemsPageResponse = mapper.map(itemsPage, type);

            return itemsPageResponse;
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{itemId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponseModel editItem(@RequestBody ItemRequestModel itemRequest,
                                        @PathVariable("sellerId") String sellerId,
                                        @PathVariable("itemId") String itemId){
        try {
            ModelMapper mapper = new ModelMapper();

            ItemDTO requestDTO = mapper.map(itemRequest, ItemDTO.class);
            requestDTO.setPublicId(itemId);
            requestDTO.setSellerId(sellerId);

            ItemDTO editedItem = itemService.editItem(requestDTO);

            ItemResponseModel modifiedItemResponse = mapper.map(editedItem, ItemResponseModel.class);

            return modifiedItemResponse;

        }catch (CustomException customException){
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{itemId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemResponseModel deleteItem(@PathVariable("sellerId") String sellerId,
                                          @PathVariable("itemId") String itemId) {
        try {
            ModelMapper mapper = new ModelMapper();

            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setPublicId(itemId);
            itemDTO.setSellerId(sellerId);

            ItemDTO deletedItemDTO = itemService.deleteItem(itemId);

            ItemResponseModel deletedItemResponse = mapper.map(deletedItemDTO, ItemResponseModel.class);

            return deletedItemResponse;

        }catch(CustomException customException){
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
