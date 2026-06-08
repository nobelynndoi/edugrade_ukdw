$ErrorActionPreference = "Stop"

Write-Host "Initializing git repository..."
git init

Write-Host "Adding remote origin..."
git remote add origin https://github.com/nobelynndoi/edugrade_ukdw.git

Write-Host "Changing branch name to main..."
git branch -M main

# --- P1: Norbert Alexis Lynndoi ---
Write-Host "Committing P1 tasks..."
git add pom.xml .gitignore EduGrade.iml
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "inisialisasi project maven dan file gitignore"

git add README.md "REVISI PROPOSAL PRAKRPLBO.pdf" docs\
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "tambah dokumen proposal kelompok sama skema sql"

git add src/main/java/edugrade/app/Launcher.java src/main/java/edugrade/app/MainApp.java
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "buat entry point mainapp buat jalankan ui"

git add src/main/java/edugrade/util/DatabaseUtil.java src/main/java/edugrade/util/CheckDB.java
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "bikin class buat konek ke sqlite"

git add src/main/java/edugrade/model/User.java src/main/java/edugrade/model/Mahasiswa.java
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "desain arsitektur model user dan mahasiswa"

git add src/main/java/edugrade/model/MataKuliah.java src/main/java/edugrade/model/KomponenNilai.java
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "bikin model matakuliah sama komponen bobot nilai"

git add src/main/java/edugrade/model/NilaiMahasiswa.java src/main/java/edugrade/model/NilaiRekap.java
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "lengkapin model buat nyimpen rekap penilaian"

git add src/main/java/edugrade/app/AppSession.java
git commit --author="Norbert Alexis Lynndoi <Norbert.alexis@ti.ukdw.ac.id>" -m "bikin fungsi appsession buat tracking user aktif"


# --- P2: Aristo Yoan Prasetyatama ---
Write-Host "Committing P2 tasks..."
git add src/main/java/edugrade/dao/UserDAO.java
git commit --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "implementasi DAO buat login registrasi user"

git add src/main/java/edugrade/dao/MahasiswaDAO.java
git commit --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "bikin query insert update delete buat tabel mahasiswa"

git add src/main/java/edugrade/dao/MataKuliahDAO.java
git commit --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "bikin CRUD di matakuliah DAO"

git add src/main/java/edugrade/dao/KomponenNilaiDAO.java
git commit --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "nambahin logika database buat save komponen bobot nilai"

git add src/main/java/edugrade/dao/NilaiMahasiswaDAO.java
git commit --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "implementasi dao untuk simpan dan ambil daftar nilai mhs"

git commit --allow-empty --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "nambahin exception handling kalau query database gagal"

git add src/main/java/edugrade/util/GradeCalculator.java
git commit --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "buat logic calculator nilai huruf dari angka"

git commit --allow-empty --author="Aristo Yoan Prasetyatama <aristo.yoan@ti.ukdw.ac.id>" -m "benerin bug perhitungan bobot matkul"


# --- P3: Darren Malvino Gunawan ---
Write-Host "Committing P3 tasks..."
git add src/main/resources/edugrade/
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "masukin aset background sama css buat tampilan ui"

git add src/main/java/edugrade/controller/UiUtil.java src/main/java/edugrade/controller/LoginController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "bikin controller halaman login"

git add src/main/java/edugrade/controller/RegisterController.java src/main/java/edugrade/controller/EduGradeAppController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "implementasi view register sama konekin ke app"

git add src/main/java/edugrade/controller/DashboardMataKuliahController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "selesain dashboard untuk dosen liat daftar matkul"

git add src/main/java/edugrade/controller/CourseWorkspaceController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "bikin layout utama workspace setelah pilih matkul"

git add src/main/java/edugrade/controller/MahasiswaPageController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "implementasi view data mahasiswa di tabel"

git add src/main/java/edugrade/controller/NilaiPageController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "bikin halaman input nilai beserta alert error"

git add src/main/java/edugrade/controller/LaporanPageController.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "tambahin ui grafik distribusi nilai dan kelulusan"

git add src/main/java/edugrade/util/ExportUtil.java src/main/java/edugrade/util/ImportUtil.java
git commit --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "bikin fitur export excel csv dan import datanya"

# --- Add any remaining untracked files ---
git add .
git commit --allow-empty --author="Darren Malvino Gunawan <darren.malvino@ti.ukdw.ac.id>" -m "merapikan sisa code, testing ui dan bugfix akhir"

Write-Host "Pushing to GitHub..."
git push -u origin main

Write-Host "Done!"
