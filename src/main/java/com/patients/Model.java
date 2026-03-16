package com.patients;

import com.patients.model.DataFrame;
import com.patients.model.DataLoader;
import java.util.ArrayList;

public class Model {

    private static Model instance;
    private DataFrame dataFrame;

    private Model() {
        String path = Model.class.getClassLoader().getResource("patients100.csv").getPath();
        DataLoader loader = new DataLoader(path);
        this.dataFrame = loader.returnFilledDataFrame();
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public String[] getColumnNames() {
        return dataFrame.getColumnNames();
    }

    public int getPatientCount() {
        return dataFrame.getRowCount();
    }

    public String[] getPatient(int index) {
        String[] names = dataFrame.getColumnNames();
        String[] patient = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            patient[i] = dataFrame.getValue(names[i], index);
        }
        return patient;
    }

    public ArrayList<String[]> getAllPatients() {
        ArrayList<String[]> patients = new ArrayList<>();
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            patients.add(getPatient(i));
        }
        return patients;
    }

    public void addPatient(String[] values) {
        dataFrame.addValues(values);
    }

    public void updatePatient(int index, String[] values) {
        String[] names = dataFrame.getColumnNames();
        for (int i = 0; i < names.length; i++) {
            dataFrame.putValue(names[i], index, values[i]);
        }
    }

    public void deletePatient(int index) {
        dataFrame.deleteRow(index);
    }

    public ArrayList<String[]> search(String query) {
        ArrayList<String[]> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String[] keywords = query.trim().toLowerCase().split("\\s+");
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String[] patient = getPatient(i);
            for (String keyword : keywords) {
                boolean matched = false;
                for (String field : patient) {
                    if (field != null && field.toLowerCase().contains(keyword)) {
                        matched = true;
                        break;
                    }
                }
                if (matched) {
                    results.add(patient);
                    break;
                }
            }
        }
        return results;
    }
}
