# Networking & Architecture: Mengkoneksikan REST API dengan ViewModel dan State UI

**Mata Kuliah:** Pemrograman Mobile (IF21507) — Pertemuan ke-6  
**Program Studi:** Informatika, Fakultas Teknik — Universitas Jenderal Soedirman  
**Dosen Pengampu:** Azis Amirulbahar, S.Pd., M.T.I.  
**Bobot & Durasi:** 2 SKS (100 Menit)  
**Tech Stack:** Jetpack Compose, MVVM, REST API, Retrofit, Kotlin Coroutines & Flow

---

## Daftar Isi
1. [Konsep Dasar Networking & REST API](#1-konsep-dasar-networking--rest-api)
2. [Format Data JSON & Pemetaannya di Kotlin](#2-format-data-json--pemetaannya-di-kotlin)
3. [Library Networking di Android](#3-library-networking-di-android)
4. [Sinkronus vs Asinkronus & Kotlin Coroutines](#4-sinkronus-vs-asinkronus--kotlin-coroutines)
5. [Pola Arsitektur Mobile: Menuju MVVM](#5-pola-arsitektur-mobile-menuju-mvvm)
6. [Alur Data pada Layer Arsitektur MVVM](#6-alur-data-pada-layer-arsitektur-mvvm)
7. [Pengelolaan UI State dengan Sealed Interface](#7-pengelolaan-ui-state-dengan-sealed-interface)
8. [Studi Kasus End-to-End: API hingga Compose UI](#8-studi-kasus-end-to-end-api-hingga-compose-ui)
9. [Setup & Konfigurasi Retrofit Singleton](#9-setup--konfigurasi-retrofit-singleton)
10. [Penanganan Error & Helper safeApiCall](#10-penanganan-error--helper-safeapicall)
11. [Stream Reaktif: Flow vs StateFlow vs SharedFlow](#11-stream-reaktif-flow-vs-stateflow-vs-sharedflow)
12. [Testing pada Layer Networking](#12-testing-pada-layer-networking)
13. [Ringkasan Materi](#13-ringkasan-materi)
14. [Rencana Pertemuan Berikutnya](#14-rencana-pertemuan-berikutnya)
15. [Referensi & Daftar Pustaka](#15-referensi--daftar-pustaka)

---

## 1. Konsep Dasar Networking & REST API

### Apa itu Networking?
Networking pada aplikasi *mobile* adalah proses pertukaran data antara aplikasi klien (*client/frontend*) dan server (*backend*) melalui jaringan internet menggunakan protokol standar **HTTP/HTTPS**.

### Mengapa Aplikasi Mobile Membutuhkan Networking?
1. **Akses Data Terpusat:** Data transaksi, katalog produk, atau profil pengguna tersimpan terpusat di server basis data.
2. **Fitur Dinamis:** Konten aplikasi dapat diperbarui seketika dari server tanpa mewajibkan pengguna memperbarui (*update*) aplikasi di Google Play Store.
3. **Sinkronisasi Multi-Perangkat:** Perubahan status yang dibuat di ponsel langsung tercermin saat pengguna membuka aplikasi melalui tablet atau web.

### HTTP Methods pada REST API
REST (*Representational State Transfer*) memanfaatkan metode HTTP standar untuk menjalankan operasi CRUD (*Create, Read, Update, Delete*):

| HTTP Method | Operasi CRUD | Deskripsi & Fungsi |
| :--- | :--- | :--- |
| **GET** | Read | Mengambil data atau kumpulan data dari server tanpa mengubah data tersebut. |
| **POST** | Create | Mengirimkan data baru di dalam *request body* untuk dibuat di sisi server. |
| **PUT** | Update | Memperbarui seluruh isi data target di server (*full replacement*). |
| **PATCH** | Update | Memperbarui sebagian field data target di server (*partial update*). |
| **DELETE** | Delete | Menghapus sumber daya data tertentu dari server. |

---

## 2. Format Data JSON & Pemetaannya di Kotlin

**JSON (*JavaScript Object Notation*)** adalah format pertukaran data berbasis teks standar industri yang ringan, fleksibel, serta mudah dibaca oleh manusia maupun mesin.

### Pemetaan JSON ke Data Class Kotlin
Gunakan anotasi `@SerializedName` dari library deserializer (misalnya Google Gson) untuk memetakan nama field berformat *snake_case* pada JSON ke konvensi penamaan *camelCase* di Kotlin.

```json
// Contoh JSON Response dari Server:
{
  "id": 1,
  "name": "Budi",
  "is_active": true,
  "age": null
}
```

```kotlin
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("age")
    val age: Int? // Gunakan nullable (?) karena server dapat mengembalikan nilai null
)
```

### Serialisasi dan Deserialisasi dengan Gson
* **Deserialisasi (`fromJson`):** Mengubah *JSON String* menjadi objek Kotlin.
* **Serialisasi (`toJson`):** Mengubah objek Kotlin menjadi representasi teks *JSON String*.

```kotlin
val gson = Gson()

// 1. JSON String -> Objek Kotlin (Deserialisasi)
val jsonString = """{"id":1,"name":"Budi","is_active":true,"age":null}"""
val user: User = gson.fromJson(jsonString, User::class.java)
println(user.name)     // Output: Budi
println(user.isActive) // Output: true
println(user.age)      // Output: null

// 2. Objek Kotlin -> JSON String (Serialisasi)
val newUser = User(id = 2, name = "Ani", isActive = false, age = 25)
val jsonOutput = gson.toJson(newUser)
// Output: {"id":2,"name":"Ani","is_active":false,"age":25}
```

> ⚠️ **Aturan Penting:** Selalu definisikan tipe data nullable (`?`) pada properti yang berpotensi tidak ada (*optional*) atau bernilai `null` dari respons server untuk menghindari `NullPointerException` saat runtime.

---

## 3. Library Networking di Android

Tiga komponen utama yang sering digunakan dalam ekosistem Android:

1. 🔶 **Retrofit (by Square):**
   * Standar de facto industri Android profesional.
   * Mengubah deklarasi HTTP API menjadi *type-safe Kotlin Interface*.
   * Mengintegrasikan converter JSON (Gson, Moshi, Kotlinx Serialization) secara otomatis.
2. 🔷 **Ktor Client (by JetBrains):**
   * Klien HTTP modern native Kotlin berbasis Coroutines.
   * Mendukung penuh **Kotlin Multiplatform (KMP)** untuk berbagi kode networking antara Android, iOS, Desktop, dan Web.
3. ⚙️ **OkHttp (by Square):**
   * Klien HTTP tingkat rendah (*low-level*) yang berjalan di balik layar Retrofit dan Ktor.
   * Menangani alokasi soket, pooling koneksi, sistem caching, dan interceptor (pencatatan log & penyisipan header auth).

---

## 4. Sinkronus vs Asinkronus & Kotlin Coroutines

### ❌ Bencana Eksekusi Sinkronus (Main Thread)
Android menjalankan antarmuka grafis (rendering 60/120 FPS dan respons gestur pengguna) pada sebuah thread tunggal: **Main Thread (UI Thread)**.

* Menjalankan operasi jaringan berat pada thread utama akan memblokir thread tersebut.
* Layar aplikasi langsung membeku (*freeze*).
* Sistem operasi Android secara otomatis memicu pengecualian fatal `android.os.NetworkOnMainThreadException` atau menampilkan dialog **ANR (*Application Not Responding*)**.

### ✅ Asinkronus dengan Kotlin Coroutines
Coroutines menyediakan mekanisme eksekusi tugas di thread latar belakang (*background thread*) secara ringan (*lightweight threads*) tanpa memblokir thread antarmuka.

```
Main Thread (UI)  ──[ User Scroll / Animasi Berjalan Tanpa Hambatan ]──>
                          │                                  ▲
                   (Jalankan Coroutine)                (Kirim Hasil)
                          ▼                                  │
IO Thread (Bg)    ────────┴──[ Request HTTP API Service ]────┘
```

* **`suspend fun`:** Fungsi penanda yang dapat dijeda (*suspended*) dan dilanjutkan kembali (*resumed*) tanpa memblokir thread eksekusi.
* **`Dispatchers.IO`:** Thread pool yang dioptimalkan secara khusus untuk beban komputasi I/O jaringan atau pembacaan file disk.

```kotlin
suspend fun getUsers(): List<User> {
    return withContext(Dispatchers.IO) {
        apiService.fetchUsers() // Berjalan aman di background thread
    }
}
```

---

## 5. Pola Arsitektur Mobile: Menuju MVVM

Tujuan utama penerapan pola arsitektur adalah menegakkan prinsip **Separation of Concerns (SoC)**, yaitu memisahkan logika antarmuka dari logika data.

### Evolusi Pola Arsitektur
* **MVC / MVP (Legacy):** `Activity` atau `Fragment` berperan sebagai Controller/Presenter. Seiring berkembangnya fitur, kelas ini menjadi terlalu besar (*God Class*), sarat dependensi, dan sulit diuji.
* **MVVM (Model-View-ViewModel) ⭐:** Standar arsitektur resmi yang direkomendasikan Google. `ViewModel` menampung logika bisnis dan status UI, sementara UI hanya bertindak sebagai pengamat (*observer*) data yang reaktif.
* **MVI (Model-View-Intent):** Menerapkan aliran data satu arah murni (*Unidirectional Data Flow / UDF*), di mana setiap aksi pengguna dikirimkan sebagai *Intent* dan menghasilkan *State* baru yang bersifat *immutable*.

### Layering Standar Rekomendasi Google

```
┌────────────────────────────────────────────────────────┐
│                        UI Layer                        │
│         (Jetpack Compose Screens & ViewModels)         │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│                 Domain Layer (Opsional)                │
│             (Use Cases / Interactor Bisnis)            │
└───────────────────────────┬────────────────────────────┘
                            │
                            ▼
┌────────────────────────────────────────────────────────┐
│                       Data Layer                       │
│    (Repositories, Room Database, Retrofit API Client)  │
└────────────────────────────────────────────────────────┘
```

### Manfaat Arsitektur bagi Tim Pengembang:
* **Maintainability:** Komponen terisolasi sehingga perubahan API tidak merusak desain layout UI.
* **Testability:** Komponen Repository dan ViewModel mudah diuji menggunakan *unit test* tanpa bergantung pada framework Android SDK.
* **Scalability & Tim Kerja:** Banyak pengembang dapat mengerjakan modul berbeda tanpa risiko konflik kode yang rumit.

---

## 6. Alur Data pada Layer Arsitektur MVVM

Setiap lapisan pada pola arsitektur MVVM memiliki batas tanggung jawab tunggal (*Single Responsibility Principle*):

```
┌──────────────────┐               ┌───────────────────┐
│     UI Layer     │  Observes UI  │  ViewModel Layer  │
│ (Compose Screen) │ <───────────  │    (ViewModel)    │
└────────┬─────────┘     State     └─────────┬─────────┘
         │                                   │
         │ Memicu Event                      │ Memanggil Data
         ▼                                   ▼
┌──────────────────┐               ┌───────────────────┐
│ API Service (IO) │  Kirim Balik  │ Repository Layer  │
│ (Remote Server)  │ ───────────>  │  (Single Source   │
└──────────────────┘     Model     │     of Truth)     │
                                   └───────────────────┘
```

1. **UI Layer (Compose):** Membaca state dari ViewModel dan merender elemen visual ke layar.
2. **ViewModel Layer:** Mengelola state layar, meluncurkan coroutine melalui `viewModelScope`, serta memproses event pengguna.
3. **Repository Layer:** Berperan sebagai **Single Source of Truth** (satu-satunya sumber kebenaran data). Repository menentukan apakah data harus diambil dari API jaringan atau cache lokal database.
4. **Data Layer (API Service):** Melakukan eksekusi panggilan HTTP ke endpoint server melalui Retrofit.

---

## 7. Pengelolaan UI State dengan Sealed Interface

Menggunakan variabel boolean terpisah (misalnya `val isLoading = true`, `val isError = false`) sering memicu inkonsistensi status (*invalid state*, seperti loading dan error bernilai true bersamaan).

Solusi terbaik adalah menggunakan **`sealed interface`** untuk memodelkan seluruh kondisi UI secara mutlak (*exhaustive* dan *type-safe*):

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

> **Keunggulan `sealed interface` dibanding `sealed class`:**  
> `sealed interface` tidak memiliki alokasi objek konstruktor kelas (*no constructor overhead*) serta memungkinkan sebuah data class mengimplementasikan lebih dari satu interface sekaligus.

### Konsumsi State di Jetpack Compose

Kompilator Kotlin dapat memeriksa kelengkapan cabang percabangan `when`. Jika salah satu state terlewat, kode tidak akan dapat dikompilasi:

```kotlin
@Composable
fun UserListContent(uiState: UiState<List<User>>) {
    when (uiState) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UiState.Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.data) { user ->
                    Text(text = user.name, modifier = Modifier.padding(16.dp))
                }
            }
        }
        is UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Terjadi Kesalahan: ${uiState.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
```

---

## 8. Studi Kasus End-to-End: API hingga Compose UI

Berikut rancangan implementasi bertahap dari pemanggilan jaringan hingga penayangan data di layar.

### Langkah 1: API Service Interface & Model DTO

```kotlin
// Data Transfer Object (DTO)
data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

// Domain Model
data class User(
    val id: Int,
    val name: String,
    val email: String
)

// Fungsi Mapper DTO -> Domain
fun UserDto.toDomain(): User = User(id = this.id, name = this.name, email = this.email)

// Retrofit Interface
interface UserApiService {
    @GET("users")
    suspend fun getUsers(): List<UserDto>
}
```

### Langkah 2: Repository Layer

```kotlin
class UserRepository(
    private val api: UserApiService
) {
    suspend fun fetchUsers(): List<User> {
        return api.getUsers().map { it.toDomain() }
    }
}
```

### Langkah 3: ViewModel Layer

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<User>>> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val data = repository.fetchUsers()
                _uiState.value = UiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Gagal memuat data pengguna")
            }
        }
    }
}
```

### Langkah 4: Compose UI Layer

Gunakan ekstensi `collectAsStateWithLifecycle()` dari library `lifecycle-runtime-compose` agar konsumsi aliran data Flow berhenti otomatis saat aplikasi masuk ke status background.

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun UserScreen(viewModel: UserViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Pengguna") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> CircularProgressIndicator()
                is UiState.Success -> {
                    LazyColumn {
                        items(state.data) { user ->
                            ListItem(
                                headlineContent = { Text(user.name) },
                                supportingContent = { Text(user.email) }
                            )
                        }
                    }
                }
                is UiState.Error -> Text(text = "Error: ${state.message}")
            }
        }
    }
}
```

---

## 9. Setup & Konfigurasi Retrofit Singleton

Untuk menghemat pemakaian resource memori dan soket jaringan, buat instance Retrofit dan OkHttpClient sebagai objek tunggal (*Singleton*).

```kotlin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://api.example.com/"

    // Logging Interceptor: Memantau lalu lintas request/response HTTP di Logcat
    private val loggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE // Cegah kebocoran data sensitif di rilis production
            }
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}
```

---

## 10. Penanganan Error & Helper safeApiCall

### Kategori Exception Jaringan yang Umum Terjadi:
1. **`HttpException` (4xx, 5xx):** Server berhasil dihubungi namun mengembalikan kode respons kegagalan (misalnya 401 Unauthorized atau 500 Internal Server Error).
2. **`IOException`:** Kegagalan koneksi fisik (kabel putus, timeout jaringan, mode pesawat aktif, atau kegagalan resolusi DNS).
3. **`SerializationException`:** Struktur dokumen JSON tidak sesuai dengan definisi tipe data class Kotlin.

### Helper Terpusat `safeApiCall`

Bungkus eksekusi API dalam wrapper generik `NetworkResult` di layer Repository:

```kotlin
sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>
    data object Loading : NetworkResult<Nothing>
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: Exception) {
        when (e) {
            is retrofit2.HttpException -> {
                NetworkResult.Error(
                    message = "Server error (${e.code()}): ${e.message()}",
                    code = e.code()
                )
            }
            is java.io.IOException -> {
                NetworkResult.Error("Gagal terhubung. Periksa koneksi internet Anda.")
            }
            else -> {
                NetworkResult.Error("Terjadi kesalahan tak terduga: ${e.localizedMessage}")
            }
        }
    }
}
```

### Daftar HTTP Status Code Penting:
* **`400 Bad Request`:** Format data yang dikirimkan klien tidak valid.
* **`401 Unauthorized`:** Token autentikasi/API key kosong atau kedaluwarsa.
* **`403 Forbidden`:** Klien terotentikasi namun tidak memiliki izin akses ke sumber daya.
* **`404 Not Found`:** Alamat endpoint API tidak ditemukan di server.
* **`500 Internal Server Error`:** Terjadi kegagalan komputasi pada server backend.
* **`503 Service Unavailable`:** Server sedang overload atau dalam masa pemeliharaan (*maintenance*).

---

## 11. Stream Reaktif: Flow vs StateFlow vs SharedFlow

Kotlin Coroutines menyediakan tipe stream data asinkron (*asynchronous streams*):

```
               ┌────────────────────────────────────────────────────────┐
               │                  KOTLIN ASYNC STREAMS                  │
               └───────────┬────────────────────────────────┬───────────┘
                           │                                │
                           ▼                                ▼
                 [ COLD STREAM ]                     [ HOT STREAM ]
                 • Berjalan jika ada collector       • Selalu aktif memancarkan data
                 • Contoh: Flow                      • Contoh: StateFlow, SharedFlow
```

### Tabel Perbandingan: Flow, StateFlow, dan SharedFlow

| Karakteristik | `Flow` | `StateFlow` | `SharedFlow` |
| :--- | :--- | :--- | :--- |
| **Sifat Stream** | **Cold** | **Hot** | **Hot** |
| **Nilai Awal (*Initial Value*)** | Tidak memiliki nilai awal | **Wajib** didefinisikan | Opsional (default tidak ada) |
| **Penyimpanan Nilai (*Replay*)** | Tidak ada | Selalu memutar ulang **1 nilai terakhir** | Dapat dikonfigurasi (`replay = n`) |
| **Perilaku Collector** | Setiap kolektor memicu eksekusi baru dari awal | Semua kolektor berbagi data nilai terbaru yang sama | Semua kolektor berbagi eksekusi yang sama |
| **Kasus Penggunaan Utama** | Pembacaan database Room, stream respon API | **State Layar UI** (Data yang selalu butuh nilai terkini) | **Event Sekali Pakai** (*Snackbar*, Event Navigasi Layar) |

### Contoh Penggunaan SharedFlow untuk One-Time Events

Event navigasi atau notifikasi Snackbar tidak boleh dieksekusi ulang saat layar dirotasi. Oleh karena itu, gunakan `SharedFlow`:

```kotlin
sealed class NavEvent {
    data class ToDetail(val itemId: String) : NavEvent()
    data object GoBack : NavEvent()
}

class DetailViewModel : ViewModel() {
    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    fun onDetailClicked(id: String) {
        viewModelScope.launch {
            _navEvents.emit(NavEvent.ToDetail(id)) // Dipancarkan sekali, tidak disimpan ulang
        }
    }
}
```

---

## 12. Testing pada Layer Networking

Untuk membangun aplikasi yang stabil, layer data dan logika ViewModel harus diuji secara menyeluruh melalui pengujian otomatis:

### 1. Kategori Pengujian
* **Unit Test:** Menguji class atau fungsi individual secara terisolasi tanpa koneksi internet sungguhan dengan memanfaatkan tiruan data (*Mock*). Pengujian berjalan sangat cepat di JVM lokal.
* **Integration Test:** Menguji interaksi gabungan antar layer (misalnya dari Repository yang membaca database SQLite lokal dan Retrofit mock server).

### 2. Tools Testing Utama
* **MockK:** Library mocking terdepan untuk Kotlin. Digunakan untuk membuat tiruan dari `ApiService` atau `Repository`.
* **kotlinx-coroutines-test:** Menyediakan `TestDispatcher` (seperti `StandardTestDispatcher` atau `UnconfinedTestDispatcher`) untuk mengontrol waktu jalannya coroutine secara deterministik.
* **Turbine:** Library pengujian dari Square untuk memverifikasi emisi item pada Kotlin Flow secara berurutan dan terstruktur.

---

## 13. Ringkasan Materi

1. **REST API & JSON:** Operasi pertukaran data standar menggunakan HTTP methods (GET, POST, PUT, DELETE) dengan JSON sebagai format representasi data.
2. **Coroutines:** Menjaga keandalan aplikasi dengan memindahkan pemanggilan API jaringan ke thread I/O latar belakang melalui fungsi `suspend`.
3. **Separation of Concerns (MVVM):** Pembagian tanggung jawab yang tegas: UI merender tampilan, ViewModel mengelola state, dan Repository menyediakan data.
4. **State Modeling:** Menggunakan `sealed interface UiState` untuk mencegah *inconsistent state* dan memastikan UI menangani kondisi Loading, Success, dan Error secara lengkap.
5. **StateFlow vs SharedFlow:** Gunakan `StateFlow` untuk data antarmuka yang persisten dan `SharedFlow` untuk event yang hanya boleh dikonsumsi satu kali.

---

## 14. Rencana Pertemuan Berikutnya

* **Topik Utama:** *Navigation in Jetpack Compose* — Membangun navigasi multi-layar, mengirimkan argumen antar-rute, dan mengintegrasikan Navigation Component dengan ViewModel arsitektur MVVM.

---

## 15. Referensi & Daftar Pustaka

1. Android Developers. (2024). *Network operations overview*. Diakses dari: [https://developer.android.com/training/basics/network-ops](https://developer.android.com/training/basics/network-ops)
2. Android Developers. (2024). *Kotlin coroutines on Android*. Diakses dari: [https://developer.android.com/kotlin/coroutines](https://developer.android.com/kotlin/coroutines)
3. Android Developers. (2024). *StateFlow and SharedFlow*. Diakses dari: [https://developer.android.com/kotlin/flow/stateflow-and-sharedflow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
4. Android Developers. (2024). *Guide to app architecture*. Diakses dari: [https://developer.android.com/topic/architecture](https://developer.android.com/topic/architecture)
5. Square, Inc. (2024). *Retrofit: A type-safe HTTP client for Android and Java*. Diakses dari: [https://square.github.io/retrofit/](https://square.github.io/retrofit/)
6. Square, Inc. (2024). *OkHttp*. Diakses dari: [https://square.github.io/okhttp/](https://square.github.io/okhttp/)
7. Google. (2024). *Now in Android: Architecture Sample Repository*. Diakses dari: [https://github.com/android/nowinandroid](https://github.com/android/nowinandroid)
8. Eckel, B., & Skeen, J. (2023). *Atomic Kotlin*. Mindview LLC.