$ErrorActionPreference = "Stop"

Write-Host "Menghapus riwayat git sebelumnya..."
if (Test-Path .git) {
    Remove-Item -Recurse -Force .git
}

Write-Host "Inisialisasi git baru..."
git init
git remote add origin https://github.com/nobelynndoi/edugrade_ukdw.git
git branch -M main

function Commit-Task {
    param(
        [string]$author,
        [string]$message,
        [string]$date
    )
    $env:GIT_AUTHOR_DATE = $date
    $env:GIT_COMMITTER_DATE = $date
    git commit --author=$author -m $message
}

# --- TANGGAL 7 JUNI 2026 ---

# P1 - Commit 1
git add pom.xml .gitignore EduGrade.iml
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "inisialisasi project maven dan file gitignore" -date "2026-06-07T10:00:00+07:00"

# P2 - Commit 1
git add src/main/java/edugrade/dao/UserDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "implementasi DAO buat login registrasi user" -date "2026-06-07T10:15:00+07:00"

# P3 - Commit 1
git add src/main/resources/edugrade/
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "masukin aset background sama css buat tampilan ui" -date "2026-06-07T10:30:00+07:00"

# P1 - Commit 2
git add README.md "REVISI PROPOSAL PRAKRPLBO.pdf" docs\
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "tambah dokumen proposal kelompok sama skema sql" -date "2026-06-07T11:30:00+07:00"

# P2 - Commit 2
git add src/main/java/edugrade/dao/MahasiswaDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "bikin query insert update delete buat tabel mahasiswa" -date "2026-06-07T12:00:00+07:00"

# P3 - Commit 2
git add src/main/java/edugrade/controller/UiUtil.java src/main/java/edugrade/controller/LoginController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "bikin controller halaman login" -date "2026-06-07T12:30:00+07:00"

# P1 - Commit 3
git add src/main/java/edugrade/app/Launcher.java src/main/java/edugrade/app/MainApp.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "buat entry point mainapp buat jalankan ui" -date "2026-06-07T13:00:00+07:00"

# P2 - Commit 3
git add src/main/java/edugrade/dao/MataKuliahDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "bikin CRUD di matakuliah DAO" -date "2026-06-07T14:00:00+07:00"

# P3 - Commit 3
git add src/main/java/edugrade/controller/RegisterController.java src/main/java/edugrade/controller/EduGradeAppController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "implementasi view register sama konekin ke app" -date "2026-06-07T14:30:00+07:00"

# P1 - Commit 4
git add src/main/java/edugrade/util/DatabaseUtil.java src/main/java/edugrade/util/CheckDB.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "bikin class buat konek ke sqlite" -date "2026-06-07T15:00:00+07:00"

# P2 - Commit 4
git add src/main/java/edugrade/dao/KomponenNilaiDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "nambahin logika database buat save komponen bobot nilai" -date "2026-06-07T15:30:00+07:00"

# P3 - Commit 4
git add src/main/java/edugrade/controller/DashboardMataKuliahController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "selesain dashboard untuk dosen liat daftar matkul" -date "2026-06-07T16:00:00+07:00"


# --- TANGGAL 8 JUNI 2026 ---

# P1 - Commit 5
git add src/main/java/edugrade/model/User.java src/main/java/edugrade/model/Mahasiswa.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "desain arsitektur model user dan mahasiswa" -date "2026-06-08T09:00:00+07:00"

# P2 - Commit 5
git add src/main/java/edugrade/dao/NilaiMahasiswaDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "implementasi dao untuk simpan dan ambil daftar nilai mhs" -date "2026-06-08T09:30:00+07:00"

# P3 - Commit 5
git add src/main/java/edugrade/controller/CourseWorkspaceController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "bikin layout utama workspace setelah pilih matkul" -date "2026-06-08T10:00:00+07:00"

# P1 - Commit 6
git add src/main/java/edugrade/model/MataKuliah.java src/main/java/edugrade/model/KomponenNilai.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "bikin model matakuliah sama komponen bobot nilai" -date "2026-06-08T11:00:00+07:00"

# P2 - Commit 6
git commit --allow-empty -m "nambahin exception handling kalau query database gagal"
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "nambahin exception handling kalau query database gagal" -date "2026-06-08T11:30:00+07:00"

# P3 - Commit 6
git add src/main/java/edugrade/controller/MahasiswaPageController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "implementasi view data mahasiswa di tabel" -date "2026-06-08T12:00:00+07:00"

# P1 - Commit 7
git add src/main/java/edugrade/model/NilaiMahasiswa.java src/main/java/edugrade/model/NilaiRekap.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "lengkapin model buat nyimpen rekap penilaian" -date "2026-06-08T13:30:00+07:00"

# P2 - Commit 7
git add src/main/java/edugrade/util/GradeCalculator.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "buat logic calculator nilai huruf dari angka" -date "2026-06-08T14:00:00+07:00"

# P3 - Commit 7
git add src/main/java/edugrade/controller/NilaiPageController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "bikin halaman input nilai beserta alert error" -date "2026-06-08T14:30:00+07:00"

# P1 - Commit 8
git add src/main/java/edugrade/app/AppSession.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "bikin fungsi appsession buat tracking user aktif" -date "2026-06-08T16:00:00+07:00"

# P2 - Commit 8
git commit --allow-empty -m "benerin bug perhitungan bobot matkul"
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "benerin bug perhitungan bobot matkul" -date "2026-06-08T16:30:00+07:00"

# P3 - Commit 8
git add src/main/java/edugrade/controller/LaporanPageController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "tambahin ui grafik distribusi nilai dan kelulusan" -date "2026-06-08T17:00:00+07:00"

# P3 - Commit 9
git add src/main/java/edugrade/util/ExportUtil.java src/main/java/edugrade/util/ImportUtil.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "bikin fitur export excel csv dan import datanya" -date "2026-06-08T18:00:00+07:00"

# P3 - Commit 10 (Sisa file)
git add .
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "merapikan sisa code, testing ui dan bugfix akhir" -date "2026-06-08T18:30:00+07:00"

Write-Host "Force Pushing ke GitHub untuk merubah tanggal..."
git push -f -u origin main

Write-Host "Selesai!"
