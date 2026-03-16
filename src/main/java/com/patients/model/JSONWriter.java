package com.patients.model;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class JSONWriter {

    private final DataFrame dataFrame;

    public JSONWriter(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    /** Write the DataFrame as a JSON array of objects to the given file path. */
    public void write(String filePath) throws IOException {
        try (Writer w = new FileWriter(filePath)) {
            write(w);
        }
    }

    /** Write the DataFrame as a JSON array of objects to the given Writer. */
    public void write(Writer out) throws IOException {
        String[] cols = dataFrame.getColumnNames();
        int rows = dataFrame.getRowCount();

        out.write("[\n");
        for (int r = 0; r < rows; r++) {
            out.write("  {\n");
            for (int c = 0; c < cols.length; c++) {
                String val = dataFrame.getValue(cols[c], r);
                out.write("    \"" + escape(cols[c]) + "\": \"" + escape(val != null ? val : "") + "\"");
                if (c < cols.length - 1) out.write(",");
                out.write("\n");
            }
            out.write("  }");
            if (r < rows - 1) out.write(",");
            out.write("\n");
        }
        out.write("]\n");
    }

    /** Return the DataFrame serialized as a JSON string. */
    public String toJsonString() {
        StringWriter sw = new StringWriter();
        try { write(sw); } catch (IOException ignored) {}
        return sw.toString();
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }
}
