const BASE = '/api';

export async function fetchExecutions() {
  const res = await fetch(`${BASE}/executions`);
  if (!res.ok) throw new Error('Failed to fetch executions');
  return res.json();
}

export async function fetchExecutionById(id) {
  const res = await fetch(`${BASE}/executions/${id}`);
  if (!res.ok) throw new Error('Execution not found');
  return res.json();
}

