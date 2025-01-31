package by.innowise.orderservice.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id")
    private UUID userId;

    @ToString.Exclude
    @OneToMany(mappedBy = "order")
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "order_date", columnDefinition = "timestamp")
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;

    public void addItems(List<OrderItem> items) {
        for (OrderItem item : items) {
            item.setOrder(this);
        }
    }
}
