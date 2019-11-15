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
        try {
            LOGGER.debug("Received storeDTO: " + newStoreDTO);
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setAmbiguityIgnored(true);
            mapper.createTypeMap(StoreDTO.class, StoreEntity.class)
                    .addMapping(StoreDTO::getPublicId, StoreEntity::setPublicId);

            StoreEntity newStore = mapper.map(newStoreDTO, StoreEntity.class);

            if (findByName(newStore.getName()) != null) {
                LOGGER.debug("There is already an existent store with id " + newStore.getPublicId() +
                        "For user " + newStore.getOwnerId());

                throw new CustomException(ErrorMessage.DUPLICATED_DATA.getMessage() +
                        ": there is already a registered store with ID " + newStoreDTO.getPublicId(),
                        ErrorMessage.DUPLICATED_DATA.getStatusCode().value());
            }

            do {
                newStore.setPublicId(idBuilder.build());
            } while (findByPublicId(newStore.getPublicId()) != null);

            StoreEntity createdStore = storeRepository.save(newStore);

            LOGGER.debug("Saved store: " + createdStore);

            StoreDTO createdStoreDTO = mapper.map(createdStore, StoreDTO.class);

            return createdStoreDTO;
        }catch (CustomException customException) {
            throw customException;
        }catch (Exception exception){
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
        try {
            StoreEntity originalStore = findByPublicId(editedStoreDTO.getPublicId());

            if (originalStore == null)
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());

            ModelMapper mapper = new ModelMapper();

            StoreEntity editedStore = mapper.map(originalStore, StoreEntity.class);
            editedStore.setName(editedStoreDTO.getName());

            StoreEntity modifiedStore = storeRepository.save(editedStore);

            StoreDTO modifiedStoreDTO = mapper.map(modifiedStore, StoreDTO.class);

            return modifiedStoreDTO;
        }catch (CustomException customException) {
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public StoreDTO deleteStore(StoreDTO storeDTO) {
        try {
            StoreEntity store = findByPublicId(storeDTO.getPublicId());

            if (store == null)
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());

            storeRepository.delete(store);

            ModelMapper mapper = new ModelMapper();
            StoreDTO deletedStore = mapper.map(store, StoreDTO.class);

            return deletedStore;
        }catch (CustomException customException) {
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public List<StoreDTO> findStores(String ownerId) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            List<StoreEntity> stores = storeRepository.findByOwnerId(ownerId);

            Type typeToken = new TypeToken<List<StoreDTO>>() {
            }.getType();

            List<StoreDTO> storesDTO = modelMapper.map(stores, typeToken);

            return storesDTO;
        }catch (CustomException customException) {
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public StoreDTO findStore(String publicId) {
        try {
            StoreEntity store = findByPublicId(publicId);

            if (store == null) {
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());
            }

            ModelMapper mapper = new ModelMapper();

            StoreDTO storeDTO = mapper.map(store, StoreDTO.class);

            return storeDTO;
        }catch (CustomException customException) {
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }
}
