import { useEffect, useState } from 'react'
import { fetchExecutions } from '@/api/executions'
import { formatDateTime } from '@/lib/time-utils'
import { useNavigate } from 'react-router-dom'
import { Trash2 } from 'lucide-react'
import { TimeAgo } from '@/components/TimeAgo'

export default function ExecutionsList() {
  const [executions, setExecutions] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [deleteModal, setDeleteModal] = useState(null)
  const navigate = useNavigate()

  useEffect(() => {
    const handleKeyPress = (event) => {
      if (deleteModal && event.key === 'Enter') {
        confirmDelete()
      } else if (deleteModal && event.key === 'Escape') {
        cancelDelete()
      }
    }

    if (deleteModal) {
      document.addEventListener('keydown', handleKeyPress)
      return () => document.removeEventListener('keydown', handleKeyPress)
    }
  }, [deleteModal])

  const openDeleteModal = (executionId, event) => {
    event.stopPropagation()
    setDeleteModal(executionId)
  }

  const confirmDelete = async () => {
    if (!deleteModal) return
    
    try {
      const response = await fetch(`/api/executions/${deleteModal}`, {
        method: 'DELETE',
      })
      
      if (response.ok) {
        setExecutions(prev => prev.filter(exec => exec.id !== deleteModal))
        setDeleteModal(null)
      } else {
        setError('Failed to delete execution')
      }
    } catch (err) {
      setError('Failed to delete execution')
    }
  }

  const cancelDelete = () => {
    setDeleteModal(null)
  }

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
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    <div style={{ fontSize: 12, color: '#aaa' }}>Steps: {(e.steps || []).length}</div>
                    <button
                      onClick={(event) => openDeleteModal(e.id, event)}
                      style={{
                        background: 'transparent',
                        border: '1px solid #3a3a3a',
                        borderRadius: 6,
                        padding: 4,
                        color: '#ff6b6b',
                        cursor: 'pointer',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                      title="Delete execution"
                    >
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 12, marginTop: 6, fontSize: 13, color: '#aaa' }}>
                  <span>Started <TimeAgo date={e.startedAt} /></span>
                  <span>Finished {e.finishedAt ? <TimeAgo date={e.finishedAt} /> : '-'}</span>
                </div>
              </button>
            ))}
          </div>
        )}
      </div>
      
      {deleteModal && (
        <div 
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.7)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000
          }}
          onClick={cancelDelete}
        >
          <div 
            style={{
              background: '#1e1e1e',
              border: '1px solid #3a3a3a',
              borderRadius: 12,
              padding: 24,
              maxWidth: 400,
              width: '90%',
              boxShadow: '0 8px 32px rgba(0, 0, 0, 0.5)'
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{ fontSize: 18, fontWeight: 600, color: '#fff', marginBottom: 8 }}>
              Delete Execution
            </div>
            <div style={{ color: '#aaa', marginBottom: 20, lineHeight: 1.5 }}>
              Are you sure you want to delete execution <strong style={{ color: '#fff' }}>{String(deleteModal).slice(0, 8)}</strong>?
              <br />This action cannot be undone.
            </div>
            <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
              <button
                onClick={cancelDelete}
                style={{
                  background: 'transparent',
                  border: '1px solid #3a3a3a',
                  borderRadius: 6,
                  padding: '8px 16px',
                  color: '#ddd',
                  cursor: 'pointer',
                  fontSize: 14
                }}
              >
                Cancel
              </button>
              <button
                onClick={confirmDelete}
                style={{
                  background: '#ff6b6b',
                  border: 'none',
                  borderRadius: 6,
                  padding: '8px 16px',
                  color: '#fff',
                  cursor: 'pointer',
                  fontSize: 14,
                  fontWeight: 600
                }}
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

