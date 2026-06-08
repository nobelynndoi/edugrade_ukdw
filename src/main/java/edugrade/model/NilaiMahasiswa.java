package edugrade.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class NilaiMahasiswa {
    private final IntegerProperty idNilai;
    private final StringProperty nim;
    private final IntegerProperty idKomponen;
    private final DoubleProperty skorNilai;

    public NilaiMahasiswa(int idNilai, String nim, int idKomponen, double skorNilai) {
        this.idNilai = new SimpleIntegerProperty(idNilai);
        this.nim = new SimpleStringProperty(nim);
        this.idKomponen = new SimpleIntegerProperty(idKomponen);
        this.skorNilai = new SimpleDoubleProperty(skorNilai);
    }

    public int getIdNilai() {
        return idNilai.get();
    }

    public IntegerProperty idNilaiProperty() {
        return idNilai;
    }

    public String getNim() {
        return nim.get();
    }

    public StringProperty nimProperty() {
        return nim;
    }

    public int getIdKomponen() {
        return idKomponen.get();
    }

    public IntegerProperty idKomponenProperty() {
        return idKomponen;
    }

    public double getSkorNilai() {
        return skorNilai.get();
    }

    public void setSkorNilai(double value) {
        skorNilai.set(value);
    }

    public DoubleProperty skorNilaiProperty() {
        return skorNilai;
    }
}
