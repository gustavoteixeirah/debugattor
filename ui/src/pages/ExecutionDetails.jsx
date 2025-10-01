import { useEffect, useState, useCallback } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { fetchExecutionById } from '@/api/executions'
import { ExecutionSteps } from '@/components/ExecutionSteps'
import { useSSE } from '@/lib/useSSE'

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

  // Atualiza steps em tempo real (normaliza o payload do SSE)
  const handleStepEvent = useCallback((payload) => {
    const step = {
      id: payload.id || payload.stepId,
      executionId: payload.executionId,
      name: payload.name,
      description: payload.description,
    }
    setExecution(prev => {
      if (!prev || prev.id !== step.executionId) return prev
      const exists = prev.steps?.some(s => s.id === step.id)
      if (exists) return prev
      return {
        ...prev,
        steps: [...(prev.steps || []), { ...step, artifacts: [] }]
      }
    })
  }, [])

  // Atualiza artifacts em tempo real (normaliza o payload do SSE)
  const handleArtifactEvent = useCallback((payload) => {
    const artifact = {
      id: payload.id || payload.artifactId,
      stepId: payload.stepId,
      type: payload.type,
      description: payload.description,
      content: payload.url || payload.content,
    }
    setExecution(prev => {
      if (!prev) return prev
      const stepIdx = prev.steps?.findIndex(s => s.id === artifact.stepId)
      if (stepIdx === undefined || stepIdx < 0) return prev
      const step = prev.steps[stepIdx]
      const exists = step.artifacts?.some(a => a.id === artifact.id)
      if (exists) return prev
      const updatedStep = {
        ...step,
        artifacts: [...(step.artifacts || []), artifact]
      }
      const steps = [...prev.steps]
      steps[stepIdx] = updatedStep
      return { ...prev, steps }
    })
  }, [])

  useSSE(`/api/events/steps`, handleStepEvent, 'step-registered', { enabled: !loading && !!execution })
  useSSE(`/api/events/artifacts`, handleArtifactEvent, 'artifact-registered', { enabled: !loading && !!execution })

  if (loading) return <div style={{ padding: 16 }}>Loading...</div>
  if (error) return <div style={{ padding: 16, color: 'tomato' }}>{error}</div>
  if (!execution) return null

  return (
    <ExecutionSteps execution={execution} onBack={() => navigate('/')} />
  )
}
