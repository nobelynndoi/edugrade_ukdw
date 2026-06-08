package edugrade;

import edugrade.util.DatabaseUtil;
import java.sql.*;

public class CheckDb {
    public static void main(String[] args) throws Exception {
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT no_urut, nim, nama FROM peserta_matkul LIMIT 5")) {
            System.out.println("peserta_matkul data:");
            while (rs.next()) {
                System.out.println("no_urut: '" + rs.getString("no_urut") + "', nim: '" + rs.getString("nim") + "'");
            }
        }
    }
}
