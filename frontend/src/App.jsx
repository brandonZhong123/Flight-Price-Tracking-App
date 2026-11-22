import { useState } from 'react'
import Login from './authentication/Login'
import Register from './authentication/Register'

function App() {
  // No router yet, so a simple toggle between the two auth screens.
  const [view, setView] = useState('login')

  return view === 'login' ? (
    <Login onSwitchToRegister={() => setView('register')} />
  ) : (
    <Register onSwitchToLogin={() => setView('login')} />
  )
}

export default App
