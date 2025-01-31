package by.innowise.orderservice.repository;

import by.innowise.orderservice.model.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
