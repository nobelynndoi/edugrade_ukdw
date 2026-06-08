package edugrade.controller;

import edugrade.app.AppSession;
import edugrade.model.MataKuliah;
import edugrade.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class CourseWorkspaceController {
    private final Runnable backToDashboard;
    private final Runnable onLogout;
    
    @FXML private StackPane content;
    @FXML private Label subtitle;
    @FXML private Label userName;
    @FXML private Button mahasiswaBtn;
    @FXML private Button nilaiBtn;
    @FXML private Button laporanBtn;
    
    private Button activeButton;

    public CourseWorkspaceController(Runnable backToDashboard, Runnable onLogout) {
        this.backToDashboard = backToDashboard;
        this.onLogout = onLogout;
    }

    public Parent createView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/edugrade/course_workspace.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            MataKuliah mk = AppSession.getActiveMataKuliah();
            subtitle.setText(mk == null ? "Mata Kuliah" : mk.getKodeMk() + " - " + mk.getNamaMk());

            User currentUser = AppSession.getCurrentUser();
            if (currentUser != null) {
                userName.setText(currentUser.getName());
                userName.setVisible(true);
                userName.setManaged(true);
            } else {
                userName.setVisible(false);
                userName.setManaged(false);
            }

            activeButton = mahasiswaBtn;
            showMahasiswa();
            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return new BorderPane();
        }
    }

    @FXML
    private void handleMahasiswa() {
        setActive(mahasiswaBtn);
        showMahasiswa();
    }

    @FXML
    private void handleNilai() {
        setActive(nilaiBtn);
        showNilai();
    }

    @FXML
    private void handleLaporan() {
        setActive(laporanBtn);
        showLaporan();
    }

    @FXML
    private void handleKembali() {
        backToDashboard.run();
    }

    @FXML
    private void handleLogout() {
        onLogout.run();
    }

    private void setActive(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-btn-active");
        }
        activeButton = button;
        if (!button.getStyleClass().contains("nav-btn-active")) {
            button.getStyleClass().add("nav-btn-active");
        }
    }

    private void showMahasiswa() {
        content.getChildren().setAll(new MahasiswaPageController().createView());
    }

    private void showNilai() {
        content.getChildren().setAll(new NilaiPageController().createView());
    }

    private void showLaporan() {
        content.getChildren().setAll(new LaporanPageController().createView());
    }
}
