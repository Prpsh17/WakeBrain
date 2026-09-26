# Material Design 3 dan Form Components

**Mata Kuliah:** Pemrograman Mobile (IF21507 / 2 SKS) — Pertemuan ke-3  
**Program Studi:** Informatika, Fakultas Teknik — Universitas Jenderal Soedirman  
**Dosen Pengampu:** Azis Amirulbahar, S.Pd., M.T.I.  
**Durasi & Bobot:** 100 Menit | 2 SKS  
**Prasyarat:** Pemahaman dasar sintaks Kotlin, dasar Jetpack Compose, serta Layout Composables (`Column`, `Row`, `Box`).

---

## Daftar Isi

1. [Review: Activity Lifecycle pada Android](#1-review-activity-lifecycle-pada-android)
2. [Anatomi Composable & Alur Recomposition](#2-anatomi-composable--alur-recomposition)
3. [Satuan Dimensi Android: dp vs sp](#3-satuan-dimensi-android-dp-vs-sp)
4. [Pengenalan Material Design 3 (M3)](#4-pengenalan-material-design-3-m3)
5. [Sistem Tema M3: Color Roles & Dynamic Color](#5-sistem-tema-m3-color-roles--dynamic-color)
6. [Sistem Tema M3: Tipografi 5 Skala Hierarki](#6-sistem-tema-m3-tipografi-5-skala-hierarki)
7. [Komponen Interaktif M3: Button Variants](#7-komponen-interaktif-m3-button-variants)
8. [Komponen Input Form: TextField Handling](#8-komponen-input-form-textfield-handling)
9. [Komponen Kontainer: Card Variants](#9-komponen-kontainer-card-variants)
10. [Studi Kasus Lengkap: Form Login Fungsional](#10-studi-kasus-lengkap-form-login-fungsional)
11. [Ringkasan Materi](#11-ringkasan-materi)
12. [Pertemuan Berikutnya](#12-pertemuan-berikutnya)
13. [Referensi & Sumber Belajar](#13-referensi--sumber-belajar)

---

## 1. Review: Activity Lifecycle pada Android

### Definisi Lifecycle
*Activity Lifecycle* adalah tahapan siklus hidup yang dialami oleh komponen `Activity` sejak pertama kali diciptakan, ditampilkan di layar, berpindah ke latar belakang (*background*), hingga dihancurkan oleh sistem operasi.

```
            [ Activity Diluncurkan ]
                       │
                       ▼
                 onCreate()      <--- Menyiapkan layout & data awal (1x)
                       │
                       ▼
                  onStart()      <--- Activity mulai tampak di layar
                       │
                       ▼
                 onResume()      <--- Foreground, siap interaksi pengguna
                       │
       ┌───────────────┴───────────────┐
       ▼                               ▼
  [ Muncul Dialog / Tertimpa Sebagian ] [ Layar Tertutup Penuh / Tekan Home ]
       │                               │
    onPause()                       onStop()
       │                               │
       ▼                               ▼
  [ Kembali ke Layar ]          [ Pengguna Kembali ]    [ Sistem Butuh RAM ]
  -> onResume()                 -> onRestart() -> onStart()   -> Process Dihapus
                                       │
                                onDestroy()
                                       │
                                       ▼
                             [ Activity Dihancurkan ]
```

### 6 Callback Utama & Fungsinya

| Callback | Status Activity | Tindakan yang Harus Dilakukan |
| :--- | :--- | :--- |
| `onCreate()` | Komponen diinisialisasi pertama kali. | Mengatur layout via `setContent {}`, inisialisasi ViewModel, alokasi data awal. |
| `onStart()` | Activity mulai terlihat oleh pengguna. | Mempersiapkan resource tampilan visual. |
| `onResume()` | Berada di posisi paling depan (*foreground*). | Mengaktifkan kamera, sensor, atau memulai pemutaran animasi. |
| `onPause()` | Kehilangan fokus sebagian (misal tertutup pop-up dialog). | Menjeda animasi, menghentikan akses sensor sementara, menyimpan draf data. |
| `onStop()` | Sudah tidak terlihat sama sekali di layar. | Melepas koneksi jaringan berat dan resource yang memakan memori. |
| `onDestroy()` | Komponen dihancurkan sepenuhnya dari memori. | Membersihkan listener, observer, dan thread aktif agar terhindar dari *memory leak*. |

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MyApp() } // Jetpack Compose
    }

    override fun onStart() { super.onStart() }
    override fun onResume() { super.onResume() }
    override fun onPause() { super.onPause() }
    override fun onStop() { super.onStop() }
    override fun onDestroy() { super.onDestroy() }
}
```

---

## 2. Anatomi Composable & Alur Recomposition

### Tiga Pilar Anatomi Composable
1. **Anotasi `@Composable`**: Memberi tahu compiler Android Studio bahwa fungsi ini mendeskripsikan node tampilan UI, bukan fungsi komputasi logika biasa.
2. **Konvensi Penamaan PascalCase**: Nama fungsi diawali huruf kapital (contoh: `GreetingScreen()`, `LoginForm()`) dan mengembalikan nilai `Unit`.
3. **Mekanisme Recomposition Cerdas**: Saat sebuah data atau *state* berubah, Compose **hanya** mengeksekusi ulang fungsi yang membaca state tersebut, bukan merender ulang seluruh layar.

### Alur Kerja Recomposition (5 Tahap)

$$
\text{State Awal } (count = 0) \longrightarrow \text{Aksi / Event Klik} \longrightarrow \text{State Berubah } (count = 1) \longrightarrow \text{Compose Mendeteksi} \longrightarrow \text{Recomposition pada Composable Terkait}
$$

```kotlin
@Composable
fun Counter() {
    // State lokal yang dipertahankan selama recomposition
    var count by remember { mutableStateOf(0) }

    Column {
        // Hanya elemen Text ini yang di-recompose saat count berubah
        Text(text = "Jumlah: $count")
        
        Button(onClick = { count++ }) {
            Text("Tambah")
        }
    }
}
```

* **Keuntungan Recomposition:**
  * **Sinkron:** UI selalu identik dengan kondisi data terbaru (*Single Source of Truth*).
  * **Efisien:** Menghemat baterai dan komputasi CPU karena proses render bersifat selektif.
  * **Reaktif:** Arsitektur kode lebih bersih tanpa manipulasi `view.setText()` secara manual.

---

## 3. Satuan Dimensi Android: dp vs sp

Untuk menjamin layout tetap proporsional di ribuan jenis ukuran dan kepadatan piksel (*density*) perangkat Android, Google membagi satuan ukuran menjadi:

| Parameter | `dp` (*Density-independent Pixels*) | `sp` (*Scale-independent Pixels*) |
| :--- | :--- | :--- |
| **Digunakan Untuk** | Ukuran elemen UI, lebar/tinggi komponen, margin, padding, ikon, gambar. | Khusus penentuan ukuran tipografi / teks (`Text`). |
| **Adaptasi Kepadatan Layar** | **Ya** (Menyesuaikan dpi layar HP). | **Ya** (Menyesuaikan dpi layar HP). |
| **Adaptasi Font Pengguna** | **Tidak** (Ukuran tetap konsisten). | **Ya** (Otomatis membesar/mengecil jika pengguna mengubah *Accessibility Font Size* di pengaturan HP). |

---

## 4. Pengenalan Material Design 3 (M3)

**Material Design 3 (M3)** adalah evolusi sistem desain open-source resmi dari Google yang diperkenalkan sejak Android 12 (2021). M3 mengusung personalisasi mendalam melalui konsep *Material You*, kontras warna yang adaptif, serta komponen UI modern.

* **Dokumentasi Desain Resmi:** [https://m3.material.io](https://m3.material.io)
* **Dokumentasi Komponen Compose:** [https://developer.android.com/develop/ui/compose/components](https://developer.android.com/develop/ui/compose/components)

### Prinsip Pedoman: Do & Don't
* ✅ **Do:** Saat menggunakan tombol toggle (*toggleable buttons*), jaga agar jumlah karakter label tetap seimbang di antara kedua kondisi.
* ❌ **Don't:** Jangan mengubah panjang teks label secara drastis (misal dari "Start" menjadi "Reset back to beginning") karena akan merusak stabilitas layout visual.

---

## 5. Sistem Tema M3: Color Roles & Dynamic Color

### Empat Peran Utama (*Color Roles*)

M3 menggunakan pendekatan warna semantik (*semantic color roles*), di mana setiap warna merepresentasikan fungsi fungsional:

```
┌────────────────────────────────────────────────────────────────────────┐
│ 1. PRIMARY: Identitas & Aksi Utama (FAB, Button Filled, Active State) │
├────────────────────────────────────────────────────────────────────────┤
│ 2. SECONDARY: Fitur Pendukung & Filter (Filter Chips, Switch, Checkbox)│
├────────────────────────────────────────────────────────────────────────┤
│ 3. TERTIARY: Aksen Kontras & Penarik Perhatian (Promo, Notifikasi Badge)│
├────────────────────────────────────────────────────────────────────────┤
│ 4. SURFACE: Wadah / Kanvas Penampung (Card, Dialog, Bottom Sheet)     │
└────────────────────────────────────────────────────────────────────────┘
```

Setiap peran warna utama selalu memiliki pasangan warna kontras berawalan `on-`:
* `primary` berpasangan dengan `onPrimary` (warna teks/ikon di atas warna primary).
* `secondaryContainer` berpasangan dengan `onSecondaryContainer`.
* `surface` berpasangan dengan `onSurface`.
* `error` berpasangan dengan `onError`.

### Aturan Kontras & Layering (Accessibility)
* Pastikan warna teks memenuhi standar keterbacaan (*accessible visual contrast*).
* Jangan memasangkan `primary` dengan `secondaryContainer` sembarangan karena dapat menyebabkan teks tidak terbaca saat pengguna mengaktifkan mode kontras tinggi (*user-controlled contrast*).

### Implementasi Dynamic Color (Android 12+ / API 31+)

*Dynamic Color* mengekstrak palet warna secara otomatis dari gambar wallpaper yang digunakan oleh pengguna:

```kotlin
@Composable
fun MyAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        // Dynamic color aktif pada Android 12 ke atas
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(context)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            dynamicLightColorScheme(context)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme // Skema warna fallback statis
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

> **Tool Rekomendasi:** Gunakan **Material Theme Builder** pada `m3.material.io` untuk mengekspor token `Color.kt` dan `Theme.kt` otomatis berdasarkan warna *brand* Anda.

---

## 6. Sistem Tema M3: Tipografi 5 Skala Hierarki

M3 mendefinisikan 5 kelompok tipografi standar dengan default typeface **Roboto**. Setiap kelompok memiliki varian `Large`, `Medium`, dan `Small`:

| Skala | Ukuran Tipikal | Peruntukan & Konteks Penggunaan |
| :--- | :--- | :--- |
| **Display** | 57sp / 45sp / 36sp | Teks hero, angka raksasa, *splash screen*. |
| **Headline** | 32sp / 28sp / 24sp | Judul utama halaman atau section konten penting. |
| **Title** | 22sp / 16sp / 14sp | Judul kartu (*card header*), sub-bab, judul dialog. |
| **Body** | 16sp / 14sp / 12sp | Teks konten paragraf panjang, artikel, deskripsi produk. |
| **Label** | 14sp / 12sp / 11sp | Teks pada tombol, tab menu navigasi, badge, dan caption. |

### Cara Memanggil Tipografi di Jetpack Compose

```kotlin
// Judul Halaman
Text(
    text = "Selamat Datang",
    style = MaterialTheme.typography.headlineMedium
)

// Paragraf Konten
Text(
    text = "Silakan masukkan detail akun Anda di bawah ini.",
    style = MaterialTheme.typography.bodyLarge
)

// Tombol
Text(
    text = "LOGIN",
    style = MaterialTheme.typography.labelLarge
)
```

> ⚠️ **Aturan Desain:** Hindari menulis ukuran font secara statis seperti `fontSize = 18.sp`. Gunakan selalu referensi dari `MaterialTheme.typography` agar skala teks konsisten di seluruh aplikasi.

---

## 7. Komponen Interaktif M3: Button Variants

M3 menyediakan varian tombol bertingkat untuk memperjelas hierarki tindakan:

| Varian Tombol | Karakteristik Visual | Prioritas Aksi | Contoh Penggunaan |
| :--- | :--- | :--- | :--- |
| **Button (Filled)** | Latar belakang solid warna `primary` | **Tinggi (Utama)** | Tombol Submit, Simpan, Login, Bayar. |
| **ElevatedButton** | Latar belakang `surface` dengan efek bayangan | **Sedang (Alternatif)** | Aksi penting pada latar belakang berpola. |
| **OutlinedButton** | Border tegas tanpa latar belakang | **Sedang (Sekunder)** | Tombol Batal, Kembali, Edit. |
| **TextButton** | Hanya teks tanpa batas border | **Rendah (Tersier)** | Lewati, Lupa Password, Bantuan. |

```kotlin
// 1. Aksi Primer
Button(onClick = { /* login */ }) {
    Text("Login")
}

// 2. Aksi Sekunder
OutlinedButton(onClick = { /* batal */ }) {
    Text("Batal")
}

// 3. Aksi Tersier
TextButton(onClick = { /* lewati */ }) {
    Text("Lewati")
}

// 4. Tombol Nonaktif (Disabled State)
Button(enabled = false, onClick = {}) {
    Text("Kirim")
}
```

> ⚠️ **Prinsip Hierarchy of Action:** Dalam satu tampilan layar, gunakan maksimal **1 Filled Button**. Menempatkan dua tombol filled secara berdampingan akan membingungkan prioritas interaksi pengguna.

---

## 8. Komponen Input Form: TextField Handling

| Fitur / Parameter | `TextField` (Filled) | `OutlinedTextField` |
| :--- | :--- | :--- |
| **Bentuk Visual** | Latar belakang kontras abu-abu dengan garis bawah (*underline*). | Memiliki border garis mengelilingi seluruh area input. |
| **Konteks Terbaik** | Di dalam kartu (*card*) atau permukaan yang sudah memiliki warna latar belakang. | Di atas latar belakang putih/terang yang luas agar batas kolom terlihat tegas. |

### Parameter Kunci Penanganan Form
* `value`: Nilai teks saat ini yang diikatkan ke *state*.
* `onValueChange`: Event lambda yang dipanggil setiap kali pengguna mengetik karakter baru.
* `label`: Judul kolom input yang akan mengecil ke atas saat kolom aktif (*floating label*).
* `isError`: Boolean penanda status validasi kolom (menampilkan border & teks merah).
* `supportingText`: Catatan petunjuk atau pesan kesalahan di bawah kolom input.
* `visualTransformation`: Mengubah tampilan karakter (misal: `PasswordVisualTransformation()` untuk menyamarkan kata sandi menjadi tanda titik).

---

## 9. Komponen Kontainer: Card Variants

Card berfungsi sebagai wadah pengelompokan informasi yang berhubungan:

1. **Card (Filled):** Menggunakan warna latar `surfaceVariant`, tanpa border dan tanpa shadow. Cocok untuk feed berita atau konten informatif santai.
2. **ElevatedCard:** Memiliki efek elevasi/bayangan halus di sekelilingnya. Cocok untuk menonjolkan item khusus di antara daftar reguler.
3. **OutlinedCard:** Memiliki garis tepi tipis (*border*). Sangat ideal untuk menampung formulir input.

---

## 10. Studi Kasus Lengkap: Form Login Fungsional

Penerapan konsep `MaterialTheme`, `Scaffold`, `OutlinedCard`, `OutlinedTextField`, dan pengelolaan state validasi input:

### Struktur Hierarki Komponen (Dari Luar ke Dalam)

$$
\text{MaterialTheme} \longrightarrow \text{Scaffold} \longrightarrow \text{Column (CenterHorizontally)} \longrightarrow \text{OutlinedCard} \longrightarrow \text{Column (Arrangement.spacedBy)}
$$

```kotlin
@Composable
fun LoginScreen() {
    // 1. State Form
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }

    MaterialTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Wadah Formulir
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        // Menjaga jarak antar-komponen seragam tanpa Spacer berulang
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        // Input Email dengan Validasi Sederhana
                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                isEmailError = !it.contains("@")
                            },
                            label = { Text("Email") },
                            isError = isEmailError,
                            supportingText = {
                                if (isEmailError) {
                                    Text("Format email tidak valid")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Input Password dengan Penyamaran Teks
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Aksi Utama (Primer)
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                isEmailError = !email.contains("@")
                            }
                        ) {
                            Text("Login")
                        }

                        // Aksi Sekunder / Navigasi (Tersier)
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { /* Navigasi ke pendaftaran */ }
                        ) {
                            Text("Belum punya akun? Daftar")
                        }
                    }
                }
            }
        }
    }
}
```

### Keputusan Desain pada Studi Kasus
* **Penggunaan `Arrangement.spacedBy(12.dp)`**: Menggantikan kebutuhan menulis `Spacer(modifier = Modifier.height(12.dp))` berkali-kali di antara setiap elemen input, menghasilkan kode yang jauh lebih ringkas.
* **Integrasi `Scaffold`**: Menyediakan padding standar sistem (*safe area insets*) agar tampilan form tidak tertimpa bilah status sistem (*status bar*) atau bilah navigasi bawah.

---

## 11. Ringkasan Materi

1. **Activity Lifecycle:** Pahami momen eksekusi kode pada `onCreate`, `onPause`, dan `onDestroy` untuk mencegah *crash* dan *memory leak*.
2. **Recomposition:** Proses pembaruan UI otomatis yang efisien berkat konsep $UI = f(\text{State})$.
3. **Satuan Dimensi:** Gunakan `dp` untuk dimensi tata letak layout dan `sp` khusus untuk ukuran font teks.
4. **Color & Typography M3:** Manfaatkan *semantic color roles* (Primary, Secondary, Tertiary, Surface) serta skala tipografi bawaan tanpa melakukan *hardcode*.
5. **Hierarki Komponen:** Bedakan penggunaan tombol (Filled vs Outlined vs Text) dan gunakan `Card` sebagai pembungkus kelompok input form.

---

## 12. Pertemuan Berikutnya

* **Topik Utama:** *Jetpack Compose — Lazy Layouts (Membangun List Dinamis dengan LazyColumn & LazyRow)*.
* **Repositori Demo Sumber Kode:** [https://gitlab.com/-/snippets/6050350](https://gitlab.com/-/snippets/6050350)

---

## 13. Referensi & Sumber Belajar

1. Google Developers. (2024). *Android Activity Lifecycle Documentation*. Tersedia di: [https://developer.android.com/guide/components/activities/activity-lifecycle](https://developer.android.com/guide/components/activities/activity-lifecycle)
2. Google Design. (2024). *Material Design 3 Guidelines*. Tersedia di: [https://m3.material.io/](https://m3.material.io/)
3. Android Open Source Project. (2024). *Material 3 Components in Jetpack Compose*. Tersedia di: [https://developer.android.com/develop/ui/compose/components](https://developer.android.com/develop/ui/compose/components)
4. Tom's Guide. (2024). *Favorite Android Apps & Layout Inspiration*. Tersedia di: [https://www.tomsguide.com/](https://www.tomsguide.com/)