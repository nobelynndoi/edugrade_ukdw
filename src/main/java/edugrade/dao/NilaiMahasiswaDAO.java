package edugrade.dao;

import edugrade.model.KomponenNilai;
import edugrade.model.Mahasiswa;
import edugrade.model.NilaiRekap;
import edugrade.util.DatabaseUtil;
import edugrade.util.GradeCalculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NilaiMahasiswaDAO {

    public void saveScore(String nim, int idKomponen, double skorNilai) throws SQLException {
        String sql = """
            INSERT INTO nilai_mahasiswa (nim, id_komponen, skor_nilai)
            VALUES (?, ?, ?)
            ON CONFLICT(nim, id_komponen) DO UPDATE SET
                skor_nilai = excluded.skor_nilai
        """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nim);
            ps.setInt(2, idKomponen);
            ps.setDouble(3, skorNilai);
            ps.executeUpdate();
        }
    }

    public List<NilaiRekap> getRekapNilai(int idMatkul, List<KomponenNilai> komponenList, String filterGrade, String keyword) {
        List<NilaiRekap> rekapList = new ArrayList<>();
        List<Mahasiswa> mahasiswaList = new MahasiswaDAO().searchByMatkul(idMatkul, keyword == null ? "" : keyword.trim());
        Map<String, Map<Integer, Double>> savedScores = getSavedScores(idMatkul);

        for (Mahasiswa mahasiswa : mahasiswaList) {
            NilaiRekap rekap = new NilaiRekap(mahasiswa.getNim(), mahasiswa.getNama(), mahasiswa.getNoUrut());
            Map<Integer, Double> perKomponen = savedScores.getOrDefault(mahasiswa.getNim(), Map.of());
            for (KomponenNilai komponen : komponenList) {
                rekap.setSkor(komponen.getIdKomponen(), perKomponen.getOrDefault(komponen.getIdKomponen(), 0.0));
            }
            refreshTotal(rekap, komponenList);
            if (filterGrade == null || filterGrade.equals("Semua") || rekap.getGradeHuruf().equals(filterGrade)) {
                rekapList.add(rekap);
            }
        }
        return rekapList;
    }

    public boolean saveWorksheet(int idMatkul, List<KomponenNilai> komponenList, List<NilaiRekap> rekapList) {
        String upsertNilai = """
            INSERT INTO nilai_mahasiswa (nim, id_komponen, skor_nilai)
            VALUES (?, ?, ?)
            ON CONFLICT(nim, id_komponen) DO UPDATE SET
                skor_nilai = excluded.skor_nilai
        """;
        try (Connection c = DatabaseUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement nilaiPs = c.prepareStatement(upsertNilai)) {
                for (NilaiRekap rekap : rekapList) {
                    refreshTotal(rekap, komponenList);
                    for (KomponenNilai komponen : komponenList) {
                        nilaiPs.setString(1, rekap.getNim());
                        nilaiPs.setInt(2, komponen.getIdKomponen());
                        nilaiPs.setDouble(3, rekap.getSkor(komponen.getIdKomponen()));
                        nilaiPs.addBatch();
                    }
                }
                nilaiPs.executeBatch();
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

    public void refreshTotal(NilaiRekap rekap, List<KomponenNilai> komponenList) {
        double nilaiAkhir = GradeCalculator.hitungNilaiAkhirDinamis(rekap.getSkorPerKomponen(), komponenList);
        rekap.setNilaiAkhir(nilaiAkhir);
        rekap.setGradeHuruf(GradeCalculator.konversiKeHuruf(nilaiAkhir));
    }

    public Map<String, Integer> getDistribusiGrade(int idMatkul, List<KomponenNilai> komponenList) {
        Map<String, Integer> distribusi = new LinkedHashMap<>();
        for (String grade : new String[]{"A", "A-", "B+", "B", "B-", "C+", "C", "D", "E"}) {
            distribusi.put(grade, 0);
        }
        for (NilaiRekap rekap : getRekapNilai(idMatkul, komponenList, "Semua", "")) {
            distribusi.computeIfPresent(rekap.getGradeHuruf(), (grade, count) -> count + 1);
        }
        return distribusi;
    }

    public int getTotalLulus(int idMatkul, List<KomponenNilai> komponenList) {
        int total = 0;
        for (NilaiRekap rekap : getRekapNilai(idMatkul, komponenList, "Semua", "")) {
            if (GradeCalculator.isLulus(rekap.getGradeHuruf())) {
                total++;
            }
        }
        return total;
    }

    public double getRataRata(int idMatkul, List<KomponenNilai> komponenList) {
        List<NilaiRekap> rekapList = getRekapNilai(idMatkul, komponenList, "Semua", "");
        if (rekapList.isEmpty()) {
            return 0;
        }
        double total = 0;
        for (NilaiRekap rekap : rekapList) {
            total += rekap.getNilaiAkhir();
        }
        return total / rekapList.size();
    }

    private Map<String, Map<Integer, Double>> getSavedScores(int idMatkul) {
        Map<String, Map<Integer, Double>> scores = new LinkedHashMap<>();
        String sql = """
            SELECT nm.nim, nm.id_komponen, nm.skor_nilai
            FROM nilai_mahasiswa nm
            JOIN komponen_nilai kn ON kn.id_komponen = nm.id_komponen
            WHERE kn.id_matkul = ?
        """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMatkul);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                scores.computeIfAbsent(rs.getString("nim"), nim -> new LinkedHashMap<>())
                    .put(rs.getInt("id_komponen"), rs.getDouble("skor_nilai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
}
