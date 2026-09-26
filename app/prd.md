PRODUCT REQUIREMENTS DOCUMENT (PRD)
Nama Proyek: WakeBrain (Aplikasi Alarm Anti-Snooze dengan Puzzle)
Tanggal Dokumen: September 2026
Lingkungan: 100% Offline (Lokal)
Tim Pengembang: 2 Frontend UI, 2 Local Data & Logic (Kotlin Android)
1. Ringkasan Eksekutif
   WakeBrain adalah aplikasi alarm mobile berbasis Android yang berjalan sepenuhnya secara offline. Dirancang untuk mengatasi kebiasaan oversleeping, aplikasi ini memaksa pengguna untuk menyelesaikan tantangan kognitif (menjawab soal matematika acak atau trivia dari database lokal) sebelum bisa mematikan alarm. Pendekatan ini memastikan otak pengguna aktif dan mencegah mereka kembali tidur.
2. Objektif & Target Pengguna
   ·        Objektif: Memastikan pengguna terbangun sepenuhnya dengan memberikan hambatan kognitif secara mandiri (tanpa butuh koneksi internet) sebelum mematikan alarm.
   ·        Target Pengguna: Mahasiswa dan pekerja yang memiliki kebiasaan mematikan alarm tanpa sadar (heavy sleepers).
3. Fitur Utama & Alur Pengguna (User Flow)
1.      Beranda (Home): Menampilkan daftar jadwal alarm yang aktif maupun non-aktif dari database memori HP.
2.      Tambah/Edit Alarm: Pengguna mengatur jam, menit, serta jenis puzzle (Matematika Dasar atau Pengetahuan Umum).
3.      Layar Dering (Active Alarm):
·        Alarm berbunyi.
·        Aplikasi mengambil soal secara lokal (meracik angka matematika atau mengambil teks dari Room Database).
·        Jawaban Benar: Alarm mati, layar kembali ke Beranda.
·        Jawaban Salah: Alarm tetap berbunyi, layar memberikan pesan error.
4. Pemenuhan Kriteria Teknis (5 Materi Jetpack Compose)
   Karena aplikasi berjalan offline (tanpa Networking & API), proyek ini menerapkan arsitektur MVVM untuk mengatur aliran data lokal. Berikut 5 materi yang diimplementasikan:
1. UI & Layout Dasar
   ·        Penerapan: Menggunakan Column untuk menyusun form vertikal, Row untuk mengatur tata letak waktu dan tombol ON/OFF, serta Box untuk meletakkan elemen di tengah layar saat puzzle muncul.
   ·        Modifier: Penggunaan Modifier.fillMaxSize(), padding, dan weight untuk antarmuka yang responsif.
2. Material Design 3 (M3)
   ·        Penerapan: Menggunakan Scaffold sebagai kerangka utama. Menampilkan setiap alarm dalam Card dengan elevasi M3. Menggunakan OutlinedTextField untuk input jawaban, serta Button untuk opsi dengan warna bawaan Material 3.
3. State Management & UDF (Unidirectional Data Flow)
   ·        Penerapan:
   ·        Menggunakan remember dan mutableStateOf untuk mencatat ketikan jawaban pengguna.
   ·        Manual Navigation: Perpindahan layar diatur sepenuhnya oleh State (Contoh: var currentScreen by remember { mutableStateOf("HOME") }). Jika state berubah menjadi "PUZZLE", UI merender tampilan soal.
4. Lazy Layouts
   ·        Penerapan: Menggunakan komponen LazyColumn di layar Beranda untuk merender daftar alarm secara efisien dari lokal database, lengkap dengan key parameter (menggunakan ID alarm) agar urutan data stabil saat dihapus/diubah.
5. Arsitektur Aplikasi (MVVM)
   ·        Penerapan: Memisahkan UI dan Logika. Menggunakan ViewModel (misal: PuzzleViewModel) untuk mengambil data soal dari database lokal dan memvalidasi jawaban.
   ·        ViewModel memancarkan UiState (contoh: Loading, QuestionReady, Correct, Wrong) yang kemudian direspons oleh UI Jetpack Compose.
5. Pembagian Tugas Tim (Job Description)
   Beban kerja dibagi menjadi tim Frontend UI (fokus pada tampilan Compose) dan tim Local Backend (fokus pada data, database, dan logika di dalam Kotlin).
   Tim Frontend (UI & Compose)
   Frontend 1 (UI, Layout, & Sistem Layar):
   ·        Mendesain antarmuka menggunakan Material Design 3 (M3) (Card, Scaffold, TextField, Button).
   ·        Menyusun struktur UI & Layout Dasar (Column, Row, Modifier).
   ·        Membangun daftar alarm di Beranda menggunakan Lazy Layouts (LazyColumn).
   ·        Membuat sistem navigasi layar manual berbasis State Management.
   Frontend 2 (State UI & Integrasi Alarm):
   ·        Mengatur State Management & UDF di level UI untuk menampung input form pengguna.
   ·        Menyambungkan UI Jetpack Compose dengan ViewModel yang dibuat oleh tim Local Backend (mengamati UiState).
   ·        Mengintegrasikan sistem Android (AlarmManager dan WakeLock) agar aplikasi dapat menyala dan membunyikan suara tepat waktu.
   Tim Local Backend (Data & MVVM Logic)
   Backend 1 (Database Architect - Room SQLite):
   ·        Mengimplementasikan Room Database (database bawaan Android).
   ·        Membuat Entity (tabel) dan DAO (kueri) untuk menyimpan jadwal alarm pengguna.
   ·        Memasukkan (pre-populate) bank soal pengetahuan umum dasar ke dalam database SQLite saat aplikasi pertama kali diinstal.
   Backend 2 (Puzzle Engine & ViewModel):
   ·        Mengimplementasikan Arsitektur Aplikasi (MVVM) dengan membuat kelas ViewModel.
   ·        Membuat algoritma Math Generator lokal (meracik soal matematika dinamis seperti angka A + angka B).
   ·        Membangun logika di dalam ViewModel untuk mengecek kebenaran jawaban pengguna, lalu meng-update UiState agar UI mematikan alarm atau memberikan pesan salah.
6. Spesifikasi Data Lokal (Kontrak Data Internal)
   Sebagai ganti API, tim Local Backend akan menyediakan Data Class / Entity berikut di dalam Room Database yang nantinya dipanggil oleh ViewModel:
   @Entity(tableName = "trivia_questions")
   data class Question(
   @PrimaryKey(autoGenerate = true) val id: Int = 0,
   val questionText: String, 	// "Apa ibu kota Australia?"
   val optionA: String,      	// "Sydney"
   val optionB: String,      	// "Canberra"
   val optionC: String,      	// "Perth"
   val correctAnswer: String 	// "Canberra"
   )
   (Tim Frontend hanya perlu membaca data ini melalui ViewModel dan menampilkannya di layar saat alarm berbunyi).


