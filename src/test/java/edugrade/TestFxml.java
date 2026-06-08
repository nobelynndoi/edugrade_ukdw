package edugrade;

import edugrade.controller.DashboardMataKuliahController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

public class TestFxml {
    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                System.out.println("Starting load...");
                DashboardMataKuliahController controller = new DashboardMataKuliahController(null, null);
                FXMLLoader loader = new FXMLLoader(TestFxml.class.getResource("/edugrade/dashboard_matakuliah.fxml"));
                loader.setController(controller);
                loader.load();
                System.out.println("Load successful!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.exit();
        });
    }
}
