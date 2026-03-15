package com.patients.model;

import java.util.ArrayList;
import org.apache.commons.csv.CSVRecord;

public class DataFrame {
    ArrayList<Column> columns;

    public DataFrame() {
        columns = new ArrayList<>();
    }

    public void addColumn(Column column) {
        // must ensure that each column must have the same row for all the columns
        // i might have to update this logic
        if (this.columns.isEmpty())
            this.columns.add(column);

        else if (!this.columns.isEmpty() && this.columns.get(0).getSize() == column.getSize()) {
            this.columns.add(column);
        }

    }

    public String[] getColumnNames() {
        // empty case
        if (this.columns.isEmpty())
            return null;

        String[] result = new String[this.columns.size()];
        for (int i = 0; i < this.columns.size(); i++) {
            result[i] = this.columns.get(i).getName();
        }
        return result;
    }

    public int getRowCount() {
        // have to handle the case when the arraylist is empty
        if (this.columns.isEmpty())
            return 0;

        return this.columns.get(0).getSize();
    }

    public String getValue(String columnName, int row) {
        // empty case
        if (this.columns.isEmpty())
            return "";

        for (Column c : this.columns) {
            if (c.name == columnName) {
                return c.getRowValue(row);
            }
        }
        return "";
    }

    public void putValue(String columnName, int row, String value) {
        for (Column c : this.columns) {
            if (c.name == columnName) {
                if (row < this.getRowCount())
                    c.putRowValue(row, value);
            }
        }
    }

    // but this will break the invariance of all the columns rows' being equal
    // unless made ensure that this operation can only be done across multiiple rows
    // i will make this method private to keep the invariance
    private void addValue(String columnName, String value) {
        for (Column c : this.columns) {
            if (c.name == columnName) {
                c.addRowValue(value);
            }
        }
    }

    public void addValues(CSVRecord csvRecord) {
        for (int i = 0; i < columns.size(); i++) {
            String value = csvRecord.get(i);
            String columnName = this.columns.get(i).name;
            this.addValue(columnName, value);
        }
    }

    public void addNames(CSVRecord csvRecord) {
        for (String colName : csvRecord) {
            Column col = new Column(colName);
            this.addColumn(col);
        }
    }

    @Override
    public String toString() {
        if (this.columns.isEmpty()) return "(empty dataframe)";

        // calculate column widths
        int[] widths = new int[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            widths[i] = columns.get(i).getName().length();
            for (int r = 0; r < getRowCount(); r++) {
                widths[i] = Math.max(widths[i], columns.get(i).getRowValue(r).length());
            }
        }

        StringBuilder sb = new StringBuilder();
        String separator = buildSeparator(widths);

        // header
        sb.append(separator);
        sb.append(buildRow(getColumnNames(), widths));
        sb.append(separator);

        // rows
        for (int r = 0; r < getRowCount(); r++) {
            String[] row = new String[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                row[i] = columns.get(i).getRowValue(r);
            }
            sb.append(buildRow(row, widths));
        }
        sb.append(separator);

        return sb.toString();
    }

    private String buildSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : widths) sb.append("-".repeat(w + 2)).append("+");
        return sb.append("\n").toString();
    }

    private String buildRow(String[] values, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < values.length; i++) {
            sb.append(String.format(" %-" + widths[i] + "s |", values[i]));
        }
        return sb.append("\n").toString();
    }

}
