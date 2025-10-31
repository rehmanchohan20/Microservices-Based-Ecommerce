package com.microservice.product.response;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Response {

    private boolean success;
    private String message;
    private Map<String, Object> data;
    private LocalDateTime timestamp;

    public Response() {
        this.timestamp = LocalDateTime.now();
    }

    public void setData(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
    }

    public static Response setResponse(boolean success, String message, Object value) {
        Response r = new Response();
        r.setSuccess(success);
        r.setMessage(message);
        r.setData("data", value);
        return r;
    }
}
