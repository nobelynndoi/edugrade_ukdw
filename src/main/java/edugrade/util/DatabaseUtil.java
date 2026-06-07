package edugrade.util;

import java.sql.*;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:edugrade.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC Driver not found", e);
            }
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // App config table (for tracking seed status)
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS app_config (
                    key TEXT PRIMARY KEY,
                    value TEXT
                )
            """);

            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id_user INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    nama_lengkap TEXT NOT NULL,
                    role TEXT DEFAULT 'user'
                )
            """);

            // Mahasiswa table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mahasiswa (
                    nim TEXT PRIMARY KEY,
                    nama TEXT NOT NULL,
                    kelas TEXT
                )
            """);

            // Mata kuliah table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS mata_kuliah (
                    id_matkul INTEGER PRIMARY KEY AUTOINCREMENT,
                    kode_mk TEXT NOT NULL,
                    nama_mk TEXT NOT NULL,
                    sks INTEGER DEFAULT 3,
                    semester TEXT NOT NULL,
                    tahun_ajaran TEXT NOT NULL,
                    kelas TEXT,
                    UNIQUE(kode_mk, semester, tahun_ajaran, kelas)
                )
            """);
            addColumnIfMissing(conn, "mata_kuliah", "kelas", "TEXT");
            addColumnIfMissing(conn, "mata_kuliah", "id_user", "INTEGER");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS peserta_matkul (
                    id_peserta INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_matkul INTEGER NOT NULL,
                    nim TEXT NOT NULL,
                    nama TEXT,
                    kelas TEXT,
                    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul) ON DELETE CASCADE,
                    FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
                    UNIQUE(id_matkul, nim)
                )
            """);
            addColumnIfMissing(conn, "peserta_matkul", "nama", "TEXT");
            addColumnIfMissing(conn, "peserta_matkul", "kelas", "TEXT");
            addColumnIfMissing(conn, "peserta_matkul", "no_urut", "TEXT");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS komponen_nilai (
                    id_komponen INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_matkul INTEGER NOT NULL,
                    nama_komponen TEXT NOT NULL,
                    bobot_persentase REAL NOT NULL CHECK (bobot_persentase > 0),
                    is_bonus INTEGER DEFAULT 0,
                    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul) ON DELETE CASCADE,
                    UNIQUE(id_matkul, nama_komponen)
                )
            """);
            addColumnIfMissing(conn, "komponen_nilai", "is_bonus", "INTEGER DEFAULT 0");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS nilai_mahasiswa (
                    id_nilai INTEGER PRIMARY KEY AUTOINCREMENT,
                    nim TEXT NOT NULL,
                    id_komponen INTEGER NOT NULL,
                    skor_nilai REAL NOT NULL DEFAULT 0 CHECK (skor_nilai >= 0 AND skor_nilai <= 100),
                    FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
                    FOREIGN KEY (id_komponen) REFERENCES komponen_nilai(id_komponen) ON DELETE CASCADE,
                    UNIQUE(nim, id_komponen)
                )
            """);

            // Only seed sample data ONCE (first run)
            if (!isSeeded(conn)) {
                seedSampleData(stmt);
                markSeeded(conn);
            }

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isSeeded(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT value FROM app_config WHERE key = 'seeded'")) {
            ResultSet rs = ps.executeQuery();
            return rs.next() && "true".equals(rs.getString("value"));
        }
    }

    private static void markSeeded(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT OR REPLACE INTO app_config (key, value) VALUES ('seeded', 'true')")) {
            ps.executeUpdate();
        }
    }

    private static void seedSampleData(Statement stmt) throws SQLException {
        // Data seed mata kuliah dihapus — setiap dosen memulai dengan daftar kosong
        // dan mengelola mata kuliahnya sendiri secara manual.
    }

    private static void addColumnIfMissing(Connection conn, String table, String column, String type) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, table, column)) {
            if (!rs.next()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
                }
            }
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
