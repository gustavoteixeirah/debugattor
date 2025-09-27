import { useState } from 'react'
import { Image, FileText, Code, Download, Eye, EyeOff } from 'lucide-react'

export function ArtifactViewer({ artifacts }) {
  const [expanded, setExpanded] = useState(() => new Set(artifacts ? artifacts.map(a => a.id) : []))

  const toggle = (id) => {
    const next = new Set(expanded)
    if (next.has(id)) next.delete(id)
    else next.add(id)
    setExpanded(next)
  }

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
    <div style={{ display: 'grid', gap: 8, width: '100%' }}>
      {artifacts.map((artifact) => {
        const isExpanded = expanded.has(artifact.id)
        const name = artifact.name || artifact.id
        return (
          <div key={artifact.id} style={{ border: '1px solid #2f2f2f', borderRadius: 8, background: '#1e1e1e' }}>
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
                <button onClick={() => downloadArtifact(artifact)} title="Download" style={{ background: 'transparent', border: '1px solid #3a3a3a', borderRadius: 6, padding: 4 }}>
                  <Download size={14} />
                </button>
              </div>
            </div>
            {isExpanded && (
              <div style={{ padding: '0 10px 10px 10px' }}>
                {renderContent(artifact)}
              </div>
            )}
          </div>
        )
      })}
    </div>
  )
}

function renderContent(artifact) {
  switch (artifact.type) {
    case 'IMAGE':
      if (!artifact.content) return <div style={{ color: '#888', fontSize: 12 }}>No image data</div>
      return (
        <div style={{ width: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center', background: '#111', borderRadius: 6, border: '1px solid #2f2f2f', minHeight: 80, maxHeight: 220, overflow: 'auto' }}>
          <img
            src={`data:image/png;base64,${artifact.content}`}
            alt={artifact.name || artifact.id}
            style={{ maxWidth: '100%', maxHeight: 200, objectFit: 'contain', borderRadius: 6, display: 'block' }}
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

function downloadArtifact(artifact) {
  try {
    let blob
    let filename = `${artifact.name || artifact.id}`
    switch (artifact.type) {
      case 'IMAGE': {
        const byteChars = atob(artifact.content)
        const byteNumbers = new Array(byteChars.length)
        for (let i = 0; i < byteChars.length; i++) byteNumbers[i] = byteChars.charCodeAt(i)
        const byteArray = new Uint8Array(byteNumbers)
        blob = new Blob([byteArray], { type: 'image/png' })
        filename += '.png'
        break
      }
      case 'JSON_DATA': {
        blob = new Blob([artifact.content], { type: 'application/json' })
        filename += '.json'
        break
      }
      case 'LOG':
      default: {
        blob = new Blob([artifact.content], { type: 'text/plain' })
        filename += '.txt'
        break
      }
    }
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = filename
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)
  } catch {
    // ignore
  }
}

