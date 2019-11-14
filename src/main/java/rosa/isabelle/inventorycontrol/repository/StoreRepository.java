package rosa.isabelle.inventorycontrol.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rosa.isabelle.inventorycontrol.model.entity.StoreEntity;

@Repository
public interface StoreRepository extends CrudRepository<StoreEntity, Long> {
    StoreEntity save(StoreEntity store);
    StoreEntity findByPublicIdAndOwnerId(String publicId, String ownerId);
}
