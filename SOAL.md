# Soal Pretest Magang Backend Engineer — Peta Jawaban

Dokumen ini merangkum tiga bagian soal dan menautkan jawaban serta hasil implementasinya. Untuk naskah soal asli, lihat [repository tabeldatadotcom](https://github.com/tabeldatadotcom/pretest-maggang-backend-java).

## 1. Knowledge Base

Bagian ini meminta penjelasan tentang REST API, pemrosesan server-side dan client-side, monolith dan microservices, IoC dan DI, serta Java dan Spring Boot.

Jawaban lengkap tersedia di [Knowledge Base — Jawaban Teori](docs/01-knowledge-base.md).

| No. | Pertanyaan | Jawaban |
|---|---|---|
| 1 | Apa yang anda ketahui tentang REST API? | [Baca jawaban](docs/01-knowledge-base.md#1-apa-yang-anda-ketahui-tentang-rest-api) |
| 2 | Apa yang anda ketahui tentang server-side dan client-side processing? | [Baca jawaban](docs/01-knowledge-base.md#2-apa-yang-anda-ketahui-tentang-server-side-dan-client-side-processing) |
| 3 | Apa yang anda ketahui tentang monolith dan microservices? Berikan contohnya. | [Baca jawaban](docs/01-knowledge-base.md#3-apa-yang-anda-ketahui-tentang-monolith-dan-microservices-berikan-contohnya) |
| 4 | Apa yang anda ketahui tentang Inversion of Control (IoC) dan Dependency Injection (DI)? | [Baca jawaban](docs/01-knowledge-base.md#4-apa-yang-anda-ketahui-tentang-inversion-of-control-ioc-dan-dependency-injection-di) |
| 5 | Apa yang anda ketahui tentang Java dan Spring Framework, khususnya Spring Boot? | [Baca jawaban](docs/01-knowledge-base.md#5-apa-yang-anda-ketahui-tentang-java-dan-spring-framework-khususnya-spring-boot) |

## 2. Design Modules

Bagian ini meminta rancangan database dan modul untuk e-commerce dengan alur katalog, detail produk, keranjang, checkout, daftar transaksi, dan detail transaksi. Sertakan alasan pemilihan desain.

Referensi gambar skenario:

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

Rancangan dan jawaban tersedia di:

[Baca analisis kebutuhan, arsitektur, pembagian modul, ERD, detail tabel, dan alasan desain](docs/02-design-modules.md).

## 3. Praktik

Bagian ini meminta implementasi project monorepo untuk sistem toko, barang, dan pembelian menggunakan Spring Boot, dengan spesifikasi:

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
