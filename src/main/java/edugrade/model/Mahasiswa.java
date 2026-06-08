package edugrade.model;

import javafx.beans.property.*;

public class Mahasiswa {
    private StringProperty nim;
    private StringProperty nama;
    private StringProperty angkatan;
    private StringProperty kelas;
    private StringProperty noUrut;
    private BooleanProperty selected;

    public Mahasiswa(String nim, String nama, String angkatan, String kelas) {
        this(nim, nama, angkatan, kelas, "");
    }

    public Mahasiswa(String nim, String nama, String angkatan, String kelas, String noUrut) {
        this.nim = new SimpleStringProperty(nim);
        this.nama = new SimpleStringProperty(nama);
        this.angkatan = new SimpleStringProperty(angkatan);
        this.kelas = new SimpleStringProperty(kelas);
        this.noUrut = new SimpleStringProperty(noUrut == null ? "" : noUrut);
        this.selected = new SimpleBooleanProperty(false);
    }

    public String getNim() { return nim.get(); }
    public void setNim(String v) { nim.set(v); }
    public StringProperty nimProperty() { return nim; }

    public String getNama() { return nama.get(); }
    public void setNama(String v) { nama.set(v); }
    public StringProperty namaProperty() { return nama; }

    public String getAngkatan() { return angkatan.get(); }
    public void setAngkatan(String v) { angkatan.set(v); }
    public StringProperty angkatanProperty() { return angkatan; }

    public String getKelas() { return kelas.get(); }
    public void setKelas(String v) { kelas.set(v); }
    public StringProperty kelasProperty() { return kelas; }

    public boolean isSelected() { return selected.get(); }
    public void setSelected(boolean v) { selected.set(v); }
    public BooleanProperty selectedProperty() { return selected; }

    public String getNoUrut() { return noUrut.get(); }
    public void setNoUrut(String v) { noUrut.set(v); }
    public StringProperty noUrutProperty() { return noUrut; }

    @Override
    public String toString() { return getNim() + " - " + getNama(); }
}
