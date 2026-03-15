package com.patients;

import java.io.IOException;
import com.patients.model.DataLoader;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileName = Main.class.getClassLoader().getResource("patients100.csv").getPath();
        DataLoader dataloader = new DataLoader(fileName);
        System.out.println(dataloader.returnFilledDataFrame());
    }
}
