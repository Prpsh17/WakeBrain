# Pengantar Pemrograman Mobile

**Mata Kuliah:** Pemrograman Mobile (IF21507) — Pertemuan 1  
**Program Studi:** Informatika, Fakultas Teknik — Universitas Jenderal Soedirman  
**Dosen Pengampu:** Azis Amirulbahar, S.Pd., M.T.I.  

---

## Daftar Isi
1. [Persiapan: Alat Pengembangan](#1-persiapan-alat-pengembangan)
2. [Apa itu Pemrograman Mobile?](#2-apa-itu-pemrograman-mobile)
3. [Sejarah Perkembangan Mobile Platform](#3-sejarah-perkembangan-mobile-platform)
4. [Market Share Sistem Operasi Mobile Global](#4-market-share-sistem-operasi-mobile-global)
5. [Mengapa Belajar Pemrograman Mobile?](#5-mengapa-belajar-pemrograman-mobile)
6. [Rencana Perkuliahan](#6-rencana-perkuliahan)
7. [Mengenal Bahasa Kotlin](#7-mengenal-bahasa-kotlin)
8. [Kotlin Multiplatform (KMP)](#8-kotlin-multiplatform-kmp)
9. [Perbandingan Sintaks: Kotlin vs Java](#9-perbandingan-sintaks-kotlin-vs-java)
10. [Sintaks Dasar & Konsep Utama Kotlin](#10-sintaks-dasar--konsep-utama-kotlin)
    - [Variables & Tipe Data](#variables--tipe-data-di-kotlin)
    - [Control Flow](#control-flow-if-when-for-while)
    - [Null Safety](#null-safety-di-kotlin)
    - [Object-Oriented Programming (OOP)](#object-oriented-programming-oop-di-kotlin)
    - [Lambda & Higher-Order Functions](#lambda--higher-order-functions)
    - [Lambda sebagai Dasar Jetpack Compose](#lambda-sebagai-dasar-jetpack-compose)
11. [Ringkasan Sintaks Kotlin](#11-ringkasan-sintaks-kotlin-yang-wajib-dikuasai)
12. [Pertemuan Berikutnya](#12-pertemuan-berikutnya)
13. [Referensi & Sumber Belajar](#13-referensi--sumber-belajar)

---

## 1. Persiapan: Alat Pengembangan

Alat pengembangan utama yang akan digunakan:
- **Android Studio:** Integrated Development Environment (IDE) resmi dari Google untuk pengembangan aplikasi Android Native.
- **Visual Studio Code (VS Code):** Editor kode ringan yang fleksibel untuk berbagai keperluan coding dan penulisan skrip pendukung.

---

## 2. Apa itu Pemrograman Mobile?

### Definisi
Pengembangan aplikasi yang berjalan pada perangkat bergerak (*mobile devices*), mencakup:
- Desain UI/UX (tampilan antarmuka dan interaksi pengguna)
- Logika bisnis (*business logic*)
- Integrasi layanan platform (kamera, sensor, GPS, penyimpanan lokal, API jaringan, dll.)

### Dua Ekosistem Utama
- **Android** (Google)
- **iOS** (Apple)

Kedua ekosistem ini mendominasi lanskap pemrograman mobile global.

### Bahasa Utama
- **Kotlin** (Android Native)
- **Swift** (iOS Native)
- **Dart / Flutter** (Cross-platform)

---

## 3. Sejarah Perkembangan Mobile Platform

| Tahun | Milestone Perkembangan |
|---|---|
| **2007** | Apple merilis **iPhone OS 1.0** — era *smartphone* modern dimulai. |
| **2008** | **Android 1.0** dirilis bersama HTC Dream (T-Mobile G1). Google telah mengakuisisi Android Inc. sejak 2005. |
| **2009 – 2013** | Perang ekosistem: **App Store vs Google Play**. Era Symbian & BlackBerry mulai runtuh. |
| **2014** | Android berekspansi ke 3 platform baru: **Android Auto**, **Android TV**, dan **Android Wear**. |
| **2019 – kini** | **Kotlin ditetapkan sebagai bahasa resmi Android** oleh Google. Jetpack Compose menjadi standar UI modern. |

---

## 4. Market Share Sistem Operasi Mobile Global

Berdasarkan data Q4 2024:

- **Android:** $\approx 74\%$ (Mendominasi pasar global secara konsisten)
- **iOS:** $\approx 22\%$ (Dominan di wilayah Amerika Utara dan Australia)
- **HarmonyOS:** $\approx 4\%$ (Tumbuh pesat di China, $+19\%$ YoY)

> **Catatan Kunci:**  
> - Android mendominasi $>95\%$ pasar di beberapa kawasan seperti Asia, Afrika, dan Amerika Selatan.  
> - *Belajar Android development = menjangkau mayoritas pengguna smartphone dunia.*  
> - *Mobile saat ini bukan hanya smartphone.*

---

## 5. Mengapa Belajar Pemrograman Mobile?

1. **Pasar Raksasa:**  
   Lebih dari 3,6 miliar pengguna *smartphone* aktif di seluruh dunia — menjadikannya pasar terbesar dalam sejarah teknologi modern.
2. **Permintaan Industri Tinggi:**  
   Kebutuhan akan *mobile developer* terus tinggi di industri teknologi Indonesia (seperti Gojek, Tokopedia, Traveloka, dan ribuan startup lokal).
3. **Tech Stack Modern:**  
   Kombinasi **Kotlin + Jetpack Compose** merupakan standar industri terkini yang digunakan langsung oleh Google dan perusahaan teknologi global.
4. **Satu Skill, Banyak Platform:**  
   Smartphone, *smartwatch*, Smart TV, hingga kendaraan (*automotive*) semuanya dapat dijangkau menggunakan satu bahasa: **Kotlin**.

---

## 6. Rencana Perkuliahan

- **Pertemuan 1 – 8:** Fondasi Pemrograman Mobile Native dengan Kotlin & Android (Jetpack Compose).
- **Pertemuan 9 – 16:** Pengembangan Aplikasi Mobile Cross-Platform (menggunakan Flutter).

---

## 7. Mengenal Bahasa Kotlin

### Mengapa Kotlin untuk Android?
- 🏆 **Bahasa Resmi Android:** Ditetapkan sebagai bahasa utama (*first-class language*) oleh Google sejak 2019, lengkap dengan dukungan ekosistem dan dokumentasi resmi.
- 🔗 **100% Interoperable dengan Java:** Kotlin dan Java dapat bekerja berdampingan dalam satu proyek tanpa konflik.
- ✍️ **Ringkas & Ekspresif:** Mengurangi *boilerplate code* secara signifikan (~40% lebih ringkas), menghasilkan kode yang lebih mudah dipelihara dan minim bug.
- ⚡ **Multi-Paradigma:** Mendukung pemrograman fungsional (*functional*) dan berorientasi objek (*object-oriented*) secara terpadu.

### Eksperimen Cepat via Browser
Anda dapat mencoba dan mempelajari sintaks Kotlin secara instan tanpa instalasi melalui:  
👉 [https://play.kotlinlang.org](https://play.kotlinlang.org)

### Keunggulan Utama Kotlin
1. **Concise:** Mengurangi kode boilerplate hingga 40%.
2. **Safe:** Dukungan *null-safety* bawaan dari compiler, mencegah `NullPointerException`.
3. **Interoperable:** Kompatibel 100% dengan ekosistem Java.
4. **Coroutines:** Menjalankan *asynchronous programming* yang bersih tanpa terjebak *callback hell*.
5. **Multiplatform:** Satu basis kode (*codebase*) yang dapat dijalankan lintas platform.

---

## 8. Kotlin Multiplatform (KMP)

**Konsep:** *Satu Bahasa, Banyak Platform.*

Kotlin Multiplatform (KMP) memungkinkan *sharing* logika bisnis antarplatform (Android, iOS, Web, Desktop, hingga Server) tanpa mengorbankan performa native:
- **Arsitektur:** Logika bisnis dibagikan bersama (*shared logic*), sedangkan antarmuka (UI) tetap dioptimalkan per platform untuk pengalaman pengguna terbaik.
- **Adopsi Industri:** Digunakan oleh perusahaan skala global seperti Netflix, VMware, Philips, dan Cash App.
- **Dukungan Kuat:** Dikembangkan oleh JetBrains dan didukung secara resmi oleh Google.

> *"Go cross-platform without compromising performance, UX, or code quality."*

---

## 9. Perbandingan Sintaks: Kotlin vs Java

| Fitur | Kotlin | Java |
|---|---|---|
| **Deklarasi Variabel** | `val name = "Informatika"` | `String name = "Informatika";` |
| **Null Safety** | Tipe *nullable* bawaan: `String?` | Rentan terhadap runtime error `NullPointerException` |
| **Fungsi** | `fun greet(name: String) = "Hello, $name"` | Memerlukan tipe *return* dan *keyword* `return` secara eksplisit |
| **Data Class** | Otomatis: `equals()`, `hashCode()`, `toString()`, `copy()` | Harus ditulis manual satu per satu (*verbose*) |
| **Volume Kode** | ~40% lebih sedikit baris kode | Memerlukan banyak kode *boilerplate* |

---

## 10. Sintaks Dasar & Konsep Utama Kotlin

### Variables & Tipe Data di Kotlin

- `val` (**Value / Immutable**): Bersifat *read-only* (mirip `final` pada Java). Nilai tidak dapat diubah setelah diinisialisasi.
- `var` (**Variable / Mutable**): Nilai dapat diubah (*reassign*) sewaktu-waktu.
- **Type Inference:** Kompilator Kotlin secara otomatis mendeteksi tipe data dari nilai yang diberikan, sehingga penulisan tipe data eksplisit bersifat opsional.

```kotlin
val nama: String = "User" // immutable (read-only)
var usia = 25             // type inference mendeteksi tipe Int
usia = 26                 // OK: variabel var bisa diubah

// nama = "Pengguna"     // ERROR: val tidak bisa diubah nilainya!
```

---

### Control Flow: if, when, for, while

- `if` di Kotlin bertindak sebagai sebuah **ekspresi** (dapat mengembalikan nilai secara langsung).
- `when` bertindak sebagai pengganti `switch-case` di Java dengan kapabilitas yang jauh lebih ekspresif.
- Perulangan `for` memanfaatkan konsep rentang nilai (*range*), contohnya: `for (i in 1..10)`.

```kotlin
// Contoh penggunaan 'when' sebagai ekspresi
val grade = when (nilai) {
    in 90..100 -> "A"
    in 80..89  -> "B"
    in 70..79  -> "C"
    else       -> "D"
}
```

---

### Null Safety di Kotlin

Kotlin membedakan secara tegas tipe yang boleh bernilai null (*nullable*) dan tipe yang tidak boleh null (*non-nullable*) di tingkat kompilasi.

- **Non-nullable Type:** `String` (tidak boleh bernilai `null`).
- **Nullable Type:** `String?` (diizinkan bernilai `null`).
- **Safe Call Operator (`?.`):** Mengeksekusi pemanggilan hanya jika objek tidak bernilai null.
- **Elvis Operator (`?:`):** Menyediakan nilai default pengganti jika objek bernilai null.

---

### Object-Oriented Programming (OOP) di Kotlin

Kotlin menyediakan kapabilitas OOP lengkap dengan sintaks yang ringkas:
- **Class & Object:** Mendukung *inheritance*, *interface*, dan *abstract class*.
- **Data Class:** Class khusus penyimpanan data; otomatis menghasilkan `equals()`, `hashCode()`, `toString()`, dan `copy()`.
- **Sealed Class:** Membatasi hierarki class; ideal untuk memodelkan *State* UI (misal: Loading, Success, Error).
- **Companion Object:** Pengganti kata kunci `static` di Java untuk mendefinisikan anggota tingkat kelas.

```kotlin
// Contoh Data Class
data class Mahasiswa(
    val nama: String,
    val nim: String
)

// Contoh Sealed Class untuk memodelkan State pada UI
sealed class UiState {
    object Loading : UiState()
    data class Success(val data: String) : UiState()
    data class Error(val msg: String) : UiState()
}
```

---

### Lambda & Higher-Order Functions

- **Lambda:** Fungsi anonim yang dapat disimpan dalam variabel atau dikirimkan sebagai argumen ke fungsi lain.  
  Format dasar: `{ parameter -> body }`
- **Higher-Order Function:** Fungsi yang menerima fungsi lain sebagai parameter atau mengembalikan fungsi.

```kotlin
// Lambda sederhana
val tambah = { a: Int, b: Int -> a + b }
println(tambah(3, 5)) // Output: 8

// Lambda pada operasi Collection
val angka = listOf(1, 2, 3, 4, 5)
val genap = angka.filter { it % 2 == 0 } 
println(genap) // Output: [2, 4]
```

---

### Lambda sebagai Dasar Jetpack Compose

Jetpack Compose sangat bergantung pada lambda untuk mendefinisikan struktur UI secara deklaratif:
- Setiap fungsi bertanda `@Composable` menerima lambda sebagai parameter kontennya.
- Penanganan event (*event handler* seperti `onClick`, `onValueChange`) merupakan blok lambda.
- Struktur UI bersarang dibangun dari penulisan lambda di dalam lambda.

```kotlin
@Composable
fun Greeting(name: String) {
    Button(
        onClick = { 
            println("Halo, $name!") 
        }
    ) {
        Text(text = "Klik Saya")
    }
}
```

---

## 11. Ringkasan: Sintaks Kotlin yang Wajib Dikuasai

1. **Variables:** Pahami perbedaan `val` (*immutable*) vs `var` (*mutable*), serta mekanisme *type inference*.
2. **Control Flow:** Menggunakan `if` sebagai ekspresi, struktur percabangan `when`, dan perulangan berbasis *range*.
3. **Null Safety:** Pemanfaatan operator safe call (`?.`), Elvis operator (`?:`), serta perbedaan tipe *nullable* vs *non-nullable*.
4. **Lambda:** Penulisan fungsi anonim, pemahaman *higher-order function*, dan penerapannya sebagai fondasi declarative UI pada Jetpack Compose.

---

## 12. Pertemuan Berikutnya

- **Topik:** *Jetpack Compose — Membangun UI Pertama Anda di Android*
- **Sesi Diskusi:** Sampaikan pertanyaan melalui forum diskusi perkuliahan atau langsung saat sesi kelas tatap muka.

---

## 13. Referensi & Sumber Belajar

1. Wikipedia. (2024). *Android version history*. Tersedia di: [https://en.wikipedia.org/wiki/Android_version_history](https://en.wikipedia.org/wiki/Android_version_history)
2. Google. (2024). *10 years ago, Android expanded to 3 new platforms*. Tersedia di: [https://blog.google/products-and-platforms/platforms/android/android-anniversary-tv-auto-wear/](https://blog.google/products-and-platforms/platforms/android/android-anniversary-tv-auto-wear/)
3. Digital Information World. (2025). *Global smartphone sales flat in Q4 2024 as Android faces record lows in India, US*. Tersedia di: [https://www.digitalinformationworld.com/2025/03/global-smartphone-sales-flat-in-q4-2024.html](https://www.digitalinformationworld.com/2025/03/global-smartphone-sales-flat-in-q4-2024.html)
4. PPC.land. (2024). *Global mobile operating systems divide follows geographic lines*. Tersedia di: [https://ppc.land/global-mobile-operating-systems-divide-follows-geographic-lines-traffic-analysis-reveals/](https://ppc.land/global-mobile-operating-systems-divide-follows-geographic-lines-traffic-analysis-reveals/)
5. Google for Developers. (2024). *Android for Cars*. Tersedia di: [https://developers.google.com/cars](https://developers.google.com/cars)
6. Android. (2024). *Cross-device services & smartphone integration*. Tersedia di: [https://www.android.com/articles/cross-device-services-and-smartphone-integration/](https://www.android.com/articles/cross-device-services-and-smartphone-integration/)
7. JetBrains. (2024). *Kotlin programming language*. Tersedia di: [https://kotlinlang.org/](https://kotlinlang.org/)
8. Google. (2024). *Android API reference*. Tersedia di: [https://developer.android.com/reference](https://developer.android.com/reference)
9. JetBrains. (2024). *Kotlin documentation*. Tersedia di: [https://kotlinlang.org/docs/home.html](https://kotlinlang.org/docs/home.html)