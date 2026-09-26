# State Management & Recomposition: Membangun UI Interaktif di Jetpack Compose

**Mata Kuliah:** Pemrograman Mobile (IF21507) — Pertemuan ke-5

**Program Studi:** Informatika, Fakultas Teknik — Universitas Jenderal Soedirman

**Dosen Pengampu:** Azis Amirulbahar, S.Pd., M.T.I.

**Durasi & Bobot:** 100 Menit | 2 SKS

---

## Daftar Isi

 1. [Apa Itu State?](#1-apa-itu-state)
 2. [Mekanisme Recomposition](#2-mekanisme-recomposition)
 3. [Mengapa Variabel Biasa Tidak Cukup?](#3-mengapa-variabel-biasa-tidak-cukup)
 4. [Solusi Reaktivitas: mutableStateOf](#4-solusi-reaktivitas-mutablestateof)
 5. [Menjaga Nilai saat Recomposition: remember](#5-menjaga-nilai-saat-recomposition-remember)
 6. [Sintaks Idiomatik: Delegasi Properti "by"](#6-sintaks-idiomatik-delegasi-properti-by)
 7. [State vs Stateless Composable](#7-state-vs-stateless-composable)
 8. [Masalah Rotasi Layar & rememberSaveable](#8-masalah-rotasi-layar--remembersaveable)
 9. [Prinsip State Hoisting](#9-prinsip-state-hoisting)
10. [Unidirectional Data Flow (UDF): State Down, Event Up](#10-unidirectional-data-flow-udf-state-down-event-up)
11. [Implementasi Kode: State Hoisting Counter](#11-implementasi-kode-state-hoisting-counter)
12. [Studi Kasus: Favorite Toggle List](#12-studi-kasus-favorite-toggle-list)
13. [Ringkasan 5 Konsep Kunci](#13-ringkasan-5-konsep-kunci)
14. [Rencana Pertemuan Berikutnya](#14-rencana-pertemuan-berikutnya)

---

## 1. Apa Itu State?

**State** adalah setiap nilai atau data yang dapat berubah sepanjang waktu dan menentukan apa yang ditampilkan pada antarmuka pengguna (UI) pada momen tertentu. Jika State berubah, maka tampilan UI harus diperbarui.

### Contoh State dalam Aplikasi Mobile:
* Teks yang sedang diketikkan pengguna di dalam `TextField`.
* Status centang pada komponen `Checkbox` (aktif/nonaktif).
* Jumlah item produk yang dimasukkan ke dalam keranjang belanja.
* Status penandaan favorit pada sebuah kartu (ikon ❤️ / 🤍).

> **Aturan Emas Jetpack Compose:**
>
> $$UI = f(\text{State})$$
>
> Tampilan antarmuka (*UI*) merupakan representasi visual langsung (*output*) dari fungsi data yang sedang aktif (*State*).

---

## 2. Mekanisme Recomposition

**Recomposition** adalah proses pemanggilan ulang fungsi `@Composable` oleh *Compose Runtime* untuk memperbarui antarmuka pengguna saat terjadi perubahan pada State yang dibaca oleh komponen tersebut.

```
       ┌──────────────────────────────┐
       │      State Data Berubah      │
       └──────────────┬───────────────┘
                      │
                      ▼
       ┌──────────────────────────────┐
       │   Compose Runtime Mendeteksi │
       └──────────────┬───────────────┘
                      │
                      ▼
       ┌──────────────────────────────┐
       │  Recomposition Terjadwal    │
       │  (Hanya fungsi terdampak)    │
       └──────────────┬───────────────┘
                      │
                      ▼
       ┌──────────────────────────────┐
       │    Node UI Layar Diperbarui  │
       └──────────────────────────────┘
```

* **Selektif & Efisien:** Compose tidak merender ulang seluruh pohon tampilan aplikasi (*full-tree invalidation*), melainkan secara cerdas hanya mengeksekusi kembali Composable yang langsung membaca variabel state terkait.
* **Idempoten:** Pemanggilan fungsi Composable dengan input nilai yang sama akan selalu menghasilkan visual antarmuka yang identik tanpa efek samping tersembunyi.

---

## 3. Mengapa Variabel Biasa Tidak Cukup?

Jika Anda menggunakan variabel Kotlin biasa (tipe data primitif `var`), sistem Compose tidak memiliki kemampuan untuk memantau perubahan nilainya:

```kotlin
// ❌ KODE YANG SALAH (Tidak Reaktif)
@Composable
fun CounterSalah() {
    var count = 0 // Variabel biasa di dalam fungsi Composable

    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}
```

* **Masalah:** Saat tombol diklik, nilai variabel `count` memang bertambah di dalam memori CPU. Namun, karena variabel tersebut tidak terdaftar di sistem pelacakan *Compose Runtime*, Compose **tidak tahu bahwa ada perubahan data**. Akibatnya, Recomposition tidak pernah dipicu dan teks di layar tetap menampilkan `Count: 0`.

---

## 4. Solusi Reaktivitas: mutableStateOf

Untuk membuat data dapat dipantau oleh Compose, bungkus nilai tersebut menggunakan `mutableStateOf<T>()`.

```kotlin
// ❌ Variabel Biasa (Tidak Reaktif)
var count = 0

// ✅ State Reaktif (Compose Memantau Nilai Ini)
val countState = mutableStateOf(0)
```

`mutableStateOf` menghasilkan objek `MutableState<T>` yang terintegrasi langsung dengan mesin Recomposition. Setiap kali properti `.value` miliknya diubah, Compose secara otomatis menjadwalkan render ulang pada komponen yang membacanya.

---

## 5. Menjaga Nilai saat Recomposition: remember

Menggunakan `mutableStateOf` saja belum menyelesaikan masalah seutuhnya:

```kotlin
// ❌ Masalah Inisialisasi Ulang
@Composable
fun CounterReset() {
    val countState = mutableStateOf(0) // Dibuat ulang setiap recomposition!

    Button(onClick = { countState.value++ }) {
        Text("Count: ${countState.value}")
    }
}
```

* Saat tombol diklik, state berubah $\rightarrow$ memicu Recomposition.
* Namun saat fungsi dieksekusi ulang dari baris pertama, `mutableStateOf(0)` akan dieksekusi kembali, sehingga nilainya selalu kembali ke titik nol (`0`).

### Peran Fungsi `remember`

Fungsi pembungkus `remember { }` memerintahkan Compose Runtime untuk mengalokasikan dan menyimpan objek state di memori internal Compose selama komponen tersebut masih berada di pohon komposisi (*Composition Tree*).

```kotlin
// ✅ Nilai Bertahan Melintasi Recomposition
@Composable
fun Counter() {
    val countState = remember { mutableStateOf(0) }

    Button(onClick = { countState.value++ }) {
        Text("Count: ${countState.value}")
    }
}
```

---

## 6. Sintaks Idiomatik: Delegasi Properti "by"

Menuliskan properti `.value` berulang kali membuat kode terlihat bertele-tele (*verbose*). Kotlin menyediakan fitur **Property Delegation** melalui kata kunci `by`:

```
Pendekatan Verbose:
val countState = remember { mutableStateOf(0) }
Akses: countState.value
Ubah : countState.value++

                ⬇ Disederhanakan menjadi ⬇

Pendekatan Idiomatik (by):
var count by remember { mutableStateOf(0) }
Akses: count
Ubah : count++
```

> ⚠️ **Catatan Penting:** Penggunaan kata kunci `by` membutuhkan impor ekstensi runtime berikut:
> ```kotlin
> import androidx.compose.runtime.getValue
> import androidx.compose.runtime.setValue
> ```

---

## 7. State vs Stateless Composable

Dalam arsitektur Jetpack Compose, komponen UI dikategorikan menjadi dua jenis:

```
┌──────────────────────────────────────┐  ┌──────────────────────────────────────┐
│        STATEFUL COMPOSABLE           │  │         STATELESS COMPOSABLE         │
├──────────────────────────────────────┤  ├──────────────────────────────────────┤
│ Komponen yang memiliki dan mengelola │  │ Komponen yang TIDAK memiliki atau    │
│ state internalnya sendiri            │  │ mengelola state internal             │
│ (menggunakan remember).              │  │ (hanya menerima data via parameter). │
│                                      │  │                                      │
│ • Bersifat mandiri (self-contained)  │  │ • Sangat mudah dipakai ulang (reuse) │
│ • Cocok untuk Screen-level (induk)   │  │ • Sangat mudah diuji (unit testing)  │
│ • Kode UI & logika bercampur         │  │ • Murni fokus pada representasi visual│
└──────────────────────────────────────┘  └──────────────────────────────────────┘
```

### Kapan Menggunakan yang Mana?
* Gunakan **Stateful** pada komponen tingkat atas (*Screen-level composable*) yang bertugas menghubungkan alur data dan logika aplikasi.
* Gunakan **Stateless** pada komponen modular antarmuka (seperti Button kustom, Card item list, Avatar) agar dapat digunakan berulang kali di berbagai layar dengan data yang berbeda.

---

## 8. Masalah Rotasi Layar & rememberSaveable

### Apa yang Terjadi Saat Layar Dirotasi?
Rotasi orientasi layar (potret $\leftrightarrow$ lanskap) pada Android tergolong sebagai **Configuration Change**. Secara default, sistem operasi Android akan **menghancurkan (`onDestroy`) Activity lama dan membuat ulang (`onCreate`) Activity yang baru**.

* Objek yang disimpan via `remember { }` hanya bertahan di memori komposisi.
* Saat Activity dihancurkan secara fisik, seluruh state yang ditampung oleh `remember` akan **musnah**, menyebabkan input form atau status scroll pengguna kembali ke kondisi awal.

### Solusi: `rememberSaveable`

`rememberSaveable` bekerja dengan menyimpan data ke dalam mekanisme persistensi status sementara Android (`Bundle`). Data yang tersimpan di dalamnya dapat bertahan melintasi *Configuration Change* maupun proses pembunuhan aplikasi oleh sistem karena kekurangan RAM.

```kotlin
// Nilai count tetap utuh meskipun HP diputar ke lanskap
var count by rememberSaveable { mutableStateOf(0) }
```

### Tabel Perbandingan: remember vs rememberSaveable

| Skenario / Kemampuan | `remember` | `rememberSaveable` |
| :--- | :---: | :---: |
| **Bertahan saat Recomposition biasa** | ✅ Ya | ✅ Ya |
| **Bertahan saat Rotasi Layar (*Config Change*)** | ❌ **Hilang** | ✅ **Bertahan** |
| **Mekanisme Penyimpanan** | Memori Komposisi | `Bundle` Android System |
| **Kasus Penggunaan Terbaik** | Animasi sementara, status fokus UI | Input teks form pengguna, data counter, status filter |

---

## 9. Prinsip State Hoisting

**State Hoisting** adalah pola arsitektur memindahkan kepemilikan status (*state*) dari komponen anak (*child*) ke komponen induknya (*parent*), sehingga komponen anak berubah menjadi *Stateless*.

### Mengapa State Hoisting Penting?
Jika sebuah komponen menyimpan statenya sendiri di dalam tubuh fungsinya (*stateful*), komponen tersebut menjadi kaku, sulit dipakai ulang pada kebutuhan lain, dan sulit diuji secara otomatis (*unit test*).

### Pola Penerapan State Hoisting:
1. Ganti variabel state internal pada child dengan parameter nilai biasa (**State Down**).
2. Tambahkan parameter fungsi *callback* lambda untuk memberi sinyal ke parent saat terjadi interaksi (**Event Up**).

---

## 10. Unidirectional Data Flow (UDF): State Down, Event Up

Konsep *State Hoisting* merupakan landasan bagi terbentuknya pola **Unidirectional Data Flow (UDF)** di Jetpack Compose:

```
                  ┌───────────────────────────────┐
                  │    Parent / ViewModel         │
                  │    (Penyimpan Sumber State)   │
                  └───────┬───────────────▲───────┘
                          │               │
             State Down   │               │   Event Up
     (Nilai Data Turun)   │               │   (Sinyal Aksi Naik)
                          ▼               │
                  ┌───────────────────────┴───────┐
                  │    Child Composable           │
                  │    (Stateless UI / Tampilan)  │
                  └───────────────────────────────┘
```

1. **State Down (Data Mengalir Turun):** Komponen induk mengirimkan data status ke komponen anak melalui argumen parameter fungsi. Komponen anak bertugas membaca dan menampilkannya ke layar tanpa mengubah data secara langsung.
2. **Event Up (Aksi Mengalir Naik):** Saat pengguna berinteraksi (misal menekan tombol atau mengetik), komponen anak memanggil fungsi *callback* lambda untuk memberitahu komponen induk agar memperbarui nilai state miliknya.
3. **Single Source of Truth:** Data hanya dimutasi pada satu titik terpusat (komponen induk atau ViewModel), sehingga aliran data aplikasi mudah diprediksi, dilacak, dan bebas dari *bug out-of-sync*.

---

## 11. Implementasi Kode: State Hoisting Counter

Berikut adalah contoh perbandingan refaktor dari komponen Stateful menjadi pasangan Stateless Child dan Stateful Parent:

```kotlin
// 1. STATELESS CHILD (Murni Tampilan, Sangat Mudah Dites & Reusable)
@Composable
fun CounterDisplay(
    count: Int,                     // State Down (Menerima nilai)
    onIncrement: () -> Unit,        // Event Up (Callback aksi bertambah)
    onDecrement: () -> Unit,        // Event Up (Callback aksi berkurang)
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jumlah: $count",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onDecrement) {
                Text("-")
            }
            Button(onClick = onIncrement) {
                Text("+")
            }
        }
    }
}

// 2. STATEFUL PARENT (Wadah Pengelola State Tunggal)
@Composable
fun CounterScreen() {
    // State disimpan di parent menggunakan rememberSaveable
    var count by rememberSaveable { mutableStateOf(0) }

    CounterDisplay(
        count = count,
        onIncrement = { count++ },
        onDecrement = { if (count > 0) count-- }
    )
}
```

---

## 12. Studi Kasus: Favorite Toggle List

Penerapan pola *Stateless Child* dengan *Stateful Parent* untuk membuat item daftar dengan tombol toggle favorit (❤️ / 🤍) yang tahan rotasi layar:

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 1. STATELESS CHILD: Komponen Baris Item
@Composable
fun FavoriteItem(
    name: String,
    isFavorite: Boolean,         // State Down
    onToggleFavorite: () -> Unit, // Event Up
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = if (isFavorite) "Hapus dari Favorit" else "Tambah ke Favorit",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

// 2. STATEFUL PARENT: Layar Penampung
@Composable
fun FavoriteScreen() {
    var isFavorite by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        FavoriteItem(
            name = "Kopi Susu Gula Aren",
            isFavorite = isFavorite,
            onToggleFavorite = { isFavorite = !isFavorite }
        )
    }
}
```

---

## 13. Ringkasan 5 Konsep Kunci

1. **State & Recomposition:** State adalah representasi data penentu UI ($UI = f(\text{State})$). Perubahan pada State memicu Recomposition selektif secara otomatis.
2. **`remember` + `mutableStateOf`:** Kombinasi wajib untuk mendeklarasikan state reaktif yang tidak di-reset nilainya saat Recomposition berlangsung.
3. **`rememberSaveable`:** Menyimpan data ke dalam mekanisme `Bundle` Android agar state tidak hilang ketika terjadi *Configuration Change* (seperti rotasi layar).
4. **State Hoisting:** Praktik arsitektur memindahkan pengelolaan state ke komponen *parent* untuk memisahkan logika dari representasi visual.
5. **Unidirectional Data Flow (UDF):** Aliran data satu arah yang teratur: nilai turun ke anak (*State Down*), aksi interaksi naik ke induk (*Event Up*).

---

## 14. Rencana Pertemuan Berikutnya

* **Topik Utama:** *Networking & Architecture (Mengintegrasikan API Jaringan, Coroutines, dan ViewModel)*.
* **Tindak Lanjut Praktikum:** Lakukan refaktorisasi seluruh komponen antarmuka yang dibuat sebelumnya agar menerapkan pola *Stateless Composable*.