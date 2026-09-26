# Lazy Layouts (List Dinamis)

**Mata Kuliah:** Pemrograman Mobile (IF21507) — Pertemuan 4  
**Program Studi:** Informatika, Fakultas Teknik — Universitas Jenderal Soedirman  
**Dosen Pengampu:** Azis Amirulbahar, S.Pd., M.T.I.  
**Durasi & Bobot:** 100 Menit | 2 SKS  

---

## Daftar Isi

1. [Tujuan Pembelajaran](#1-tujuan-pembelajaran)
2. [Problem Statement: Column Biasa vs Lazy Layouts](#2-problem-statement-column-biasa-vs-lazy-layouts)
3. [Solusi Cerdas: Apa itu Lazy Layouts?](#3-solusi-cerdas-apa-itu-lazy-layouts)
4. [Tantangan Lanjutan: Operasi Asinkronus vs Sinkronus](#4-tantangan-lanjutan-operasi-asinkronus-vs-sinkronus)
5. [LazyColumn: Sintaks Dasar & LazyListScope DSL](#5-lazycolumn-sintaks-dasar--lazylistscope-dsl)
6. [LazyRow: List Horizontal & Carousel](#6-lazyrow-list-horizontal--carousel)
7. [LazyVerticalGrid: Tampilan Grid Responsif](#7-lazyverticalgrid-tampilan-grid-responsif)
8. [Optimasi Performa: Parameter key](#8-optimasi-performa-parameter-key)
9. [Studi Kasus: Katalog Produk Dinamis](#9-studi-kasus-katalog-produk-dinamis)
10. [Ringkasan & Tabel Perbandingan Komponen](#10-ringkasan--tabel-perbandingan-komponen)
11. [Rencana Pertemuan Berikutnya](#11-rencana-pertemuan-berikutnya)

---

## 1. Tujuan Pembelajaran

Pertemuan ini berfokus pada teknik menampilkan kumpulan data dinamis dalam jumlah besar secara efisien:

1. **Memahami Masalah Performa `Column` + Scroll:** Mengetahui alasan mengapa `Column` standar tidak boleh digunakan untuk dataset besar.
2. **Menguasai Sintaks Lazy Layouts:** Memahami DSL dan konfigurasi utama pada `LazyColumn`, `LazyRow`, dan `LazyVerticalGrid`.
3. **Optimasi Performa dengan Parameter `key`:** Menghindari rekomposisi yang tidak perlu saat dataset mengalami mutasi (*insert*, *delete*, *reorder*).
4. **Interaktivitas & Studi Kasus:** Mengintegrasikan model data, kartu Material Design 3, dan event interaksi ke dalam aplikasi katalog produk.

---

## 2. Problem Statement: Column Biasa vs Lazy Layouts

Dalam pengembangan antarmuka, sering kali aplikasi harus menampilkan daftar data yang sangat panjang (misal: ribuan produk di marketplace seperti Shopee atau feed postingan media sosial).

```
          [ PENDEKATAN COLUMN BIASA ]                     [ PENDEKATAN LAZY LAYOUT ]
       ┌───────────────────────────────┐               ┌───────────────────────────────┐
       │ Item 1  (Aktif di Memori)     │               │ Item 1  (Belum dimuat)        │
       │ Item 2  (Aktif di Memori)     │               ├───────────────────────────────┤
  ┌───>│ Item 3  (Aktif di Layar)      │<── Viewport ──>│ Item 3  (Aktif di Layar)      │
  │    │ Item 4  (Aktif di Layar)      │   (Layar HP)  │ Item 4  (Aktif di Layar)      │
  │    │ Item 5  (Aktif di Layar)      │               │ Item 5  (Aktif di Layar)      │
  │    ├───────────────────────────────┤               ├───────────────────────────────┤
  │    │ Item 6  (Aktif di Memori)     │               │ Item 6  (Dihancurkan/Disposed)│
  │    │ ...                           │               │ ...                           │
  │    │ Item 1000 (Aktif di Memori)   │               │ Item 1000 (Belum dimuat)      │
  │    └───────────────────────────────┘               └───────────────────────────────┘
  │         Semua 1.000 item dirender                    Hanya 3 item di viewport yang
  │         sekaligus ke memori RAM!                     aktif; lainnya di-dispose!
```

### ❌ Masalah pada `Column` Biasa
* Jika kita memasukkan 1.000 data ke dalam `Column` dengan modifier `.verticalScroll()`, Compose akan membuat **1.000 node Composable aktif di memori secara bersamaan**, meskipun layar hanya mampu menampilkan 5–8 item sekaligus.
* **Akibatnya:** Penggunaan RAM melonjak drastis, rendering frame anjlok (*jank/stuttering*), baterai boros, dan aplikasi berpotensi mengalami **OOM (*Out of Memory*) Crash**.
* *Analogi:* Seperti memaksakan seluruh pakaian di dalam lemari dikeluarkan dan ditumpuk sekaligus ke lantai kamar hingga meluap.

### ✅ Keunggulan Lazy Layout
* Menerapkan prinsip **Rendering On-Demand**: Hanya Composable yang sedang berada di dalam area pandang layar (*viewport*) yang akan dibuat dan dialokasikan ke memori.
* Ketika item digulir ke luar layar, item tersebut akan segera dilepas (*disposed*) atau didaur ulang.

---

## 3. Solusi Cerdas: Apa itu Lazy Layouts?

Kata **"Lazy"** di sini bermakna positif, yaitu **"malas" merender apa pun sampai benar-benar dibutuhkan**.

1. **Hanya Render yang Tampak:** Elemen baru akan digambar tepat saat pengguna mulai menggulir layar mendekati item tersebut.
2. **Daur Ulang Otomatis (*Recycling*):** Mirip dengan prinsip ban berjalan (*conveyor belt*) di pabrik, komponen yang telah melewati layar atas akan didaur ulang untuk menampung data baru yang masuk dari layar bawah.

> **💡 Catatan Sejarah:**  
> Konsep ini identik dengan komponen `RecyclerView` pada sistem XML klasik Android, namun di Jetpack Compose implementasinya jauh lebih ringkas tanpa perlu lagi membuat `Adapter`, `ViewHolder`, ataupun file layout XML yang terpisah.

---

## 4. Tantangan Lanjutan: Operasi Asinkronus vs Sinkronus

Saat daftar item berhasil dirender dengan lancar, timbul tantangan baru: **bagaimana jika setiap item perlu mengunduh gambar profil atau produk dari internet?**

Mengambil data dari jaringan membutuhkan waktu (1–5 detik, tergantung kualitas sinyal). Cara aplikasi menangani jeda waktu ini menentukan apakah aplikasi akan membeku (*freeze*) atau tetap interaktif:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│ 🚫 SINKRONUS (Synchronous)                                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│ Tugas dijalankan satu per satu secara berurutan. Thread utama HARUS         │
│ MENUNGGU hingga tugas pertama tuntas sebelum dapat mengerjakan hal lain.    │
│                                                                             │
│ • Analogi: Pelayan restoran mencatat pesanan, lalu BERDIRI DIAM di samping  │
│   meja pelanggan menunggu koki selesai memasak, menolak melayani meja lain. │
│ • Dampak di Android: Layar membeku (*freeze*), scroll macet, dan memicu     │
│   dialog peringatan sistem ANR (*Application Not Responding*).               │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│ ✅ ASINKRONUS (Asynchronous)                                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│ Memulai tugas di latar belakang (background thread), lalu thread utama      │
│ langsung melanjutkan tugas rendering UI tanpa hambatan.                      │
│                                                                             │
│ • Analogi: Pelayan mengantar pesanan ke dapur, kembali melayani meja lain   │
│   atau menuangkan air, lalu mengantar makanan saat koki sudah selesai.       │
│ • Dampak di Android: UI tetap responsif 60–120 FPS; placeholder/shimmer     │
│   dapat ditampilkan sementara gambar diunduh di balik layar.                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 5. LazyColumn: Sintaks Dasar & LazyListScope DSL

`LazyColumn` adalah komponen utama untuk membangun daftar scrollable vertikal. Di dalamnya, kita tidak meletakkan Composable anak secara langsung, melainkan menggunakan blok pembangun `LazyListScope`.

### Sintaks Dasar

```kotlin
@Composable
fun DaftarNamaMahasiswa(mahasiswaList: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Memberikan padding di sekeliling area scroll (tepi tidak terpotong)
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        // Mengatur jarak antar item tanpa perlu Spacer manual
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. Menampilkan satu item tunggal (misal: Header)
        item {
            Text(
                text = "Daftar Mahasiswa Aktif",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // 2. Menampilkan kumpulan item dari koleksi List
        items(mahasiswaList) { nama ->
            Text(
                text = nama,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}
```

---

## 6. LazyRow: List Horizontal & Carousel

`LazyRow` memiliki arsitektur identik dengan `LazyColumn`, namun arah scroll bergerak secara **horizontal (sumbu X)**. Komponen ini sangat cocok digunakan untuk:
* Pilihan chip kategori / filter pencarian.
* Banner promo carousel di halaman depan.
* Daftar avatar story atau thumbnail produk terkait.

```kotlin
@Composable
fun FilterKategoriSection(daftarKategori: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(daftarKategori) { kategori ->
            SuggestionChip(
                onClick = { /* Tangani filter kategori */ },
                label = { Text(text = kategori) }
            )
        }
    }
}
```

---

## 7. LazyVerticalGrid: Tampilan Grid Responsif

Untuk menampilkan katalog produk atau galeri foto dalam format baris dan kolom sekaligus, Compose menyediakan `LazyVerticalGrid`.

Parameter kunci penentu jumlah kolom adalah **`columns = GridCells`**:

```
        GridCells.Fixed(2)                   GridCells.Adaptive(150.dp)
  ┌─────────────────────────────┐        ┌─────────────────────────────┐
  │ ┌─────────┐   ┌─────────┐   │        │ ┌───────┐ ┌───────┐ ┌───────┐
  │ │ Kolom 1 │   │ Kolom 2 │   │        │ │   1   │ │   2   │ │   3   │
  │ └─────────┘   └─────────┘   │        │ └───────┘ └───────┘ └───────┘
  │ ┌─────────┐   ┌─────────┐   │        │ Lebar kolom minimal 150.dp;
  │ │ Kolom 1 │   │ Kolom 2 │   │        │ Jumlah kolom otomatis bertambah
  │ └─────────┘   └─────────┘   │        │ di tablet / layar yang lebih lebar.
  └─────────────────────────────┘        └─────────────────────────────┘
```

### Dua Mode Penataan Kolom Grid

1. **`GridCells.Fixed(count: Int)`:**  
   Mengunci jumlah kolom tepat sejumlah angka yang ditentukan, apa pun ukuran layarnya.  
   *Contoh:* `GridCells.Fixed(2)` akan selalu menampilkan 2 kolom berdampingan baik di layar ponsel kompak maupun di tablet.

2. **`GridCells.Adaptive(minSize: Dp)`:**  
   Menghitung jumlah kolom secara dinamis dan adaptif berdasarkan lebar layar yang tersedia. Setiap kolom dijamin memiliki lebar setidaknya sebesar `minSize`.
   * Layar kecil ($< 600\text{ dp}$): 2 kolom
   * Layar sedang ($600 - 900\text{ dp}$): 3 kolom
   * Layar besar/tablet ($> 900\text{ dp}$): 4 kolom atau lebih

```kotlin
@Composable
fun GaleriProdukGrid(produkList: List<Produk>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = produkList, key = { it.id }) { produk ->
            KartuProduk(produk = produk)
        }
    }
}
```

---

## 8. Optimasi Performa: Parameter key

Secara bawaan (*default*), Compose melacak posisi setiap item dalam Lazy Layout berdasarkan **indeks posisinya (0, 1, 2, ...)**.

### Perbandingan Pelacakan: Index vs Identity

```
Kasus: Item baru disisipkan di posisi paling atas (indeks 0)

❌ TANPA KEY (Index Tracking):
Indeks lama 0 bergeser menjadi 1, 1 menjadi 2, dst.
Compose mengira SEMUA elemen telah berubah dan memicu rekomposisi pada
seluruh daftar item di layar! Kehilangan status animasi scroll.

✅ DENGAN KEY (Identity Tracking):
Compose mengenali identitas unik objek melalui ID permanennya.
Compose hanya merender 1 item baru yang disisipkan dan cukup menggeser posisi
item lain secara halus tanpa merender ulang kontennya.
```

```kotlin
// IMPLEMENTASI YANG DIREKOMENDASIKAN
items(
    items = daftarProduk,
    key = { produk -> produk.id } // Gunakan ID unik dari database/model
) { produk ->
    KartuProduk(produk = produk)
}
```

> ⚠️ **Aturan Emas:** Selalu sertakan parameter `key` apabila dataset bersifat dinamis (dapat ditambah, dihapus, disortir, atau diubah urutannya).

---

## 9. Studi Kasus: Katalog Produk Dinamis

Berikut adalah implementasi menyeluruh aplikasi katalog produk menggunakan `LazyColumn`, komponen `Card` Material Design 3, dan format data Kotlin.

### A. Model Data Class
Data class di Kotlin bersifat *immutable by default*, menjadikannya sangat aman dan efisien saat diproses oleh algoritma perbandingan state Compose:

```kotlin
data class Produk(
    val id: Int,
    val namaProduk: String,
    val deskripsiSingkat: String,
    val harga: Double
)

// Mock Data
val daftarProduk = listOf(
    Produk(
        id = 1,
        namaProduk = "Laptop Pro X",
        deskripsiSingkat = "Performa tinggi untuk developer & desainer",
        harga = 15_000_000.0
    ),
    Produk(
        id = 2,
        namaProduk = "Mechanical Keyboard",
        deskripsiSingkat = "Tactile switch dengan RGB backlit",
        harga = 850_000.0
    ),
    Produk(
        id = 3,
        namaProduk = "Monitor 4K 27\"",
        deskripsiSingkat = "Panel IPS, 144Hz refresh rate, 99% sRGB",
        harga = 5_200_000.0
    )
)
```

### B. Komponen Kartu Produk (`KartuProduk`)
Menggunakan `OutlinedCard` Material 3 untuk menampilkan informasi item secara terstruktur:

```kotlin
@Composable
fun KartuProduk(
    produk: Produk,
    modifier: Modifier = Modifier,
    onAddToCartClick: (Produk) -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = produk.namaProduk,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = produk.deskripsiSingkat,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Rp ${String.format("%,.0f", produk.harga)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onAddToCartClick(produk) }
            ) {
                Text(text = "Add to Cart")
            }
        }
    }
}
```

### C. Layar Utama Katalog (`LayarKatalogProduk`)

```kotlin
@Composable
fun LayarKatalogProduk(
    produkList: List<Produk>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Katalog Produk") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = produkList,
                key = { item -> item.id }
            ) { produk ->
                KartuProduk(
                    produk = produk,
                    onAddToCartClick = { dipilih ->
                        println("Produk ditambahkan: ${dipilih.namaProduk}")
                    }
                )
            }
        }
    }
}
```

---

## 10. Ringkasan & Tabel Perbandingan Komponen

| Komponen | Arah Scroll | Kasus Penggunaan Populer | Parameter Konfigurasi Kunci |
| :--- | :--- | :--- | :--- |
| **`LazyColumn`** | Vertikal (Y) | Daftar berita, feed linimasa, chat, daftar produk | `verticalArrangement`, `contentPadding` |
| **`LazyRow`** | Horizontal (X) | Chip filter kategori, status story, carousel gambar | `horizontalArrangement`, `contentPadding` |
| **`LazyVerticalGrid`** | Vertikal (Grid X & Y) | Galeri foto, dashboard menu, katalog toko online | `columns = GridCells.Fixed / Adaptive` |
| **`key` Parameter** | — *(Optimasi)* | Wajib digunakan pada list yang dapat berubah/bermutasi | `key = { it.id }` *(Identity Tracking)* |

### Rumus Kunci Efisiensi List:
$$
\text{Efisiensi} = \text{Lazy Layout} + \text{Parameter } key + \text{PaddingValues \& spacedBy}
$$

---

## 11. Rencana Pertemuan Berikutnya

* **Agenda Utama:** Kuis Pemahaman Materi Dasar + *State Management Lanjutan (`State Hoisting`, `ViewModel`, dan Reaktivitas UI)*.
* **Diskusi:** Bawa kendala atau pertanyaan praktikum ke dalam forum diskusi kelas tatap muka!