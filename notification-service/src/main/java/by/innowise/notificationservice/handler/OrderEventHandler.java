package by.innowise.notificationservice.handler;

import by.innowise.notificationservice.constant.Message;
import by.innowise.notificationservice.constant.TopicName;
import by.innowise.notificationservice.model.dto.OrderSummaryDto;
import by.innowise.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventHandler {

    private static final String DEFAULT_TYPE_PROPERTY = "spring.json.value.default.type=by.innowise.notificationservice.model.dto.OrderSummaryDto";
    private final EmailService emailService;

    @KafkaListener(topics = TopicName.ORDER_CREATE_EVENTS_TOPIC, properties = {DEFAULT_TYPE_PROPERTY})
    public void handleCreateOrder(OrderSummaryDto orderSummaryDto) {
        log.info("Created order: {}", orderSummaryDto);

        String text = String.format(Message.TEXT_ORDER_CREATED,
                orderSummaryDto.getId(), orderSummaryDto.getUserId(), orderSummaryDto.getOrderDate());
        emailService.sendEmailToUser(orderSummaryDto.getUserEmail(), Message.SUBJECT_ORDER_CREATED, text);
        emailService.sendEmailToAdmin(Message.SUBJECT_ORDER_CREATED, text);
    }

    @KafkaListener(topics = TopicName.ORDER_STATUS_UPDATES_EVENTS_TOPIC, properties = {DEFAULT_TYPE_PROPERTY})
    public void handleStatusUpdate(OrderSummaryDto orderSummaryDto) {
        log.info("Updated order: {}. New status: {}", orderSummaryDto, orderSummaryDto.getStatus());

        String text = String.format(Message.TEXT_ORDER_STATUS_UPDATED,
                orderSummaryDto.getId(), orderSummaryDto.getStatus());
        emailService.sendEmailToUser(orderSummaryDto.getUserEmail(), Message.SUBJECT_ORDER_STATUS_UPDATED, text);
    }

}
