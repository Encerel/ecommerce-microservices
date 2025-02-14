package by.innowise.notificationservice.handler;

import by.innowise.notificationservice.constant.Message;
import by.innowise.notificationservice.model.dto.ProductReadDto;
import by.innowise.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static by.innowise.notificationservice.constant.TopicName.PRODUCT_CREATE_EVENTS_TOPIC;
import static by.innowise.notificationservice.constant.TopicName.PRODUCT_STATUS_UPDATES_EVENTS_TOPIC;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventHandler {

    private static final String DEFAULT_TYPE_PROPERTY = "spring.json.value.default.type=by.innowise.notificationservice.model.dto.ProductReadDto";
    private final EmailService emailService;

    @KafkaListener(topics = PRODUCT_CREATE_EVENTS_TOPIC, properties = {DEFAULT_TYPE_PROPERTY})
    public void handleCreateProduct(ProductReadDto productReadDto) {
        log.info("Created product: {}", productReadDto);

        String text = String.format(Message.TEXT_PRODUCT_CREATED,
                productReadDto.getName(), productReadDto.getDescription(), productReadDto.getPrice());
        emailService.sendEmailToAdmin(Message.SUBJECT_PRODUCT_CREATED, text);
    }

    @KafkaListener(topics = PRODUCT_STATUS_UPDATES_EVENTS_TOPIC, properties = {DEFAULT_TYPE_PROPERTY})
    public void handleStatusUpdate(ProductReadDto productReadDto) {
        log.info("Updated product: {}. New status: {}", productReadDto, productReadDto.getStatus());

        String text = String.format(Message.TEXT_PRODUCT_STATUS_UPDATED,
                productReadDto.getName(), productReadDto.getStatus());
        emailService.sendEmailToAdmin(Message.SUBJECT_PRODUCT_STATUS_UPDATED, text);
    }
}

