import { CheckCircle, XCircle, Loader2 } from 'lucide-react'
import { ArtifactViewer } from './ArtifactViewer'
import { formatDateTime, formatDuration } from '@/lib/time-utils'

export function StepCard({ step, index }) {
  const icon = (() => {
    switch (step.status) {
      case 'RUNNING':
        return <Loader2 size={18} className="spin" />
      case 'COMPLETED':
        return <CheckCircle size={18} color="#22c55e" />
      case 'FAILED':
        return <XCircle size={18} color="#ef4444" />
      default:
        return null
    }
  })()

  const start = step.startTime || step.registeredAt
  const end = step.endTime || step.completedAt

  const durationMs = step.duration != null
    ? step.duration
    : (start && end ? Math.max(0, new Date(end) - new Date(start)) : null)

  return (
    <div style={{ border: '1px solid #2f2f2f', background: '#1e1e1e', borderRadius: 10, padding: 12, height: '100%', display: 'flex', flexDirection: 'column' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          <span style={{ fontSize: 12, fontFamily: 'monospace', color: '#aaa', background: '#111', padding: '2px 6px', borderRadius: 4 }}>{(index || 0) + 1}</span>
          {icon}
        </div>
        <span style={{ fontSize: 12, border: '1px solid #3a3a3a', borderRadius: 4, padding: '1px 6px' }}>{(step.artifacts || []).length} artifacts</span>
      </div>

      <div style={{ marginBottom: 8 }}>
        <div style={{ fontWeight: 600, fontSize: 14, color: '#eee' }}>{step.name}</div>
        {step.description && (
          <div style={{ fontSize: 12, color: '#aaa' }}>{step.description}</div>
        )}
      </div>

      <div style={{ borderTop: '1px solid #2a2a2a', paddingTop: 8, marginBottom: 8 }}>
        <div style={{ fontSize: 12, color: '#aaa' }}>Started: {formatDateTime(start)}</div>
        {end && <div style={{ fontSize: 12, color: '#aaa' }}>Ended: {formatDateTime(end)}</div>}
        {durationMs != null && <div style={{ fontSize: 12, color: '#aaa' }}>Duration: {formatDuration(durationMs)}</div>}
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        <div style={{ fontWeight: 600, fontSize: 13, color: '#eee' }}>Artifacts</div>
        <ArtifactViewer artifacts={step.artifacts || []} />
      </div>
    </div>
  )
}

