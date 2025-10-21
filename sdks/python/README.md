# Debugattor Python SDK

This folder contains the Python SDK for interacting with the Debugattor API, a tool for logging and tracking executions, steps, and artifacts during development and debugging processes.

## Status: Work in Progress 🚧

This SDK is currently under active development. The goal is to deliver a complete, production-ready package available through pip for easy installation and use.

## Current Capabilities

While the full pip package is in development, you can already use the SDK with the provided files:

### Core Files
- `src/lib.py` - Main library containing functions for starting executions, adding steps, and logging artifacts
- `src/http_utils.py` - HTTP utilities for making API requests with error handling

### Artifact Types Supported
- **IMAGE**: Log images from various sources (OpenCV arrays, Matplotlib figures, PIL images, file paths)
- **LOG**: Log text messages and status updates
- **JSON_DATA**: Log structured data with intelligent serialization

## Quick Start

See `debugattor_demo.ipynb` for a comprehensive demonstration of all features, including:

- Starting new executions
- Adding steps to executions
- Logging different types of artifacts
- Error handling and fallbacks

### Basic Usage Example

```python
from src.lib import start_execution, add_step, log_text_artifact

# Start a new execution
execution_id = start_execution()

# Add a step
step_id = add_step(execution_id, "Data Processing")

# Log some artifacts
log_text_artifact(execution_id, step_id, "Processing started")
```

## Future Plans

- Complete pip package with proper packaging
- Comprehensive documentation
- Additional artifact types
- Async support
- Configuration management
- CLI tools

## Requirements

- Python 3.7+
- requests library
- numpy (for image processing)
- matplotlib (optional, for plotting)
- opencv-python (optional, for computer vision)
- PIL/Pillow (optional, for image manipulation)

## Contributing

This SDK is part of the Debugattor project. Contributions are welcome as we work towards the stable release.