
# Pengenalan Compose & Layout Dasar

**Mata Kuliah:** Pemrograman Mobile (IF21507 / 2 SKS) — Pertemuan 2  
**Program Studi:** Informatika, Fakultas Teknik — Universitas Jenderal Soedirman  
**Dosen Pengampu:** Azis Amirulbahar, S.Pd., M.T.I.  

---

## Daftar Isi
1. [Android App Fundamentals](#1-android-app-fundamentals)
2. [Konfigurasi Project Baru di Android Studio](#2-konfigurasi-project-baru-di-android-studio)
3. [Komponen Utama & Mekanisme Intent](#3-komponen-utama--mekanisme-intent)
4. [File Manifest dan App Resources](#4-file-manifest-dan-app-resources)
5. [Activity & Activity Lifecycle](#5-activity--activity-lifecycle)
6. [Paradigma UI: Imperatif vs Deklaratif](#6-paradigma-ui-imperatif-vs-deklaratif)
7. [Anotasi @Composable & Mekanisme Recomposition](#7-anotasi-composable--mekanisme-recomposition)
8. [Layout Composables Dasar: Column, Row, dan Box](#8-layout-composables-dasar-column-row-dan-box)
9. [Arrangement vs Alignment](#9-arrangement-vs-alignment)
10. [Menguasai Modifiers: Urutan & Perilaku](#10-menguasai-modifiers-urutan--perilaku)
11. [Kesimpulan 5 Konsep Kunci](#11-kesimpulan-5-konsep-kunci)
12. [Rencana Pertemuan Berikutnya](#12-rencana-pertemuan-berikutnya)

---

## 1. Android App Fundamentals

Aplikasi Android dibangun dengan bahasa Kotlin, Java, atau C++. Kode sumber beserta aset dikompilasi menjadi paket instalasi:
* **APK (*Android Package*)** atau
* **AAB (*Android App Bundle*)**

Setiap aplikasi berjalan dalam lingkungan terisolasi miliknya sendiri (*security sandbox*). Sistem operasi Android menerapkan prinsip hak akses minimal (*least privilege*), sehingga setiap aplikasi hanya memiliki akses ke komponen yang dibutuhkan dan diizinkan secara eksplisit.

---

## 2. Konfigurasi Project Baru di Android Studio

Saat membuat proyek baru menggunakan template **Empty Activity** (Jetpack Compose), terdapat beberapa pengaturan parameter penting:

| Parameter | Deskripsi & Aturan |
| :--- | :--- |
| **Name** | Nama aplikasi yang akan tampil langsung di layar pengguna (*home screen*, *app drawer*, *settings*). |
| **Package Name** | Identitas unik aplikasi di ekosistem Android dan Google Play Store. Ditulis dengan format domain terbalik (*reverse domain name notation*), contoh: `com.unsoed.myapplication`. |
| **Save Location** | Direktori penyimpanan lokal di komputer. **Catatan:** Jangan memuat spasi/whitespace karena dapat memicu masalah pada *toolchain* NDK/C++. |
| **Minimum SDK** | Versi Android terendah yang diizinkan menginstal aplikasi (misal: API 24 Android 7.0 Nougat mencakup $\approx 99{,}2\%$ perangkat aktif). |
| **Build Configuration Language** | Bahasa skrip sistem otomasi *build* Gradle. Standar modern menggunakan **Kotlin DSL (`build.gradle.kts`)**. |

---

## 3. Komponen Utama & Mekanisme Intent

### 4 Komponen Utama Aplikasi Android
Aplikasi Android tidak memiliki fungsi `main()` tunggal seperti program desktop/CLI. Sistem mengaktifkan komponen secara independen sesuai kebutuhan:
1. **Activity:** Komponen antarmuka pengguna (UI) yang merepresentasikan satu layar interaksi.
2. **Service:** Komponen yang berjalan di latar belakang (*background*) tanpa antarmuka langsung untuk tugas berdurasi panjang.
3. **Broadcast Receiver:** Komponen penerima pesan atau siaran (*announcement*) sistem secara global (contoh: baterai lemah, notifikasi jaringan).
4. **Content Provider:** Pengelola dan penyedia akses data terstruktur antaraplikasi.

### Mengaktifkan Komponen dengan Intent
**Intent** adalah objek pesan asinkron yang digunakan untuk memicu tindakan dan memfasilitasi komunikasi antar-komponen:

#### A. Explicit Intent
Menentukan komponen target secara eksplisit (biasanya dalam aplikasi yang sama).
```kotlin
// Explicit Intent: membuka DetailActivity
val intent = Intent(this, DetailActivity::class.java).apply {
    putExtra("id", itemId)
}
startActivity(intent)
```

#### B. Implicit Intent
Mendeklarasikan aksi umum tanpa menyebut nama kelas tujuan secara spesifik. Sistem Android akan menyaring aplikasi lain yang sanggup melayani aksi tersebut.
```kotlin
// Implicit Intent: membuka browser web
val intent = Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse("https://unsoed.ac.id")
}
startActivity(intent)
```

**Alur Kerja Intent:**
$$\text{Komponen Sumber} \xrightarrow{\quad\text{Kirim Intent}\quad} \text{Sistem Android (OS)} \xrightarrow{\quad\text{Resolusi & Filter}\quad} \text{Komponen Target Diaktifkan}$$

---

## 4. File Manifest dan App Resources

### AndroidManifest.xml
Merupakan "buku tamu" dan konfigurasi identitas aplikasi. OS Android wajib membaca manifest sebelum menjalankan komponen apa pun. Manifest mendefinisikan:
* Komponen aplikasi (Activity, Service, Receiver).
* Izin (*permissions*) yang dibutuhkan (Internet, Kamera, Lokasi).
* Filter intent (*intent filters*) agar Activity dapat merespons aksi global:

```xml
<activity 
    android:name=".ShareActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.SEND" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:mimeType="image/*" />
    </intent-filter>
</activity>
```
> **Catatan:** Activity tanpa deklarasi `<intent-filter>` hanya bisa dipanggil secara *explicit* dari dalam aplikasi yang sama.

### App Resources (`res/`)
Praktik terbaik arsitektur Android adalah memisahkan logika kode Kotlin dari aset statis:

```text
res/
├── drawable/       # Gambar grafis bitmap / vektor XML
├── layout/         # Berkas XML layout (pendekatan legacy View)
├── values/         # Nilai skalar yang dapat diakses via R.string, R.color, dll.
│   ├── strings.xml # Teks dan pelabelan (mendukung multi-bahasa / lokalisasi)
│   ├── colors.xml  # Definisi palet warna
│   └── dimens.xml  # Ukuran padding dan dimensi layar
├── mipmap/         # Ikon launcher berbagai resolusi perangkat
└── raw/            # Berkas media mentah (audio, video, json statis)
```

---

## 5. Activity & Activity Lifecycle

### Definisi Activity
* Komponen inti yang menampilkan layar antarmuka pengguna (UI).
* Berfungsi sebagai gerbang masuk (*entry point*) interaksi pengguna.
* Wajib didaftarkan di dalam `AndroidManifest.xml` menggunakan tag `<activity>`.

### Mengapa Lifecycle Sangat Penting?
1. **Memory Leak:** Gagal melepas alokasi resource di `onStop()` / `onDestroy()` menyebabkan pemborosan RAM.
2. **Data Loss:** Tidak menyimpan status sementara di `onPause()` membuat data input pengguna hilang saat orientasi layar berganti.
3. **Crash:** Mengakses resource atau konteks yang telah dihancurkan oleh sistem memicu crash aplikasi.

### 6 Callback Utama Siklus Hidup Activity
```text
            [ Activity Diluncurkan ]
                       │
                       ▼
                 onCreate()      <--- Inisialisasi awal UI & ViewModel (hanya 1x)
                       │
                       ▼
                  onStart()      <--- Activity mulai tampak di layar
                       │
                       ▼
                 onResume()      <--- Berada di foreground, aktif berinteraksi
                       │
       ┌───────────────┴───────────────┐
       ▼                               ▼
  [ Layar Tertimpa Dialog ]     [ Berpindah Layar / Tombol Home ]
       │                               │
    onPause()                       onStop()
       │                               │
       ▼                               ▼
  [ Kembali Fokus ]             [ Sistem Butuh RAM / Selesai ]
  -> onResume()                        │
                                onDestroy()
                                       │
                                       ▼
                             [ Activity Dihancurkan ]
```

### Implementasi Lifecycle dalam Kode
```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Mengaktifkan tampilan layar penuh edge-to-edge
        setContent {
            MyApp() // Komponen Jetpack Compose
        }
    }

    override fun onStart() {
        super.onStart()
        // Activity mulai terlihat oleh pengguna
    }

    override fun onResume() {
        super.onResume()
        // Memulai animasi, koneksi sensor, atau kamera
    }

    override fun onPause() {
        super.onPause()
        // Menghentikan animasi, menyimpan draf data sementara
    }

    override fun onStop() {
        super.onStop()
        // Melepas resource berat yang tidak diperlukan saat layar tertutup
    }

    override fun onDestroy() {
        super.onDestroy()
        // Pembersihan akhir sebelum instance dihapus dari RAM
    }
}
```

---

## 6. Paradigma UI: Imperatif vs Deklaratif

| Aspek | Legacy UI (Imperatif - XML) | Modern UI (Deklaratif - Jetpack Compose) |
| :--- | :--- | :--- |
| **Pemisahan File** | Desain di XML terpisah dari logika di Kotlin/Java | UI dideskripsikan langsung di kode Kotlin murni |
| **Manipulasi View** | Manual via `findViewById<View>()` atau ViewBinding | Otomatis dirender ulang melalui mekanisme *Recomposition* |
| **Konsistensi State** | Rentan *out-of-sync* antara state data dan visual | Satu sumber kebenaran (*single source of truth*) |
| **Prinsip Dasar** | Menginstruksikan **bagaimana** mengubah elemen | Mendeskripsikan **seperti apa** bentuk tampilan untuk setiap status |

Formulasi UI modern:
$$\text{UI} = f(\text{State})$$

```kotlin
// Contoh Pendekatan Imperatif (XML)
val tvName = findViewById<TextView>(R.id.tvName)
tvName.text = "Halo, Mahasiswa!"
tvName.visibility = View.VISIBLE

// Contoh Pendekatan Deklaratif (Jetpack Compose)
@Composable
fun Greeting(name: String) {
    Text(text = "Halo, $name!")
}
```

---

## 7. Anotasi @Composable & Mekanisme Recomposition

### Apa itu Anotasi?
Anotasi adalah metadata yang diawali dengan simbol `@`. Anotasi memberi instruksi khusus kepada *compiler* tanpa memerlukan kode *boilerplate* tambahan.

* `@Composable`: Memberitahu compiler bahwa fungsi Kotlin tersebut bertugas menyusun hierarki antarmuka grafis.
* `@Preview`: Menampilkan pratinjau komponen secara langsung di Android Studio tanpa perlu menjalankan emulator/perangkat fisik.

### Anatomi Fungsi Composable
1. **Diawali `@Composable`**: Wajib disematkan agar fungsi dapat memanggil composable lainnya.
2. **Penamaan PascalCase**: Mengikuti tata cara penamaan tipe data/komponen (misal: `GreetingPreview`, `UserProfileScreen`).
3. **Mengembalikan `Unit`**: Composable mendeskripsikan struktur, bukan mengembalikan objek view ke pemanggil.

```kotlin
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExampleTheme {
        Greeting(name = "Android")
    }
}
```

### Mekanisme Recomposition
$$\text{State Data} \xrightarrow{\quad\text{Input}\quad} \text{@Composable Function} \xrightarrow{\quad\text{Render}\quad} \text{Tampilan Layar (UI)}$$
$$\text{State Berubah} \longrightarrow \textbf{Hanya bagian Composable terkait yang di-eksekusi ulang (Recomposition)}$$

Compose melacak dependensi state secara otomatis sehingga tidak membuang daya komputasi untuk merender ulang seluruh pohon view.

---

## 8. Layout Composables Dasar: Column, Row, dan Box

Tiga pilar layout ini mencakup $\approx 75\%$ kebutuhan tata letak antarmuka aplikasi Android:

```text
       Column                 Row                     Box
   (Vertikal / Y)       (Horizontal / X)         (Tumpukan / Z)
  ┌──────────────┐    ┌───────────────────┐    ┌─────────────────┐
  │  [ Item 1 ]  │    │ [A]   [B]   [C]   │    │ ┌─────────────┐ │ (Layer 3)
  │  [ Item 2 ]  │    └───────────────────┘    │ │ ┌─────────┐ │ │ (Layer 2)
  │  [ Item 3 ]  │                             │ │ │ Layer 1 │ │ │ (Layer 1)
  └──────────────┘                             │ └─┴─────────┴─┘ │
                                               └─────────────────┘
```

### 1. Column (Sumbu Vertikal - Y)
Menyusun elemen dari atas ke bawah.
```kotlin
Column(
    modifier = Modifier.fillMaxWidth()
) {
    Text("Item 1")
    Text("Item 2")
    Text("Item 3")
}
```

### 2. Row (Sumbu Horizontal - X)
Menyusun elemen berdampingan dari kiri ke kanan.
```kotlin
Row(
    modifier = Modifier.fillMaxWidth()
) {
    Text("Kiri")
    Spacer(Modifier.weight(1f)) // Mendorong teks kanan ke tepi layar
    Text("Kanan")
}
```

### 3. Box (Sumbu Kedalaman - Z / Tumpukan)
Menumpuk elemen secara berlapis (*overlay*). Posisi child diatur dengan ekstensi `Modifier.align()`.
```kotlin
Box(
    modifier = Modifier.size(100.dp)
) {
    Image(
        painter = painterResource(id = R.drawable.sample_image),
        contentDescription = null
    )
    Text(
        text = "Overlay",
        modifier = Modifier.align(Alignment.Center)
    )
}
```

---

## 9. Arrangement vs Alignment

Kedua parameter ini mengatur tata letak pada sumbu yang berbeda:

* **Arrangement (Main Axis / Sumbu Utama):** Mengatur distribusi ruang kosong dan posisi elemen sepanjang alur layout.
  * Pada `Column`: Sumbu Utama adalah **Vertikal**.
  * Pada `Row`: Sumbu Utama adalah **Horizontal**.
* **Alignment (Cross Axis / Sumbu Silang):** Mengatur penempatan elemen pada sumbu yang berlawanan arah.
  * Pada `Column`: Sumbu Silang adalah **Horizontal**.
  * Pada `Row`: Sumbu Silang adalah **Vertikal**.

### Opsi Nilai yang Sering Digunakan
* **Arrangement Options:**
  * `Arrangement.Center`: Seluruh item dikumpulkan di tengah sumbu utama.
  * `Arrangement.SpaceBetween`: Jarak antar-item dibagi merata, tanpa margin di ujung terluar.
  * `Arrangement.SpaceAround`: Jarak antar-item merata, ujung terluar mendapat setengah porsi jarak.
  * `Arrangement.SpaceEvenly`: Jarak antar-item dan ujung terluar terbagi sama persis.
* **Alignment Options:**
  * Di dalam `Column`: `Alignment.Start`, `Alignment.CenterHorizontally`, `Alignment.End`.
  * Di dalam `Row`: `Alignment.Top`, `Alignment.CenterVertically`, `Alignment.Bottom`.

---

## 10. Menguasai Modifiers: Urutan & Perilaku

**Modifier** adalah kumpulan instruksi terantai (*chained methods*) yang mengatur:
* Ukuran, tata letak, dan padding.
* Penampilan visual (latar belakang, bentuk sudut, border, bayangan).
* Interaksi pengguna (klik, scroll, gesture).

### Urutan Penulisan Bersifat Kritis (*Order Matters*)

#### Kasus A: Padding DILUAR Area Warna (Menghasilkan Efek Margin)
```kotlin
Modifier
    .padding(16.dp)            // 1. Berikan ruang kosong di sekeliling
    .background(Color.Red)     // 2. Warnai area yang tersisa
```
*Hasil:* Ruang transparan selebar `16.dp` berada di luar latar merah.

#### Kasus B: Padding DIDALAM Area Warna (Menghasilkan Efek Padding Internal)
```kotlin
Modifier
    .background(Color.Red)     // 1. Warnai seluruh area komponen
    .padding(16.dp)            // 2. Dorong konten teks/gambar ke dalam
```
*Hasil:* Latar belakang merah membentang penuh, sedangkan konten di dalamnya memiliki jarak tepi `16.dp`.

---

## 11. Kesimpulan 5 Konsep Kunci

1. **Paradigma Deklaratif ($UI = f(\text{State})$):** State berubah, tampilan otomatis menyesuaikan diri tanpa perlu manipulasi referensi view secara langsung.
2. **@Composable & Recomposition:** Fungsi UI cerdas yang mengeksekusi ulang hanya bagian komponen yang nilainya terpengaruh perubahan data.
3. **Pilar Layout (Column, Row, Box):** Menangani susunan vertikal, susunan horizontal, dan tumpukan lapis Z-axis secara efisien.
4. **Arrangement vs Alignment:** Arrangement berfokus pada sebaran di *main axis*, sedangkan Alignment menentukan posisi di *cross axis*.
5. **Modifier Chaining & Urutan:** Perilaku visual bergantung penuh pada urutan panggilan fungsi modifier.

---

## 12. Rencana Pertemuan Berikutnya

* **Topik Utama:** *State Management & `remember`* — Mengelola state lokal, event handling, dan membangun reaktivitas antarmuka Jetpack Compose.
* **Topik Desain:** *Jetpack Compose — Penerapan Material Design 3*.
* **Repositori Contoh Kasus Google:** [https://github.com/android/compose-samples.git](https://github.com/android/compose-samples.git?utm_source=gemini)
* **Dokumentasi Resmi Android UI:** [https://developer.android.com/design/ui/mobile](https://developer.android.com/design/ui/mobile?utm_source=gemini)