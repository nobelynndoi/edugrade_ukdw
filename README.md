# EduGrade

EduGrade adalah aplikasi desktop JavaFX untuk membantu dosen mengelola data mahasiswa, mata kuliah, input nilai, kalkulasi nilai akhir, dan laporan distribusi grade.

## Status Implementasi 30%

Fitur yang sudah tersedia:

- CRUD data mahasiswa.
- CRUD data mata kuliah.
- Alur rigid: aplikasi dibuka di Dashboard Mata Kuliah, lalu double click/klik `Buka Mata Kuliah` masuk ke workspace internal.
- Relasi mahasiswa per mata kuliah melalui tabel `peserta_matkul`.
- Pengaturan komponen nilai dinamis per mata kuliah.
- Input nilai dengan kolom TableView yang mengikuti komponen nilai mata kuliah.
- Perhitungan otomatis nilai akhir dan grade huruf berdasarkan `nilai_mahasiswa` dan `komponen_nilai`.
- Laporan dengan BarChart distribusi grade dan ProgressIndicator kelulusan.
- Database SQLite otomatis dibuat dan diisi data contoh saat aplikasi pertama dijalankan.

Belum dibuat pada tahap ini:

- Login dan otorisasi user.
- Import/export Excel atau PDF.
- Cetak laporan.
- Pengaturan multi-dosen.

## Struktur Project

```text
src/main/java/edugrade/app          Entry point aplikasi
src/main/java/edugrade/controller   UI dan alur fitur utama
src/main/java/edugrade/dao          Akses database SQLite
src/main/java/edugrade/model        Model data JavaFX
src/main/java/edugrade/util         Utilitas database dan kalkulasi grade
src/main/resources/edugrade         Stylesheet aplikasi
docs/database-schema-edugrade.sql    DDL alur relasional rigid lengkap
docs/database-dynamic-assessment.sql DDL tabel komponen nilai dinamis
```

## Cara Menjalankan

Project disiapkan sebagai Maven project.

```bash
mvn javafx:run
```

Prasyarat:

- JDK 17 atau lebih baru.
- Maven.

Saat ini login sengaja dilewati sesuai target implementasi 30%, jadi aplikasi langsung masuk ke halaman Mata Kuliah.
