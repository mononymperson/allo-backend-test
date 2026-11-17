# Split Bill API Contract

Base URL:

```text
/api
```

All request and response bodies use `application/json`. IDs are UUIDs and
timestamps use ISO-8601 UTC format.

## Response envelope

Every response uses this structure:

```json
{
  "message": "Success",
  "data": {},
  "errors": null
}
```

- `message` is always a string.
- `data` contains the response payload, or `null` when there is no payload.
- `errors` contains validation or processing errors, or `null` on success.

## Create a bill group

Creates a bill group with its initial participants.

```http
POST /api/bill-groups
```

Request:

```json
{
  "name": "Trip Bandung",
  "participants": [
    "Budi",
    "Andi" 
  ]
}
```

Response: `201 Created`

```json
{
  "message": "Bill group created successfully",
  "data": {
    "id": "01933c5e-8f00-7abc-8000-000000000001",
    "name": "Trip Bandung",
    "participants": [
      {
        "id": "01933c5e-8f00-7abc-8000-000000000002",
        "name": "Budi"
      },
      {
        "id": "01933c5e-8f00-7abc-8000-000000000003",
        "name": "Andi"
      }
    ],
    "createdAt": "2026-11-15T15:00:00Z",
    "lastModifiedAt": "2026-11-15T15:00:00Z"
  },
  "errors": null
}
```

## Get group participants

Returns the participant list for a bill group.

```http
GET /api/bill-groups/{billGroupId}/participants
```

Response: `200 OK`

```json
{
  "message": "Participants retrieved successfully",
  "data": [
    {
      "id": "01933c5e-8f00-7abc-8000-000000000002",
      "name": "Budi"
    },
    {
      "id": "01933c5e-8f00-7abc-8000-000000000003",
      "name": "Andi"
    }
  ],
  "errors": null
}
```

## Add an expense

Adds an expense to a bill group. Amounts are represented as decimal strings
to avoid floating-point precision issues.

```http
POST /api/bill-groups/{billGroupId}/expenses
```

Request:

```json
{
  "description": "Dinner",
  "amount": 300000,
  "paidBy": "01933c5e-8f00-7abc-8000-000000000002",
  "forParticipants": [
    "01933c5e-8f00-7abc-8000-000000000002",
    "01933c5e-8f00-7abc-8000-000000000003"
  ]
}
```

Response: `201 Created`

```json
{
  "message": "Expense created successfully",
  "data": {
    "id": "01a1e568-05ad-7534-bf17-9bf550cc38ba",
    "description": "",
    "total": 300000.00,
    "paidBy": {
      "id": "01a1e564-2f41-7708-bf64-4946bc8aa2a0",
      "name": "Daus"
    },
    "forParticipants": [
      {
        "id": "01a1e568-05ad-7c06-93db-d3fcd9562818",
        "name": "Daus",
        "amount": 156000.00
      },
      {
        "id": "01a1e568-05ad-7fa0-abf1-7af9352a46f3",
        "name": "Firdaus",
        "amount": 156000.00
      }
    ],
  },
  "errors": null

}
```

## Get settlement summary

Returns the settlement calculation for a bill group.

```http
GET /api/bill-groups/{billGroupId}/settlement
```

Response: `200 OK`

```json
{
  "message": "Settlement calculated successfully",
  "data": {
    "billGroup": {
      "id": "01933c5e-8f00-7abc-8000-000000000001",
      "name": "Dinner Trip"
    },
    "summary": {
      "totalExpenses": 300000.00,
      "serviceChargePct": 4,
      "serviceChargeAmount": 15000.00,
      "grandTotal": 315000.00
    },
    "transfers": [
      {
        "from": {
          "id": "01933c5e-8f00-7abc-8000-000000000003",
          "name": "Firdaus"
        },
        "to": {
          "id": "01933c5e-8f00-7abc-8000-000000000002",
          "name": "Daus"
        },
        "amount": "105000.00"
      },
      {
        "from": {
          "id": "01933c5e-8f00-7abc-8000-000000000004",
          "name": "UsDaus"
        },
        "to": {
          "id": "01933c5e-8f00-7abc-8000-000000000002",
          "name": "Daus"
        },
        "amount": "105000.00"
      }
    ]
  },
  "errors": null
}
```

## Validation errors

Validation errors use `422 Unprocessable Entity`. Field errors are returned
as a map inside `errors`.

```json
{
  "message": "Validation failed",
  "data": null,
  "errors": {
    "name": "Name must not be blank",
    "participants": "Participants must contain at least one item"
  }
}
```

## Resource errors

Resource errors use the same response envelope.

```json
{
  "message": "Bill group was not found",
  "data": null,
  "errors": {
    "code": "BILL_GROUP_NOT_FOUND"
  }
}
```

Common status codes:

| Status | Meaning |
|---|---|
| `200` | Request completed successfully |
| `201` | Resource created |
| `400` | Malformed request |
| `404` | Resource does not exist |
| `409` | Request conflicts with current state |
| `422` | Validation error |
| `500` | Unexpected server error |
