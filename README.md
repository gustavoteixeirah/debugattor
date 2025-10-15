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

## Architecture (bird’s‑eye view)

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

---

## Run locally (for development)

- Start infrastructure (Postgres + MinIO) with Docker Compose:

```bash
cd scripts
docker compose -f compose.yaml up -d
```

- Backend API (port 8080 by default):

```bash
cd backend
gradle :infrastructure:bootRun
```

- Web UI (Vite dev server on port 5173 by default):

```bash
cd ui
npm install
npm run dev
```

---

## Who is this for?

- Individuals and small teams who want traceable experiments without adopting a heavy platform
- People who value clarity and reproducibility over dashboards and complexity

---

## Status and roadmap

Debugattor is a focused, evolving project. Planned directions:

- Labels on executions for better filtering
- Authentication (login/session or tokens)
- Projects: optional scoping of executions by project
- Export timeline as a formatted PDF report
- Step lifecycle endpoints (complete/fail step, finish execution)
- Filtering and search on execution lists
- Artifact storage backends (e.g., S3/GCS for large blobs)
- Authorization for multi‑user teams
- Observability: metrics and structured logs

If you’re curious, kick the tires and tell us what you wish existed.

---

## Test the API locally with Bruno (CLI)

We keep HTTP requests as versioned .bru files in this repo. Run them with the bru CLI.

#### Where the requests live

- Collection: bruno/bruno.json
- (Optional) env: bruno/environments/local.bru
- Requests used in the example below:
  - bruno/executions/create.bru
  - bruno/steps/create.bru
  - bruno/artifacts/create_log.bru
    
### References

- [Bruno website](https://www.usebruno.com/)