package by.innowise.inventoryservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "inventories")
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@ToString(exclude = "items")
public class Inventory {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "location")
    private String location;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryItem> items;

    public void addItem(InventoryItem item) {
        items.add(item);
        item.setInventory(this);
    }
}
