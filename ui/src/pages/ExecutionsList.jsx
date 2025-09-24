import { useEffect, useState } from 'react'
import { fetchExecutions } from '@/api/executions'
import { formatDateTime } from '@/lib/time-utils'
import { useNavigate } from 'react-router-dom'

export default function ExecutionsList() {
  const [executions, setExecutions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    let mounted = true
    ;(async () => {
      try {
        const data = await fetchExecutions()
        if (mounted) setExecutions(data)
      } catch (e) {
      } finally {
        if (mounted) setLoading(false)
      }
    })()
    return () => { mounted = false }
  }, [])

  if (loading) return <div style={{ padding: 16 }}>Loading...</div>
  if (error) return <div style={{ padding: 16, color: 'tomato' }}>{error}</div>

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(180deg, #0f0f0f, #0b0b0b)' }}>
      <div style={{ maxWidth: 900, margin: '0 auto', padding: 16 }}>
        <div style={{ fontSize: 22, fontWeight: 800, color: '#fff', marginBottom: 12 }}>Executions</div>
        {executions && executions.length === 0 ? (
          <div style={{ color: '#aaa' }}>No executions yet.</div>
        ) : (
          <div style={{ display: 'grid', gap: 10 }}>
            {executions.map((e) => (
              <button key={e.id} onClick={() => navigate(`/executions/${e.id}`)} style={{ textAlign: 'left', border: '1px solid #2f2f2f', background: '#1e1e1e', borderRadius: 10, padding: 12, color: '#ddd' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <div style={{ fontWeight: 700, color: '#fff' }}>Execution {String(e.id).slice(0, 8)}</div>
                  <div style={{ fontSize: 12, color: '#aaa' }}>Steps: {(e.steps || []).length}</div>
                </div>
                <div style={{ display: 'flex', gap: 12, marginTop: 6, fontSize: 13, color: '#aaa' }}>
                  <span>Started: {formatDateTime(e.startedAt)}</span>
                  <span>Finished: {e.finishedAt ? formatDateTime(e.finishedAt) : '-'}</span>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}

