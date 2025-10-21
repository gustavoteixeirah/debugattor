"""
Library for debugging and executions.

This module contains specific functions to start executions
in the debug API.
"""

from typing import Optional
import os
import sys

# Import handling for both package and direct execution
try:
    from .http_utils import make_post_request, parse_json_response
except ImportError:
    # Add current directory to path for direct execution
    current_dir = os.path.dirname(os.path.abspath(__file__))
    sys.path.insert(0, current_dir)
    try:
        from http_utils import make_post_request, parse_json_response
    except ImportError:
        # Fallback: implement basic functionality inline
        import requests
        import json
        
        def make_post_request(url, json_data=None, headers=None, timeout=30):
            default_headers = {"Content-Type": "application/json"}
            if headers:
                default_headers.update(headers)
            return requests.post(url, json=json_data, headers=default_headers, timeout=timeout)
        
        def parse_json_response(response):
            try:
                return response.json()
            except json.JSONDecodeError:
                return None


def start_execution() -> Optional[str]:
    """
    Starts a new execution in the API and returns the execution ID.
    
    Makes a POST request to 'localhost:8080/api/executions' to start
    a new execution and extracts the ID from the JSON response.
    
    Returns:
        Optional[str]: Execution ID if successful, None if there's an error.
    
    Examples:
        >>> execution_id = start_execution()
        >>> if execution_id:
        >>>     print(f"Execution started with ID: {execution_id}")
        >>> else:
        >>>     print("Failed to start execution")
    
    Expected API Response:
        {
            "id": "7a2554de-7c7f-42ea-a79a-4c57ff7146d6",
            "steps": null,
            "startedAt": "2025-09-28T01:45:19.78764-03:00",
            "finishedAt": null
        }
    """
    url = "http://localhost:8080/api/executions"
    
    try:
        # Fazer requisição POST
        response = make_post_request(url)
        
        # Verificar se foi bem-sucedida
        response.raise_for_status()
        
        # Parsear resposta JSON
        data = parse_json_response(response)
        
        if data and "id" in data:
            execution_id = data["id"]
            print(f"[INFO] Execution started with ID: {execution_id}")
            return execution_id
        else:
            print("[ERROR] API response does not contain the 'id' field")
            return None
            
    except Exception as e:
        print(f"[ERROR] Failed to start execution: {e}")
        return None


def add_step(execution_id: str, step_name: str) -> Optional[str]:
    """
    Adds a step to an existing execution and returns the step ID.
    
    Makes a POST request to 'localhost:8080/api/executions/{execution_id}/steps'
    to add a new step to the specified execution and returns the created step ID
    for later use (e.g., logging artifacts).
    
    Args:
        execution_id (str): ID of the execution where to add the step.
        step_name (str): Name of the step to be added.
    
    Returns:
        Optional[str]: Step ID if successful, None if there's an error.
    
    Examples:
        >>> step_id = add_step("362e803b-0484-46d9-a726-05e537060eb1", "loading images")
        >>> if step_id:
        >>>     print(f"Step created with ID: {step_id}")
        >>> else:
        >>>     print("Failed to add step")
    
    Expected API Request:
        POST /api/executions/{execution_id}/steps
        Content-Type: application/json
        {
            "name": "loading images"
        }
    
    Expected API Response:
        {
            "id": "6699b347-7686-4a5f-9a4b-976f6e364e94",
            "name": "loading images",
            "status": "RUNNING",
            "artifacts": [],
            "registeredAt": "2025-09-28T05:05:32.103538Z",
            "completedAt": null
        }
    """
    if not execution_id:
        print("[ERROR] execution_id is required")
        return None
    
    if not step_name:
        print("[ERROR] step_name is required")
        return None
    
    url = f"http://localhost:8080/api/executions/{execution_id}/steps"
    data = {"name": step_name}
    
    try:
        # Fazer requisição POST
        response = make_post_request(url, json_data=data)
        
        # Verificar se foi bem-sucedida
        response.raise_for_status()
        
        # Parsear resposta JSON
        step_data = parse_json_response(response)
        
        if step_data and "id" in step_data:
            step_id = step_data["id"]
            print(f"[INFO] Step '{step_name}' created with ID: {step_id}")
            return step_id
        else:
            print("[ERROR] API response does not contain the step 'id' field")
            return None
        
    except Exception as e:
        print(f"[ERROR] Failed to add step: {e}")
        return None


def log_image_artifact(
    execution_id: str, 
    step_id: str, 
    image, 
    description: str,
    filename: Optional[str] = None
) -> bool:
    """
    Logs an image artifact for a specific step.
    
    Accepts different types of image input and uploads to the API.
    Supports: numpy arrays (OpenCV), matplotlib figures, file paths, etc.
    
    Args:
        execution_id (str): Execution ID.
        step_id (str): Step ID where to add the artifact.
        image: Image in different formats:
            - str/Path: path to image file
            - numpy.ndarray: OpenCV/matplotlib image array
            - matplotlib.figure.Figure: matplotlib figure
            - bytes: raw image bytes
        description (str): Artifact description.
        filename (Optional[str]): File name. If None, will be generated automatically.
    
    Returns:
        bool: True if the artifact was logged successfully, False otherwise.
    
    Examples:
        >>> # With OpenCV file
        >>> image = cv2.imread("image.jpg")
        >>> log_image_artifact(exec_id, step_id, image, "Original image")
        
        >>> # With file path
        >>> log_image_artifact(exec_id, step_id, "/path/to/image.png", "Input image")
        
        >>> # With matplotlib figure
        >>> fig, ax = plt.subplots()
        >>> ax.plot([1,2,3])
        >>> log_image_artifact(exec_id, step_id, fig, "Plot result")
    """
    if not execution_id:
        print("[ERROR] execution_id is required")
        return False
    
    if not step_id:
        print("[ERROR] step_id is required")
        return False
    
    if not description:
        print("[ERROR] description is required")
        return False
    
    try:
        # Conditional imports to avoid mandatory dependencies
        import tempfile
        import io
        from pathlib import Path
        
        # Determine input type and convert to bytes
        image_bytes, detected_filename = _process_image_input(image, filename)
        
        if image_bytes is None:
            print("[ERROR] Could not process the provided image")
            return False
        
        # Use provided filename or detected one
        final_filename = filename or detected_filename or "image.png"
        
        # Build API URL
        url = f"http://localhost:8080/api/executions/{execution_id}/steps/{step_id}/artifacts/upload"
        
        # Prepare form data
        form_data = {
            'type': ('', 'IMAGE'),
            'description': ('', description)
        }
        
        # Prepare file
        files = {
            'file': (final_filename, io.BytesIO(image_bytes), 'image/jpeg')
        }
        
        # Debug da requisição
        print(f"[DEBUG] POST {url}")
        print(f"[DEBUG] Form data: {form_data}")
        print(f"[DEBUG] File: {final_filename}, size: {len(image_bytes)} bytes")
        
        # Fazer requisição multipart/form-data
        response = _make_multipart_request(url, form_data, files)
        
        # Debug da resposta
        print(f"[DEBUG] Response status: {response.status_code}")
        
        # Verificar sucesso
        response.raise_for_status()
        
        print(f"[INFO] Artifact '{description}' logged successfully for step {step_id}")
        return True
        
    except Exception as e:
        print(f"[ERROR] Failed to log image artifact: {e}")
        return False


def _process_image_input(image, filename_hint=None):
    """
    Processes different types of image input and converts to PNG bytes.
    
    Returns:
        tuple: (image_bytes, detected_filename)
    """
    try:
        import numpy as np
        from pathlib import Path
        
        # Case 1: String or Path (file path)
        if isinstance(image, (str, Path)):
            image_path = Path(image)
            if image_path.exists():
                with open(image_path, 'rb') as f:
                    return f.read(), image_path.name
            else:
                print(f"[ERROR] File not found: {image_path}")
                return None, None
        
        # Case 2: Direct bytes
        elif isinstance(image, bytes):
            detected_name = filename_hint or "image.png"
            return image, detected_name
        
        # Case 3: numpy array (OpenCV)
        elif isinstance(image, np.ndarray):
            return _numpy_to_png_bytes(image), "opencv_image.jpg"
        
        # Case 4: matplotlib Figure
        elif hasattr(image, 'savefig') and hasattr(image, 'canvas'):
            return _matplotlib_to_png_bytes(image), "matplotlib_figure.png"
        
        # Case 5: PIL Image
        elif hasattr(image, 'save') and hasattr(image, 'mode'):
            return _pil_to_png_bytes(image), "pil_image.png"
        
        else:
            print(f"[ERROR] Unsupported image type: {type(image)}")
            return None, None
            
    except ImportError as e:
        print(f"[ERROR] Dependency not found: {e}")
        return None, None
    except Exception as e:
        print(f"[ERROR] Error processing image: {e}")
        return None, None


def _numpy_to_png_bytes(img_array):
    """Converts numpy array to PNG bytes with optimized compression."""
    try:
        import cv2
        import numpy as np
        
        # Normalizar array se necessário
        if img_array.dtype != np.uint8:
            if img_array.max() <= 1.0:
                img_array = (img_array * 255).astype(np.uint8)
            else:
                img_array = img_array.astype(np.uint8)
        
        # Convert BGR to RGB if OpenCV (3 channels)
        if len(img_array.shape) == 3 and img_array.shape[2] == 3:
            img_array = cv2.cvtColor(img_array, cv2.COLOR_BGR2RGB)
        
        # Resize image if too large to reduce file size
        height, width = img_array.shape[:2]
        max_dimension = 800  # Maximum 800px for any dimension
        
        if height > max_dimension or width > max_dimension:
            scale = max_dimension / max(height, width)
            new_width = int(width * scale)
            new_height = int(height * scale)
            img_array = cv2.resize(img_array, (new_width, new_height), interpolation=cv2.INTER_AREA)
        
        # Encode to JPEG with reduced quality for smaller files
        encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 85]
        success, buffer = cv2.imencode('.jpg', img_array, encode_param)
        if success:
            return buffer.tobytes()
        else:
            raise ValueError("Failed to encode image to JPEG")
    except ImportError:
        # Fallback using PIL if OpenCV is not available
        try:
            from PIL import Image
            import numpy as np
            
            if len(img_array.shape) == 3:
                img = Image.fromarray(img_array.astype(np.uint8), 'RGB')
            else:
                img = Image.fromarray(img_array.astype(np.uint8), 'L')
            
            import io
            buffer = io.BytesIO()
            img.save(buffer, format='PNG')
            return buffer.getvalue()
        except ImportError:
            raise ImportError("OpenCV or PIL are required to process numpy arrays")
def _matplotlib_to_png_bytes(fig):
    """Converts matplotlib figure to PNG bytes."""
    import io
    
    buffer = io.BytesIO()
    fig.savefig(buffer, format='png', bbox_inches='tight', dpi=150)
    buffer.seek(0)
    return buffer.getvalue()


def _pil_to_png_bytes(pil_image):
    """Converts PIL Image to PNG bytes."""
    import io
    
    buffer = io.BytesIO()
    # Convert to RGB if necessary
    if pil_image.mode in ('RGBA', 'LA', 'P'):
        pil_image = pil_image.convert('RGB')
    pil_image.save(buffer, format='PNG')
    buffer.seek(0)
    return buffer.getvalue()


def _make_multipart_request(url, form_data, files):
    """Makes multipart/form-data request."""
    import requests
    
    # Convert form_data to the format expected by requests
    data = {key: value[1] for key, value in form_data.items()}
    
    return requests.post(url, data=data, files=files)


def log_artifact(execution_id: str, step_id: str, artifact_type: str, content: str) -> bool:
    """
    Logs a generic artifact for a specific step.
    
    Makes a POST request to 'localhost:8080/api/executions/{execution_id}/steps/{step_id}/artifacts'
    to add an artifact of any type to the specified step.
    
    Args:
        execution_id (str): Execution ID.
        step_id (str): Step ID where to add the artifact.
        artifact_type (str): Artifact type (LOG, JSON_DATA, etc.).
        content (str): Artifact content as string.
    
    Returns:
        bool: True if the artifact was logged successfully, False otherwise.
    """
    if not execution_id:
        print("[ERROR] execution_id is required")
        return False
    
    if not step_id:
        print("[ERROR] step_id is required")
        return False
    
    if not artifact_type:
        print("[ERROR] artifact_type is required")
        return False
    
    url = f"http://localhost:8080/api/executions/{execution_id}/steps/{step_id}/artifacts"
    data = {
        "type": artifact_type,
        "content": content
    }
    
    try:
        # Debug da requisição
        print(f"[DEBUG] POST {url}")
        print(f"[DEBUG] Data: {data}")
        
        # Fazer requisição POST
        response = make_post_request(url, json_data=data)
        
        # Verificar sucesso
        response.raise_for_status()
        
        print(f"[INFO] Artifact '{artifact_type}' logged successfully for step {step_id}")
        return True
        
    except Exception as e:
        print(f"[ERROR] Failed to log artifact: {e}")
        return False


def log_text_artifact(execution_id: str, step_id: str, message: str) -> bool:
    """
    Logs a text/log artifact for a specific step.
    
    Convenient for logging simple log messages.
    
    Args:
        execution_id (str): Execution ID.
        step_id (str): Step ID where to add the artifact.
        message (str): Log message.
    
    Returns:
        bool: True if the artifact was logged successfully, False otherwise.
    
    Examples:
        >>> log_text_artifact(exec_id, step_id, "Converted to BW")
        >>> log_text_artifact(exec_id, step_id, "Processing completed successfully")
    """
    return log_artifact(execution_id, step_id, "LOG", message)


def complete_step(execution_id: str, step_id: str) -> bool:
    """
    Marks a step as complete.
    
    Makes a POST request to 'localhost:8080/api/executions/{execution_id}/steps/{step_id}/complete'
    to mark the specified step as completed.
    
    Args:
        execution_id (str): Execution ID.
        step_id (str): Step ID to be marked as complete.
    
    Returns:
        bool: True if the step was marked as complete successfully, False otherwise.
    
    Examples:
        >>> success = complete_step("375ca9af-8e31-43fc-a4a1-5a08458b146f", "dca044e2-fd3b-4d70-9582-6059417244c9")
        >>> if success:
        >>>     print("Step marked as complete")
        >>> else:
        >>>     print("Failed to complete step")
    
    Expected API Request:
        POST /api/executions/{execution_id}/steps/{step_id}/complete
    
    Expected API Response:
        Status 200 OK (no response body required)
    """
    if not execution_id:
        print("[ERROR] execution_id is required")
        return False
    
    if not step_id:
        print("[ERROR] step_id is required")
        return False
    
    url = f"http://localhost:8080/api/executions/{execution_id}/steps/{step_id}/complete"
    
    try:
        # Debug da requisição
        print(f"[DEBUG] POST {url}")
        
        # Fazer requisição POST sem dados
        response = make_post_request(url)
        
        # Verificar sucesso
        response.raise_for_status()
        
        print(f"[INFO] Step {step_id} marked as complete successfully")
        return True
        
    except Exception as e:
        print(f"[ERROR] Failed to complete step: {e}")
        return False


def log_json_artifact(execution_id: str, step_id: str, data) -> bool:
    """
    Logs a JSON artifact for a specific step.
    
    Accepts any Python object and attempts to convert it to JSON using multiple strategies.
    Serialization strategies:
    1. Direct JSON (basic types)
    2. Dictionary conversion via __dict__ or asdict()
    3. List conversion via to_dict() or similar
    4. Fallback to string representation
    
    Args:
        execution_id (str): Execution ID.
        step_id (str): Step ID where to add the artifact.
        data: Any Python object to serialize as JSON.
    
    Returns:
        bool: True if the artifact was logged successfully, False otherwise.
    
    Examples:
        >>> # With dictionary
        >>> data = {"step": "enhance_contrast", "status": "success"}
        >>> log_json_artifact(exec_id, step_id, data)
        
        >>> # With list of complex objects
        >>> answer_groups = [AnswerLineGroup(...), AnswerLineGroup(...)]
        >>> log_json_artifact(exec_id, step_id, answer_groups)
        
        >>> # With dataclass object
        >>> group = AnswerLineGroup(...)
        >>> log_json_artifact(exec_id, step_id, group)
    """
    import json
    
    def _try_convert_to_serializable(obj):
        """Attempts to convert an object to JSON serializable format."""
        
        # Estratégia 1: Já é serializável
        try:
            json.dumps(obj)
            return obj
        except (TypeError, ValueError):
            pass
        
        # Strategy 2: List of objects - try to convert each item
        if isinstance(obj, (list, tuple)):
            try:
                converted_items = []
                for item in obj:
                    converted_items.append(_try_convert_to_serializable(item))
                return converted_items
            except:
                pass
        
        # Strategy 3: Dataclass - use dataclasses.asdict()
        try:
            from dataclasses import asdict, is_dataclass
            if is_dataclass(obj):
                return asdict(obj)
        except (ImportError, TypeError):
            pass
        
        # Estratégia 4: Objeto com método to_dict()
        if hasattr(obj, 'to_dict') and callable(obj.to_dict):
            try:
                return obj.to_dict()
            except:
                pass
        
        # Estratégia 5: Objeto com método dict()
        if hasattr(obj, 'dict') and callable(obj.dict):
            try:
                return obj.dict()
            except:
                pass
        
        # Strategy 6: Use __dict__ if available
        if hasattr(obj, '__dict__'):
            try:
                # Recursively convert __dict__ items
                result = {}
                for key, value in obj.__dict__.items():
                    if key.startswith('_'):  # Skip private attributes
                        continue
                    try:
                        result[key] = _try_convert_to_serializable(value)
                    except:
                        result[key] = str(value)
                return result
            except:
                pass
        
        # Estratégia 7: Tipos especiais conhecidos
        if hasattr(obj, 'x') and hasattr(obj, 'y') and hasattr(obj, 'radius'):
            # Provavelmente um Circle
            try:
                return {
                    'x': obj.x,
                    'y': obj.y, 
                    'radius': obj.radius,
                    'marked': getattr(obj, 'marked', False)
                }
            except:
                pass
        
        # Fallback: convert to string
        return str(obj)
    
    try:
        # Tentar serialização direta primeiro
        json_content = json.dumps(data, ensure_ascii=False, indent=None)
        print(f"[DEBUG] Direct JSON serialization successful")
    except (TypeError, ValueError, OverflowError) as e:
        print(f"[DEBUG] Direct JSON failed, trying conversion strategies: {e}")
        
        try:
            # Tentar conversão inteligente
            converted_data = _try_convert_to_serializable(data)
            json_content = json.dumps(converted_data, ensure_ascii=False, indent=None)
            print(f"[DEBUG] Smart conversion successful")
        except Exception as conversion_error:
            print(f"[DEBUG] Smart conversion failed: {conversion_error}")
            
            # Fallback: structure with metadata
            try:
                # Tentar extrair informações úteis se for uma lista
                if isinstance(data, (list, tuple)) and len(data) > 0:
                    sample_item = data[0]
                    fallback_data = {
                        "total_items": len(data),
                        "item_type": type(sample_item).__name__,
                        "sample_item": str(sample_item),
                        "raw_data": repr(data)
                    }
                else:
                    fallback_data = {
                        "data_type": type(data).__name__,
                        "raw_data": repr(data)
                    }
                
                json_content = json.dumps(fallback_data, ensure_ascii=False, indent=None)
                print(f"[DEBUG] Fallback serialization with metadata successful")
            except Exception as final_error:
                # Último recurso
                json_content = json.dumps({
                    "error": f"Complete serialization failed: {final_error}",
                    "data_str": str(data)
                }, ensure_ascii=False)
                print(f"[DEBUG] Final fallback serialization used")
    
    return log_artifact(execution_id, step_id, "JSON_DATA", json_content)


if __name__ == "__main__":
    """Example usage when executed directly."""
    print("Testing execution start...")
    
    # Start execution
    execution_id = start_execution()
    if execution_id:
        print(f"✅ Execution created successfully: {execution_id}")
        
        # Add some example steps
        steps = ["loading images", "processing data", "detecting circles"]
        
        for i, step in enumerate(steps):
            step_id = add_step(execution_id, step)
            if step_id:
                print(f"✅ Step '{step}' created with ID: {step_id}")
                
                # Demonstrate image artifact logging (only for first step)
                if i == 0:
                    try:
                        import numpy as np
                        # Create example image
                        demo_image = np.zeros((50, 80, 3), dtype=np.uint8)
                        demo_image[10:40, 20:60] = [0, 255, 255]  # Cyan rectangle
                        
                        success_img = log_image_artifact(
                            execution_id, step_id, demo_image, 
                            f"Demo image for {step}"
                        )
                        if success_img:
                            print(f"  📸 Image artifact added to step")
                        
                        # Add LOG and JSON artifacts for demonstration
                        log_text_artifact(execution_id, step_id, f"Step '{step}' completed successfully")
                        
                        demo_data = {"step": step, "status": "completed", "demo": True}
                        log_json_artifact(execution_id, step_id, demo_data)
                        
                        # Mark step as complete
                        if complete_step(execution_id, step_id):
                            print(f"  ✅ Step '{step}' marked as complete")
                        
                    except ImportError:
                        print(f"  ⚠️ NumPy not available for image demo")
                else:
                    # For other steps, just mark as complete after adding basic artifact
                    log_text_artifact(execution_id, step_id, f"Step '{step}' completed successfully")
                    complete_step(execution_id, step_id)
            else:
                print(f"❌ Failed to add step '{step}'")
    else:
        print("❌ Failed to create execution")
