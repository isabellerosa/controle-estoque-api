package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
        this.idGenerator = new IdBuilder().appendValidCharacters(IdBuilder.ALPHABET_CAPS)
                .appendValidCharacters(IdBuilder.ALPHABET_NO_CAPS);
    }

    @Override
    public ItemDTO registerItem(ItemDTO newItemDTO) {
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

            ItemEntity savedItem = itemRepository.save(newItem);

            ItemDTO savedItemDTO = mapper.map(savedItem, ItemDTO.class);

            return savedItemDTO;
        }catch (CustomException customException) {
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    private ItemEntity findByPublicId(String publidId) {
        return itemRepository.findByPublicId(publidId);
    }

    private ItemEntity findByName(String itemName) {
        return itemRepository.findByName(itemName);
    }

    private boolean isPublicIdUnique(String publicId) {
        return findByPublicId(publicId) == null;
    }

    @Override
    public ItemDTO editItem(ItemDTO editedItemDTO) {
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

            ItemEntity modifiedItem = itemRepository.save(editedItem);

            ItemDTO modifiedItemDTO = mapper.map(modifiedItem, ItemDTO.class);

            return modifiedItemDTO;
        }catch (CustomException customException){
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public ItemDTO deleteItem(String publicId) {
        try {
            ItemEntity item = itemRepository.findByPublicId(publicId);

            if (item == null) {
                ErrorMessage error = ErrorMessage.NO_DATA_FOUND;
                throw new CustomException(error.getMessage(), error.getStatusCode().value());
            }

            itemRepository.delete(item);

            ModelMapper mapper = new ModelMapper();
            ItemDTO deletedItemDTO = mapper.map(item, ItemDTO.class);

            return deletedItemDTO;
        }catch (CustomException customException){
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    @Override
    public List<ItemDTO> findItems(String sellerId, int page, int size) {
        try {
            ModelMapper modelMapper = new ModelMapper();
            final int DEFAULT_SIZE = 15;
            final int FIRST_PAGE = 0;

            Pageable pageable = PageRequest.of(
                    Math.max(page, FIRST_PAGE),
                    size > 0 ? size : DEFAULT_SIZE);

            Page<ItemEntity> itemsPage = itemRepository.findBySellerId(sellerId, pageable);

            Type typeToken = new TypeToken<List<ItemDTO>>() {
            }.getType();

            List<ItemDTO> itemsPageDTO = modelMapper.map(itemsPage.getContent(), typeToken);

            return itemsPageDTO;
        }catch (CustomException customException){
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

    public ItemDTO findItem(String publicId) {
        try {
            ItemEntity item = findByPublicId(publicId);

            if (item == null) {
                throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                        ErrorMessage.NO_DATA_FOUND.getStatusCode().value());
            }

            ModelMapper modelMapper = new ModelMapper();

            ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);

            return itemDTO;
        }catch (CustomException customException){
            throw customException;
        }catch (Exception exception){
            ErrorMessage error = ErrorMessage.DEFAULT_ERROR;
            throw new CustomException(error.getMessage(), error.getStatusCode().value());
        }
    }

}
