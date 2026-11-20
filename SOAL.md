<<<<<<< HEAD
# Pretest Magang Backend Engineer (Spring Boot)

Pengerjaan pretest magang Java Developer: aplikasi e-commerce sederhana (Toko, Barang, Pembelian).

**Nama:** Rafi Putra Perkasa

## Dokumentasi

| Dokumen | Isi |
|---------|-----|
| [Soal dan peta jawaban](SOAL.md) | Ringkasan soal, tautan jawaban, dan hasil praktik |
| [Knowledge Base](docs/01-knowledge-base.md) | Jawaban lengkap 5 pertanyaan teori |
| [Design Modules](docs/02-design-modules.md) | Jawaban desain database, arsitektur, dan modul |

## Teknologi

- Java: JDK 17
- Spring Boot: 3.0.13
- Database: PostgreSQL 15
- Build tool: Maven

## Persiapan

Pastikan Java 17 atau lebih baru, Maven, dan PostgreSQL 15 tersedia. Jalankan service PostgreSQL dan buat database `pretest_shop` jika belum ada. Flyway akan membuat tabel aplikasi saat aplikasi pertama kali dijalankan.

Koneksi default menggunakan URL `jdbc:postgresql://localhost:5432/pretest_shop` dan username `postgres`. Atur `DB_URL`, `DB_USERNAME`, dan `DB_PASSWORD` jika nilainya berbeda. Contoh PowerShell (password diminta secara tersembunyi dan tidak disimpan di file):

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/pretest_shop"
$env:DB_USERNAME = "postgres"
$secure = Read-Host "Password PostgreSQL" -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new("", $secure).Password
```

<<<<<<<< HEAD:SOAL.md
terkait design system Toko, Barang, Pembelian pada ecommerse tersebut.
========
## Menjalankan Aplikasi

Port default Spring Boot adalah 8080. Jika port tersebut sudah dipakai, jalankan di port 8083:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8083"
```

Tunggu sampai terminal menampilkan `Started ShopApplication`. Biarkan terminal tetap terbuka selama aplikasi digunakan; hentikan dengan `Ctrl+C`.

## Tampilan Toko

Buka alamat dasar sesuai port yang digunakan, misalnya [http://localhost:8083/](http://localhost:8083/). Halaman ini menampilkan katalog, pencarian produk, keranjang, dan form checkout. Produk yang ditampilkan dibaca dari PostgreSQL melalui API. Jika database belum memiliki produk, katalog akan kosong sampai toko dan produk dibuat lewat API.

## Pemeriksaan Status

Buka `/api/health` pada port aplikasi, misalnya [http://localhost:8083/api/health](http://localhost:8083/api/health). Respons `{"status":"UP","database":"UP"}` berarti aplikasi dan database dapat diakses.

## Daftar Endpoint

| Method | Endpoint | Deskripsi |
|---------|----------|-----------|
| GET | `/api/health` | Memeriksa status aplikasi dan koneksi PostgreSQL |
| POST | `/api/stores` | Membuat toko |
| POST | `/api/stores/{tokoId}/products` | Menambahkan barang ke toko |
| PUT | `/api/products/{barangId}` | Mengubah informasi barang |
| GET | `/api/products?q=&tokoId=` | Mencari barang, opsional berdasarkan kata kunci atau toko |
| GET | `/api/products/{barangId}` | Melihat detail barang |
| POST | `/api/customers` | Membuat data pelanggan |
| GET | `/api/customers/{pelangganId}/cart` | Melihat keranjang |
| POST | `/api/customers/{pelangganId}/cart/items` | Menambahkan barang ke keranjang |
| PUT | `/api/customers/{pelangganId}/cart/items/{barangId}` | Mengubah jumlah barang di keranjang |
| DELETE | `/api/customers/{pelangganId}/cart/items/{barangId}` | Menghapus barang dari keranjang |
| POST | `/api/customers/{pelangganId}/checkout` | Checkout dan membuat transaksi |
| GET | `/api/customers/{pelangganId}/transactions` | Melihat daftar transaksi pelanggan |
| GET | `/api/transactions/{transaksiId}` | Melihat detail transaksi |

### Contoh Alur Pemakaian API (PowerShell)

Jalankan setelah aplikasi aktif. Contoh berikut membuat toko, barang, dan pelanggan baru. Email harus unik; contoh ini membuat email baru berdasarkan waktu setiap kali dijalankan.

```powershell
$base = "http://localhost:8083"
$email = "demo-$(Get-Date -Format 'yyyyMMddHHmmss')@example.com"

$toko = Invoke-RestMethod -Method Post -Uri "$base/api/stores" `
  -ContentType "application/json" `
  -Body (@{ nama = "Toko Contoh"; deskripsi = "Toko demo"; kota = "Jakarta" } | ConvertTo-Json)

$barang = Invoke-RestMethod -Method Post -Uri "$base/api/stores/$($toko.id)/products" `
  -ContentType "application/json" `
  -Body (@{ nama = "Headset Bluetooth"; deskripsi = "Headset nirkabel untuk penggunaan harian"; harga = 189000; stok = 10 } | ConvertTo-Json)

$pelanggan = Invoke-RestMethod -Method Post -Uri "$base/api/customers" `
  -ContentType "application/json" `
  -Body (@{ nama = "Pelanggan Demo"; email = $email; noTelepon = "08123456789" } | ConvertTo-Json)

Invoke-RestMethod -Method Post -Uri "$base/api/customers/$($pelanggan.id)/cart/items" `
  -ContentType "application/json" `
  -Body (@{ barangId = $barang.id; jumlah = 2 } | ConvertTo-Json)

Invoke-RestMethod -Method Post -Uri "$base/api/customers/$($pelanggan.id)/checkout"
Invoke-RestMethod -Method Get -Uri "$base/api/customers/$($pelanggan.id)/transactions"
```

API ini belum memakai autentikasi; ID pelanggan pada URL digunakan untuk kebutuhan demo dan pretest.

### Catatan

`mvn test` menjalankan pengujian otomatis. Browser storefront tersedia pada `/`; endpoint API mengembalikan JSON dan dapat diuji dengan PowerShell atau alat seperti Postman.
>>>>>>>> d633cfa (Update ecommerce application):README.md
=======
# Pretest Magang Backend Engineer — Soal dan Peta Jawaban

Dokumen ini merangkum soal pretest dan mengarahkan pembaca langsung ke jawaban atau hasil implementasinya. Jawaban lengkap dipisahkan per topik supaya dokumen soal tetap ringkas dan tidak ada isi yang terduplikasi.

## 1. Knowledge Base

Jawaban lengkap: [Knowledge Base — Jawaban Teori](docs/01-knowledge-base.md).

| No. | Pertanyaan | Jawaban |
|---|---|---|
| 1 | Apa yang anda ketahui tentang REST API? | [Baca jawaban](docs/01-knowledge-base.md#1-apa-yang-anda-ketahui-tentang-rest-api) |
| 2 | Apa yang anda ketahui tentang server-side dan client-side processing? | [Baca jawaban](docs/01-knowledge-base.md#2-apa-yang-anda-ketahui-tentang-server-side-dan-client-side-processing) |
| 3 | Apa yang anda ketahui tentang monolith dan microservices? Berikan contohnya. | [Baca jawaban](docs/01-knowledge-base.md#3-apa-yang-anda-ketahui-tentang-monolith-dan-microservices-berikan-contohnya) |
| 4 | Apa yang anda ketahui tentang Inversion of Control (IoC) dan Dependency Injection (DI)? | [Baca jawaban](docs/01-knowledge-base.md#4-apa-yang-anda-ketahui-tentang-inversion-of-control-ioc-dan-dependency-injection-di) |
| 5 | Apa yang anda ketahui tentang Java dan Spring Framework, khususnya Spring Boot? | [Baca jawaban](docs/01-knowledge-base.md#5-apa-yang-anda-ketahui-tentang-java-dan-spring-framework-khususnya-spring-boot) |

## 2. Design Modules

### Soal

Buat rancangan database dan modul (monolith atau microservices) untuk skenario e-commerce berikut:

1. **Catalog** — pelanggan mencari produk di toko.
   ![Contoh catalog](imgs/catalog.png)
2. **Item** — pelanggan melihat detail informasi produk.
   ![Contoh detail item](imgs/item.png)
3. **Cart** — pelanggan menambahkan produk yang ingin dibeli ke keranjang.
   ![Contoh cart](imgs/cart.png)
4. **List transaction** — setelah checkout, transaksi muncul di daftar.
   ![Contoh daftar transaksi](imgs/list-transaction.png)
5. **Detail transaction** — pelanggan dapat melihat detail transaksi.
   ![Contoh detail transaksi](imgs/detail-transaction.png)

Jelaskan alasan pemilihan desain tersebut.

### Jawaban

[Baca analisis kebutuhan, arsitektur, pembagian modul, ERD, detail tabel, dan alasan desain](docs/02-design-modules.md).

## 3. Praktik

### Soal

Berdasarkan analisis, buat project monorepo menggunakan Spring Boot dengan spesifikasi:

- Database: PostgreSQL 15
- JDK: Oracle JDK 17 atau lebih baru
- Spring Boot: 3.0.x
- Sistem: Toko, Barang, dan Pembelian pada e-commerce

### Hasil implementasi

- [Panduan menjalankan aplikasi dan contoh alur API](README.md)
- [Source code aplikasi](src/main/java/com/pretest/shop/)
- [Migrasi skema database](src/main/resources/db/migration/V1__init_schema.sql)
- [Tampilan katalog toko](src/main/resources/static/index.html)

## 4. Teknologi dan Identitas Pengerjaan

- **Nama:** Rafi Putra Perkasa
- **Java:** JDK 17 atau lebih baru
- **Spring Boot:** 3.0.13
- **Database:** PostgreSQL 15
- **Build tool:** Maven
>>>>>>> d633cfa (Update ecommerce application)
