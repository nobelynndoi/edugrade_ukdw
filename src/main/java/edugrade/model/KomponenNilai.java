package edugrade.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KomponenNilai {
    private final IntegerProperty idKomponen;
    private final IntegerProperty idMatkul;
    private final StringProperty namaKomponen;
    private final DoubleProperty bobotPersentase;
    private final BooleanProperty bonus;

    public KomponenNilai(int idKomponen, int idMatkul, String namaKomponen, double bobotPersentase) {
        this(idKomponen, idMatkul, namaKomponen, bobotPersentase, false);
    }

    public KomponenNilai(int idKomponen, int idMatkul, String namaKomponen, double bobotPersentase, boolean bonus) {
        this.idKomponen = new SimpleIntegerProperty(idKomponen);
        this.idMatkul = new SimpleIntegerProperty(idMatkul);
        this.namaKomponen = new SimpleStringProperty(namaKomponen);
        this.bobotPersentase = new SimpleDoubleProperty(bobotPersentase);
        this.bonus = new SimpleBooleanProperty(bonus);
    }

    public int getIdKomponen() {
        return idKomponen.get();
    }

    public IntegerProperty idKomponenProperty() {
        return idKomponen;
    }

    public int getIdMatkul() {
        return idMatkul.get();
    }

    public IntegerProperty idMatkulProperty() {
        return idMatkul;
    }

    public String getNamaKomponen() {
        return namaKomponen.get();
    }

    public void setNamaKomponen(String value) {
        namaKomponen.set(value);
    }

    public StringProperty namaKomponenProperty() {
        return namaKomponen;
    }

    public double getBobotPersentase() {
        return bobotPersentase.get();
    }

    public void setBobotPersentase(double value) {
        bobotPersentase.set(value);
    }

    public DoubleProperty bobotPersentaseProperty() {
        return bobotPersentase;
    }

    public boolean isBonus() {
        return bonus.get();
    }

    public void setBonus(boolean value) {
        bonus.set(value);
    }

    public BooleanProperty bonusProperty() {
        return bonus;
    }
}

