package com.example.service;


import java.util.List;

/**
 * Resultado de la importación masiva de usuarios.
 * Contiene el número de registros insertados y una lista de emails duplicados.
 */
public class ImportResult {
    private int inserted;
    private List<String> duplicates;

    public ImportResult(int inserted, List<String> duplicates) {
        this.inserted = inserted;
        this.duplicates = duplicates;
    }

    public int getInserted() {
        return inserted;
    }

    public void setInserted(int inserted) {
        this.inserted = inserted;
    }

    public List<String> getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(List<String> duplicates) {
        this.duplicates = duplicates;
    }
}
