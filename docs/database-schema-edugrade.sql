-- EduGrade relational schema for dynamic assessment workflow.
-- SQLite DDL. For MySQL, replace INTEGER PRIMARY KEY AUTOINCREMENT with
-- INT AUTO_INCREMENT PRIMARY KEY and TEXT with VARCHAR where preferred.

CREATE TABLE IF NOT EXISTS mata_kuliah (
    id_matkul INTEGER PRIMARY KEY AUTOINCREMENT,
    kode_mk TEXT NOT NULL,
    nama_mk TEXT NOT NULL,
    sks INTEGER NOT NULL DEFAULT 3,
    semester TEXT NOT NULL,
    tahun_ajaran TEXT NOT NULL,
    UNIQUE(kode_mk, semester, tahun_ajaran)
);

CREATE TABLE IF NOT EXISTS mahasiswa (
    nim TEXT PRIMARY KEY,
    nama TEXT NOT NULL,
    kelas TEXT
);

CREATE TABLE IF NOT EXISTS peserta_matkul (
    id_peserta INTEGER PRIMARY KEY AUTOINCREMENT,
    id_matkul INTEGER NOT NULL,
    nim TEXT NOT NULL,
    nama TEXT,
    kelas TEXT,
    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul) ON DELETE CASCADE,
    FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
    UNIQUE(id_matkul, nim)
);

CREATE TABLE IF NOT EXISTS komponen_nilai (
    id_komponen INTEGER PRIMARY KEY AUTOINCREMENT,
    id_matkul INTEGER NOT NULL,
    nama_komponen TEXT NOT NULL,
    bobot_persentase REAL NOT NULL CHECK (bobot_persentase > 0),
    FOREIGN KEY (id_matkul) REFERENCES mata_kuliah(id_matkul) ON DELETE CASCADE,
    UNIQUE(id_matkul, nama_komponen)
);

CREATE TABLE IF NOT EXISTS nilai_mahasiswa (
    id_nilai INTEGER PRIMARY KEY AUTOINCREMENT,
    nim TEXT NOT NULL,
    id_komponen INTEGER NOT NULL,
    skor_nilai REAL NOT NULL DEFAULT 0 CHECK (skor_nilai >= 0 AND skor_nilai <= 100),
    FOREIGN KEY (nim) REFERENCES mahasiswa(nim) ON DELETE CASCADE,
    FOREIGN KEY (id_komponen) REFERENCES komponen_nilai(id_komponen) ON DELETE CASCADE,
    UNIQUE(nim, id_komponen)
);
