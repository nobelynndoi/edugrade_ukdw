package edugrade.util;

import edugrade.model.KomponenNilai;
import edugrade.model.NilaiRekap;
import org.apache.poi.ss.usermodel.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportUtil {

    /**
     * Memvalidasi struktur file CSV dengan mencocokkan header kolom terhadap
     * daftar komponen nilai yang sudah terdaftar di sistem.
     *
     * @return List nama komponen yang TIDAK ditemukan di header CSV.
     *         List kosong berarti struktur valid.
     */
    public static List<String> validateCsvStructure(File file, List<KomponenNilai> komponenList) throws Exception {
        List<String> missing = new ArrayList<>();
        List<List<String>> data = readCsv(file);
        if (data.isEmpty()) {
            // File kosong — semua komponen dianggap hilang
            for (KomponenNilai k : komponenList) missing.add(k.getNamaKomponen());
            return missing;
        }

        // Baris pertama diasumsikan sebagai header
        List<String> headerRow = data.get(0);
        List<String> lowerHeaders = new ArrayList<>();
        for (String h : headerRow) lowerHeaders.add(h.toLowerCase().trim());

        for (KomponenNilai k : komponenList) {
            String namaLower = k.getNamaKomponen().toLowerCase();
            boolean found = false;
            for (String h : lowerHeaders) {
                if (h.contains(namaLower)) {
                    found = true;
                    break;
                }
            }
            if (!found) missing.add(k.getNamaKomponen());
        }
        return missing;
    }



    public static List<List<String>> readExcel(File file) throws Exception {
        List<List<String>> data = new ArrayList<>();
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                short lastCellNum = row.getLastCellNum();
                if (lastCellNum < 0) lastCellNum = 0;
                for (int cn = 0; cn < lastCellNum; cn++) {
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.add(getCellValueAsString(cell));
                }
                data.add(rowData);
            }
        }
        return data;
    }

    public static List<List<String>> readCsv(File file) throws Exception {
        List<List<String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("[,;\\t]");
                List<String> row = new ArrayList<>();
                for (String part : parts) {
                    row.add(part.trim());
                }
                data.add(row);
            }
        }
        return data;
    }

    public static List<List<String>> parseClipboardText(String text) {
        List<List<String>> data = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) return data;
        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\t");
            List<String> row = new ArrayList<>();
            for (String part : parts) {
                row.add(part.trim());
            }
            data.add(row);
        }
        return data;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val);
                }
                return String.valueOf(val);
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    try { return cell.getStringCellValue(); } catch (Exception ex) { return ""; }
                }
            default: return "";
        }
    }

    public static String cleanNim(String rawNim) {
        if (rawNim == null) return "";
        String clean = rawNim.trim();
        if (clean.endsWith(".0")) {
            clean = clean.substring(0, clean.length() - 2);
        }
        return clean;
    }

    public static class ImportResult {
        public int successCount = 0;
        public List<String> errors = new ArrayList<>();
    }

    public static ImportResult processImportedData(List<List<String>> data, List<NilaiRekap> tableData, List<KomponenNilai> komponenList, edugrade.dao.NilaiMahasiswaDAO dao) {
        ImportResult result = new ImportResult();
        if (data == null || data.isEmpty()) return result;

        int headerRowIndex = -1;
        int nimColIndex = -1;

        // Cari header row (baris yang mengandung 'nim')
        for (int i = 0; i < Math.min(data.size(), 10); i++) {
            List<String> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j).toLowerCase().contains("nim")) {
                    headerRowIndex = i;
                    nimColIndex = j;
                    break;
                }
            }
            if (headerRowIndex != -1) break;
        }

        Map<Integer, Integer> komponenColMap = new HashMap<>();

        if (headerRowIndex != -1) {
            List<String> headerRow = data.get(headerRowIndex);
            for (KomponenNilai k : komponenList) {
                String namaKomponen = k.getNamaKomponen().toLowerCase();
                for (int j = 0; j < headerRow.size(); j++) {
                    if (headerRow.get(j).toLowerCase().contains(namaKomponen)) {
                        komponenColMap.put(k.getIdKomponen(), j);
                        break;
                    }
                }
            }
        } else {
            // Asumsikan kolom pertama adalah NIM jika tidak ada header
            nimColIndex = 0;
            for (int i = 0; i < komponenList.size(); i++) {
                komponenColMap.put(komponenList.get(i).getIdKomponen(), i + 1);
            }
        }

        int successCount = 0;
        int startRow = headerRowIndex != -1 ? headerRowIndex + 1 : 0;

        for (int i = startRow; i < data.size(); i++) {
            List<String> row = data.get(i);
            if (row.size() <= nimColIndex) continue;
            
            String nim = cleanNim(row.get(nimColIndex));
            if (nim.isEmpty() || nim.toLowerCase().contains("nim")) continue;

            NilaiRekap targetRekap = null;
            for (NilaiRekap r : tableData) {
                if (r.getNim().equals(nim)) {
                    targetRekap = r;
                    break;
                }
            }

            if (targetRekap != null) {
                boolean hasUpdates = false;
                for (KomponenNilai k : komponenList) {
                    Integer colIndex = komponenColMap.get(k.getIdKomponen());
                    if (colIndex != null && colIndex < row.size()) {
                        String valStr = row.get(colIndex).trim().replace(",", ".");
                        if (valStr.isEmpty() || valStr.equals("-")) continue;
                        try {
                            double skor = Double.parseDouble(valStr);
                            if (skor >= 0 && skor <= 100) { 
                                targetRekap.setSkor(k.getIdKomponen(), skor);
                                hasUpdates = true;
                            } else {
                                result.errors.add("Baris NIM " + nim + ": Nilai " + k.getNamaKomponen() + " (" + valStr + ") di luar rentang 0-100.");
                            }
                        } catch (NumberFormatException e) {
                            result.errors.add("Baris NIM " + nim + ": Nilai " + k.getNamaKomponen() + " (" + valStr + ") bukan format angka.");
                        }
                    }
                }
                if (hasUpdates) {
                    dao.refreshTotal(targetRekap, komponenList);
                    result.successCount++;
                }
            } else {
                result.errors.add("Baris NIM " + nim + ": Mahasiswa tidak ditemukan di kelas ini.");
            }
        }
        return result;
    }
}
