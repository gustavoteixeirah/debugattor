# Debugattor API Documentation

This document provides comprehensive documentation for the Debugattor API endpoints, including request/response formats and cURL examples.

## Base URL

```
http://localhost:8080/api
```

## Content-Type

All requests that include a body should use:
```
Content-Type: application/json
```

---

## Endpoints

### 1. Start Execution

Creates a new execution to track an experiment.

**Endpoint:** `POST /api/executions`

**Request:**
- Method: POST
- Body: None required

**Response:**
- Status: 200 OK
- Body: Execution object

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/executions
```

**Response Example:**
```json
{
  "id": "e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b",
  "startedAt": "2025-09-27T15:30:12.345-03:00",
  "finishedAt": null,
  "steps": []
}
```

---

### 2. Get All Executions

Retrieves all executions with their steps and artifacts.

**Endpoint:** `GET /api/executions`

**Request:**
- Method: GET
- Body: None

**Response:**
- Status: 200 OK
- Body: Array of Execution objects

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/executions
```

**Response Example:**
```json
[
  {
    "id": "e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b",
    "startedAt": "2025-09-27T15:30:12.345-03:00",
    "finishedAt": null,
    "steps": [
      {
        "id": "b2c1d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e",
        "name": "Data Preprocessing",
        "status": "RUNNING",
        "registeredAt": "2025-09-27T18:30:13.123Z",
        "completedAt": null,
        "artifacts": [
          {
            "id": "a1d2e3f4-5b6c-7d8e-9f0a-1b2c3d4e5f6a",
            "type": "LOG",
            "content": "Started data preprocessing pipeline",
            "loggedAt": "2025-09-27T15:30:14.001-03:00"
          }
        ]
      }
    ]
  }
]
```

---

### 3. Get Execution by ID

Retrieves a specific execution by its ID.

**Endpoint:** `GET /api/executions/{executionId}`

**Request:**
- Method: GET
- Path Parameter: `executionId` (UUID)
- Body: None

**Response:**
- Status: 200 OK - Execution found
- Status: 404 Not Found - Execution not found
- Body: Execution object (if found)

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/executions/e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b
```

**Response Example:**
```json
{
  "id": "e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b",
  "startedAt": "2025-09-27T15:30:12.345-03:00",
  "finishedAt": null,
  "steps": [
    {
      "id": "b2c1d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e",
      "name": "Data Preprocessing",
      "status": "RUNNING",
      "registeredAt": "2025-09-27T18:30:13.123Z",
      "completedAt": null,
      "artifacts": []
    }
  ]
}
```

---

### 4. Register Step

Registers a new step for an existing execution.

**Endpoint:** `POST /api/executions/{executionId}/steps`

**Request:**
- Method: POST
- Path Parameter: `executionId` (UUID)
- Body: RegisterStepDto object

**Request Body Schema:**
```json
{
  "name": "string"
}
```

**Response:**
- Status: 200 OK
- Body: Updated Execution object

**cURL Example:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"Data Preprocessing"}' \
  http://localhost:8080/api/executions/e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b/steps
```

**Response Example:**
```json
{
  "id": "e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b",
  "startedAt": "2025-09-27T15:30:12.345-03:00",
  "finishedAt": null,
  "steps": [
    {
      "id": "b2c1d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e",
      "name": "Data Preprocessing",
      "status": "RUNNING",
      "registeredAt": "2025-09-27T18:30:13.123Z",
      "completedAt": null,
      "artifacts": []
    }
  ]
}
```

---

### 5. Log Artifact

Logs an artifact (log, image, or JSON data) for a specific step.

**Endpoint:** `POST /api/executions/{executionId}/steps/{stepId}/artifacts`

**Request:**
- Method: POST
- Path Parameters:
  - `executionId` (UUID)
  - `stepId` (UUID)
- Body: LogArtifact object

**Request Body Schema:**
```json
{
  "type": "LOG|IMAGE|JSON_DATA",
  "content": "string"
}
```

**Artifact Types:**
- `LOG`: Text-based log messages
- `IMAGE`: Base64-encoded image data
- `JSON_DATA`: JSON object as string

**Response:**
- Status: 200 OK
- Body: Created Artifact object

**cURL Examples:**

**Log Artifact:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"type":"LOG","content":"Data preprocessing completed successfully"}' \
  http://localhost:8080/api/executions/e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b/steps/b2c1d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e/artifacts
```

**Image Artifact:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"type":"IMAGE","content":"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="}' \
  http://localhost:8080/api/executions/e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b/steps/b2c1d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e/artifacts
```

**JSON Data Artifact:**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"type":"JSON_DATA","content":"{\"accuracy\":0.95,\"loss\":0.05,\"epochs\":100}"}' \
  http://localhost:8080/api/executions/e3b3c442-8b5e-4c8f-9d1a-5b7c8e9f0a1b/steps/b2c1d3e4-f5a6-7b8c-9d0e-1f2a3b4c5d6e/artifacts
```

**Response Example:**
```json
{
  "id": "a1d2e3f4-5b6c-7d8e-9f0a-1b2c3d4e5f6a",
  "type": "LOG",
  "content": "Data preprocessing completed successfully",
  "loggedAt": "2025-09-27T15:35:20.123-03:00"
}
```

---

## Complete Workflow Example

Here's a complete example showing how to create an execution, add steps, and log artifacts:

```bash
# 1. Start a new execution
EXECUTION_RESPONSE=$(curl -s -X POST http://localhost:8080/api/executions)
EXECUTION_ID=$(echo $EXECUTION_RESPONSE | jq -r '.id')
echo "Started execution: $EXECUTION_ID"

# 2. Register a preprocessing step
STEP_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"Data Preprocessing"}' \
  http://localhost:8080/api/executions/$EXECUTION_ID/steps)
STEP_ID=$(echo $STEP_RESPONSE | jq -r '.steps[0].id')
echo "Registered step: $STEP_ID"

# 3. Log a preprocessing log
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"type":"LOG","content":"Loaded 10,000 training samples"}' \
  http://localhost:8080/api/executions/$EXECUTION_ID/steps/$STEP_ID/artifacts

# 4. Register a training step
TRAINING_RESPONSE=$(curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"name":"Model Training"}' \
  http://localhost:8080/api/executions/$EXECUTION_ID/steps)
TRAINING_STEP_ID=$(echo $TRAINING_RESPONSE | jq -r '.steps[1].id')

# 5. Log training metrics
curl -s -X POST \
  -H "Content-Type: application/json" \
  -d '{"type":"JSON_DATA","content":"{\"epoch\":1,\"loss\":0.8,\"accuracy\":0.72}"}' \
  http://localhost:8080/api/executions/$EXECUTION_ID/steps/$TRAINING_STEP_ID/artifacts

# 6. Get the complete execution with all steps and artifacts
curl -s http://localhost:8080/api/executions/$EXECUTION_ID | jq .
```

---

## Data Models

### Execution
```json
{
  "id": "UUID",
  "startedAt": "ISO 8601 datetime with timezone",
  "finishedAt": "ISO 8601 datetime with timezone | null",
  "steps": "Array of Step objects"
}
```

### Step
```json
{
  "id": "UUID",
  "name": "string",
  "status": "RUNNING | COMPLETED | FAILED",
  "registeredAt": "ISO 8601 datetime (UTC)",
  "completedAt": "ISO 8601 datetime (UTC) | null",
  "artifacts": "Array of Artifact objects"
}
```

### Artifact
```json
{
  "id": "UUID",
  "type": "LOG | IMAGE | JSON_DATA",
  "content": "string",
  "loggedAt": "ISO 8601 datetime with timezone"
}
```

---

## Error Responses

### 404 Not Found
When requesting a non-existent execution:
```json
{
  "timestamp": "2025-09-27T18:30:15.123Z",
  "status": 404,
  "error": "Not Found",
  "path": "/api/executions/non-existent-id"
}
```

### 400 Bad Request
When sending invalid data:
```json
{
  "timestamp": "2025-09-27T18:30:15.123Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request body",
  "path": "/api/executions/some-id/steps"
}
```

---

## Notes

- All timestamps are returned in the configured timezone (America/Sao_Paulo by default)
- UUIDs are generated automatically for all entities
- Steps are created in RUNNING status by default
- The API currently doesn't support updating step status or finishing executions (planned for future versions)
- Artifact content is stored as TEXT in the database, so large binary data should be base64-encoded for IMAGE type artifacts