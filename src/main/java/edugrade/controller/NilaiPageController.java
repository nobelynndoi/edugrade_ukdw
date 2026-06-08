package edugrade.controller;

import edugrade.app.AppSession;
import edugrade.dao.KomponenNilaiDAO;
import edugrade.dao.NilaiMahasiswaDAO;
import edugrade.model.KomponenNilai;
import edugrade.model.MataKuliah;
import edugrade.model.NilaiRekap;
import edugrade.util.ExportUtil;
import edugrade.util.ImportUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NilaiPageController {
    private final KomponenNilaiDAO komponenNilaiDAO = new KomponenNilaiDAO();
    private final NilaiMahasiswaDAO nilaiMahasiswaDAO = new NilaiMahasiswaDAO();
    
    @FXML private Label tableTitle;
    @FXML private TableView<NilaiRekap> table;
    @FXML private Button aturKomponenBtn;
    @FXML private Button importBtn;
    @FXML private Button templateBtn;
    @FXML private Button pasteBtn;
    @FXML private Button simpanBtn;
    @FXML private Button exportBtn;
    
    private ObservableList<NilaiRekap> data;
    private MataKuliah mk;

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/nilai_page.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            mk = AppSession.getActiveMataKuliah();
            data = FXCollections.observableArrayList();
            table.setItems(data);

            tableTitle.setText("Input Nilai Dinamis - " + mk.getKodeMk() + " (" + mk.getNamaMk() + ")");

            table.setOnKeyPressed(event -> {
                if (event.isControlDown() && event.getCode() == KeyCode.V) {
                    handlePaste();
                    event.consume();
                }
            });

            loadData();

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }
    
    private void loadData() {
        List<KomponenNilai> komponen = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
        rebuildTable(table, komponen);
        data.setAll(nilaiMahasiswaDAO.getRekapNilai(mk.getIdMatkul(), komponen, "Semua", ""));
    }

    @FXML
    private void handleAturKomponen() {
        if (showKomponenDialog(mk)) {
            loadData();
        }
    }

    @FXML
    private void handlePaste() {
        String text = Clipboard.getSystemClipboard().getString();
        if (text == null || text.trim().isEmpty()) {
            UiUtil.alert(Alert.AlertType.WARNING, "Clipboard kosong. Salin data dari Excel terlebih dahulu.");
            return;
        }
        try {
            List<List<String>> importedData = ImportUtil.parseClipboardText(text);
            List<KomponenNilai> komponenList = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
            ImportUtil.ImportResult result = ImportUtil.processImportedData(importedData, new ArrayList<>(data), komponenList, nilaiMahasiswaDAO);
            if (result.successCount > 0) {
                nilaiMahasiswaDAO.saveWorksheet(mk.getIdMatkul(), komponenList, new ArrayList<>(data));
                table.refresh();
            }
            
            if (result.errors.isEmpty()) {
                if (result.successCount > 0) {
                    UiUtil.alert(Alert.AlertType.INFORMATION, "Berhasil menempel dan menyimpan nilai untuk " + result.successCount + " mahasiswa.");
                } else {
                    UiUtil.alert(Alert.AlertType.WARNING, "Tidak ada data yang berhasil ditempel. Pastikan data yang disalin memiliki format yang sesuai.");
                }
            } else {
                StringBuilder errorMsg = new StringBuilder();
                if (result.successCount > 0) {
                    errorMsg.append("Berhasil menyimpan nilai untuk ").append(result.successCount).append(" mahasiswa, namun terdapat beberapa error:\n\n");
                } else {
                    errorMsg.append("Gagal menempel data karena error berikut:\n\n");
                }
                for (int i = 0; i < Math.min(result.errors.size(), 10); i++) {
                    errorMsg.append("• ").append(result.errors.get(i)).append("\n");
                }
                if (result.errors.size() > 10) {
                    errorMsg.append("... dan ").append(result.errors.size() - 10).append(" error lainnya.");
                }
                UiUtil.alert(Alert.AlertType.WARNING, errorMsg.toString());
            }
        } catch (Exception ex) {
            UiUtil.alert(Alert.AlertType.ERROR, "Gagal menempel data: " + ex.getMessage());
        }
    }

    @FXML
    private void handleTemplate() {
        List<KomponenNilai> komponenList = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
        if (komponenList.isEmpty()) {
            UiUtil.alert(Alert.AlertType.WARNING,
                "Belum ada komponen nilai yang didefinisikan untuk mata kuliah ini.\n" +
                "Silakan tambahkan komponen nilai terlebih dahulu sebelum mengunduh template.");
            return;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle("Simpan Template CSV");
        fc.setInitialFileName("Template_Nilai_" + mk.getKodeMk() + ".csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fc.showSaveDialog(templateBtn.getScene().getWindow());
        if (file != null) {
            try {
                ExportUtil.exportCsvTemplate(file, mk, komponenList, new ArrayList<>(data));
                UiUtil.alert(Alert.AlertType.INFORMATION,
                    "Template CSV berhasil diunduh ke:\n" + file.getAbsolutePath() +
                    "\n\nHeader kolom telah disesuaikan dengan komponen nilai:\n" +
                    buildKomponenSummary(komponenList));
            } catch (Exception ex) {
                UiUtil.alert(Alert.AlertType.ERROR, "Gagal membuat template CSV: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleImport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Import Nilai dari Excel / CSV");
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Semua File yang Didukung (*.xlsx, *.csv)", "*.xlsx", "*.csv"),
            new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"),
            new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv")
        );
        File file = fc.showOpenDialog(importBtn.getScene().getWindow());
        if (file != null) {
            try {
                List<List<String>> importedData;
                List<KomponenNilai> komponenList = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
                if (file.getName().toLowerCase().endsWith(".xlsx")) {
                    importedData = ImportUtil.readExcel(file);
                } else {
                    List<String> missingKomponen = ImportUtil.validateCsvStructure(file, komponenList);
                    if (!missingKomponen.isEmpty()) {
                        StringBuilder sb = new StringBuilder(
                            "Struktur file CSV tidak sesuai dengan template yang diunduh.\n\n" +
                            "Komponen nilai berikut TIDAK ditemukan di header CSV:\n");
                        for (String nama : missingKomponen) sb.append("  \u2022 ").append(nama).append("\n");
                        sb.append("\nSilakan unduh ulang template CSV dan pastikan nama kolom tidak diubah.");
                        UiUtil.alert(Alert.AlertType.ERROR, sb.toString());
                        return;
                    }
                    importedData = ImportUtil.readCsv(file);
                }
                ImportUtil.ImportResult result = ImportUtil.processImportedData(importedData, new ArrayList<>(data), komponenList, nilaiMahasiswaDAO);
                if (result.successCount > 0) {
                    nilaiMahasiswaDAO.saveWorksheet(mk.getIdMatkul(), komponenList, new ArrayList<>(data));
                    table.refresh();
                }

                if (result.errors.isEmpty()) {
                    if (result.successCount > 0) {
                        UiUtil.alert(Alert.AlertType.INFORMATION, "Berhasil memuat dan menyimpan nilai untuk " + result.successCount + " mahasiswa.");
                    } else {
                        UiUtil.alert(Alert.AlertType.WARNING, "Tidak ada data nilai yang valid untuk disimpan.");
                    }
                } else {
                    StringBuilder errorMsg = new StringBuilder();
                    if (result.successCount > 0) {
                        errorMsg.append("Berhasil menyimpan nilai untuk ").append(result.successCount).append(" mahasiswa, namun terdapat beberapa error:\n\n");
                    } else {
                        errorMsg.append("Gagal memuat data karena error berikut:\n\n");
                    }
                    for (int i = 0; i < Math.min(result.errors.size(), 10); i++) {
                        errorMsg.append("• ").append(result.errors.get(i)).append("\n");
                    }
                    if (result.errors.size() > 10) {
                        errorMsg.append("... dan ").append(result.errors.size() - 10).append(" error lainnya.");
                    }
                    UiUtil.alert(Alert.AlertType.WARNING, errorMsg.toString());
                }
            } catch (Exception ex) {
                String errMsg = ex.getMessage();
                if (errMsg != null && errMsg.contains("being used by another process")) {
                    UiUtil.alert(Alert.AlertType.ERROR, "Gagal import file.\n\nFile Excel sedang dibuka di program lain (misalnya Microsoft Excel). Harap tutup file tersebut di Excel terlebih dahulu, lalu coba import lagi.");
                } else {
                    UiUtil.alert(Alert.AlertType.ERROR, "Gagal import file: " + errMsg);
                }
            }
        }
    }

    @FXML
    private void handleSimpan() {
        List<KomponenNilai> komponen = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
        if (nilaiMahasiswaDAO.saveWorksheet(mk.getIdMatkul(), komponen, new ArrayList<>(data))) {
            UiUtil.alert(Alert.AlertType.INFORMATION, "Perubahan nilai berhasil disimpan.");
            loadData();
        } else {
            UiUtil.alert(Alert.AlertType.ERROR, "Perubahan nilai gagal disimpan.");
        }
    }

    @FXML
    private void handleExport() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Nilai ke Excel");
        fc.setInitialFileName("Nilai_" + mk.getKodeMk() + ".xlsx");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fc.showSaveDialog(exportBtn.getScene().getWindow());
        if (file != null) {
            try {
                List<KomponenNilai> komponen = komponenNilaiDAO.getByMatkul(mk.getIdMatkul());
                ExportUtil.exportNilaiToExcel(file, mk, komponen, new ArrayList<>(data));
                UiUtil.alert(Alert.AlertType.INFORMATION, "Data nilai berhasil di-export ke:\n" + file.getAbsolutePath());
            } catch (Exception ex) {
                UiUtil.alert(Alert.AlertType.ERROR, "Gagal export: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /** Membuat ringkasan teks komponen nilai untuk ditampilkan di dialog konfirmasi template. */
    private String buildKomponenSummary(List<KomponenNilai> komponenList) {
        StringBuilder sb = new StringBuilder();
        for (KomponenNilai k : komponenList) {
            sb.append("  \u2022 ").append(k.getNamaKomponen())
              .append(" (").append(UiUtil.DECIMAL.format(k.getBobotPersentase())).append("%")
              .append(k.isBonus() ? ", Bonus" : "").append(")\n");
        }
        return sb.toString();
    }

    private void rebuildTable(TableView<NilaiRekap> table, List<KomponenNilai> komponenList) {
        table.getColumns().clear();

        TableColumn<NilaiRekap, String> noCol = column("No", "noUrut");
        noCol.setMaxWidth(50);
        noCol.setMinWidth(50);
        noCol.setCellFactory(col -> new TableCell<NilaiRekap, String>() {
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
        noCol.setComparator((s1, s2) -> {
            try {
                if (s1 == null && s2 == null) return 0;
                if (s1 == null) return -1;
                if (s2 == null) return 1;
                return Integer.compare(Integer.parseInt(s1.trim()), Integer.parseInt(s2.trim()));
            } catch (NumberFormatException e) {
                return s1.compareTo(s2);
            }
        });
        table.getColumns().add(noCol);

        TableColumn<NilaiRekap, String> nimCol = column("NIM", "nim");
        nimCol.setMinWidth(120);
        nimCol.setMaxWidth(160);
        table.getColumns().add(nimCol);

        TableColumn<NilaiRekap, String> namaCol = column("Nama", "namaMahasiswa");
        namaCol.setMinWidth(160);
        namaCol.setMaxWidth(280);
        table.getColumns().add(namaCol);

        for (KomponenNilai komponen : komponenList) {
            String header = komponen.getNamaKomponen() + " (" + UiUtil.DECIMAL.format(komponen.getBobotPersentase()) + "%)";
            if (komponen.isBonus()) header += " ★";
            TableColumn<NilaiRekap, String> scoreColumn = new TableColumn<>(header);
            scoreColumn.setMinWidth(90);
            scoreColumn.setMaxWidth(150);
            scoreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(UiUtil.DECIMAL.format(cellData.getValue().getSkor(komponen.getIdKomponen())))
            );
            scoreColumn.setCellFactory(col -> new NumericEditingCell());
            scoreColumn.setOnEditCommit(event -> {
                try {
                    NilaiRekap row = event.getRowValue();
                    row.setSkor(komponen.getIdKomponen(), UiUtil.parseScoreText(event.getNewValue()));
                    nilaiMahasiswaDAO.refreshTotal(row, komponenList);
                    table.refresh();
                } catch (NumberFormatException ex) {
                    UiUtil.alert(Alert.AlertType.WARNING, "Nilai harus berupa angka 0 sampai 100.");
                    table.refresh();
                }
            });
            table.getColumns().add(scoreColumn);
        }

        TableColumn<NilaiRekap, String> akhir = new TableColumn<>("Total Nilai Akhir");
        akhir.setMinWidth(120);
        akhir.setCellValueFactory(cellData ->
            new SimpleStringProperty(UiUtil.DECIMAL.format(cellData.getValue().getNilaiAkhir()))
        );
        table.getColumns().add(akhir);

        TableColumn<NilaiRekap, String> gradeCol = column("Grade Huruf", "gradeHuruf");
        gradeCol.setMinWidth(90);
        gradeCol.setMaxWidth(110);
        table.getColumns().add(gradeCol);
    }

    private boolean showKomponenDialog(MataKuliah mk) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Komponen Nilai");
        dialog.setHeaderText(mk.getKodeMk() + " - " + mk.getNamaMk());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox rowsBox = new VBox(10);
        List<ComponentInputRow> rows = new ArrayList<>();
        for (KomponenNilai komponen : komponenNilaiDAO.getByMatkul(mk.getIdMatkul())) {
            addRow(rowsBox, rows, komponen.getIdKomponen(), komponen.getNamaKomponen(), komponen.getBobotPersentase(), komponen.isBonus());
        }
        if (rows.isEmpty()) {
            addRow(rowsBox, rows, 0, "Tugas 1", 30, false);
            addRow(rowsBox, rows, 0, "UTS", 30, false);
            addRow(rowsBox, rows, 0, "UAS", 40, false);
        }

        Label totalLabel = new Label();
        totalLabel.getStyleClass().add("section-title");
        Runnable refreshTotal = () -> {
            double normal = totalBobotNormal(rows);
            double bonus = totalBobotBonus(rows);
            String text = "Total bobot normal: " + UiUtil.DECIMAL.format(normal) + "%";
            if (bonus > 0) text += "  |  Bonus: " + UiUtil.DECIMAL.format(bonus) + "%";
            totalLabel.setText(text);
        };

        Button tambah = UiUtil.secondaryButton("Tambah Komponen");
        tambah.setOnAction(event -> {
            addRow(rowsBox, rows, 0, "", 0, false);
            refreshTotal.run();
        });

        Button tambahBonus = UiUtil.secondaryButton("Tambah Bonus");
        tambahBonus.setStyle("-fx-background-color: #FFF8E1; -fx-text-fill: #F57C00; -fx-font-size: 12px; -fx-padding: 7 14 7 14; -fx-background-radius: 5; -fx-cursor: hand; -fx-border-color: #FFD54F; -fx-border-radius: 5;");
        tambahBonus.setOnAction(event -> {
            addRow(rowsBox, rows, 0, "", 0, true);
            refreshTotal.run();
        });

        ScrollPane scroll = new ScrollPane(rowsBox);
        scroll.setFitToWidth(true);
        scroll.setPrefViewportHeight(320);
        HBox buttons = new HBox(10, tambah, tambahBonus);
        VBox box = new VBox(12, scroll, buttons, totalLabel);
        box.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(box);
        refreshTotal.run();

        Button ok = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        ok.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                double totalNormal = totalBobotNormal(rows);
                if (Math.abs(totalNormal - 100) > 0.001) {
                    UiUtil.alert(Alert.AlertType.WARNING, "Total bobot normal (tanpa bonus) wajib tepat 100%.\nSaat ini: " + UiUtil.DECIMAL.format(totalNormal) + "%");
                    event.consume();
                    return;
                }
                komponenNilaiDAO.replaceForMatkul(mk.getIdMatkul(), buildComponents(mk.getIdMatkul(), rows));
            } catch (NumberFormatException ex) {
                UiUtil.alert(Alert.AlertType.WARNING, "Bobot harus berupa angka positif.");
                event.consume();
            } catch (IllegalArgumentException ex) {
                UiUtil.alert(Alert.AlertType.WARNING, ex.getMessage());
                event.consume();
            } catch (SQLException ex) {
                UiUtil.alert(Alert.AlertType.ERROR, "Komponen nilai gagal disimpan.");
                event.consume();
            }
        });

        return dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void addRow(VBox rowsBox, List<ComponentInputRow> rows, int id, String nama, double bobot, boolean isBonus) {
        TextField namaField = UiUtil.input("Nama komponen");
        namaField.setText(nama);
        TextField bobotField = UiUtil.input("Bobot %");
        bobotField.setText(bobot == 0 ? "" : UiUtil.DECIMAL.format(bobot));
        bobotField.setTextFormatter(UiUtil.numericTextFormatter());
        CheckBox bonusCheck = new CheckBox("Bonus");
        bonusCheck.setSelected(isBonus);
        if (isBonus) {
            namaField.setStyle("-fx-background-color: #FFF8E1; -fx-border-color: #FFD54F; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 12 8 12; -fx-font-size: 13px;");
        }
        bonusCheck.selectedProperty().addListener((obs, old, val) -> {
            if (val) {
                namaField.setStyle("-fx-background-color: #FFF8E1; -fx-border-color: #FFD54F; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 8 12 8 12; -fx-font-size: 13px;");
            } else {
                namaField.setStyle("");
                namaField.getStyleClass().add("text-field-style");
            }
        });
        Button hapus = UiUtil.dangerButton("Hapus");
        HBox rowBox = new HBox(10, namaField, bobotField, bonusCheck, hapus);
        rowBox.setAlignment(Pos.CENTER_LEFT);
        ComponentInputRow row = new ComponentInputRow(id, namaField, bobotField, bonusCheck);
        rows.add(row);
        rowsBox.getChildren().add(rowBox);
        hapus.setOnAction(event -> {
            rows.remove(row);
            rowsBox.getChildren().remove(rowBox);
        });
    }

    private List<KomponenNilai> buildComponents(int idMatkul, List<ComponentInputRow> rows) {
        List<KomponenNilai> result = new ArrayList<>();
        for (ComponentInputRow row : rows) {
            String nama = row.nama().getText().trim();
            if (nama.isEmpty()) {
                throw new IllegalArgumentException("Nama komponen tidak boleh kosong.");
            }
            double bobot = UiUtil.parseDouble(row.bobot());
            if (bobot <= 0) {
                throw new NumberFormatException("bobot");
            }
            result.add(new KomponenNilai(row.idKomponen(), idMatkul, nama, bobot, row.bonusCheck().isSelected()));
        }
        return result;
    }

    private double totalBobotNormal(List<ComponentInputRow> rows) {
        double total = 0;
        for (ComponentInputRow row : rows) {
            if (!row.bonusCheck().isSelected() && !row.bobot().getText().isBlank()) {
                total += Double.parseDouble(row.bobot().getText().replace(",", "."));
            }
        }
        return total;
    }

    private double totalBobotBonus(List<ComponentInputRow> rows) {
        double total = 0;
        for (ComponentInputRow row : rows) {
            if (row.bonusCheck().isSelected() && !row.bobot().getText().isBlank()) {
                total += Double.parseDouble(row.bobot().getText().replace(",", "."));
            }
        }
        return total;
    }

    private <S, T> TableColumn<S, T> column(String title, String property) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    private record ComponentInputRow(int idKomponen, TextField nama, TextField bobot, CheckBox bonusCheck) {
    }

    private class NumericEditingCell extends TableCell<NilaiRekap, String> {
        private TextField textField;

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                textField = new TextField(getItem());
                textField.setTextFormatter(UiUtil.numericTextFormatter());
                textField.setOnAction(event -> commitEdit(textField.getText()));
                textField.focusedProperty().addListener((obs, old, focused) -> {
                    if (!focused) {
                        commitEdit(textField.getText());
                    }
                });
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();
            setText(getItem());
            setGraphic(null);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else if (isEditing()) {
                setText(null);
                setGraphic(textField);
            } else {
                setText(item);
                setGraphic(null);
            }
        }
    }
}
