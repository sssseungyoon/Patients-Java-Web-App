package com.patients.model;

import java.io.Reader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class DataLoader {
    DataFrame dataFrame;
    String fileName;

    public DataLoader(String fileName) {
        dataFrame = new DataFrame();
        this.fileName = fileName;
    }

    // first create columns for the first row
    // implement add rows for all the cols -> need a method

    public DataFrame returnFilledDataFrame() throws IOException {
        try (Reader reader = new FileReader(fileName);
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT)) {

            Boolean firstRow = true;
            for (CSVRecord csvRecord : csvParser) {
                if (firstRow) {
                    dataFrame.addNames(csvRecord);
                    firstRow = false;
                } else {
                    dataFrame.addValues(csvRecord);
                }
            }
        }
        return this.dataFrame;
    }
}
