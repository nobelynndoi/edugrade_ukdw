package edugrade.dao;

import edugrade.model.Mahasiswa;
import edugrade.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MahasiswaDAO {

    public List<Mahasiswa> getAll() {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa ORDER BY nim";
        try (Connection c = DatabaseUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Mahasiswa(
                    rs.getString("nim"),
                    rs.getString("nama"),
                    "",
                    rs.getString("kelas") != null ? rs.getString("kelas") : ""
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Mahasiswa> search(String keyword) {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = "SELECT * FROM mahasiswa WHERE nim LIKE ? OR nama LIKE ? ORDER BY nim";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Mahasiswa(
                    rs.getString("nim"),
                    rs.getString("nama"),
                    "",
                    rs.getString("kelas") != null ? rs.getString("kelas") : ""
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Mahasiswa> getByMatkul(int idMatkul) {
        return searchByMatkul(idMatkul, "");
    }

    public List<Mahasiswa> searchByMatkul(int idMatkul, String keyword) {
        List<Mahasiswa> list = new ArrayList<>();
        String sql = """
            SELECT pm.nim,
                   COALESCE(pm.nama, m.nama) AS nama,
                   COALESCE(pm.kelas, m.kelas) AS kelas,
                   pm.no_urut
            FROM peserta_matkul pm
            JOIN mahasiswa m ON m.nim = pm.nim
            WHERE pm.id_matkul = ?
              AND (pm.nim LIKE ? OR COALESCE(pm.nama, m.nama) LIKE ?)
            ORDER BY CAST(pm.no_urut AS INTEGER), pm.nim
        """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            ps.setInt(1, idMatkul);
            ps.setString(2, k);
            ps.setString(3, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Mahasiswa(
                    rs.getString("nim"),
                    rs.getString("nama"),
                    "",
                    rs.getString("kelas") != null ? rs.getString("kelas") : "",
                    rs.getString("no_urut")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Mahasiswa m) {
        try (Connection c = DatabaseUtil.getConnection()) {
            boolean hasAngkatan = hasColumn(c, "mahasiswa", "angkatan");
            String sql = hasAngkatan
                ? "INSERT INTO mahasiswa (nim, nama, angkatan, kelas) VALUES (?, ?, ?, ?)"
                : "INSERT INTO mahasiswa (nim, nama, kelas) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, m.getNim());
                ps.setString(2, m.getNama());
                if (hasAngkatan) {
                    ps.setString(3, "");
                    ps.setString(4, m.getKelas());
                } else {
                    ps.setString(3, m.getKelas());
                }
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean insertForMatkul(int idMatkul, Mahasiswa m) {
        try (Connection c = DatabaseUtil.getConnection()) {
            c.setAutoCommit(false);
            boolean hasAngkatan = hasColumn(c, "mahasiswa", "angkatan");
            String insertSql = hasAngkatan
                ? """
                    INSERT INTO mahasiswa (nim, nama, angkatan, kelas)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT(nim) DO UPDATE SET
                        nama = excluded.nama,
                        kelas = excluded.kelas
                  """
                : """
                    INSERT INTO mahasiswa (nim, nama, kelas)
                    VALUES (?, ?, ?)
                    ON CONFLICT(nim) DO UPDATE SET
                        nama = excluded.nama,
                        kelas = excluded.kelas
                  """;
            try (PreparedStatement insertMahasiswa = c.prepareStatement(insertSql);
                 PreparedStatement insertRelasi = c.prepareStatement("""
                    INSERT INTO peserta_matkul (id_matkul, nim, nama, kelas, no_urut)
                    VALUES (?, ?, ?, ?, ?)
                    ON CONFLICT(id_matkul, nim) DO UPDATE SET
                        nama = excluded.nama,
                        kelas = excluded.kelas,
                        no_urut = excluded.no_urut
                 """)) {
                insertMahasiswa.setString(1, m.getNim());
                insertMahasiswa.setString(2, m.getNama());
                if (hasAngkatan) {
                    insertMahasiswa.setString(3, "");
                    insertMahasiswa.setString(4, m.getKelas());
                } else {
                    insertMahasiswa.setString(3, m.getKelas());
                }
                insertMahasiswa.executeUpdate();

                insertRelasi.setInt(1, idMatkul);
                insertRelasi.setString(2, m.getNim());
                insertRelasi.setString(3, m.getNama());
                insertRelasi.setString(4, m.getKelas());
                insertRelasi.setString(5, m.getNoUrut());
                insertRelasi.executeUpdate();
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Mahasiswa m) {
        try (Connection c = DatabaseUtil.getConnection()) {
            boolean hasAngkatan = hasColumn(c, "mahasiswa", "angkatan");
            String sql = hasAngkatan
                ? "UPDATE mahasiswa SET nama=?, angkatan=?, kelas=? WHERE nim=?"
                : "UPDATE mahasiswa SET nama=?, kelas=? WHERE nim=?";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, m.getNama());
                if (hasAngkatan) {
                    ps.setString(2, "");
                    ps.setString(3, m.getKelas());
                    ps.setString(4, m.getNim());
                } else {
                    ps.setString(2, m.getKelas());
                    ps.setString(3, m.getNim());
                }
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateForMatkul(int idMatkul, Mahasiswa m) {
        String sql = "UPDATE peserta_matkul SET nama=?, kelas=?, no_urut=? WHERE id_matkul=? AND nim=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getNama());
            ps.setString(2, m.getKelas());
            ps.setString(3, m.getNoUrut());
            ps.setInt(4, idMatkul);
            ps.setString(5, m.getNim());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String nim) {
        try (Connection c = DatabaseUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement deleteNilaiDinamis = c.prepareStatement("DELETE FROM nilai_mahasiswa WHERE nim=?");
                 PreparedStatement deleteMahasiswa = c.prepareStatement("DELETE FROM mahasiswa WHERE nim=?")) {
                deleteNilaiDinamis.setString(1, nim);
                deleteNilaiDinamis.executeUpdate();
                deleteMahasiswa.setString(1, nim);
                deleteMahasiswa.executeUpdate();
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

    public boolean removeFromMatkul(int idMatkul, String nim) {
        try (Connection c = DatabaseUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement deleteNilai = c.prepareStatement("""
                    DELETE FROM nilai_mahasiswa
                    WHERE nim = ?
                      AND id_komponen IN (SELECT id_komponen FROM komponen_nilai WHERE id_matkul = ?)
                 """);
                 PreparedStatement deleteRelasi = c.prepareStatement("DELETE FROM peserta_matkul WHERE id_matkul=? AND nim=?")) {
                deleteNilai.setString(1, nim);
                deleteNilai.setInt(2, idMatkul);
                deleteNilai.executeUpdate();

                deleteRelasi.setInt(1, idMatkul);
                deleteRelasi.setString(2, nim);
                deleteRelasi.executeUpdate();
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean nimExists(String nim) {
        String sql = "SELECT COUNT(*) FROM mahasiswa WHERE nim=?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nim);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private boolean hasColumn(Connection c, String table, String column) throws SQLException {
        try (ResultSet rs = c.getMetaData().getColumns(null, null, table, column)) {
            return rs.next();
        }
    }
}
