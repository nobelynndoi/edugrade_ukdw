package edugrade.dao;

import edugrade.model.KomponenNilai;
import edugrade.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class KomponenNilaiDAO {

    public List<KomponenNilai> getByMatkul(int idMatkul) {
        List<KomponenNilai> list = new ArrayList<>();
        String sql = """
            SELECT id_komponen, id_matkul, nama_komponen, bobot_persentase, COALESCE(is_bonus, 0) AS is_bonus
            FROM komponen_nilai
            WHERE id_matkul = ?
            ORDER BY is_bonus ASC, id_komponen ASC
        """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMatkul);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new KomponenNilai(
                    rs.getInt("id_komponen"),
                    rs.getInt("id_matkul"),
                    rs.getString("nama_komponen"),
                    rs.getDouble("bobot_persentase"),
                    rs.getInt("is_bonus") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void replaceForMatkul(int idMatkul, List<KomponenNilai> komponenList) throws SQLException {
        try (Connection c = DatabaseUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement update = c.prepareStatement("""
                    UPDATE komponen_nilai
                    SET nama_komponen = ?, bobot_persentase = ?, is_bonus = ?
                    WHERE id_komponen = ? AND id_matkul = ?
                 """);
                 PreparedStatement insert = c.prepareStatement("""
                    INSERT INTO komponen_nilai (id_matkul, nama_komponen, bobot_persentase, is_bonus)
                    VALUES (?, ?, ?, ?)
                 """, Statement.RETURN_GENERATED_KEYS)) {
                List<Integer> retainedIds = new ArrayList<>();
                for (KomponenNilai komponen : komponenList) {
                    if (komponen.getIdKomponen() > 0) {
                        update.setString(1, komponen.getNamaKomponen());
                        update.setDouble(2, komponen.getBobotPersentase());
                        update.setInt(3, komponen.isBonus() ? 1 : 0);
                        update.setInt(4, komponen.getIdKomponen());
                        update.setInt(5, idMatkul);
                        update.executeUpdate();
                        retainedIds.add(komponen.getIdKomponen());
                    } else {
                        insert.setInt(1, idMatkul);
                        insert.setString(2, komponen.getNamaKomponen());
                        insert.setDouble(3, komponen.getBobotPersentase());
                        insert.setInt(4, komponen.isBonus() ? 1 : 0);
                        insert.executeUpdate();
                        try (ResultSet keys = insert.getGeneratedKeys()) {
                            if (keys.next()) {
                                retainedIds.add(keys.getInt(1));
                            }
                        }
                    }
                }
                deleteRemovedComponents(c, idMatkul, retainedIds);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private void deleteRemovedComponents(Connection c, int idMatkul, List<Integer> retainedIds) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM komponen_nilai WHERE id_matkul = ?");
        if (!retainedIds.isEmpty()) {
            StringJoiner placeholders = new StringJoiner(",", " AND id_komponen NOT IN (", ")");
            for (int ignored : retainedIds) {
                placeholders.add("?");
            }
            sql.append(placeholders);
        }
        try (PreparedStatement ps = c.prepareStatement(sql.toString())) {
            ps.setInt(1, idMatkul);
            for (int i = 0; i < retainedIds.size(); i++) {
                ps.setInt(i + 2, retainedIds.get(i));
            }
            ps.executeUpdate();
        }
    }

    public boolean hasComponents(int idMatkul) {
        String sql = "SELECT COUNT(*) FROM komponen_nilai WHERE id_matkul = ?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMatkul);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
