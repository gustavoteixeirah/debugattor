import { useState, useEffect } from 'react'
import { Image, FileText, Code, Eye, EyeOff, X, Maximize2, Copy } from 'lucide-react'

export function ArtifactViewer({ artifacts }) {
  const [expanded, setExpanded] = useState(() => new Set(artifacts ? artifacts.map(a => a.id) : []))
  const [imageModal, setImageModal] = useState(null)
  const [jsonModal, setJsonModal] = useState(null)

  const toggle = (id) => {
    const next = new Set(expanded)
    if (next.has(id)) next.delete(id)
    else next.add(id)
    setExpanded(next)
  }

  const openImageModal = (artifact) => {
    setImageModal(artifact)
  }

  const openJsonModal = (artifact) => {
    setJsonModal(artifact)
  }

  const copyJsonToClipboard = async (artifact, event) => {
    if (event) {
      event.stopPropagation()
    }
    try {
      let textToCopy
      if (artifact.type === 'JSON_DATA') {
        try {
          const parsed = JSON.parse(artifact.content)
          textToCopy = JSON.stringify(parsed, null, 2)
        } catch {
          textToCopy = artifact.content
        }
      } else {
        textToCopy = artifact.content
      }
      await navigator.clipboard.writeText(textToCopy)
      // Feedback visual opcional - pode adicionar toast/notification aqui
    } catch (err) {
      // Fallback para browsers que não suportam clipboard API
      console.error('Failed to copy:', err)
    }
  }

  useEffect(() => {
    const handleEscape = (event) => {
      if (event.key === 'Escape') {
        if (imageModal) {
          setImageModal(null)
        } else if (jsonModal) {
          setJsonModal(null)
        }
      }
    }

    if (imageModal || jsonModal) {
      document.addEventListener('keydown', handleEscape)
      return () => document.removeEventListener('keydown', handleEscape)
    }
  }, [imageModal, jsonModal])

  const iconFor = (type) => {
    switch (type) {
      case 'IMAGE':
        return <Image size={14} />
      case 'LOG':
        return <FileText size={14} />
      case 'JSON_DATA':
        return <Code size={14} />
      default:
        return null
    }
  }

  if (!artifacts || artifacts.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '8px', color: '#777' }}>
        <FileText size={18} style={{ opacity: 0.6 }} />
        <div style={{ fontSize: 12 }}>No artifacts</div>
      </div>
    )
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 8, width: '100%', minWidth: 0 }}>
      {artifacts.map((artifact) => {
        const isExpanded = expanded.has(artifact.id)
        const name = artifact.description || artifact.id
        return (
          <div key={artifact.id} style={{ border: '1px solid #2f2f2f', borderRadius: 8, background: '#1e1e1e', minWidth: 0, width: '100%', boxSizing: 'border-box' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '8px 10px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8, minWidth: 0, flex: 1 }}>
                <div style={{ padding: 4, border: '1px solid #3a3a3a', borderRadius: 6 }}>{iconFor(artifact.type)}</div>
                <div style={{ minWidth: 0, flex: 1 }}>
                  <div style={{ fontSize: 12, fontWeight: 600, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{name}</div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginTop: 2 }}>
                    <span style={{ fontSize: 11, border: '1px solid #3a3a3a', borderRadius: 4, padding: '1px 4px' }}>{artifact.type}</span>
                  </div>
                </div>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                <button onClick={() => toggle(artifact.id)} title={isExpanded ? 'Hide' : 'Show'} style={{ background: 'transparent', border: '1px solid #3a3a3a', borderRadius: 6, padding: 4 }}>
                  {isExpanded ? <EyeOff size={14} /> : <Eye size={14} />}
                </button>
                {artifact.type === 'JSON_DATA' && (
                  <>
                    <button onClick={(e) => copyJsonToClipboard(artifact, e)} title="Copy JSON" style={{ background: 'transparent', border: '1px solid #3a3a3a', borderRadius: 6, padding: 4 }}>
                      <Copy size={14} />
                    </button>
                    <button onClick={() => openJsonModal(artifact)} title="View Full JSON" style={{ background: 'transparent', border: '1px solid #3a3a3a', borderRadius: 6, padding: 4 }}>
                      <Maximize2 size={14} />
                    </button>
                  </>
                )}
              </div>
            </div>
            {isExpanded && (
              <div style={{ padding: '0 10px 10px 10px' }}>
                {renderContent(artifact, openImageModal, openJsonModal, copyJsonToClipboard)}
              </div>
            )}
          </div>
        )
      })}
      {imageModal && (
        <div 
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.9)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            padding: 40
          }}
          onClick={() => setImageModal(null)}
        >
          <div style={{ 
            position: 'relative', 
            width: '100%', 
            height: '100%',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center'
          }}>
            <button
              onClick={() => setImageModal(null)}
              style={{
                position: 'absolute',
                top: 10,
                right: 10,
                background: 'rgba(30, 30, 30, 0.9)',
                border: '1px solid #3a3a3a',
                borderRadius: '50%',
                width: 40,
                height: 40,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
                zIndex: 1001,
                backdropFilter: 'blur(4px)'
              }}
            >
              <X size={20} color="#fff" />
            </button>
            <img
              src={imageModal.content}
              alt={imageModal.name || imageModal.id}
              style={{
                maxWidth: '100%',
                maxHeight: '100%',
                width: 'auto',
                height: 'auto',
                objectFit: 'contain',
                borderRadius: 8,
                boxShadow: '0 8px 40px rgba(0, 0, 0, 0.6)',
                display: 'block'
              }}
              onClick={(e) => e.stopPropagation()}
            />
          </div>
        </div>
      )}
      {jsonModal && (
        <div 
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.9)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
            padding: 40
          }}
          onClick={() => setJsonModal(null)}
        >
          <div 
            style={{ 
              position: 'relative', 
              width: '90%', 
              height: '90%',
              background: '#1e1e1e',
              border: '1px solid #3a3a3a',
              borderRadius: 12,
              display: 'flex',
              flexDirection: 'column',
              overflow: 'hidden'
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <div style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '16px 20px',
              borderBottom: '1px solid #3a3a3a',
              background: '#2a2a2a'
            }}>
              <div style={{ fontSize: 16, fontWeight: 600, color: '#fff' }}>
                {jsonModal.description || jsonModal.id} - JSON Data
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <button
                  onClick={() => copyJsonToClipboard(jsonModal)}
                  title="Copy JSON"
                  style={{
                    background: 'transparent',
                    border: '1px solid #3a3a3a',
                    borderRadius: 6,
                    padding: 8,
                    color: '#fff',
                    cursor: 'pointer'
                  }}
                >
                  <Copy size={16} />
                </button>
                <button
                  onClick={() => setJsonModal(null)}
                  style={{
                    background: 'transparent',
                    border: '1px solid #3a3a3a',
                    borderRadius: 6,
                    padding: 8,
                    color: '#fff',
                    cursor: 'pointer'
                  }}
                >
                  <X size={20} />
                </button>
              </div>
            </div>
            <div style={{
              flex: 1,
              overflow: 'auto',
              padding: 20
            }}>
              <div style={{ position: 'relative' }}>
                <button
                  onClick={() => copyJsonToClipboard(jsonModal)}
                  title="Copy JSON"
                  style={{
                    position: 'absolute',
                    top: 8,
                    right: 8,
                    background: 'rgba(30, 30, 30, 0.9)',
                    border: '1px solid #3a3a3a',
                    borderRadius: 6,
                    padding: 6,
                    color: '#fff',
                    cursor: 'pointer',
                    zIndex: 1,
                    backdropFilter: 'blur(4px)'
                  }}
                >
                  <Copy size={14} />
                </button>
                <pre style={{
                  background: '#141414',
                  color: '#ddd',
                  padding: 16,
                  borderRadius: 8,
                  fontSize: 14,
                  lineHeight: 1.4,
                  overflow: 'auto',
                  margin: 0,
                  border: '1px solid #2f2f2f',
                  fontFamily: 'Monaco, Consolas, "Liberation Mono", "Courier New", monospace',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                  overflowWrap: 'break-word'
                }}>
                {(() => {
                  try {
                    const parsed = JSON.parse(jsonModal.content)
                    return JSON.stringify(parsed, null, 2)
                  } catch {
                    return jsonModal.content
                  }
                })()}
                </pre>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

function renderContent(artifact, openImageModal, openJsonModal, copyJsonToClipboard) {
  switch (artifact.type) {
    case 'IMAGE':
      if (!artifact.content) return <div style={{ color: '#888', fontSize: 12 }}>No image data</div>
      return (
        <div style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', background: '#111', borderRadius: 6, border: '1px solid #2f2f2f', minHeight: 80, maxHeight: 220, overflow: 'auto' }}>
          <img
            src={artifact.content}
            alt={artifact.name || artifact.id}
            style={{ maxWidth: '100%', maxHeight: 200, objectFit: 'contain', borderRadius: 6, display: 'block', cursor: 'pointer' }}
            onClick={() => openImageModal(artifact)}
            title="Click to view full size"
          />
        </div>
      )
    case 'LOG':
      return (
        <pre style={{ background: '#141414', color: '#ddd', padding: 8, borderRadius: 6, fontSize: 12, overflow: 'auto', maxHeight: 150, border: '1px solid #2f2f2f' }}>
          {truncateLines(artifact.content)}
        </pre>
      )
    case 'JSON_DATA':
      try {
        const parsed = JSON.parse(artifact.content)
        const str = JSON.stringify(parsed, null, 2)
        return (
          <pre style={{ background: '#141414', color: '#ddd', padding: 8, borderRadius: 6, fontSize: 12, overflow: 'auto', maxHeight: 150, border: '1px solid #2f2f2f' }}>
            {truncateLines(str)}
          </pre>
        )
      } catch {
        return (
          <pre style={{ background: '#141414', color: '#ddd', padding: 8, borderRadius: 6, fontSize: 12, overflow: 'auto', maxHeight: 150, border: '1px solid #2f2f2f' }}>
            {truncateLines(artifact.content)}
          </pre>
        )
      }
    default:
      return null
  }
}

function truncateLines(text) {
  const lines = String(text || '').split('\n')
  const slice = lines.slice(0, 12)
  return slice.join('\n') + (lines.length > 12 ? '\n...' : '')
}



