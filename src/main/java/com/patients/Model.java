package com.patients;

import com.patients.model.DataFrame;
import com.patients.model.DataLoader;
import com.patients.model.JSONWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class Model {

    private static Model instance;
    private DataFrame dataFrame;
    private String filePath;

    private Model() {
        filePath = Model.class.getClassLoader().getResource("patients100.csv").getPath();
        DataLoader loader = new DataLoader(filePath);
        this.dataFrame = loader.returnFilledDataFrame();
    }

    public void saveToCSV() {
        try (Writer writer = new FileWriter(filePath);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord((Object[]) dataFrame.getColumnNames());
            for (int i = 0; i < dataFrame.getRowCount(); i++) {
                printer.printRecord((Object[]) getPatient(i));
            }
        } catch (IOException e) {
            System.err.println("Error saving CSV: " + e.getMessage());
        }
    }

    public String getFilePath() { return filePath; }

    public void writeJSON(Writer out) throws IOException {
        new JSONWriter(dataFrame).write(out);
    }

    public void saveToJSON(String jsonFilePath) throws IOException {
        new JSONWriter(dataFrame).write(jsonFilePath);
    }

    public Map<String, Long> getAgeDistribution() {
        String[] labels = {"0-9","10-19","20-29","30-39","40-49","50-59","60-69","70-79","80-89","90+"};
        Map<String, Long> buckets = new LinkedHashMap<>();
        for (String label : labels) buckets.put(label, 0L);
        LocalDate today = LocalDate.now();
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String deathdate = dataFrame.getValue("DEATHDATE", i);
            if (deathdate != null && !deathdate.isEmpty()) continue;
            String birthStr = dataFrame.getValue("BIRTHDATE", i);
            if (birthStr == null || birthStr.isEmpty()) continue;
            try {
                int age = Period.between(LocalDate.parse(birthStr), today).getYears();
                String label = labels[Math.min(age / 10, 9)];
                buckets.merge(label, 1L, Long::sum);
            } catch (Exception ignored) {}
        }
        return buckets;
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
        saveToCSV();
    }

    public void updatePatient(int index, String[] values) {
        String[] names = dataFrame.getColumnNames();
        for (int i = 0; i < names.length; i++) {
            dataFrame.putValue(names[i], index, values[i]);
        }
        saveToCSV();
    }

    public void deletePatient(int index) {
        dataFrame.deleteRow(index);
        saveToCSV();
    }

    public String[] getOldestPatient() {
        String[] oldest = null;
        LocalDate oldestDate = null;
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String deathdate = dataFrame.getValue("DEATHDATE", i);
            if (deathdate != null && !deathdate.isEmpty()) continue;
            String birthStr = dataFrame.getValue("BIRTHDATE", i);
            if (birthStr == null || birthStr.isEmpty()) continue;
            LocalDate birth = LocalDate.parse(birthStr);
            if (oldestDate == null || birth.isBefore(oldestDate)) {
                oldestDate = birth;
                oldest = getPatient(i);
            }
        }
        return oldest;
    }

    public String[] getYoungestPatient() {
        String[] youngest = null;
        LocalDate youngestDate = null;
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String deathdate = dataFrame.getValue("DEATHDATE", i);
            if (deathdate != null && !deathdate.isEmpty()) continue;
            String birthStr = dataFrame.getValue("BIRTHDATE", i);
            if (birthStr == null || birthStr.isEmpty()) continue;
            LocalDate birth = LocalDate.parse(birthStr);
            if (youngestDate == null || birth.isAfter(youngestDate)) {
                youngestDate = birth;
                youngest = getPatient(i);
            }
        }
        return youngest;
    }

    private Map<String, Long> countByColumn(String columnName) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String val = dataFrame.getValue(columnName, i);
            if (val == null) val = "";
            counts.merge(val, 1L, Long::sum);
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<String, Long> countByCity() { return countByColumn("CITY"); }
    public Map<String, Long> countByState() { return countByColumn("STATE"); }
    public Map<String, Long> countByGender() { return countByColumn("GENDER"); }
    public Map<String, Long> countByMaritalStatus() { return countByColumn("MARITAL"); }
    public Map<String, Long> countByRace() { return countByColumn("RACE"); }

    public double getAverageAge() {
        LocalDate today = LocalDate.now();
        long totalYears = 0;
        int count = 0;
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String deathdate = dataFrame.getValue("DEATHDATE", i);
            if (deathdate != null && !deathdate.isEmpty()) continue;
            String birthStr = dataFrame.getValue("BIRTHDATE", i);
            if (birthStr == null || birthStr.isEmpty()) continue;
            totalYears += Period.between(LocalDate.parse(birthStr), today).getYears();
            count++;
        }
        return count == 0 ? 0.0 : (double) totalYears / count;
    }

    public Map<String, Long> countAliveVsDeceased() {
        long alive = 0, deceased = 0;
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String deathdate = dataFrame.getValue("DEATHDATE", i);
            if (deathdate != null && !deathdate.isEmpty()) deceased++;
            else alive++;
        }
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("Alive", alive);
        result.put("Deceased", deceased);
        return result;
    }

    public List<String> getDistinctValues(String columnName) {
        TreeSet<String> seen = new TreeSet<>();
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            String val = dataFrame.getValue(columnName, i);
            if (val != null) seen.add(val);
        }
        return new ArrayList<>(seen);
    }

    public Map<String, List<String>> getAllDistinctValues() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String col : dataFrame.getColumnNames()) {
            result.put(col, getDistinctValues(col));
        }
        return result;
    }

    public String getDistinctValuesJson() {
        Map<String, List<String>> all = getAllDistinctValues();
        StringBuilder sb = new StringBuilder("{");
        boolean firstCol = true;
        for (Map.Entry<String, List<String>> entry : all.entrySet()) {
            if (!firstCol) sb.append(",");
            firstCol = false;
            sb.append("\"").append(jsonEscape(entry.getKey())).append("\":[");
            boolean firstVal = true;
            for (String v : entry.getValue()) {
                if (!firstVal) sb.append(",");
                firstVal = false;
                sb.append("\"").append(jsonEscape(v)).append("\"");
            }
            sb.append("]");
        }
        return sb.append("}").toString();
    }

    private String jsonEscape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public List<String> validate(String[] cols, String[] values, boolean isAdd, int editIndex) {
        List<String> errors = new ArrayList<>();
        Map<String, String> f = new HashMap<>();
        for (int i = 0; i < cols.length; i++) f.put(cols[i], values[i]);

        // ID — required and unique on add
        String id = f.getOrDefault("ID", "");
        if (id.isEmpty()) {
            errors.add("ID is required.");
        } else if (isAdd) {
            String[] existingCols = dataFrame.getColumnNames();
            int idColIdx = -1;
            for (int i = 0; i < existingCols.length; i++) {
                if ("ID".equals(existingCols[i])) { idColIdx = i; break; }
            }
            if (idColIdx >= 0) {
                for (String[] p : getAllPatients()) {
                    if (id.equals(p[idColIdx])) {
                        errors.add("A patient with ID \"" + id + "\" already exists.");
                        break;
                    }
                }
            }
        }

        // FIRST and LAST — required
        if (f.getOrDefault("FIRST", "").isEmpty()) errors.add("First name is required.");
        if (f.getOrDefault("LAST", "").isEmpty())  errors.add("Last name is required.");

        // BIRTHDATE — required, YYYY-MM-DD
        String birthStr = f.getOrDefault("BIRTHDATE", "");
        LocalDate birthDate = null;
        if (birthStr.isEmpty()) {
            errors.add("BIRTHDATE is required.");
        } else {
            try { birthDate = LocalDate.parse(birthStr); }
            catch (Exception e) { errors.add("BIRTHDATE must be in YYYY-MM-DD format."); }
        }

        // DEATHDATE — optional, YYYY-MM-DD, must be after BIRTHDATE
        String deathStr = f.getOrDefault("DEATHDATE", "");
        if (!deathStr.isEmpty()) {
            try {
                LocalDate deathDate = LocalDate.parse(deathStr);
                if (birthDate != null && !deathDate.isAfter(birthDate))
                    errors.add("DEATHDATE must be after BIRTHDATE.");
            } catch (Exception e) {
                errors.add("DEATHDATE must be in YYYY-MM-DD format.");
            }
        }

        // GENDER — M or F if present
        String gender = f.getOrDefault("GENDER", "");
        if (!gender.isEmpty() && !gender.equals("M") && !gender.equals("F"))
            errors.add("GENDER must be M or F.");

        // MARITAL — M or S if present
        String marital = f.getOrDefault("MARITAL", "");
        if (!marital.isEmpty() && !marital.equals("M") && !marital.equals("S"))
            errors.add("MARITAL must be M (married) or S (single).");

        // SSN — ###-##-#### if present
        String ssn = f.getOrDefault("SSN", "");
        if (!ssn.isEmpty() && !ssn.matches("\\d{3}-\\d{2}-\\d{4}"))
            errors.add("SSN must be in format ###-##-#### (e.g. 123-45-6789).");

        // ZIP — 5 digits if present
        String zip = f.getOrDefault("ZIP", "");
        if (!zip.isEmpty() && !zip.matches("\\d{5}"))
            errors.add("ZIP must be exactly 5 digits.");

        return errors;
    }

    public ArrayList<String[]> searchByColumn(String columnName, String value) {
        ArrayList<String[]> results = new ArrayList<>();
        if (columnName == null || value == null) return results;
        for (int i = 0; i < dataFrame.getRowCount(); i++) {
            if (value.equals(dataFrame.getValue(columnName, i))) {
                results.add(getPatient(i));
            }
        }
        return results;
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
