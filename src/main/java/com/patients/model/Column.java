package com.patients.model;

import java.util.ArrayList;

public class Column {
    String name;
    ArrayList<String> rows;

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

    public String getRowValue(int index) {
        return this.rows.get(index);
    }

    public void addRowValue(String value) {
        this.rows.add(value);
    }

    public void putRowValue(int index, String value) {
        this.rows.set(index, value);
    }
}
