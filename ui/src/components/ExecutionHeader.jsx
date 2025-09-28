import { formatDateTime } from '@/lib/time-utils'
import { TimeAgo } from './TimeAgo'

export function ExecutionHeader({ execution }) {
  const started = execution.startedAt
  const finished = execution.finishedAt
  const steps = execution.steps || []
  const counts = {
    COMPLETED: steps.filter((s) => s.status === 'COMPLETED').length,
    RUNNING: steps.filter((s) => s.status === 'RUNNING').length,
    FAILED: steps.filter((s) => s.status === 'FAILED').length,
  }

  return (
    <div style={{ border: '1px solid #2f2f2f', background: '#1e1e1e', borderRadius: 10, padding: 14, marginBottom: 16 }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12, flexWrap: 'wrap' }}>
        <div style={{ fontSize: 18, fontWeight: 700, color: '#fff' }}>Execution {shortId(execution.id)}</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12, color: '#aaa', fontSize: 13 }}>
          <span>Started <TimeAgo date={started} /></span>
          <span>Finished {finished ? <TimeAgo date={finished} /> : '-'}</span>
          <span>Steps: {steps.length}</span>
          <span>✓ {counts.COMPLETED} • ▶ {counts.RUNNING} • ✕ {counts.FAILED}</span>
        </div>
      </div>
    </div>
  )
}

function shortId(id) {
  if (!id) return ''
  return String(id).slice(0, 8)
}

