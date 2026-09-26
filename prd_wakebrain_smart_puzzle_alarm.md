# Product Requirements Document (PRD)

| Metadata | Keterangan |
| :--- | :--- |
| **Product Name** | WakeBrain (Smart Puzzle Alarm) |
| **Document Version** | 1.0 |
| **Date** | September 22, 2026 |
| **Status** | Draft |
| **Target Platform** | Android (Native - Jetpack Compose) |

---

## Daftar Isi
1. [Executive Summary](#1-executive-summary)
2. [Product Vision & Goals](#2-product-vision--goals)
3. [Scope & Core Features](#3-scope--core-features)
4. [Technical Requirements & Engineering Standards](#4-technical-requirements--engineering-standards)
5. [User Flow & State Resolution](#5-user-flow--state-resolution)
6. [Data Model](#6-data-model)
7. [Release Phases & Roadmap](#7-release-phases--roadmap)

---

## 1. Executive Summary

**WakeBrain** adalah aplikasi alarm mobile Android yang dirancang khusus untuk pengguna dengan kesulitan bangun pagi (*heavy sleepers*) dan individu yang memiliki kecenderungan mematikan alarm secara tidak sadar (*chronic oversleeping*). 

Berbeda dengan aplikasi jam weker standar yang dapat dimatikan atau ditunda (*snooze*) secara otomatis melalui satu ketukan refleks, WakeBrain mewajibkan pengguna mencapai tingkat kewaspadaan kognitif tertentu sebelum nada alarm dapat dimatikan. Mekanisme ini diwujudkan dengan mengharuskan pengguna menyelesaikan persamaan matematika atau menjawab kuis pilihan ganda secara tepat.

---

## 2. Product Vision & Goals

### 2.1. Vision
Membantu pengguna memulai hari tepat waktu dengan kondisi mental yang terjaga penuh melalui stimulasi kognitif interaktif tepat saat terbangun.

### 2.2. Business & Product Goals
* **Mengurangi Tingkat Snooze:** Menurunkan frekuensi penundaan alarm (*snooze rate*) rata-rata pengguna hingga setidaknya **80%**.
* **Stabilitas & Performa Kelas Industri:** Menghadirkan performa aplikasi yang responsif, *crash-free rate* $\ge 99{,}5\%$, dan transisi animasi layar 60–120 FPS dengan standar pengembangan modern Android.
* **Retensi Pengguna:** Membangun kebiasaan bangun pagi yang konsisten bagi pengguna aktif harian (*DAU*).

### 2.3. Target Audience
* **Mahasiswa & Pelajar:** Individu dengan jadwal kuliah pagi yang padat dan rentan begadang.
* **Profesional Kerja:** Karyawan yang membutuhkan disiplin waktu ketat dan persiapan mental sebelum bekerja.
* **Chronic Oversleepers:** Pengguna yang kebal terhadap dering jam weker konvensional.

---

## 3. Scope & Core Features

```
                            ┌───────────────────────────────────┐
                            │      WakeBrain Core Features      │
                            └─────────────────┬─────────────────┘
                                              │
                    ┌─────────────────────────┴─────────────────────────┐
                    ▼                                                   ▼
       ┌───────────────────────────┐                       ┌───────────────────────────┐
       │   3.1. Alarm Management   │                       │  3.2. Cognitive Challenge │
       ├───────────────────────────┤                       ├───────────────────────────┤
       │ • Full CRUD Operations    │                       │ • Math Equations          │
       │ • Quick Active/Inactive   │                       │ • Multiple-Choice Trivia  │
       │ • Weekly Repeat Days      │                       │ • 3 Difficulty Levels     │
       │ • Label & Tone Selection  │                       │ • Strict Anti-Cheat Lock  │
       └───────────────────────────┘                       └───────────────────────────┘
```

### 3.1. Alarm Management
* **Operasi CRUD Lengkap:** Pengguna dapat membuat (*create*), melihat (*read*), menyunting (*update*), dan menghapus (*delete*) banyak alarm sekaligus.
* **Toggle Status Cepat:** Pengguna dapat mengaktifkan atau menonaktifkan alarm langsung dari halaman utama (*Home Screen*).
* **Alarm Berulang (*Recurring Days*):** Pengguna dapat menentukan hari berulang tertentu (Senin sampai Minggu).

### 3.2. Cognitive Challenges (The "Wake Up" Mechanism)
* **Kategori Tantangan:**
  1. **Matematika Mental (*Math Equations*):** Soal perhitungan angka (contoh: $12 + 45 = ?$, $7 \times 8 - 14 = ?$).
  2. **Kuis Pengetahuan Umum (*Multiple-Choice Questions*):** Pertanyaan logika dan pengetahuan umum dengan 4 opsi pilihan jawaban.
* **Tingkat Kesulitan (*Difficulty Levels*):**
  * `EASY`: Operasi matematika 1 tingkat atau kuis fakta umum dasar.
  * `MEDIUM`: Operasi dua langkah (perkalian/penjumlahan kombinasi).
  * `HARD`: Soal aljabar dasar cepat atau kuis dengan batas waktu reaksi ketat.
* **Mekanisme Anti-Curang (*Anti-Cheat System*):**
  * Layar alarm aktif bersifat *lock-task* (mengunci tombol navigasi *Back*, gestur *Home*, serta memblokir interaksi notifikasi).
  * Satu-satunya syarat terminasi audio dan getaran alarm adalah verifikasi jawaban yang benar.

---

## 4. Technical Requirements & Engineering Standards

Aplikasi wajib dibangun secara *native* pada platform Android dengan ekosistem **Kotlin + Jetpack Compose**, mengacu pada kaidah-kaidah arsitektur modern:

```
┌────────────────────────────────────────────────────────────────────────┐
│ UI Layer                                                               │
│ • Jetpack Compose (Column, Row, Box, Modifiers)                        │
│ • Material Design 3 (Dynamic Colors, Typography Scale, Scaffold)       │
│ • Lazy Layouts (LazyColumn dengan Identity Tracking Key)               │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Unidirectional Data Flow (UDF)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ ViewModel & State Layer                                                │
│ • StateFlow & UiState (Sealed Interface: Loading, Success, Error)       │
│ • State Hoisting: Stateless UI Components & Stateful Containers        │
│ • Configuration Change Survival: rememberSaveable & ViewModels         │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Repository Pattern
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│ Data & System Layer                                                    │
│ • Local Database: Room ORM (SQLite)                                    │
│ • Type-Safe Navigation Compose (Kotlinx Serialization)                 │
│ • System Triggers: Android AlarmManager & Foreground Service WakeLock  │
└────────────────────────────────────────────────────────────────────────┘
```

### 4.1. UI & Basic Layouts
* **Pondasi Tata Letak:** Pemanfaatan hierarki layout fundamental:
  * `Column`: Untuk formulir vertikal pembuatan/pengeditan alarm (*Add/Edit Screen*).
  * `Row`: Untuk komponen item daftar alarm horizontal (informasi jam di kiri, switch toggle di kanan).
  * `Box`: Untuk menumpuk elemen visual (*overlay/stacking*) pada layar *Active Alarm Screen* di atas wallpaper layar kunci.
* **Modifier Best Practices:** Pengendalian ukuran (`fillMaxWidth`, `size`), padding bertingkat, serta *alignment* silang untuk menjamin konsistensi visual di berbagai dimensi layar.

### 4.2. Material Design 3 (M3)
* **Kerangka Induk:** Penggunaan `Scaffold` untuk mengelola area aman sistem (*edge-to-edge*), `TopAppBar`, dan penempatan `FloatingActionButton (FAB)`.
* **Sistem Tema Adaptif:** Integrasi *Dynamic Color* (`dynamicLightColorScheme` / `dynamicDarkColorScheme`) serta hierarki teks 5 skala M3 (`Display`, `Headline`, `Title`, `Body`, `Label`).
* **Komponen Interaktif M3:**
  * `OutlinedCard` / `ElevatedCard` sebagai wadah kartu alarm individual.
  * `OutlinedTextField` untuk input angka/jawaban persamaan matematika.
  * `Button` (Filled) untuk aksi utama dan `OutlinedButton` untuk opsi pilihan ganda.

### 4.3. State Management & Unidirectional Data Flow (UDF)
* **Reaktivitas Lokal:** Penggunaan `remember` dan `rememberSaveable` untuk memastikan input teks sementara tidak musnah saat orientasi layar ponsel diputar.
* **Prinsip State Hoisting:** Seluruh komponen UI penampil kuis (*puzzle composable*) harus bersifat **Stateless**, hanya menerima data melalui parameter nilai (*State Down*) dan mengalirkan aksi pengguna ke atas melalui callback lambda (*Event Up*).

### 4.4. Lazy Layouts
* **Efisiensi Memori:** Layar utama menggunakan `LazyColumn` untuk merender daftar alarm secara *on-demand*.
* **Identity Tracking:** Wajib menyertakan parameter `key = { alarm.id }` pada fungsi `items()` guna mengeliminasi rendering redundan saat item diaktifkan, dimatikan, atau dihapus.

### 4.5. Arsitektur Aplikasi (MVVM)
* **Separation of Concerns:** Pemisahan tegas antara UI (*Presentation*), logika bisnis (*ViewModel*), dan persistensi data (*Repository*).
* **Pemodelan UiState:** Status UI dimodelkan menggunakan `sealed interface UiState` yang mencakup state: `Loading`, `Success<T>`, `Error`, dan `Active`.

### 4.6. Type-Safe Navigation Compose
* **Routing Bebas Bug:** Menggunakan Navigation Compose modern berbasis **Kotlin Serialization** (`@Serializable`).
* **Tiga Rute Utama:**
  1. `HomeRoute`: Rute beranda.
  2. `AddEditAlarmRoute(val alarmId: Int? = null)`: Menerima argumen opsional untuk mode edit.
  3. `ActiveAlarmRoute(val difficulty: String, val type: String)`: Menerima parameter konfigurasi tantangan yang harus dipecahkan.

---

## 5. User Flow & State Resolution

### 5.1. Alur Interaksi Pengguna

```
[ Buka Aplikasi ] ──────────> ( Home Screen: List Alarm )
                                    │
                                    ├───> [ Tekan Tombol FAB ]
                                    │            │
                                    │            ▼
                                    │     ( Layar Add/Edit Alarm )
                                    │     • Pilih Waktu & Hari
                                    │     • Tentukan Tantangan & Tingkat Kesulitan
                                    │            │
                                    │     [ Simpan Alarm ]
                                    │            │
                                    ◄────────────┘
                                    │
                       ( Waktu Alarm Tiba di Background )
                                    │
                                    ▼
                         [ ActiveAlarmRoute ]
                         • Layar menyala & suara alarm berbunyi
                         • Tampilan tantangan kognitif muncul
                                    │
                     ┌──────────────┴──────────────┐
                     ▼                             ▼
              [ Jawaban Salah ]             [ Jawaban Benar ]
              • Layar flash merah           • Suara alarm berhenti
              • Getaran & suara lanjut      • Muncul state sukses
              • Generate soal baru                 │
                     │                             ▼
                     └─────────────────────> ( Kembali ke Home )
```

### 5.2. State Resolution Matrix
| Status | Respon UI | Audio / Haptic | Aksi Sistem |
| :--- | :--- | :--- | :--- |
| **Alarm Triggered** | Layar penuh *ActiveAlarmRoute* aktif seketika. | Nada alarm volume maksimal + getaran konstan. | Memperoleh `WakeLock` & mengaktifkan status layar penuh. |
| **Jawaban Salah** | Kotak teks bergetar, indikator visual merah, pesan error tampil. | Audio terus berputar tanpa henti. | Soal lama diganti atau diacak ulang. |
| **Jawaban Benar** | Animasi konfeti/centang hijau sukses ditampilkan. | Audio dan motor getar langsung dihentikan. | Menjadwalkan alarm berulang berikutnya jika ada; rute kembali ke `Home`. |

---

## 6. Data Model

### Entitas: `Alarm`
Persistensi database lokal memanfaatkan pustaka Android Room ORM.

```kotlin
@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timeHour: Int,           // 0 - 23
    val timeMinute: Int,         // 0 - 59
    val isActive: Boolean = true,
    val daysOfWeek: List<Int>,   // 1 (Senin) .. 7 (Minggu)
    val puzzleType: PuzzleType,  // MATH, MULTIPLE_CHOICE
    val difficulty: Difficulty   // EASY, MEDIUM, HARD
)
```

| Field Name | Data Type | Batasan / Deskripsi |
| :--- | :--- | :--- |
| `id` | `Integer` | **Primary Key**, Auto-increment, ID unik pelacakan. |
| `timeHour` | `Integer` | Rentang nilai $0 \le \text{timeHour} \le 23$. |
| `timeMinute` | `Integer` | Rentang nilai $0 \le \text{timeMinute} \le 59$. |
| `isActive` | `Boolean` | Penanda status aktif; *default value:* `true`. |
| `daysOfWeek` | `List<Int>` | Hari perulangan alarm: `1` (Senin) hingga `7` (Minggu). |
| `puzzleType` | `Enum / String` | Tipe tantangan: `MATH`, `MULTIPLE_CHOICE`. |
| `difficulty` | `Enum / String` | Tingkat kerumitan soal: `EASY`, `MEDIUM`, `HARD`. |

---

## 7. Release Phases & Roadmap

```
  Phase 1 ──> Phase 2 ──> Phase 3 ──> Phase 4 ──> Phase 5
(Foundation)  (UI & DB)  (Puzzle Eng) (System In)   (QA)
```

| Fase | Target Milestone | Deliverables Utama |
| :--- | :--- | :--- |
| **Phase 1: Foundation** | Fondasi Arsitektur & Navigasi | Setup *build configuration*, tema Material Design 3, kerangka MVVM, dan konfigurasi rute *Type-Safe Navigation*. |
| **Phase 2: Core UI & Storage** | Presentasi Data & Penyimpanan Lokal | Implementasi `LazyColumn` di Home Screen, formulir Add/Edit dengan `Column`, serta implementasi Room Database (Entity, DAO, Repository). |
| **Phase 3: Puzzle Engine** | Logika Validasi & State Kognitif | Pembuatan `PuzzleViewModel`, generator rumus matematika & trivia, serta perancangan komponen *Stateless UI* berbasis UDF. |
| **Phase 4: System Integration** | Layanan Sistem & Latar Belakang | Integrasi sistem waktu `Android AlarmManager`, registrasi `BroadcastReceiver`, izin `WakeLock`, serta implementasi *Foreground Service*. |
| **Phase 5: QA & Polish** | Uji Keandalan & Rilis Versi Beta | Pengujian transisi rotasi (*Configuration Change*), uji konsumsi daya baterai, perbaikan bug UI/UX, dan perilisan berkas APK/AAB beta. |