package edugrade.controller;

import edugrade.app.AppSession;
import edugrade.dao.KomponenNilaiDAO;
import edugrade.dao.MahasiswaDAO;
import edugrade.dao.NilaiMahasiswaDAO;
import edugrade.model.KomponenNilai;
import edugrade.model.MataKuliah;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LaporanPageController {
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    private final KomponenNilaiDAO komponenNilaiDAO = new KomponenNilaiDAO();
    private final NilaiMahasiswaDAO nilaiMahasiswaDAO = new NilaiMahasiswaDAO();
    
    @FXML private Label lblTotalMhs;
    @FXML private Label lblLulus;
    @FXML private Label lblTidakLulus;
    @FXML private Label lblRataRata;
    @FXML private Label lblPersenKelulusan;
    
    @FXML private Label lblGrafikTitle;
    @FXML private BarChart<String, Number> chart;
    
    @FXML private ProgressIndicator indicator;
    @FXML private Label lblPercentIndicator;

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/laporan_page.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            MataKuliah mk = AppSession.getActiveMataKuliah();
            List<KomponenNilai> komponen = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
            int totalMahasiswa = mahasiswaDAO.getByMatkul(mk.getIdMatkul()).size();
            int lulus = nilaiMahasiswaDAO.getTotalLulus(mk.getIdMatkul(), komponen);
            double rata = nilaiMahasiswaDAO.getRataRata(mk.getIdMatkul(), komponen);
            double persenLulus = totalMahasiswa == 0 ? 0 : lulus * 100.0 / totalMahasiswa;

            int tidakLulus = totalMahasiswa - lulus;

            lblTotalMhs.setText(String.valueOf(totalMahasiswa));
            lblLulus.setText(String.valueOf(lulus));
            lblTidakLulus.setText(String.valueOf(tidakLulus));
            lblRataRata.setText(UiUtil.DECIMAL.format(rata));
            lblPersenKelulusan.setText(UiUtil.DECIMAL.format(persenLulus) + "%");

            Map<String, Integer> distribusi = nilaiMahasiswaDAO.getDistribusiGrade(mk.getIdMatkul(), komponen);
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : distribusi.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            chart.getData().add(series);

            indicator.setProgress(persenLulus / 100.0);
            lblPercentIndicator.setText(UiUtil.DECIMAL.format(persenLulus) + "% Mahasiswa Lulus");
            
            lblGrafikTitle.setText("Grafik Laporan - " + mk.getKodeMk() + " (" + mk.getNamaMk() + ")");

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }
}
