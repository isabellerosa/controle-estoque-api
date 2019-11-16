package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rosa.isabelle.inventorycontrol.dto.StoreDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.exception.ErrorMessage;
import rosa.isabelle.inventorycontrol.model.entity.StoreEntity;
import rosa.isabelle.inventorycontrol.repository.StoreRepository;
import rosa.isabelle.inventorycontrol.utils.IdBuilder;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    private StoreRepository storeRepository;
    private IdBuilder idBuilder;
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
        this.idBuilder = new IdBuilder().appendValidCharacters(IdBuilder.ALPHABET_CAPS)
                                        .appendValidCharacters(IdBuilder.ALPHABET_NO_CAPS);
    }

    @Override
    public StoreDTO createStore(StoreDTO newStoreDTO) {
        LOGGER.debug("Starting service createStore with publicId: {}", newStoreDTO.getPublicId());

        try {
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setAmbiguityIgnored(true);
            mapper.createTypeMap(StoreDTO.class, StoreEntity.class)
                    .addMapping(StoreDTO::getPublicId, StoreEntity::setPublicId);

            StoreEntity newStore = mapper.map(newStoreDTO, StoreEntity.class);

            if (findByName(newStore.getName()) != null) {
                throw new CustomException(ErrorMessage.DUPLICATED_DATA.getMessage(),
                        ErrorMessage.DUPLICATED_DATA.getStatusCode().value());
            }

            do {
                newStore.setPublicId(idBuilder.build());
            } while (findByPublicId(newStore.getPublicId()) != null);

            LOGGER.debug("Trying to insert store on database");
            StoreEntity createdStore = storeRepository.save(newStore);

            LOGGER.debug("Saved store: " + createdStore);

            return mapper.map(createdStore, StoreDTO.class);
        }catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    private StoreEntity findByPublicId(String publicId){
        return storeRepository.findByPublicId(publicId);
    }

    private StoreEntity findByName(String name){
        return storeRepository.findByName(name);
    }

    @Override
    public StoreDTO editStore(StoreDTO editedStoreDTO) {
        LOGGER.debug("Starting service editStore with publicId: {}", editedStoreDTO.getPublicId());

        try {
            StoreEntity originalStore = findByPublicId(editedStoreDTO.getPublicId());

            if (originalStore == null)
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());

            ModelMapper mapper = new ModelMapper();

            StoreEntity editedStore = mapper.map(originalStore, StoreEntity.class);
            editedStore.setName(editedStoreDTO.getName());

            LOGGER.debug("Trying to update store on database");
            StoreEntity modifiedStore = storeRepository.save(editedStore);

            return mapper.map(modifiedStore, StoreDTO.class);
        }catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public StoreDTO deleteStore(String publicId) {
        LOGGER.debug("Starting service deleteStore with publicId: {}", publicId);

        try {
            StoreEntity store = findByPublicId(publicId);

            if (store == null)
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());

            LOGGER.debug("Trying to delete store from database");
            storeRepository.delete(store);

            ModelMapper mapper = new ModelMapper();

            return mapper.map(store, StoreDTO.class);
        }catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public List<StoreDTO> findStores(String ownerId) {
        LOGGER.debug("Starting service findStores with ownerId: {}", ownerId);

        try {
            ModelMapper modelMapper = new ModelMapper();

            LOGGER.debug("Searching stores on database");
            List<StoreEntity> stores = storeRepository.findByOwnerId(ownerId);

            Type typeToken = new TypeToken<List<StoreDTO>>() {
            }.getType();

            return modelMapper.map(stores, typeToken);
        }catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public StoreDTO findStore(String publicId) {
        LOGGER.debug("Starting service findStore with publicId: {}", publicId);

        try {
            StoreEntity store = findByPublicId(publicId);

            if (store == null) {
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());
            }

            ModelMapper mapper = new ModelMapper();

            return mapper.map(store, StoreDTO.class);
        }catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }
}
