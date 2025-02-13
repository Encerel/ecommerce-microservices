package by.innowise.orderservice.repository;

import by.innowise.orderservice.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByUserId(UUID userId);
}
