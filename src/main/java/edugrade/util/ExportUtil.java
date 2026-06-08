package edugrade.util;

import edugrade.model.KomponenNilai;
import edugrade.model.Mahasiswa;
import edugrade.model.NilaiRekap;
import edugrade.model.MataKuliah;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import edugrade.controller.UiUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExportUtil {

    /**
     * Mengekspor template CSV dinamis berdasarkan komponen nilai yang sudah
     * didefinisikan dosen untuk mata kuliah tersebut. Header kolom menyesuaikan
     * secara otomatis dengan komponen yang aktif.
     *
     * Format header: NIM,Nama,<NamaKomponen1>,<NamaKomponen2>,...
     * Baris data: NIM dan Nama mahasiswa sudah terisi, kolom nilai dikosongkan.
     */
    public static void exportCsvTemplate(File file, MataKuliah mk,
                                         List<KomponenNilai> komponenList,
                                         List<NilaiRekap> rekapList) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // --- Baris header ---
            StringBuilder header = new StringBuilder("No,NIM,Nama");
            for (KomponenNilai k : komponenList) {
                // Nama komponen dikutip agar aman jika mengandung koma
                header.append(",").append(escapeCsv(k.getNamaKomponen()));
            }
            writer.println(header);

            // --- Baris data (NIM & Nama terisi, nilai dikosongkan untuk diisi dosen) ---
            for (NilaiRekap r : rekapList) {
                StringBuilder row = new StringBuilder();
                row.append(escapeCsv(r.getNoUrut()))
                   .append(",")
                   .append(escapeCsv(r.getNim()))
                   .append(",")
                   .append(escapeCsv(r.getNamaMahasiswa()));
                for (int i = 0; i < komponenList.size(); i++) {
                    row.append(","); // kolom nilai dikosongkan
                }
                writer.println(row);
            }
        }
    }

    /** Membungkus nilai CSV dengan tanda kutip jika mengandung koma, kutip, atau newline. */
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }


    public static void exportNilaiToExcel(File file, MataKuliah mk,
                                          List<KomponenNilai> komponenList,
                                          List<NilaiRekap> rekapList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nilai");

            // Header styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Title style
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.cloneStyleFrom(dataStyle);
            numberStyle.setAlignment(HorizontalAlignment.CENTER);
            DataFormat format = workbook.createDataFormat();
            numberStyle.setDataFormat(format.getFormat("0.00"));

            // Title row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Rekap Nilai - " + mk.getKodeMk() + " " + mk.getNamaMk());
            titleCell.setCellStyle(titleStyle);

            Row infoRow = sheet.createRow(1);
            infoRow.createCell(0).setCellValue("Semester: " + mk.getSemester() + " | Tahun: " + mk.getTahunAjaran() + " | SKS: " + mk.getSks());

            // Header row
            Row header = sheet.createRow(3);
            int col = 0;
            setCellWithStyle(header, col++, "No", headerStyle);
            setCellWithStyle(header, col++, "NIM", headerStyle);
            setCellWithStyle(header, col++, "Nama", headerStyle);
            for (KomponenNilai komponen : komponenList) {
                String label = komponen.getNamaKomponen() + " (" + UiUtil.DECIMAL.format(komponen.getBobotPersentase()) + "%)";
                if (komponen.isBonus()) label += " [BONUS]";
                setCellWithStyle(header, col++, label, headerStyle);
            }
            setCellWithStyle(header, col++, "Nilai Akhir", headerStyle);
            setCellWithStyle(header, col, "Grade", headerStyle);

            // Data rows
            int rowNum = 4;
            for (int i = 0; i < rekapList.size(); i++) {
                NilaiRekap rekap = rekapList.get(i);
                Row row = sheet.createRow(rowNum++);
                col = 0;
                setCellWithStyle(row, col++, rekap.getNoUrut(), dataStyle);
                setCellWithStyle(row, col++, rekap.getNim(), dataStyle);
                setCellWithStyle(row, col++, rekap.getNamaMahasiswa(), dataStyle);
                for (KomponenNilai komponen : komponenList) {
                    setCellDoubleWithStyle(row, col++, rekap.getSkor(komponen.getIdKomponen()), numberStyle);
                }
                setCellDoubleWithStyle(row, col++, rekap.getNilaiAkhir(), numberStyle);
                setCellWithStyle(row, col, rekap.getGradeHuruf(), dataStyle);
            }

            // Auto-size columns
            for (int i = 0; i <= 3 + komponenList.size() + 2; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    public static void exportMahasiswaToExcel(File file, MataKuliah mk,
                                               List<Mahasiswa> mahasiswaList) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data Mahasiswa");

            // Header styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Title style
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            // Data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // Title
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Data Mahasiswa - " + mk.getKodeMk() + " " + mk.getNamaMk());
            titleCell.setCellStyle(titleStyle);

            Row infoRow = sheet.createRow(1);
            infoRow.createCell(0).setCellValue("Semester: " + mk.getSemester() + " | Tahun: " + mk.getTahunAjaran());

            // Header
            Row header = sheet.createRow(3);
            setCellWithStyle(header, 0, "No", headerStyle);
            setCellWithStyle(header, 1, "NIM", headerStyle);
            setCellWithStyle(header, 2, "Nama", headerStyle);
            setCellWithStyle(header, 3, "Kelas", headerStyle);

            // Data
            int rowNum = 4;
            for (int i = 0; i < mahasiswaList.size(); i++) {
                Mahasiswa mhs = mahasiswaList.get(i);
                Row row = sheet.createRow(rowNum++);
                setCellNumberWithStyle(row, 0, i + 1, dataStyle);
                setCellWithStyle(row, 1, mhs.getNim(), dataStyle);
                setCellWithStyle(row, 2, mhs.getNama(), dataStyle);
                setCellWithStyle(row, 3, mhs.getKelas(), dataStyle);
            }

            // Auto-size
            for (int i = 0; i <= 3; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
        }
    }

    private static void setCellWithStyle(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void setCellNumberWithStyle(Row row, int col, int value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private static void setCellDoubleWithStyle(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
