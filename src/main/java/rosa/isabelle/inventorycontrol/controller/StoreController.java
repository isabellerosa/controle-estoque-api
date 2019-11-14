package rosa.isabelle.inventorycontrol.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import rosa.isabelle.inventorycontrol.dto.StoreDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.model.request.StoreRequestModel;
import rosa.isabelle.inventorycontrol.model.response.StoreResponseModel;
import rosa.isabelle.inventorycontrol.service.StoreService;

import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("{ownerId}/store")
public class StoreController {
    private StoreService storeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestController.class);

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public StoreResponseModel createStore(@RequestBody StoreRequestModel storeRequest,
                                   @PathVariable("ownerId") String ownerId){
        try {
            LOGGER.debug("Received store: " + storeRequest);

            ModelMapper mapper = new ModelMapper();

            StoreDTO requestDTO = mapper.map(storeRequest, StoreDTO.class);
            requestDTO.setOwnerId(ownerId);

            StoreDTO savedStore = storeService.createStore(requestDTO);
            LOGGER.debug("Created store: " + savedStore);

            StoreResponseModel responseModel = mapper.map(savedStore, StoreResponseModel.class);

            return responseModel;

        }catch (CustomException customException){
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StoreResponseModel> getStores(@PathVariable("ownerId") String ownerId){
        try {
            ModelMapper mapper = new ModelMapper();

            List<StoreDTO> ownerStores = storeService.getStores(ownerId);

            Type type = new TypeToken<List<StoreResponseModel>>() {
            }.getType();

            List<StoreResponseModel> returnedStores = mapper.map(ownerStores, type);

            return returnedStores;
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
