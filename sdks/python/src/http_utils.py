"""
HTTP request utilities.

This module contains generic functions for making HTTP requests
with error handling and logging.
"""

import requests
from typing import Optional, Dict, Any, Union
import json


def make_http_request(
    method: str,
    url: str,
    data: Optional[Union[Dict[str, Any], str]] = None,
    headers: Optional[Dict[str, str]] = None,
    timeout: int = 30,
    json_data: Optional[Dict[str, Any]] = None
) -> requests.Response:
    """
    Executes a generic HTTP request.
    
    Args:
        method (str): HTTP method (GET, POST, PUT, DELETE, etc.).
        url (str): Complete URL for the request.
        data (Optional[Union[Dict[str, Any], str]]): Data to send in the body.
        headers (Optional[Dict[str, str]]): Request headers.
        timeout (int): Timeout in seconds. Default is 30.
        json_data (Optional[Dict[str, Any]]): JSON data to send.
    
    Returns:
        requests.Response: HTTP request response object.
    
    Raises:
        requests.exceptions.RequestException: If there's an error in the request.
        requests.exceptions.Timeout: If the request exceeds the timeout.
        requests.exceptions.ConnectionError: If unable to connect to the server.
    """
    # Headers padrão
    default_headers = {
        "Content-Type": "application/json"
    }
    
    # Combine default headers with custom headers
    if headers:
        default_headers.update(headers)
    
    try:
        # Prepare request arguments
        request_kwargs = {
            "url": url,
            "headers": default_headers,
            "timeout": timeout
        }
        
        # Adicionar dados conforme o tipo
        if json_data is not None:
            request_kwargs["json"] = json_data
        elif data is not None:
            request_kwargs["data"] = data
        
        # Fazer a requisição
        response = requests.request(method.upper(), **request_kwargs)
        
        # Basic log for debug
        # print(f"[DEBUG] {method.upper()} {url}")
        # print(f"[DEBUG] Status Code: {response.status_code}")
        
        return response
        
    except requests.exceptions.Timeout:
        print(f"[ERROR] Timeout in request to {url}")
        raise
    except requests.exceptions.ConnectionError:
        print(f"[ERROR] Connection error with {url}")
        raise
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] Request error: {e}")
        raise


def make_post_request(
    url: str,
    json_data: Optional[Dict[str, Any]] = None,
    headers: Optional[Dict[str, str]] = None,
    timeout: int = 30
) -> requests.Response:
    """
    Convenience function for POST requests.
    
    Args:
        url (str): URL for the POST request.
        json_data (Optional[Dict[str, Any]]): JSON data to send.
        headers (Optional[Dict[str, str]]): Additional headers.
        timeout (int): Timeout in seconds.
    
    Returns:
        requests.Response: Request response.
    """
    return make_http_request(
        method="POST",
        url=url,
        json_data=json_data,
        headers=headers,
        timeout=timeout
    )


def parse_json_response(response: requests.Response) -> Optional[Dict[str, Any]]:
    """
    Extracts JSON data from an HTTP response.
    
    Args:
        response (requests.Response): HTTP response object.
    
    Returns:
        Optional[Dict[str, Any]]: JSON data or None if not valid JSON.
    """
    try:
        return response.json()
    except json.JSONDecodeError:
        print(f"[WARNING] Response is not valid JSON: {response.text[:100]}")
        return None
    except Exception as e:
        print(f"[ERROR] Error processing JSON: {e}")
        return None