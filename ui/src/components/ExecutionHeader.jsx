import { formatDateTime } from '@/lib/time-utils'
import { TimeAgo } from './TimeAgo'
import { CheckCircle2, XCircle, Loader2 } from 'lucide-react'

const StatusBadge = ({ status }) => {
  const getStatusConfig = () => {
    switch (status) {
      case 'COMPLETED':
        return {
          icon: CheckCircle2,
          label: 'Sucesso',
          bgColor: 'rgba(34, 197, 94, 0.15)',
          borderColor: 'rgba(34, 197, 94, 0.3)',
          textColor: '#22c55e',
          iconColor: '#22c55e'
        }
      case 'FAILED':
        return {
          icon: XCircle,
          label: 'Falhou',
          bgColor: 'rgba(239, 68, 68, 0.15)',
          borderColor: 'rgba(239, 68, 68, 0.3)',
          textColor: '#ef4444',
          iconColor: '#ef4444'
        }
      case 'RUNNING':
        return {
          icon: Loader2,
          label: 'Executando',
          bgColor: 'rgba(59, 130, 246, 0.15)',
          borderColor: 'rgba(59, 130, 246, 0.3)',
          textColor: '#3b82f6',
          iconColor: '#3b82f6'
        }
      default:
        return {
          icon: Loader2,
          label: 'Desconhecido',
          bgColor: 'rgba(156, 163, 175, 0.15)',
          borderColor: 'rgba(156, 163, 175, 0.3)',
          textColor: '#9ca3af',
          iconColor: '#9ca3af'
        }
    }
  }

  const config = getStatusConfig()
  const Icon = config.icon

  return (
    <div
      style={{
        display: 'inline-flex',
        alignItems: 'center',
        gap: 6,
        padding: '6px 12px',
        borderRadius: 6,
        backgroundColor: config.bgColor,
        border: `1px solid ${config.borderColor}`,
        fontSize: 13,
        fontWeight: 600,
        color: config.textColor
      }}
    >
      <Icon size={16} style={{ color: config.iconColor }} />
      <span>{config.label}</span>
    </div>
  )
}

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
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          <div style={{ fontSize: 18, fontWeight: 700, color: '#fff' }}>Execution {shortId(execution.id)}</div>
          {execution.status && <StatusBadge status={execution.status} />}
        </div>
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

