package com.example.api.payload;

import java.time.Instant;

/**
 * Envoltorio genérico para respuestas exitosas de la API.
 */
public class ApiResponse<T> {
    private boolean ok;
    private T data;
    private String message;
    private Instant timestamp = Instant.now();

    // Respuesta con datos
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.ok = true;
        resp.data = data;
        resp.message = "success";
        return resp;
    }

    // Respuesta con solo un mensaje (sin datos)
    public static <T> ApiResponse<T> msg(String msg) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.ok = true;
        resp.message = msg;
        return resp;
    }

    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}


