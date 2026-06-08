package edugrade.model;

import javafx.beans.property.*;

public class MataKuliah {
    private IntegerProperty idMatkul;
    private StringProperty kodeMk;
    private StringProperty namaMk;
    private IntegerProperty sks;
    private DoubleProperty bobotTugas;
    private DoubleProperty bobotUts;
    private DoubleProperty bobotUas;
    private StringProperty semester;
    private StringProperty tahunAjaran;
    private StringProperty kelas;

    public MataKuliah(int id, String kode, String nama, int sks, double bTugas, double bUts, double bUas, String sem, String tahun, String kelas) {
        this.idMatkul = new SimpleIntegerProperty(id);
        this.kodeMk = new SimpleStringProperty(kode);
        this.namaMk = new SimpleStringProperty(nama);
        this.sks = new SimpleIntegerProperty(sks);
        this.bobotTugas = new SimpleDoubleProperty(bTugas);
        this.bobotUts = new SimpleDoubleProperty(bUts);
        this.bobotUas = new SimpleDoubleProperty(bUas);
        this.semester = new SimpleStringProperty(sem);
        this.tahunAjaran = new SimpleStringProperty(tahun);
        this.kelas = new SimpleStringProperty(kelas);
    }

    public int getIdMatkul() { return idMatkul.get(); }
    public IntegerProperty idMatkulProperty() { return idMatkul; }

    public String getKodeMk() { return kodeMk.get(); }
    public void setKodeMk(String v) { kodeMk.set(v); }
    public StringProperty kodeMkProperty() { return kodeMk; }

    public String getNamaMk() { return namaMk.get(); }
    public void setNamaMk(String v) { namaMk.set(v); }
    public StringProperty namaMkProperty() { return namaMk; }

    public int getSks() { return sks.get(); }
    public void setSks(int v) { sks.set(v); }
    public IntegerProperty sksProperty() { return sks; }

    public double getBobotTugas() { return bobotTugas.get(); }
    public void setBobotTugas(double v) { bobotTugas.set(v); }
    public DoubleProperty bobotTugasProperty() { return bobotTugas; }

    public double getBobotUts() { return bobotUts.get(); }
    public void setBobotUts(double v) { bobotUts.set(v); }
    public DoubleProperty bobotUtsProperty() { return bobotUts; }

    public double getBobotUas() { return bobotUas.get(); }
    public void setBobotUas(double v) { bobotUas.set(v); }
    public DoubleProperty bobotUasProperty() { return bobotUas; }

    public String getSemester() { return semester.get(); }
    public void setSemester(String v) { semester.set(v); }
    public StringProperty semesterProperty() { return semester; }

    public String getTahunAjaran() { return tahunAjaran.get(); }
    public void setTahunAjaran(String v) { tahunAjaran.set(v); }
    public StringProperty tahunAjaranProperty() { return tahunAjaran; }

    public String getKelas() { return kelas.get(); }
    public void setKelas(String v) { kelas.set(v); }
    public StringProperty kelasProperty() { return kelas; }

    @Override
    public String toString() { return getNamaMk() + " " + getKelas() + " - " + getSemester() + " " + getTahunAjaran(); }
}
