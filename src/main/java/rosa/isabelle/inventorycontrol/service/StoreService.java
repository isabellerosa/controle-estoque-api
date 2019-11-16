package rosa.isabelle.inventorycontrol.service;

import rosa.isabelle.inventorycontrol.dto.StoreDTO;

import java.util.List;

public interface StoreService {
    StoreDTO createStore(StoreDTO storeDTO);
    StoreDTO editStore(StoreDTO storeDTO);
    StoreDTO deleteStore(String publicId);
    List<StoreDTO> findStores(String ownerId);
    StoreDTO findStore(String publicId);
}
