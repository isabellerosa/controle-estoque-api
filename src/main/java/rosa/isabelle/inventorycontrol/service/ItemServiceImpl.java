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
    public ItemDTO registerItem(ItemDTO itemDTO) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);

        mapper.createTypeMap(ItemDTO.class, ItemEntity.class)
                .addMapping(ItemDTO::getPublicId, ItemEntity::setPublicId);

        ItemEntity received = mapper.map(itemDTO, ItemEntity.class);

        if(findByName(received.getName(), received.getSellerId()) != null) {
            throw new CustomException(ErrorMessage.DUPLICATED_DATA.getMessage(),
                                      ErrorMessage.DUPLICATED_DATA.getStatusCode().value());
        }

        do{
            received.setPublicId(idGenerator.build());
        }while(findByPublicId(received.getPublicId()) != null);

        ItemEntity saved = itemRepository.save(received);

        ItemDTO savedDTO = mapper.map(saved, ItemDTO.class);

        return savedDTO;
    }

    private ItemEntity findByPublicId(String publidId){
        return itemRepository.findByPublicId(publidId);
    }

    private ItemEntity findByName(String name, String sellerId){
        return itemRepository.findByNameAndSellerId(name, sellerId);
    }

    @Override
    public ItemDTO editItem(ItemDTO itemDTO) {
        ItemEntity originalItem = findByPublicId(itemDTO.getPublicId());

        if(originalItem == null)
            throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                    ErrorMessage.NO_DATA_FOUND.getStatusCode().value());

        ModelMapper mapper = new ModelMapper();

        ItemEntity request = mapper.map(originalItem, ItemEntity.class);
        request.setName(itemDTO.getName());
        request.setPrice(itemDTO.getPrice());

        ItemEntity modified = itemRepository.save(request);

        ItemDTO modifiedDTO = mapper.map(modified, ItemDTO.class);

        return modifiedDTO;
    }

    @Override
    public ItemDTO deleteItem(ItemDTO itemDTO) {
        ItemEntity request = findByPublicId(itemDTO.getPublicId());

        if(request == null)
            throw new CustomException(ErrorMessage.NO_DATA_FOUND.getMessage(),
                    ErrorMessage.NO_DATA_FOUND.getStatusCode().value());

        itemRepository.delete(request);

        ModelMapper mapper = new ModelMapper();
        ItemDTO deleted = mapper.map(request, ItemDTO.class);

        return deleted;
    }

    @Override
    public List<ItemDTO> getItems(String sellerId, int page, int size) {
        ModelMapper modelMapper = new ModelMapper();
        final int DEFAULT_SIZE = 15;
        final int FIRST_PAGE = 0;

        Pageable pageable = PageRequest.of(
                Math.max(page, FIRST_PAGE),
                size > 0 ? size : DEFAULT_SIZE);

        Page<ItemEntity> items = itemRepository.findBySellerId(sellerId, pageable);

        Type typeToken = new TypeToken<List<ItemDTO>>(){}.getType();

        List<ItemDTO> returnedItems = modelMapper.map(items.getContent(), typeToken);

        return returnedItems;
    }
}
