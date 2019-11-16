package rosa.isabelle.inventorycontrol.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/user/{ownerId}/store")
public class StoreController {
    private StoreService storeService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public StoreResponseModel createStore(@RequestBody StoreRequestModel newStoreRequest,
                                   @PathVariable("ownerId") String ownerId){
        LOGGER.debug("Starting createStore with store name: {} and ownerId: {}", newStoreRequest.getName(), ownerId);

        try {
            ModelMapper mapper = new ModelMapper();

            StoreDTO newStoreDTO = mapper.map(newStoreRequest, StoreDTO.class);
            newStoreDTO.setOwnerId(ownerId);

            StoreDTO createdStoreDTO = storeService.createStore(newStoreDTO);

            return mapper.map(createdStoreDTO, StoreResponseModel.class);

        }catch (CustomException customException){
            LOGGER.error("An exception occurred at createStore with store name: {} and ownerId: {}",
                    newStoreRequest.getName(), ownerId);

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            LOGGER.error("An exception occurred at createStore with store name: {} and ownerId: {}",
                    newStoreRequest.getName(), ownerId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<StoreResponseModel> findStores(@PathVariable("ownerId") String ownerId){
        LOGGER.debug("Starting findStores with ownerId: {}", ownerId);

        try {
            ModelMapper mapper = new ModelMapper();

            List<StoreDTO> ownerStores = storeService.findStores(ownerId);

            Type type = new TypeToken<List<StoreResponseModel>>() {}.getType();

            return mapper.map(ownerStores, type);
        }catch (Exception exception){
            LOGGER.error("An exception occurred at findStores with ownerId: {}", ownerId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path = "/{storeId}")
    public StoreResponseModel deleteStore(@PathVariable("ownerId") String ownerId,
                                          @PathVariable("storeId") String storeId) {
        LOGGER.debug("Starting deleteStore with storeId: {}", storeId);

        try {
            ModelMapper mapper = new ModelMapper();

            StoreDTO storeDTO = new StoreDTO();
            storeDTO.setPublicId(storeId);
            storeDTO.setOwnerId(ownerId);

            StoreDTO deletedStoreDTO = storeService.deleteStore(storeId);

            return mapper.map(deletedStoreDTO, StoreResponseModel.class);

        }catch(CustomException customException){
            LOGGER.error("An exception occurred at deleteStore with storeId: {}", storeId);

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            LOGGER.error("An exception occurred at deleteStore with storeId: {}", storeId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(path = "/{storeId}")
    public StoreResponseModel updateStore(@RequestBody StoreRequestModel editedStoreRequest,
                                          @PathVariable("ownerId") String ownerId,
                                          @PathVariable("storeId") String storeId){
        LOGGER.debug("Starting updateStore with ownerId: {} and storeId: {}", ownerId, storeId);

        try {
            ModelMapper mapper = new ModelMapper();

            StoreDTO editedStoreDTO = mapper.map(editedStoreRequest, StoreDTO.class);
            editedStoreDTO.setPublicId(storeId);
            editedStoreDTO.setOwnerId(ownerId);

            StoreDTO modifiedStoreDTO = storeService.editStore(editedStoreDTO);
            LOGGER.debug("Modified store: " + modifiedStoreDTO);

            return mapper.map(modifiedStoreDTO, StoreResponseModel.class);

        }catch (CustomException customException){
            LOGGER.error("An exception occurred at updateStore with ownerId: {} and storeId: {}", ownerId, storeId);

            HttpStatus code = HttpStatus.valueOf(customException.getStatusCode());
            throw new ResponseStatusException(code, customException.getMessage());
        }catch (Exception exception){
            LOGGER.error("An exception occurred at updateStore with ownerId: {} and storeId: {}", ownerId, storeId);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
