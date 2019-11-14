package rosa.isabelle.inventorycontrol.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rosa.isabelle.inventorycontrol.dto.ItemDTO;
import rosa.isabelle.inventorycontrol.exception.CustomException;
import rosa.isabelle.inventorycontrol.exception.ErrorMessage;
import rosa.isabelle.inventorycontrol.model.entity.ItemEntity;
import rosa.isabelle.inventorycontrol.repository.ItemRepository;
import rosa.isabelle.inventorycontrol.utils.IdBuilder;

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
        return null;
    }

    @Override
    public ItemDTO deleteItem(ItemDTO itemDTO) {
        return null;
    }

    @Override
    public List<ItemDTO> getItems(String ownerId) {
        return null;
    }
}
