import { useEffect } from 'react';

/**
 * Hook para consumir eventos SSE genéricos.
 * @param {string} url - endpoint SSE
 * @param {function} onEvent - callback para cada evento recebido
 * @param {string} [eventName] - nome do evento SSE (opcional)
 * @param {{ enabled?: boolean }} [options] - opções (ex.: habilitar/desabilitar)
 */
export function useSSE(url, onEvent, eventName, options = {}) {
  const { enabled = true } = options;

  useEffect(() => {
    if (!enabled) return;

    const eventSource = new EventSource(url);
    const handler = (event) => {
      try {
        const data = JSON.parse(event.data);
        onEvent(data);
      } catch (e) {
        // fallback para texto puro
        onEvent(event.data);
      }
    };
    if (eventName) {
      eventSource.addEventListener(eventName, handler);
    } else {
      eventSource.onmessage = handler;
    }
    eventSource.onerror = (err) => {
      // Silencia erros intermitentes de reconexão; EventSource tenta reconectar automaticamente
      // console.debug('SSE error', err);
    };
    return () => {
      eventSource.close();
    };
  }, [url, eventName, onEvent, enabled]);
}
