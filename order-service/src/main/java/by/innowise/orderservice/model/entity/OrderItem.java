package by.innowise.orderservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "orders_products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "order")
@EqualsAndHashCode(of = "id")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "inventory_id")
    private Integer inventoryId;


}
