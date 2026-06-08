package edugrade.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class NilaiRekap {
    private final StringProperty nim;
    private final StringProperty namaMahasiswa;
    private final Map<Integer, Double> skorPerKomponen = new HashMap<>();
    private final DoubleProperty nilaiAkhir = new SimpleDoubleProperty(0);
    private final StringProperty gradeHuruf = new SimpleStringProperty("-");
    private final StringProperty noUrut = new SimpleStringProperty("");

    public NilaiRekap(String nim, String namaMahasiswa) {
        this(nim, namaMahasiswa, "");
    }

    public NilaiRekap(String nim, String namaMahasiswa, String noUrut) {
        this.nim = new SimpleStringProperty(nim);
        this.namaMahasiswa = new SimpleStringProperty(namaMahasiswa);
        this.noUrut.set(noUrut == null ? "" : noUrut);
    }

    public String getNim() {
        return nim.get();
    }

    public StringProperty nimProperty() {
        return nim;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa.get();
    }

    public StringProperty namaMahasiswaProperty() {
        return namaMahasiswa;
    }

    public double getSkor(int idKomponen) {
        return skorPerKomponen.getOrDefault(idKomponen, 0.0);
    }

    public void setSkor(int idKomponen, double skor) {
        skorPerKomponen.put(idKomponen, skor);
    }

    public Map<Integer, Double> getSkorPerKomponen() {
        return skorPerKomponen;
    }

    public double getNilaiAkhir() {
        return nilaiAkhir.get();
    }

    public void setNilaiAkhir(double value) {
        nilaiAkhir.set(value);
    }

    public DoubleProperty nilaiAkhirProperty() {
        return nilaiAkhir;
    }

    public String getGradeHuruf() {
        return gradeHuruf.get();
    }

    public void setGradeHuruf(String value) {
        gradeHuruf.set(value);
    }

    public StringProperty gradeHurufProperty() {
        return gradeHuruf;
    }

    public String getNoUrut() { return noUrut.get(); }
    public void setNoUrut(String value) { noUrut.set(value); }
    public StringProperty noUrutProperty() { return noUrut; }
}
