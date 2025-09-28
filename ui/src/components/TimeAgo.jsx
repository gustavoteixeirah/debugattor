import { formatTimeAgo, formatDateTime } from '@/lib/time-utils'

export function TimeAgo({ date, prefix = '' }) {
  if (!date) return '-'
  
  const relativeTime = formatTimeAgo(date)
  const exactTime = formatDateTime(date)
  
  return (
    <span 
      title={exactTime}
      style={{ 
        cursor: 'help',
        textDecoration: 'underline dotted transparent',
        transition: 'text-decoration-color 0.2s ease'
      }}
      onMouseEnter={(e) => {
        e.target.style.textDecorationColor = '#666'
      }}
      onMouseLeave={(e) => {
        e.target.style.textDecorationColor = 'transparent'
      }}
    >
      {prefix}{relativeTime}
    </span>
  )
}