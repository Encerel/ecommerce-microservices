package by.innowise.inventoryservice.repository;

import by.innowise.inventoryservice.model.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    List<InventoryItem> findByProductId(Integer productId);

    List<InventoryItem> findByInventoryIdInAndProductIdIn(Set<Integer> inventoryId, Set<Integer> productId);

    Optional<InventoryItem> findByInventoryIdAndProductId(Integer inventoryId, Integer productId);
}
