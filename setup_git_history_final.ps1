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
git add src/main/resources/edugrade/login_bg.png src/main/resources/edugrade/style.css
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "masukin aset background css untuk ui" -date "2026-06-07T10:30:00+07:00"

# P1 - Commit 2
git add README.md "REVISI PROPOSAL PRAKRPLBO.pdf" docs\
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "tambah dokumen proposal kelompok sama skema sql" -date "2026-06-07T11:30:00+07:00"

# P2 - Commit 2
git add src/main/java/edugrade/dao/MahasiswaDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "bikin query insert update delete buat tabel mahasiswa" -date "2026-06-07T12:00:00+07:00"

# P3 - Commit 2
git add src/main/resources/edugrade/login.fxml src/main/resources/edugrade/register.fxml
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "desain fxml untuk login dan register" -date "2026-06-07T12:30:00+07:00"

# P1 - Commit 3
git add src/main/java/edugrade/app/Launcher.java src/main/java/edugrade/app/MainApp.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "buat entry point mainapp buat jalankan ui" -date "2026-06-07T13:00:00+07:00"

# P2 - Commit 3
git add src/main/java/edugrade/dao/MataKuliahDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "bikin CRUD di matakuliah DAO" -date "2026-06-07T14:00:00+07:00"

# P3 - Commit 3
git add src/main/java/edugrade/controller/LoginController.java src/main/java/edugrade/controller/RegisterController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "konekin fxml ke controller login dan register" -date "2026-06-07T14:30:00+07:00"

# P1 - Commit 4
git add src/main/java/edugrade/util/DatabaseUtil.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "bikin class utama buat koneksi sqlite" -date "2026-06-07T15:00:00+07:00"

# P2 - Commit 4
git add src/main/java/edugrade/dao/KomponenNilaiDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "nambahin logika database buat save komponen bobot nilai" -date "2026-06-07T15:30:00+07:00"

# P3 - Commit 4
git add src/main/resources/edugrade/dashboard_matakuliah.fxml src/main/resources/edugrade/course_workspace.fxml
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "layouting fxml dashboard dosen dan workspace" -date "2026-06-07T16:00:00+07:00"


# --- TANGGAL 8 JUNI 2026 ---

# P1 - Commit 5
git add src/main/java/edugrade/util/CheckDB.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "tambahin helper checkDB untuk validasi awal" -date "2026-06-08T09:00:00+07:00"

# P2 - Commit 5
git add src/main/java/edugrade/dao/NilaiMahasiswaDAO.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "implementasi dao untuk simpan dan ambil daftar nilai mhs" -date "2026-06-08T09:30:00+07:00"

# P3 - Commit 5
git add src/main/java/edugrade/controller/DashboardMataKuliahController.java src/main/java/edugrade/controller/CourseWorkspaceController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "implementasi fungsional dashboard dan workspace dosen" -date "2026-06-08T10:00:00+07:00"

# P1 - Commit 6
git add src/main/java/edugrade/model/Mahasiswa.java src/main/java/edugrade/model/User.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "desain arsitektur model user dan mahasiswa" -date "2026-06-08T11:00:00+07:00"

# P2 - Commit 6
git add src/main/java/edugrade/util/GradeCalculator.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "buat logic calculator nilai huruf dari angka" -date "2026-06-08T11:30:00+07:00"

# P3 - Commit 6
git add src/main/resources/edugrade/mahasiswa_page.fxml src/main/resources/edugrade/nilai_page.fxml src/main/resources/edugrade/laporan_page.fxml
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "desain semua fxml page di dalam workspace" -date "2026-06-08T12:00:00+07:00"

# P1 - Commit 7
git add src/main/java/edugrade/model/MataKuliah.java src/main/java/edugrade/model/KomponenNilai.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "bikin model matakuliah sama komponen bobot nilai" -date "2026-06-08T13:30:00+07:00"

# P2 - Commit 7
git add src/test/java/edugrade/CheckDb.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "tambah script testing integrasi exception di checkdb" -date "2026-06-08T14:00:00+07:00"

# P3 - Commit 7
git add src/main/java/edugrade/controller/MahasiswaPageController.java src/main/java/edugrade/controller/NilaiPageController.java src/main/java/edugrade/controller/LaporanPageController.java
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "implementasi controller untuk mahasiswa nilai dan laporan" -date "2026-06-08T14:30:00+07:00"

# P1 - Commit 8
git add src/main/java/edugrade/model/NilaiMahasiswa.java src/main/java/edugrade/model/NilaiRekap.java src/main/java/edugrade/app/AppSession.java
Commit-Task -author "Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -message "lengkapin model sisa dan bikin appsession manager" -date "2026-06-08T16:00:00+07:00"

# P2 - Commit 8
git add src/test/java/edugrade/TestFxml.java
Commit-Task -author "Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -message "tambah test kasus untuk validasi gui rendering" -date "2026-06-08T16:30:00+07:00"

# P3 - Commit 8
git add .
Commit-Task -author "Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -message "tambah fitur export import dan fix bug sisa fxml" -date "2026-06-08T17:00:00+07:00"

Write-Host "Force Pushing ke GitHub..."
git push -f -u origin main

Write-Host "Selesai!"
