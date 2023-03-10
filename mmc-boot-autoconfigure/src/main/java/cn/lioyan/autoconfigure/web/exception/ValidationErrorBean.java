package cn.lioyan.autoconfigure.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
class ValidationErrorBean {

    private String message;

    private String messageTemplate;

    private String path;

    private Object invalidValue;

    public ValidationErrorBean(String message, String path, Object invalidValue) {
        this.message = message;
        this.path = path;
        this.invalidValue = invalidValue;
    }

    public ValidationErrorBean(String message, String messageTemplate, String path, Object invalidValue) {
        this.message = message;
        this.messageTemplate = messageTemplate;
        this.path = path;
        this.invalidValue = invalidValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public void setInvalidValue(String invalidValue) {
        this.invalidValue = invalidValue;
    }
}
