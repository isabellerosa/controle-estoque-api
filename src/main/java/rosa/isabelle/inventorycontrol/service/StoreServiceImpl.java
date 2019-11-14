package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rosa.isabelle.inventorycontrol.dto.StoreDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.exception.ErrorMessage;
import rosa.isabelle.inventorycontrol.model.entity.StoreEntity;
import rosa.isabelle.inventorycontrol.repository.StoreRepository;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    private StoreRepository storeRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public StoreDTO createStore(StoreDTO storeDTO) {
        LOGGER.debug("Received storeDTO: " + storeDTO);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);

        mapper.createTypeMap(StoreDTO.class, StoreEntity.class)
                .addMapping(StoreDTO::getPublicId, StoreEntity::setPublicId);

        StoreEntity received = mapper.map(storeDTO, StoreEntity.class);

        if(findByPublicId(received.getPublicId(), received.getOwnerId()) != null) {
            LOGGER.debug("There is already an existent store with id " + received.getPublicId() +
                    "For user " + received.getOwnerId());

            throw new CustomException(ErrorMessage.DUPLICATED_DATA.getMessage() +
                    ": there is already a registered store with ID " + storeDTO.getPublicId(),
                    ErrorMessage.DUPLICATED_DATA.getStatusCode().value());
        }

        StoreEntity saved = storeRepository.save(received);

        LOGGER.debug("Saved store: " + saved);

        StoreDTO savedDTO = mapper.map(saved, StoreDTO.class);

        return savedDTO;
    }

    private StoreEntity findByPublicId(String publidId, String ownerId){
        return storeRepository.findByPublicIdAndOwnerId(publidId, ownerId);
    }

    @Override
    public StoreDTO editStore(StoreDTO storeDTO) {
        return null;
    }

    @Override
    public StoreDTO deleteStore(StoreDTO storeDTO) {
        return null;
    }

    @Override
    public List<StoreDTO> getStores() {
        return null;
    }
}
