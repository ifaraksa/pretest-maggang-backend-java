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
