package edugrade.app;

import edugrade.controller.EduGradeAppController;
import edugrade.util.DatabaseUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseUtil.initializeDatabase();

        EduGradeAppController controller = new EduGradeAppController();
        Scene scene = new Scene(controller.createView(), 1180, 720);
        String stylesheet = MainApp.class.getResource("/edugrade/style.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        stage.setTitle("EduGrade - Manajemen Nilai Akademik");
        stage.setMinWidth(1060);
        stage.setMinHeight(640);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        DatabaseUtil.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
