import { BrowserRouter, Routes, Route } from 'react-router-dom'
import ExecutionsList from '@/pages/ExecutionsList'
import ExecutionDetails from '@/pages/ExecutionDetails'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<ExecutionsList />} />
        <Route path="/executions/:id" element={<ExecutionDetails />} />
      </Routes>
    </BrowserRouter>
  )
}
