-- Dynamic Assessment Components - EduGrade
-- SQLite DDL. Untuk MySQL, ganti INTEGER PRIMARY KEY AUTOINCREMENT
-- menjadi INT AUTO_INCREMENT PRIMARY KEY.

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

CREATE INDEX IF NOT EXISTS idx_komponen_nilai_matkul
    ON komponen_nilai(id_matkul);

CREATE INDEX IF NOT EXISTS idx_nilai_mahasiswa_komponen
    ON nilai_mahasiswa(id_komponen);
