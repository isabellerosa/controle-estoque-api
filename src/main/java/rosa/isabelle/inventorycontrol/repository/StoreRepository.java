package rosa.isabelle.inventorycontrol.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rosa.isabelle.inventorycontrol.model.entity.StoreEntity;

import java.util.List;

@Repository
public interface StoreRepository extends CrudRepository<StoreEntity, Long> {
    StoreEntity save(StoreEntity store);
    StoreEntity findByPublicId(String publicId);
    List<StoreEntity> findByOwnerId(String ownerId);
    void delete(StoreEntity store);
    StoreEntity findByName(String name);
}
