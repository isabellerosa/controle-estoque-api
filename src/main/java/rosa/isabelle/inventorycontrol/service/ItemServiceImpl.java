package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rosa.isabelle.inventorycontrol.dto.ItemDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.exception.ErrorMessage;
import rosa.isabelle.inventorycontrol.model.entity.ItemEntity;
import rosa.isabelle.inventorycontrol.repository.ItemRepository;
import rosa.isabelle.inventorycontrol.utils.IdBuilder;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private ItemRepository itemRepository;
    private final IdBuilder idGenerator;
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.idGenerator = new IdBuilder().appendValidCharacters(IdBuilder.ALPHABET_CAPS)
                .appendValidCharacters(IdBuilder.ALPHABET_NO_CAPS);
    }

    @Override
    public ItemDTO registerItem(ItemDTO newItemDTO) {
        LOGGER.debug("Starting service registerItem with item name: {}", newItemDTO.getName());

        try {
            ModelMapper mapper = new ModelMapper();
            mapper.getConfiguration().setAmbiguityIgnored(true);

            mapper.createTypeMap(ItemDTO.class, ItemEntity.class)
                    .addMapping(ItemDTO::getPublicId, ItemEntity::setPublicId);

            ItemEntity newItem = mapper.map(newItemDTO, ItemEntity.class);

            if (findByName(newItem.getName()) != null) {
                ErrorMessage error = ErrorMessage.DUPLICATED_DATA;
                throw new CustomException(error.getMessage(),
                        error.getStatusCode().value());
            }

            do {
                newItem.setPublicId(idGenerator.build());
            } while (!isPublicIdUnique(newItem.getPublicId()));

            LOGGER.debug("Trying to insert item on database");
            ItemEntity savedItem = itemRepository.save(newItem);

            return mapper.map(savedItem, ItemDTO.class);
        }catch (CustomException customException) {
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    private ItemEntity findByPublicId(String publicId) {
        return itemRepository.findByPublicId(publicId);
    }

    private ItemEntity findByName(String itemName) {
        return itemRepository.findByName(itemName);
    }

    private boolean isPublicIdUnique(String publicId) {
        return findByPublicId(publicId) == null;
    }

    @Override
    public ItemDTO editItem(ItemDTO editedItemDTO) {
        LOGGER.debug("Starting service editItem with publicId: {}", editedItemDTO.getPublicId());

        try {
            ItemEntity originalItem = findByPublicId(editedItemDTO.getPublicId());

            if (originalItem == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            ModelMapper mapper = new ModelMapper();

            ItemEntity editedItem = mapper.map(originalItem, ItemEntity.class);
            editedItem.setName(editedItemDTO.getName());
            editedItem.setPrice(editedItemDTO.getPrice());

            LOGGER.debug("Updating item on database");
            ItemEntity modifiedItem = itemRepository.save(editedItem);

            return mapper.map(modifiedItem, ItemDTO.class);
        }catch (CustomException customException){
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public ItemDTO deleteItem(String publicId) {
        LOGGER.debug("Starting service deleteItem with publicId: {}", publicId);

        try {
            ItemEntity item = itemRepository.findByPublicId(publicId);

            if (item == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            LOGGER.debug("Trying to delete item from database");
            itemRepository.delete(item);

            ModelMapper mapper = new ModelMapper();

            return mapper.map(item, ItemDTO.class);
        }catch (CustomException customException){
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public List<ItemDTO> findItems(String sellerId, int page, int size) {
        LOGGER.debug("Starting service findItems with sellerId: {}", sellerId);

        try {
            ModelMapper modelMapper = new ModelMapper();
            final int DEFAULT_SIZE = 15;
            final int FIRST_PAGE = 0;

            Pageable pageable = PageRequest.of(
                    Math.max(page, FIRST_PAGE),
                    size > 0 ? size : DEFAULT_SIZE);

            LOGGER.debug("Searching for items on database");
            Page<ItemEntity> itemsPage = itemRepository.findBySellerId(sellerId, pageable);

            Type typeToken = new TypeToken<List<ItemDTO>>() {
            }.getType();

            return modelMapper.map(itemsPage.getContent(), typeToken);
        }catch (CustomException customException){
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    public ItemDTO findItem(String publicId) {
        LOGGER.debug("Starting service findItem with publicId: {}", publicId);

        try {
            ItemEntity item = findByPublicId(publicId);

            if (item == null) {
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());
            }

            ModelMapper modelMapper = new ModelMapper();

            return modelMapper.map(item, ItemDTO.class);
        }catch (CustomException customException){
            LOGGER.error("An exception occurred: {}", customException.getMessage());

            throw customException;
        }catch (Exception exception){
            LOGGER.error("An exception occurred: {}", exception.getMessage());

            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }
}
