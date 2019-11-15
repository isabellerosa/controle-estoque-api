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
    public StoreResponseModel createStore(@RequestBody StoreRequestModel newStoreRequest,
                                   @PathVariable("ownerId") String ownerId){
        try {
            LOGGER.debug("Received store: " + newStoreRequest);

            ModelMapper mapper = new ModelMapper();

            StoreDTO newStoreDTO = mapper.map(newStoreRequest, StoreDTO.class);
            newStoreDTO.setOwnerId(ownerId);

            StoreDTO createdStoreDTO = storeService.createStore(newStoreDTO);
            LOGGER.debug("Created store: " + createdStoreDTO);

            StoreResponseModel createdStoreResponse = mapper.map(createdStoreDTO, StoreResponseModel.class);

            return createdStoreResponse;

        }catch (CustomException customException){
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<StoreResponseModel> findStores(@PathVariable("ownerId") String ownerId){
        try {
            ModelMapper mapper = new ModelMapper();

            List<StoreDTO> ownerStores = storeService.findStores(ownerId);

            Type type = new TypeToken<List<StoreResponseModel>>() {}.getType();

            List<StoreResponseModel> storeListResponse = mapper.map(ownerStores, type);

            return storeListResponse;
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{storeId}",
                   produces = MediaType.APPLICATION_JSON_VALUE)
    public StoreResponseModel deleteStore(@PathVariable("ownerId") String ownerId,
                                          @PathVariable("storeId") String storeId) {
        try {
            ModelMapper mapper = new ModelMapper();

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setPublicId(storeId);
            storeDTO.setOwnerId(ownerId);

            StoreDTO deletedStoreDTO = storeService.deleteStore(storeDTO);

            StoreResponseModel deletedStoreResponse = mapper.map(deletedStoreDTO, StoreResponseModel.class);

            return deletedStoreResponse;

        }catch(CustomException customException){
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{storeId}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public StoreResponseModel updateStore(@RequestBody StoreRequestModel editedStoreRequest,
                                          @PathVariable("ownerId") String ownerId,
                                          @PathVariable("storeId") String storeId){
        try {
            LOGGER.debug("Received payload: " + editedStoreRequest);

            ModelMapper mapper = new ModelMapper();

            StoreDTO editedStoreDTO = mapper.map(editedStoreRequest, StoreDTO.class);
            editedStoreDTO.setPublicId(storeId);
            editedStoreDTO.setOwnerId(ownerId);

            StoreDTO modifiedStoreDTO = storeService.editStore(editedStoreDTO);
            LOGGER.debug("Modified store: " + modifiedStoreDTO);

            StoreResponseModel modifiedStoreResponse = mapper.map(modifiedStoreDTO, StoreResponseModel.class);

            return modifiedStoreResponse;

        }catch (CustomException customException){
            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
