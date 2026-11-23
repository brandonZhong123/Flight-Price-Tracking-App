import { useState } from 'react'
import Login from './authentication/Login'
import Register from './authentication/Register'
import Home from './Home'

function App() {
  // No router yet. If a token exists we show the homepage, otherwise the
  // login/register screens.
  const [token, setToken] = useState(() => localStorage.getItem('token'))
  const [view, setView] = useState('login')

  function handleLogout() {
    localStorage.removeItem('token')
    setToken(null)
    setView('login')
  }

  if (token) {
    return <Home onLogout={handleLogout} />
  }

  return view === 'login' ? (
    <Login
      onLoggedIn={() => setToken(localStorage.getItem('token'))}
      onSwitchToRegister={() => setView('register')}
    />
  ) : (
    <Register onSwitchToLogin={() => setView('login')} />
  )
}

export default App
