package by.innowise.notificationservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSummaryDto {

    private Integer id;
    private UUID userId;
    private String userEmail;
    private OrderStatus status;
    private LocalDateTime orderDate;

}