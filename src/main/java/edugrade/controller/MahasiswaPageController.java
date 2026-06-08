package edugrade.controller;

import edugrade.app.AppSession;
import edugrade.dao.MahasiswaDAO;
import edugrade.model.Mahasiswa;
import edugrade.model.MataKuliah;
import edugrade.util.ExportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MahasiswaPageController {
    private final MahasiswaDAO mahasiswaDAO = new MahasiswaDAO();
    
    @FXML private Label tableTitle;
    @FXML private TableView<Mahasiswa> table;
    @FXML private TableColumn<Mahasiswa, Boolean> colPilih;
    @FXML private TableColumn<Mahasiswa, String> colNo;
    @FXML private TableColumn<Mahasiswa, String> colNim;
    @FXML private TableColumn<Mahasiswa, String> colNama;
    
    @FXML private TextField noUrut;
    @FXML private TextField nim;
    @FXML private TextField nama;
    @FXML private Button simpanBtn;
    
    private ObservableList<Mahasiswa> data;
    private MataKuliah mk;

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/mahasiswa_page.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            mk = AppSession.getActiveMataKuliah();
            data = FXCollections.observableArrayList(mahasiswaDAO.getByMatkul(mk.getIdMatkul()));
            
            tableTitle.setText("Data Mahasiswa - " + mk.getKodeMk() + " (" + mk.getNamaMk() + ")");

            colPilih.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
            colPilih.setCellFactory(CheckBoxTableCell.forTableColumn(colPilih));
            
            colNo.setCellValueFactory(new PropertyValueFactory<>("noUrut"));
            colNo.setCellFactory(col -> new TableCell<Mahasiswa, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        if (item != null && !item.trim().isEmpty()) {
                            setText(item);
                        } else {
                            setText(String.valueOf(getIndex() + 1));
                        }
                    }
                }
            });
            colNo.setComparator((s1, s2) -> {
                try {
                    if (s1 == null && s2 == null) return 0;
                    if (s1 == null) return -1;
                    if (s2 == null) return 1;
                    return Integer.compare(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()));
                } catch (NumberFormatException e) {
                    return s1.compareTo(s2);
                }
            });
            colNim.setCellValueFactory(new PropertyValueFactory<>("nim"));
            colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));

            table.setItems(data);

            table.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
                if (selected != null) {
                    noUrut.setText(selected.getNoUrut());
                    nim.setText(selected.getNim());
                    nim.setDisable(true);
                    nama.setText(selected.getNama());
                    simpanBtn.setText("Simpan");
                }
            });

            handleBersih();

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }

    @FXML
    private void handleSimpan() {
        if (UiUtil.isBlank(nim, nama)) {
            UiUtil.alert(Alert.AlertType.WARNING, "NIM dan nama wajib diisi.");
            return;
        }
        Mahasiswa mahasiswa = new Mahasiswa(nim.getText().trim(), nama.getText().trim(), "", "", noUrut.getText().trim());
        boolean success = nim.isDisable()
            ? mahasiswaDAO.updateForMatkul(mk.getIdMatkul(), mahasiswa)
            : mahasiswaDAO.insertForMatkul(mk.getIdMatkul(), mahasiswa);
        if (success) {
            resequenceAndRefresh(mk.getIdMatkul(), data, mahasiswaDAO);
            handleBersih();
        } else {
            UiUtil.alert(Alert.AlertType.ERROR, "Mahasiswa gagal disimpan.");
        }
    }

    @FXML
    private void handleHapus() {
        java.util.List<Mahasiswa> selectedList = data.stream().filter(Mahasiswa::isSelected).toList();
        if (selectedList.isEmpty()) {
            UiUtil.alert(Alert.AlertType.WARNING, "Pilih mahasiswa yang akan dihapus dengan mencentang kotak 'Pilih'.");
            return;
        }
        boolean allSuccess = true;
        for (Mahasiswa mhs : selectedList) {
            if (!mahasiswaDAO.removeFromMatkul(mk.getIdMatkul(), mhs.getNim())) {
                allSuccess = false;
            }
        }
        if (allSuccess) {
            UiUtil.alert(Alert.AlertType.INFORMATION, "Berhasil menghapus " + selectedList.size() + " mahasiswa terpilih.");
            resequenceAndRefresh(mk.getIdMatkul(), data, mahasiswaDAO);
        } else {
            UiUtil.alert(Alert.AlertType.ERROR, "Beberapa mahasiswa gagal dihapus dari mata kuliah.");
            data.setAll(mahasiswaDAO.getByMatkul(mk.getIdMatkul()));
        }
        handleBersih();
    }

    @FXML
    private void handleBersih() {
        if (table != null) table.getSelectionModel().clearSelection();
        if (noUrut != null) noUrut.setText(String.valueOf(table.getItems().size() + 1));
        if (nim != null) {
            nim.setDisable(false);
            nim.clear();
        }
        if (nama != null) nama.clear();
        if (simpanBtn != null) simpanBtn.setText("Tambah Mahasiswa");
    }

    @FXML
    private void handleImportCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Data Mahasiswa dari CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showOpenDialog(table.getScene().getWindow());
        if (file != null) {
            int success = 0, failed = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNum = 0;
                while ((line = br.readLine()) != null) {
                    lineNum++;
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    if (lineNum == 1 && (line.toLowerCase().contains("nim") || line.toLowerCase().contains("nama"))) {
                        continue;
                    }
                    String[] parts = line.split("[,;\\t]");
                    if (parts.length < 2) {
                        failed++;
                        continue;
                    }
                    
                    String csvNoUrut = "";
                    String csvNim = "";
                    String csvNama = "";
                    
                    if (parts.length >= 3 && parts[1].trim().matches("\\d+")) {
                        csvNoUrut = parts[0].trim();
                        csvNim = parts[1].trim();
                        csvNama = parts[2].trim();
                    } else {
                        csvNim = parts[0].trim();
                        csvNama = parts[1].trim();
                    }
                    
                    if (csvNim.isEmpty() || csvNama.isEmpty()) {
                        failed++;
                        continue;
                    }
                    Mahasiswa mhs = new Mahasiswa(csvNim, csvNama, "", "", csvNoUrut);
                    if (mahasiswaDAO.insertForMatkul(mk.getIdMatkul(), mhs)) {
                        success++;
                    } else {
                        failed++;
                    }
                }
            } catch (Exception ex) {
                UiUtil.alert(Alert.AlertType.ERROR, "Error membaca file CSV: " + ex.getMessage());
                return;
            }
            resequenceAndRefresh(mk.getIdMatkul(), data, mahasiswaDAO);
            UiUtil.alert(Alert.AlertType.INFORMATION,
                "Import selesai!\nBerhasil: " + success + " mahasiswa\nGagal/duplikat: " + failed);
        }
    }

    @FXML
    private void handlePaste() {
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        if (!clipboard.hasString()) {
            UiUtil.alert(Alert.AlertType.WARNING, "Clipboard kosong. Silakan copy data dari Excel/Eclass terlebih dahulu.");
            return;
        }
        String cbText = clipboard.getString();
        if (cbText == null || cbText.trim().isEmpty()) return;

        String[] lines = cbText.split("\\r?\\n");
        int success = 0;
        int failed = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            if (i == 0 && (line.toLowerCase().contains("nim") || line.toLowerCase().contains("nama"))) {
                continue; 
            }
            String[] parts = line.split("\\t");
            if (parts.length < 2) {
                failed++;
                continue;
            }
            String csvNoUrut = "";
            String csvNim = "";
            String csvNama = "";
            
            if (parts.length >= 3 && parts[1].trim().matches("\\d+")) {
                csvNoUrut = parts[0].trim();
                csvNim = parts[1].trim();
                csvNama = parts[2].trim();
            } else {
                csvNim = parts[0].trim();
                csvNama = parts[1].trim();
            }
            
            if (csvNim.isEmpty() || csvNama.isEmpty()) {
                failed++;
                continue;
            }
            Mahasiswa mhs = new Mahasiswa(csvNim, csvNama, "", "", csvNoUrut);
            if (mahasiswaDAO.insertForMatkul(mk.getIdMatkul(), mhs)) {
                success++;
            } else {
                failed++;
            }
        }
        resequenceAndRefresh(mk.getIdMatkul(), data, mahasiswaDAO);
        UiUtil.alert(Alert.AlertType.INFORMATION, "Paste selesai!\nBerhasil ditambahkan: " + success + "\nGagal/Duplikat: " + failed);
    }

    @FXML
    private void handleTemplateCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Simpan Template CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fc.setInitialFileName("Template_Mahasiswa.csv");
        File file = fc.showSaveDialog(table.getScene().getWindow());
        if (file != null) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                writer.println("No,NIM,Nama Mahasiswa");
                writer.println("1,71241001,Leonardo Tanuwijaya");
                writer.println("2,71241002,Doni Simanjuntak");
                UiUtil.alert(Alert.AlertType.INFORMATION, "Template CSV berhasil disimpan ke:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                UiUtil.alert(Alert.AlertType.ERROR, "Gagal menyimpan template CSV: " + ex.getMessage());
            }
        }
    }

    private void resequenceAndRefresh(int idMatkul, ObservableList<Mahasiswa> data, MahasiswaDAO dao) {
        java.util.List<Mahasiswa> currentList = dao.getByMatkul(idMatkul);
        currentList.sort((a, b) -> {
            int noA = Integer.MAX_VALUE;
            int noB = Integer.MAX_VALUE;
            try { noA = Integer.parseInt(a.getNoUrut()); } catch(Exception ignored) {}
            try { noB = Integer.parseInt(b.getNoUrut()); } catch(Exception ignored) {}
            if (noA == noB) return a.getNim().compareTo(b.getNim());
            return Integer.compare(noA, noB);
        });

        for (int i = 0; i < currentList.size(); i++) {
            Mahasiswa m = currentList.get(i);
            String newNo = String.valueOf(i + 1);
            if (!newNo.equals(m.getNoUrut())) {
                m.setNoUrut(newNo);
                dao.updateForMatkul(idMatkul, m);
            }
        }
        data.setAll(dao.getByMatkul(idMatkul));
    }
}
