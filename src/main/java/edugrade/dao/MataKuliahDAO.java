package edugrade.dao;

import edugrade.model.MataKuliah;
import edugrade.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MataKuliahDAO {

    public List<MataKuliah> getAll() {
        List<MataKuliah> list = new ArrayList<>();
        String sql = "SELECT * FROM mata_kuliah ORDER BY tahun_ajaran DESC, semester, nama_mk";
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<MataKuliah> getByUser(int idUser) {
        List<MataKuliah> list = new ArrayList<>();
        String sql = "SELECT * FROM mata_kuliah WHERE id_user = ? ORDER BY tahun_ajaran DESC, semester, nama_mk";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public MataKuliah getById(int id) {
        String sql = "SELECT * FROM mata_kuliah WHERE id_matkul=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(MataKuliah mk, int idUser) {
        String sql = "INSERT INTO mata_kuliah (kode_mk, nama_mk, sks, semester, tahun_ajaran, kelas, id_user) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mk.getKodeMk());
            ps.setString(2, mk.getNamaMk());
            ps.setInt(3, mk.getSks());
            ps.setString(4, mk.getSemester());
            ps.setString(5, mk.getTahunAjaran());
            ps.setString(6, mk.getKelas());
            ps.setInt(7, idUser);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(MataKuliah mk) {
        String sql = "UPDATE mata_kuliah SET kode_mk=?, nama_mk=?, sks=?, semester=?, tahun_ajaran=?, kelas=? WHERE id_matkul=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, mk.getKodeMk());
            ps.setString(2, mk.getNamaMk());
            ps.setInt(3, mk.getSks());
            ps.setString(4, mk.getSemester());
            ps.setString(5, mk.getTahunAjaran());
            ps.setString(6, mk.getKelas());
            ps.setInt(7, mk.getIdMatkul());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        try (Connection c = DatabaseUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement deleteNilaiDinamis = c.prepareStatement("""
                    DELETE FROM nilai_mahasiswa
                    WHERE id_komponen IN (SELECT id_komponen FROM komponen_nilai WHERE id_matkul=?)
                 """);
                 PreparedStatement deleteKomponen = c.prepareStatement("DELETE FROM komponen_nilai WHERE id_matkul=?");
                 PreparedStatement deletePeserta = c.prepareStatement("DELETE FROM peserta_matkul WHERE id_matkul=?");
                 PreparedStatement deleteMatkul = c.prepareStatement("DELETE FROM mata_kuliah WHERE id_matkul=?")) {
                deleteNilaiDinamis.setInt(1, id);
                deleteNilaiDinamis.executeUpdate();
                deleteKomponen.setInt(1, id);
                deleteKomponen.executeUpdate();
                deletePeserta.setInt(1, id);
                deletePeserta.executeUpdate();
                deleteMatkul.setInt(1, id);
                deleteMatkul.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private MataKuliah mapRow(ResultSet rs) throws SQLException {
        String kelas = "";
        try { kelas = rs.getString("kelas"); } catch (SQLException e) {}
        if (kelas == null) kelas = "";
        return new MataKuliah(
            rs.getInt("id_matkul"),
            rs.getString("kode_mk"),
            rs.getString("nama_mk"),
            rs.getInt("sks"),
            30,
            30,
            40,
            rs.getString("semester"),
            rs.getString("tahun_ajaran"),
            kelas
        );
    }
}
