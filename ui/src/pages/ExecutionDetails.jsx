import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { fetchExecutionById } from '@/api/executions'
import { ExecutionSteps } from '@/components/ExecutionSteps'

export default function ExecutionDetails() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [execution, setExecution] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let mounted = true
    ;(async () => {
      try {
        const data = await fetchExecutionById(id)
        if (mounted) setExecution(data)
      } catch (e) {
        setError('Execution not found')
      } finally {
        if (mounted) setLoading(false)
      }
    })()
    return () => { mounted = false }
  }, [id])

  if (loading) return <div style={{ padding: 16 }}>Loading...</div>
  if (error) return <div style={{ padding: 16, color: 'tomato' }}>{error}</div>
  if (!execution) return null

  return (
    <ExecutionSteps execution={execution} onBack={() => navigate('/')} />
  )
}

