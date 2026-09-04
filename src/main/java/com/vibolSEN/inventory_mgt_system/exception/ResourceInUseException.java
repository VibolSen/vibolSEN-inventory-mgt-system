package com.vibolSEN.inventory_mgt_system.exception;

public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }

    public ResourceInUseException(String resourceName, String fieldName, Object fieldValue, String reason) {
        super(String.format("Cannot delete %s with %s: '%s' because %s", resourceName, fieldName, fieldValue, reason));
    }
}
