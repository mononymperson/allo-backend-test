# Split Bill API

Spring Boot REST API untuk mengelola shared expenses dalam satu grup dan menghitung siapa utang siapa di akhir.

**Tech stack:** Java 25 · Spring Boot 4.1.2 · MySQL 8.4 · Maven · Docker

---

## Cara Running

### Docker Compose (recommended)

```bash
docker compose up --build
```

App jalan di **http://localhost:4110/api** dengan MySQL 8.4 (dibikin otomatis via `docker-compose.yml`, termasuk healthcheck + persistent volume).

### Tanpa Docker

Butuh MySQL lokal lalu jalankan:

```bash
./mvnw spring-boot:run
```

Spring juga membaca file `.env` di root folder proyek (via `spring.config.import`), jadi credential bisa ditaruh di file `.env`:

```dotenv
SERVER_PORT=4110
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/splitbill
SPRING_DATASOURCE_USERNAME=dev
SPRING_DATASOURCE_PASSWORD=rahasia
```

Tanpa `.env`, fallback default dipakai (`jdbc:mysql://localhost:3306/splitbill`, user `splitbill`/`splitbill`).

### Menjalankan test

```bash
./mvnw test
```



---

## API Contract

Detail lengkap request/response tiap endpoint ada di [**API_CONTRACT.md**](API_CONTRACT.md).

---

## Endpoints

Base URL: `http://localhost:4110/api`

| Method | Path | Keterangan |
|---|---|---|
| `POST` | `/bill-groups/` | Buat bill group + participants |
| `GET` | `/bill-groups/{billGroupId}/participants` | List participants dalam group |
| `POST` | `/bill-groups/{billGroupId}/expenses` | Tambah expense (siapa bayar, berapa, buat siapa) |
| `GET` | `/bill-groups/{billGroupId}/settlement` | Settlement summary: ringkasan + siapa bayar ke siapa |

### 1. Create bill group

```bash
curl -X POST http://localhost:4110/api/bill-groups/ \
  -H "Content-Type: application/json" \
  -d '{"name": "Dinner Trip", "participants": ["Daus", "Firdaus", "UsDaus"]}'
```

Response `201 Created`:

```json
{
  "message": "Bill group created successfully",
  "data": {
    "id": "01933c5e-8f00-7abc-8000-000000000001",
    "name": "Dinner Trip",
    "participants": [
      { "id": "01933c5e-8f00-7abc-8000-000000000002", "name": "Daus" },
      { "id": "01933c5e-8f00-7abc-8000-000000000003", "name": "Firdaus" }
    ]
  },
  "errors": null
}
```

### 2. Get participants

```bash
curl http://localhost:4110/api/bill-groups/{billGroupId}/participants
```

### 3. Create expense

`paidBy` = UUID participant yang nyetor, `forParticipants` = UUID participant yang ikut patungan (payer gak usah dimasukin lagi).

```bash
curl -X POST http://localhost:4110/api/bill-groups/{billGroupId}/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Makan",
    "amount": 300000,
    "paidBy": "01933c5e-8f00-7abc-8000-000000000002",
    "forParticipants": [
      "01933c5e-8f00-7abc-8000-000000000003",
      "01933c5e-8f00-7abc-8000-000000000004"
    ]
  }'
```

Response `201 Created` berisi expense yang tersimpan + breakdown split per participant.

### 4. Get settlement summary

```bash
curl http://localhost:4110/api/bill-groups/{billGroupId}/settlement
```

Response `200 OK`:

```json
{
  "message": "Settlement calculated successfully",
  "data": {
    "billGroup": {
      "id": "01933c5e-8f00-7abc-8000-000000000001",
      "name": "Dinner Trip"
    },
    "summary": {
      "totalExpenses": "300000.00",
      "serviceChargePct": "4",
      "serviceChargeAmount": "12000.00",
      "grandTotal": "312000.00"
    },
    "transfers": [
      {
        "from": { "id": "01933c5e-...-0003", "name": "Firdaus" },
        "to":   { "id": "01933c5e-...-0002", "name": "Daus" },
        "amount": "156000.00"
      },
      {
        "from": { "id": "01933c5e-...-0004", "name": "UsDaus" },
        "to":   { "id": "01933c5e-...-0002", "name": "Daus" },
        "amount": "156000.00"
      }
    ]
  },
  "errors": null
}
```

Semua nilai uang diserialisasi sebagai string untuk menjaga presisi `BigDecimal`.

---

## Personalization

- **GitHub username:** `mononymperson`
- Unicode sum: `109+111+110+111+110+121+109+112+101+114+115+111+110` = **1444**
- `service_charge_pct = 1444 % 10` = **4%**

Nilai ini dihitung oleh kode (`SettlementService.calculateServiceChargePct()`), bukan hardcode di response.

---

## Struktur Kode

Package-by-feature, satu modul = satu domain:

```
com.allobank.splitbill
├── billgroup/    # grup + participants
├── participant/  # entitas peserta
├── expense/      # expense, expense split, settlement
└── common/       # ApiResponse, validation, exception handler
```


---

## Trade-offs

**Keputusan paling berat: algoritma settlement.**

Settlement yang dihitung itu **creditor–debitor**:

1. Hitung **net balance** tiap participant: `sum(total yg dia bayar sebagai payer) − sum(split yang dia tanggung)`. Positif = harus *diterima* (creditor), negatif = harus *bayar* (debitor).
2. Sort debtor dari yang paling negatif, creditor dari yang terbesar.
3. Pasangkan transfer = `min(utang debtor, piutang creditor)`, sisanya di-loop sampai semua balance nol.

Saya memilih algoritma greedy creditor-debtor matching untuk menghitung settlement berdasarkan net balance masing-masing peserta. Pendekatan ini memungkinkan utang dan piutang antar peserta saling dikompensasikan sehingga transaksi settlement menjadi lebih sederhana. Trade-off-nya, hasil settlement tidak mempertahankan hubungan utang berdasarkan expense tertentu, sehingga detail asal-usul transaksi harus dilihat melalui histori expense dan split masing-masing. Saya menerima keterbatasan tersebut karena tujuan utama fitur settlement adalah menentukan pembayaran akhir antar peserta, sedangkan histori setiap pengeluaran tetap tersedia secara terpisah.

Trade-off lain:

- **`BigDecimal` untuk semua nilai uang** — tidak ada `float`/`double` sama sekali; pembagian split selalu eksplisit `scale = 2, RoundingMode.DOWN`.
