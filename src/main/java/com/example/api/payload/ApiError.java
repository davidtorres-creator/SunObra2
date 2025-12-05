package com.example.api.payload;

import java.time.Instant;

/**
 * Representa un error de la API.
 */
public class ApiError {
    private boolean ok = false;
    private String error;
    private String path;
    private Instant timestamp = Instant.now();

    public boolean isOk() {
        return ok;
    }
    public void setOk(boolean ok) {
        this.ok = ok;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}


