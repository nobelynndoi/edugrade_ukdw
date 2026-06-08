package edugrade.controller;

import edugrade.app.AppSession;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class EduGradeAppController {
    private final StackPane root = new StackPane();

    public Parent createView() {
        showLogin();
        return root;
    }

    private void showLogin() {
        LoginController login = new LoginController(this::showDashboard, this::showRegister);
        root.getChildren().setAll(login.createView());
    }

    private void showRegister() {
        RegisterController register = new RegisterController(this::showLogin, this::showLogin);
        root.getChildren().setAll(register.createView());
    }

    void showDashboard() {
        AppSession.clearActiveMataKuliah();
        DashboardMataKuliahController dashboard = new DashboardMataKuliahController(this::showWorkspace, this::logout);
        root.getChildren().setAll(dashboard.createView());
    }

    private void showWorkspace() {
        CourseWorkspaceController workspace = new CourseWorkspaceController(this::showDashboard, this::logout);
        root.getChildren().setAll(workspace.createView());
    }

    private void logout() {
        AppSession.setCurrentUser(null);
        AppSession.clearActiveMataKuliah();
        showLogin();
    }
}
