# Knowledge Base

Jawaban 5 pertanyaan teori pretest magang Backend Engineer (Spring Boot).

---

## 1. Apa yang anda ketahui tentang REST API?

**Jawaban:**

API (*Application Programming Interface*) adalah "pintu" yang memungkinkan dua aplikasi saling berkomunikasi. Misalnya, aplikasi toko online di HP kita meminta data produk dari server.

REST (*Representational State Transfer*) adalah aturan atau gaya penulisan API supaya komunikasinya rapi dan seragam. Komunikasinya memakai protokol HTTP, dan datanya biasanya berformat JSON.

Gampangnya seperti pelayan di restoran. Kita (client) tidak masuk ke dapur (server), tapi memesan lewat pelayan (API). Pelayan membawa pesanan ke dapur lalu mengantar hasilnya kembali.

Prinsip utamanya:

- **Client dan server terpisah.** Tampilan (misalnya aplikasi mobile) dan pengolahan data di server berjalan sendiri-sendiri.
- **Stateless.** Setiap permintaan berdiri sendiri dan membawa semua informasi yang dibutuhkan, jadi server tidak menyimpan "ingatan" tentang permintaan sebelumnya.
- **Berbasis resource.** Setiap data punya alamat (URL) sendiri, misalnya `/products` untuk daftar produk dan `/products/5` untuk produk nomor 5.

Jenis permintaan (HTTP method) dan fungsinya:

| Method | Fungsi | Contoh |
|--------|--------|--------|
| GET | Mengambil data | `GET /products` → lihat daftar produk |
| POST | Menambah data baru | `POST /cart` → masukkan produk ke keranjang |
| PUT / PATCH | Mengubah data | `PATCH /cart/items/3` → ubah jumlah barang |
| DELETE | Menghapus data | `DELETE /cart/items/3` → hapus barang dari keranjang |

Server membalas dengan kode status, misalnya `200` (berhasil), `201` (data berhasil dibuat), `400` (permintaan salah), `404` (data tidak ditemukan), dan `500` (error di server).

---

## 2. Apa yang anda ketahui tentang Server Side dan Client Side Processing?

**Jawaban:**

Keduanya adalah pembagian tugas: bagian mana yang dikerjakan di komputer server, dan bagian mana yang dikerjakan di perangkat pengguna (browser atau aplikasi HP).

**Server side processing** adalah proses yang berjalan di server. Pengguna hanya mengirim permintaan dan menerima hasilnya. Biasanya ini mencakup pengolahan data, akses database, logika bisnis, dan keamanan. Contoh teknologinya Java (Spring Boot), PHP, Node.js, dan Python. Contoh kasusnya: saat checkout, server mengecek stok, menghitung total harga, lalu menyimpan transaksi ke database.

**Client side processing** adalah proses yang berjalan di perangkat pengguna. Biasanya ini mencakup tampilan, interaksi, dan validasi sederhana. Contoh teknologinya HTML, CSS, JavaScript, React, dan aplikasi mobile. Contoh kasusnya: saat mengisi form, browser langsung memberi tahu kalau format email salah, tanpa menunggu server.

Perbandingannya:

| | Server Side | Client Side |
|---|---|---|
| Dijalankan di | Server | Browser / perangkat pengguna |
| Fokus | Data, logika bisnis, keamanan | Tampilan dan interaksi |
| Kelebihan | Lebih aman, data terpusat, kode tidak terlihat pengguna | Respons cepat, mengurangi beban server |
| Kekurangan | Beban server bertambah, tiap aksi butuh jaringan | Kode bisa dilihat dan dimodifikasi pengguna, bergantung pada kemampuan perangkat |

Dalam praktiknya keduanya dipakai bersamaan. Validasi di client side memberi pengalaman yang enak untuk pengguna, tetapi validasi di server side tetap wajib karena data dari client tidak bisa sepenuhnya dipercaya. Dalam pretest ini, backend Spring Boot yang kita buat adalah server side. Client side-nya bisa berupa aplikasi web atau mobile yang memanggil REST API tadi.

---

## 3. Apa yang anda ketahui tentang Monolith dan Microservices? Berikan contohnya

**Jawaban:**

**Monolith** adalah arsitektur di mana seluruh fitur aplikasi dibangun menjadi satu kesatuan: satu kode, satu proyek, satu proses deployment, dan biasanya satu database. Bayangkan satu gedung besar yang berisi semua bagian toko (kasir, gudang, katalog) di dalamnya.

**Microservices** adalah arsitektur di mana aplikasi dipecah menjadi beberapa layanan kecil yang mandiri. Tiap layanan mengurus satu fungsi bisnis, punya kode dan biasanya database sendiri, bisa di-deploy sendiri-sendiri, dan berkomunikasi lewat API (misalnya REST) atau message broker. Bayangkan beberapa toko kecil yang berdiri sendiri dan saling bekerja sama.

Perbandingannya:

| | Monolith | Microservices |
|---|---|---|
| Struktur | Satu aplikasi utuh | Banyak layanan kecil |
| Deployment | Sekali deploy untuk semuanya | Tiap layanan di-deploy sendiri |
| Scaling | Seluruh aplikasi digandakan sekaligus | Hanya layanan yang ramai yang digandakan |
| Kegagalan | Satu bug serius bisa menjatuhkan seluruh aplikasi | Kegagalan satu layanan lebih terisolasi |
| Kompleksitas | Sederhana di awal, makin berat saat aplikasi membesar | Rumit di awal (jaringan, monitoring, konsistensi data) |
| Cocok untuk | Aplikasi kecil-menengah, tim kecil, prototipe | Aplikasi besar, banyak tim, kebutuhan scaling berbeda per fitur |

**Contoh dengan skenario e-commerce di soal:**

- **Monolith:** satu aplikasi Spring Boot berisi modul catalog, cart, dan transaksi, dengan satu database PostgreSQL.
- **Microservices:** `catalog-service`, `cart-service`, dan `transaction-service` dipisah, masing-masing dengan database sendiri. Saat checkout, `transaction-service` memanggil `catalog-service` untuk mengecek stok.

Microservices bukan selalu pilihan terbaik. Untuk aplikasi kecil, monolith yang tersusun rapi per modul (*modular monolith*) sering lebih praktis dan lebih murah dikelola. Microservices baru terasa manfaatnya ketika aplikasi sudah besar dan tiap fitur punya kebutuhan scaling yang berbeda.

---

## 4. Apa yang anda ketahui tentang Inversion of Control (IoC) dan Dependency Injection (DI)?

**Jawaban:**

**Inversion of Control (IoC)** adalah prinsip di mana pembuatan dan pengelolaan objek tidak dilakukan sendiri oleh kelas yang membutuhkannya, tetapi diserahkan ke pihak lain, yaitu framework atau container. Kendalinya "dibalik": bukan kelas yang mencari dan membuat kebutuhannya, tetapi kebutuhannya yang disediakan dari luar.

**Dependency Injection (DI)** adalah teknik untuk menerapkan IoC. Dependency adalah objek lain yang dibutuhkan sebuah kelas. Dengan DI, dependency itu diberikan (di-*inject*) dari luar, bukan dibuat sendiri dengan `new`.

Gampangnya seperti koki di restoran. Koki tinggal memasak, sedangkan bahan disiapkan dan diantar oleh bagian pengadaan. Koki tidak perlu belanja ke pasar sendiri.

Contoh kode:

```java
// Tanpa DI: OrderService membuat sendiri dependency-nya
public class OrderService {
    private ProductRepository repo = new ProductRepository();
}

// Dengan DI: dependency diberikan dari luar
@Service
public class OrderService {
    private final ProductRepository repo;

    public OrderService(ProductRepository repo) {
        this.repo = repo;
    }
}
```

Pada contoh pertama, `OrderService` terikat erat dengan `ProductRepository` (*tight coupling*), jadi sulit diganti atau diuji. Pada contoh kedua, `OrderService` hanya menyatakan apa yang dibutuhkannya, dan penyediaannya diurus dari luar.

Jenis injection:

- **Constructor injection:** lewat konstruktor (paling dianjurkan di Spring)
- **Setter injection:** lewat method setter
- **Field injection:** langsung ke atribut dengan `@Autowired`

**Penerapan di Spring:** Spring memiliki *IoC container* (`ApplicationContext`) yang membuat dan mengelola objek-objek yang disebut *bean*. Kelas yang diberi anotasi seperti `@Component`, `@Service`, `@Repository`, atau `@RestController` didaftarkan sebagai bean, lalu Spring menyuntikkannya otomatis ke kelas yang membutuhkan.

**Manfaatnya:** kode lebih longgar keterkaitannya (*loose coupling*), implementasi mudah diganti, mudah diuji (misalnya dengan objek tiruan/mock), dan lebih rapi.

---

## 5. Apa yang anda ketahui tentang Java dan Spring Framework, khususnya Spring Boot?

**Jawaban:**

**Java** adalah bahasa pemrograman berorientasi objek (*OOP*) yang banyak dipakai untuk membangun aplikasi backend, aplikasi enterprise, dan aplikasi Android. Kode Java dikompilasi menjadi *bytecode* yang dijalankan oleh *JVM* (*Java Virtual Machine*), sehingga program yang sama bisa berjalan di berbagai sistem operasi (*platform independent*).

**Spring Framework** adalah framework Java untuk membangun aplikasi, terutama aplikasi enterprise dan backend. Inti Spring adalah IoC container dan Dependency Injection yang dibahas pada jawaban sebelumnya. Selain itu, Spring punya banyak modul pendukung, misalnya untuk web/REST (Spring MVC), akses database (Spring Data), dan keamanan (Spring Security).

**Spring Boot** adalah pengembangan dari Spring yang membuat proses memulai dan menjalankan aplikasi jauh lebih ringkas. Dibandingkan Spring biasa yang banyak membutuhkan konfigurasi manual, Spring Boot menyediakan:

- **Auto-configuration:** konfigurasi dasar disiapkan otomatis berdasarkan library yang dipakai
- **Starter dependencies:** paket dependency siap pakai, misalnya `spring-boot-starter-web` untuk membuat REST API
- **Embedded server:** server seperti Tomcat sudah ada di dalam aplikasi, jadi bisa langsung dijalankan tanpa memasang server terpisah
- **Konfigurasi sederhana:** pengaturan cukup lewat file `application.properties` atau `application.yml`

Anotasi yang sering dipakai:

| Anotasi | Fungsi |
|---------|--------|
| `@SpringBootApplication` | Penanda kelas utama aplikasi |
| `@RestController` | Kelas yang menerima request dan mengembalikan data (JSON) |
| `@Service` | Kelas untuk logika bisnis |
| `@Repository` | Kelas untuk akses database |
| `@Entity` | Kelas yang dipetakan menjadi tabel database (JPA) |

**Spring Data JPA** mempermudah akses database dengan memetakan kelas Java ke tabel, sehingga operasi dasar seperti menyimpan, mencari, dan menghapus data bisa dilakukan tanpa menulis SQL manual untuk setiap kasus.

Dalam pretest ini, Spring Boot dipakai untuk membangun backend e-commerce dengan PostgreSQL sebagai database.
