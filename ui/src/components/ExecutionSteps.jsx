import { ArrowLeft } from 'lucide-react'
import { ExecutionHeader } from './ExecutionHeader'
import { StepCard } from './StepCard'

export function ExecutionSteps({ execution, onBack }) {
  const steps = execution.steps || []
  const completed = steps.filter((s) => s.status === 'COMPLETED').length
  const running = steps.filter((s) => s.status === 'RUNNING').length
  const failed = steps.filter((s) => s.status === 'FAILED').length

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(180deg, #0f0f0f, #0b0b0b)', width: '100%' }}>
      {/* Header section with fixed padding */}
      <div style={{ padding: '16px', paddingBottom: 0 }}>
        {onBack && (
          <div style={{ marginBottom: 16 }}>
            <button onClick={onBack} style={{ background: 'transparent', border: '1px solid #3a3a3a', color: '#aaa', borderRadius: 8, padding: '6px 10px', display: 'inline-flex', alignItems: 'center', gap: 6 }}>
              <ArrowLeft size={16} /> Back to Executions
            </button>
          </div>
        )}

        <ExecutionHeader execution={execution} />

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 8, marginTop: 8 }}>
          <div style={{ fontSize: 16, fontWeight: 600, color: '#fff' }}>Execution Steps ({steps.length})</div>
          <div style={{ fontSize: 13, color: '#aaa' }}>{completed} completed, {running} running, {failed} failed</div>
        </div>
      </div>

      {/* Steps section with horizontal scroll */}
      <div style={{
        overflowX: 'auto',
        paddingBottom: 16,
        paddingLeft: 16,
        paddingRight: 16,
      }}>
        <div
          style={{
            display: 'flex',
            flexWrap: 'nowrap',
            gap: 16,
            minWidth: 'fit-content',
            flexDirection: 'row',
          }}
        >
          {steps.map((step, index) => (
            <div
              key={step.id}
              style={{
                flex: '0 0 320px',
                minWidth: 320,
                maxWidth: 400,
                boxSizing: 'border-box',
              }}
            >
              <StepCard step={step} index={index} />
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

