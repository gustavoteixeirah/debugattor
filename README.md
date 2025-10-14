![logo.png](logo.png)

Debugattor — a lightweight way to turn your experiments into a story you can revisit, share, and learn from.

A simple, self‑hosted tracker for ML/AI/Computer Vision experiments. Start an execution, add meaningful steps as you go, and attach artifacts (logs, images, JSON) that capture what happened. In the end, you get a clean, queryable timeline of your work.

![img.png](img.png)

---

## The problem

If you build models or pipelines, you know the pain:

- Experiment context lives across notebooks, terminals, screenshots, and DMs
- Key details (parameters, datasets, outputs) drift and get lost
- When something finally works, it’s hard to reconstruct why
- Sharing progress with teammates means pasting logs and hoping for the best

You don’t need a heavy platform. You need a tiny, reliable journal of what actually happened.

---

## The idea

Debugattor is a small execution journal:

- Start an execution when you begin an experiment
- Record steps as you move through your workflow (e.g., "Preprocess", "Train", "Evaluate")
- Attach artifacts to steps (logs, images, JSON snippets, notes)
- Review a timeline that tells the story—what you tried, what you saw, and when

It’s intentionally simple, so it fits into any workflow. Keep using your tools—just pin the highlights here.

---

## How it works (3 building blocks)

- Execution
  - A single run or attempt. Has an id, start/finish timestamps, and contains steps.
- Step
  - A named unit of work within an execution. Tracks status (RUNNING/COMPLETED/FAILED) and timestamps.
- Artifact
  - Evidence attached to a step. Can be a log line, an image, a JSON blob, or a note.

These pieces form a timeline you can browse and query, from high‑level progress to fine‑grained evidence.

---

## What you can do with it

- Keep a clean record of your experiments without changing your stack
- Share a link and let teammates explore your timeline
- Compare runs and see exactly what changed
- Capture results right where they are produced

---

## Architecture (birds‑eye view)

Debugattor is designed to be small, understandable, and replaceable by parts when needed.

- Hexagonal design (Ports & Adapters)
  - Domain (pure model): Execution, Step, Artifact
  - Application (use cases): start execution, register step, log artifact, etc.
  - Infrastructure (adapters): REST API + Postgres persistence + wiring
- Storage you can trust
  - PostgreSQL with schema migrations (Flyway) for metadata and timelines
  - Type‑safe SQL (jOOQ) for clean mapping and queries
  - MinIO (S3‑compatible) for image artifacts: images are stored in the `artifacts` bucket, with anonymous download enabled by default for easy previewing from the UI
- Simple interface
  - Spring Boot REST API
  - Minimal web UI to browse executions

This separation keeps the domain clean and makes the system easy to evolve.


## Who is this for?

- Individuals and small teams who want traceable experiments without adopting a heavy platform
- People who value clarity and reproducibility over dashboards and complexity

---

## Status and roadmap

Debugattor is a focused, evolving project. Planned directions:

- Step lifecycle endpoints (complete/fail step, finish execution)
- Labels on executions for better filtering
- Filtering and search on execution lists
- Projects: optional scoping of executions by project
- Export timeline as a formatted PDF report
- Artifact storage backends (e.g., S3/GCS for large blobs)
- Authentication (login/session or tokens)
- Authorization for multi‑user teams
- Observability: metrics and structured logs

If you’re curious, kick the tires and tell us what you wish existed.

---

## Test the API locally with Bruno (CLI)

Want to replace Postman/Insomnia with a lightweight, file‑based client? Bruno stores requests as text in your repo and runs them from the CLI.

Prerequisites:
- Node.js LTS
- Bruno CLI

Install (once):

```bash
npm install -g @usebruno/cli
```

Collection in this repo (simple format, no tests/scripts):
- Collection config: `bruno/bruno.json`
- Environments: `bruno/environments/local.bru` (optional)
- Requests:
  1. `bruno/executions/list.bru` → GET /api/executions
  2. `bruno/executions/create.bru` → POST /api/executions
  3. `bruno/executions/get_by_id.bru` → GET /api/executions/{executionId}
  4. `bruno/steps/create.bru` → POST /api/executions/{executionId}/steps
  5. `bruno/artifacts/create_log.bru` → POST /api/executions/{executionId}/steps/{stepId}/artifacts (JSON)
  6. `bruno/steps/complete.bru` → POST /api/executions/{executionId}/steps/{stepId}/complete
  7. `bruno/artifacts/upload_file.bru` → POST /api/executions/{executionId}/steps/{stepId}/artifacts/upload (multipart)
  8. `bruno/executions/delete.bru` → DELETE /api/executions/{executionId}

How to use (zsh):

1) List executions
```bash
bru run bruno/executions/list.bru
```

2) Create an execution and copy the `id` from the response
```bash
bru run bruno/executions/create.bru
```

3) Get execution by id (edit the file and replace `REPLACE_WITH_EXECUTION_ID`)
```bash
bru run bruno/executions/get_by_id.bru
```

4) Create a step in that execution (edit and replace `REPLACE_WITH_EXECUTION_ID`)
```bash
bru run bruno/steps/create.bru
```

5) Create a LOG artifact on that step (edit and replace `REPLACE_WITH_EXECUTION_ID` and `REPLACE_WITH_STEP_ID`)
```bash
bru run bruno/artifacts/create_log.bru
```

6) Complete the step (edit and replace `REPLACE_WITH_EXECUTION_ID` and `REPLACE_WITH_STEP_ID`)
```bash
bru run bruno/steps/complete.bru
```

7) Upload a file as an artifact (multipart)
- Edit `bruno/artifacts/upload_file.bru` and replace `REPLACE_WITH_EXECUTION_ID`, `REPLACE_WITH_STEP_ID`, and the file path in `file: @./path/to/local/file.png`.
```bash
bru run bruno/artifacts/upload_file.bru
```

8) Delete an execution (edit and replace `REPLACE_WITH_EXECUTION_ID`)
```bash
bru run bruno/executions/delete.bru
```

Tip: you can use `sed` to produce temporary variants with ids filled in:
```bash
cp bruno/artifacts/create_log.bru /tmp/create_log_$EXEC_ID_$STEP_ID.bru
sed -i "s/REPLACE_WITH_EXECUTION_ID/$EXEC_ID/g" /tmp/create_log_$EXEC_ID_$STEP_ID.bru
sed -i "s/REPLACE_WITH_STEP_ID/$STEP_ID/g" /tmp/create_log_$EXEC_ID_$STEP_ID.bru
bru run /tmp/create_log_$EXEC_ID_$STEP_ID.bru
```

Useful docs:
- Official docs: https://docs.usebruno.com/
- CLI: https://docs.usebruno.com/bru-cli/overview
- .bru language: https://docs.usebruno.com/bru-lang/overview
