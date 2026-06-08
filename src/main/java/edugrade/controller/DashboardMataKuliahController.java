package edugrade.controller;

import edugrade.app.AppSession;
import edugrade.dao.MataKuliahDAO;
import edugrade.model.MataKuliah;
import edugrade.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardMataKuliahController {
    private final MataKuliahDAO mataKuliahDAO = new MataKuliahDAO();
    private final Runnable openWorkspace;
    private final Runnable onLogout;

    @FXML private TableView<MataKuliah> table;
    @FXML private TableColumn<MataKuliah, String> colKode;
    @FXML private TableColumn<MataKuliah, String> colNama;
    @FXML private TableColumn<MataKuliah, Integer> colSks;
    @FXML private TableColumn<MataKuliah, String> colSemester;
    @FXML private TableColumn<MataKuliah, String> colTahun;
    @FXML private TableColumn<MataKuliah, String> colKelas;

    @FXML private TextField kode;
    @FXML private TextField nama;
    @FXML private TextField sks;
    @FXML private ComboBox<String> semester;
    @FXML private TextField tahun;
    @FXML private TextField kelas;
    @FXML private Button tambah;
    @FXML private Label userLabel;

    private int selectedId = 0;
    private ObservableList<MataKuliah> data;
    private int currentUserId;

    public DashboardMataKuliahController(Runnable openWorkspace, Runnable onLogout) {
        this.openWorkspace = openWorkspace;
        this.onLogout = onLogout;
    }

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/dashboard_matakuliah.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            currentUserId = AppSession.getCurrentUser() != null ? AppSession.getCurrentUser().getId() : 0;
            data = FXCollections.observableArrayList(mataKuliahDAO.getByUser(currentUserId));

            colKode.setCellValueFactory(new PropertyValueFactory<>("kodeMk"));
            colNama.setCellValueFactory(new PropertyValueFactory<>("namaMk"));
            colSks.setCellValueFactory(new PropertyValueFactory<>("sks"));
            colSemester.setCellValueFactory(new PropertyValueFactory<>("semester"));
            colTahun.setCellValueFactory(new PropertyValueFactory<>("tahunAjaran"));
            colKelas.setCellValueFactory(new PropertyValueFactory<>("kelas"));

            table.setItems(data);

            User currentUser = AppSession.getCurrentUser();
            userLabel.setText(currentUser != null ? currentUser.getName() : "Guest");

            table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
                if (selected != null) {
                    selectedId = selected.getIdMatkul();
                    kode.setText(selected.getKodeMk());
                    nama.setText(selected.getNamaMk());
                    sks.setText(String.valueOf(selected.getSks()));
                    semester.setValue(selected.getSemester());
                    tahun.setText(selected.getTahunAjaran());
                    kelas.setText(selected.getKelas());
                    tambah.setText("Simpan");
                }
            });

            table.setOnMouseClicked((MouseEvent event) -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    handleBuka();
                }
            });

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new BorderPane();
        }
    }

    @FXML
    private void handleTambah() {
        try {
            if (UiUtil.isBlank(kode, nama, sks, tahun) || semester.getValue() == null) {
                UiUtil.alert(Alert.AlertType.WARNING, "Kode, nama, SKS, semester, dan tahun wajib diisi.");
                return;
            }
            MataKuliah mk = new MataKuliah(selectedId, kode.getText().trim(), nama.getText().trim(),
                Integer.parseInt(sks.getText().trim()), 30, 30, 40, semester.getValue(), tahun.getText().trim(), kelas.getText().trim());
            boolean success = selectedId == 0 ? mataKuliahDAO.insert(mk, currentUserId) : mataKuliahDAO.update(mk);
            if (success) {
                data.setAll(mataKuliahDAO.getByUser(currentUserId));
                handleBersih();
            } else {
                UiUtil.alert(Alert.AlertType.ERROR, "Mata kuliah gagal disimpan.");
            }
        } catch (NumberFormatException ex) {
            UiUtil.alert(Alert.AlertType.WARNING, "SKS harus berupa angka.");
        }
    }

    @FXML
    private void handleBuka() {
        MataKuliah selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            UiUtil.alert(Alert.AlertType.WARNING, "Pilih mata kuliah terlebih dahulu.");
            return;
        }
        AppSession.setActiveMataKuliah(selected);
        openWorkspace.run();
    }

    @FXML
    private void handleHapus() {
        if (selectedId == 0) {
            UiUtil.alert(Alert.AlertType.WARNING, "Pilih mata kuliah yang ingin dihapus terlebih dahulu.");
            return;
        }
        MataKuliah selected = table.getSelectionModel().getSelectedItem();
        String namaInfo = selected != null
            ? selected.getKodeMk() + " - " + selected.getNamaMk()
            : "mata kuliah ini";

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText("Hapus Mata Kuliah");
        konfirmasi.setContentText(
            "Anda yakin ingin menghapus:\n\n" + namaInfo + "?\n\n" +
            "Semua data mahasiswa, komponen nilai, dan nilai yang terkait\n" +
            "akan ikut terhapus secara permanen.");
        konfirmasi.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        konfirmasi.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                boolean success = mataKuliahDAO.delete(selectedId);
                if (success) {
                    data.setAll(mataKuliahDAO.getByUser(currentUserId));
                    handleBersih();
                    UiUtil.alert(Alert.AlertType.INFORMATION, "Mata kuliah " + namaInfo + " berhasil dihapus.");
                } else {
                    UiUtil.alert(Alert.AlertType.ERROR, "Gagal menghapus mata kuliah.");
                }
            }
        });
    }

    @FXML
    private void handleBersih() {
        table.getSelectionModel().clearSelection();
        selectedId = 0;
        kode.clear();
        nama.clear();
        sks.clear();
        tahun.clear();
        kelas.clear();
        semester.setValue("Gasal");
        tambah.setText("Tambah Mata Kuliah");
    }

    @FXML
    private void handleLogout() {
        onLogout.run();
    }
}
