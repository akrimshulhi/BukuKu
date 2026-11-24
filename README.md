# BukuKu — Aplikasi Pencatatan Buku Pribadi
> **Tugas Ujian Tengah Semester (UTS) Take-Home**  
> **Mata Kuliah:** Pemrograman Bergerak (3 SKS)  
> **Topik:** Topik A — Buku Bacaan (Digit Akhir NIM: 9)

---

## 👤 Identitas Mahasiswa
* **Nama:** Akrim Shulhi
* **NIM:** 0102523009
* **Topik:** Topik A — Buku Bacaan (BukuKu)
* **Tautan Repositori GitHub:** [https://github.com/akrimshulhi/BukuKu](https://github.com/akrimshulhi/BukuKu)
* **Tautan Video Presentasi & Demo (6–8 menit):** https://drive.google.com/drive/folders/1-ct3H6SAtJvoCD_6w2pyeCafeiMdWa8n?usp=sharing

---

## 📖 Deskripsi Aplikasi
**BukuKu** adalah aplikasi Android modern berbasis **Jetpack Compose** untuk mencatat koleksi buku pribadi yang dimiliki atau ingin dibaca. Pengguna dapat mengelola data buku (judul, penulis, kategori, status bacaan), mengubah preferensi tampilan menjadi mode ringkas, serta memastikan data tetap tersimpan secara permanen meskipun aplikasi ditutup.

---

## 🛠️ Stack Teknologi & Library
* **Bahasa:** Kotlin (`2.0.21`)
* **UI Toolkit:** Jetpack Compose dengan Material 3 (Declarative UI)
* **Arsitektur:** MVVM (Model-View-ViewModel) + Repository Pattern + Single Activity
* **Navigasi:** Jetpack Navigation Compose (`2.8.5`)
* **State Management:** ViewModel (`lifecycle-viewmodel-compose 2.8.7`) & `StateFlow`
* **Local Database (Persistence):** Room Database (`2.6.1`) dengan KSP Compiler
* **Preferences Storage:** AndroidX Preferences DataStore (`1.1.1`)
* **Asynchronous:** Kotlin Coroutines & Flow

---

## 📁 Struktur File Proyek
```text
com.example.bukuku/
├── data/
│   ├── Buku.kt               # Entity Room tabel "buku"
│   ├── BukuDao.kt            # DAO Room untuk operasi CRUD (Flow-based)
│   ├── BukuDatabase.kt       # Database Room Singleton
│   ├── BukuRepository.kt     # Repository penghubung DAO ke ViewModel
│   └── SettingsRepository.kt # Repository DataStore (Preferensi Mode Ringkas)
├── ui/
│   ├── theme/                # Pengaturan Tema Material 3 (Color, Theme, Type)
│   ├── BukuViewModel.kt      # ViewModel, BukuUiState, dan ViewModelFactory
│   ├── DaftarScreen.kt       # Layar 1: Daftar Buku (LazyColumn + Switch Ringkas)
│   ├── DetailScreen.kt       # Layar 2: Detail Buku (Ubah Status, Edit, Hapus)
│   └── FormScreen.kt         # Layar 3: Form Tambah & Ubah Buku (Validasi Judul)
└── MainActivity.kt           # NavHost & Inisialisasi Repository + ViewModel
```

---

## ✨ Fitur-Fitur Utama (Sesuai Ketentuan Soal)
1. **Tiga Layar Utama & Navigasi Mulus:**
   * **Daftar Buku (`daftar`)**: Menampilkan koleksi buku dalam bentuk kartu menggunakan `LazyColumn`. Jika kosong, menampilkan teks *"Belum ada buku"*.
   * **Detail Buku (`detail/{id}`)**: Menampilkan informasi lengkap buku, tombol ubah status bacaan, tombol edit data, dan tombol hapus dengan dialog konfirmasi.
   * **Form Tambah / Ubah (`form?id={id}`)**: Satu formulir cerdas untuk menambah data baru maupun mengedit data lama berdasarkan argumen ID.
2. **Operasi CRUD Lengkap:**
   * **Create:** Menambah buku baru.
   * **Read:** Menampilkan daftar buku dan rincian detail buku.
   * **Update:** Memperbarui judul, penulis, kategori, dan status bacaan.
   * **Delete:** Menghapus buku dari database dengan konfirmasi aman.
3. **Validasi Input Ketat:**
   * Judul buku wajib diisi. Jika kosong, field judul menampilkan pesan error berwarna merah *"Judul tidak boleh kosong"* dan data tidak akan disimpan.
4. **Pengelolaan State & Reaktivitas (ViewModel + StateFlow):**
   * Seluruh status UI dikelola di `BukuViewModel` menggunakan `StateFlow`. Composable hanya mengamati state dan mengirim event (bebas dari logika bisnis).
5. **Persistensi Data (Room Database):**
   * Menggunakan Room SQLite dengan DAO yang mengembalikan `Flow`, sehingga perubahan data langsung ter-update secara otomatis di UI. Data tetap aman tersimpan setelah aplikasi ditutup.
6. **Preferensi Tampilan (Preferences DataStore):**
   * Switch **"Ringkas"** di TopAppBar memungkinkan beralih antara Mode Normal (judul, penulis, kategori, status) dan Mode Ringkas (hanya judul dan status). Status switch tersimpan permanen di DataStore.

---

## 🚀 Cara Menjalankan Aplikasi
1. Buka Android Studio (versi Ladybug / Koala / Narwhal / Iguana atau yang lebih baru).
2. Lakukan *Clone* repositori ini:
   ```bash
   git clone https://github.com/akrimshulhi/BukuKu.git
   ```
3. Buka folder proyek di Android Studio dan tunggu proses *Gradle Sync* selesai.
4. Pilih target perangkat (Emulator Pixel / Device fisik dengan Android 7.0+ / API 24 ke atas).
5. Tekan tombol **Run 'app' (Shift + F10)**.

---

## 🤖 Deklarasi Penggunaan AI (Academic Integrity)
* **Alat AI:** Google Gemini / Antigravity Agent
* **Tujuan Penggunaan:** Membantu penyusunan struktur arsitektur MVVM, konfigurasi dependency Gradle, pembuatan tata letak Compose Material 3, serta panduan pengujian bertahap sesuai rubrik UTS.
* **Bagian Kode yang Dibantu:**
  * Penataan relasi Room (Entity, DAO, Database Singleton, Repository).
  * Penggabungan StateFlow di ViewModel menggunakan operator `combine`.
  * Konfigurasi Navigation Compose antar 3 layar.
  * Penyusunan checklist validasi dan pengujian alur bisnis.
* Seluruh kode dan alur logika telah dipelajari, dipahami, dan dapat dijelaskan secara mandiri oleh mahasiswa dalam video presentasi dan uji lisan.
