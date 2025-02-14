package by.innowise.notificationservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Message {

    public static final String SUBJECT_ORDER_CREATED = "New order created";
    public static final String SUBJECT_ORDER_STATUS_UPDATED = "Order status updated";
    public static final String SUBJECT_PRODUCT_CREATED = "New product created";
    public static final String SUBJECT_PRODUCT_STATUS_UPDATED = "Product status updated";

    public static final String TEXT_ORDER_CREATED = "Order #%d has been created by user %s. Order date: %s";
    public static final String TEXT_ORDER_STATUS_UPDATED = "Order #%d has been updated to status: %s";
    public static final String TEXT_PRODUCT_CREATED = "Product %s has been created. Description: %s, Price: %.2f";
    public static final String TEXT_PRODUCT_STATUS_UPDATED = "Product %s status has been updated to: %s";
}
