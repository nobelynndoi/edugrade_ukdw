package edugrade.util;

import edugrade.model.KomponenNilai;

import java.util.List;
import java.util.Map;

public class GradeCalculator {

    public static double hitungNilaiAkhir(double tugas, double uts, double uas,
                                          double bobotTugas, double bobotUts, double bobotUas) {
        return (tugas * bobotTugas / 100.0) + (uts * bobotUts / 100.0) + (uas * bobotUas / 100.0);
    }

    public static double hitungNilaiAkhirDinamis(Map<Integer, Double> skorPerKomponen,
                                                 List<KomponenNilai> komponenList) {
        double total = 0;
        for (KomponenNilai komponen : komponenList) {
            double skor = skorPerKomponen.getOrDefault(komponen.getIdKomponen(), 0.0);
            total += skor * (komponen.getBobotPersentase() / 100.0);
        }
        return total;
    }

    public static String konversiKeHuruf(double nilaiAkhir) {
        if (nilaiAkhir >= 85) return "A";
        else if(nilaiAkhir >= 80) return "A-";
        else if (nilaiAkhir >= 75) return "B+";
        else if (nilaiAkhir >= 70) return "B";
        else if (nilaiAkhir >= 65) return "B-";
        else if (nilaiAkhir >= 60) return "C+";
        else if (nilaiAkhir >= 55) return "C";
        else if (nilaiAkhir >= 45) return "D";
        else return "E";
    }

    public static String getGradeColor(String grade) {
        return switch (grade) {
            case "A", "A-" -> "#2ecc71";
            case "B+", "B", "B-" -> "#3498db";
            case "C+", "C" -> "#f39c12";
            case "D" -> "#e67e22";
            case "E" -> "#e74c3c";
            default -> "#95a5a6";
        };
    }

    public static boolean isLulus(String grade) {
        return grade.equals("A") || grade.equals("A-") || grade.equals("B+") || grade.equals("B") ||grade.equals("B-")||grade.equals("C+") ||  grade.equals("C");
    }
}
