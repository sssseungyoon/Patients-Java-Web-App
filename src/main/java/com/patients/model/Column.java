package com.patients.model;

import java.util.ArrayList;

public class Column {
    private String name;
    private ArrayList<String> rows;

    public Column(String name) {
        this.name = name;
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return this.rows.size();
    }

    public ArrayList<String> getRows() {
        return this.rows;
    }

    public String getRowValue(int index) {
        return this.rows.get(index);
    }

    public void addRowValue(String value) {
        this.rows.add(value);
    }

    public void putRowValue(int index, String value) {
        this.rows.set(index, value);
    }

    public void removeRowValue(int index) {
        this.rows.remove(index);
    }
}
