package rosa.isabelle.inventorycontrol.service;

import rosa.isabelle.inventorycontrol.dto.ItemDTO;

import java.util.List;

public interface ItemService {
    ItemDTO registerItem(ItemDTO itemDTO);
    ItemDTO editItem(ItemDTO itemDTO);
    ItemDTO deleteItem(String publicId);
    List<ItemDTO> findItems(String sellerId, int page, int size);
}
