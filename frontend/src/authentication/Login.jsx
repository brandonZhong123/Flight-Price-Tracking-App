import { useState } from 'react'
import { login } from './api'

export default function Login({ onSwitchToRegister, onLoggedIn }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await login(email, password)
      localStorage.setItem('token', res.token)
      onLoggedIn()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-sm bg-gray-800 rounded-xl p-8 shadow-lg"
      >
        <h1 className="text-2xl font-bold text-white mb-6">Sign in</h1>

        {error && (
          <p className="mb-4 text-sm text-red-400 bg-red-950/50 rounded px-3 py-2">
            {error}
          </p>
        )}

        <label className="block text-sm text-gray-300 mb-1">Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full mb-4 rounded bg-gray-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
        />

        <label className="block text-sm text-gray-300 mb-1">Password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="w-full mb-6 rounded bg-gray-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
        />

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white font-medium py-2 transition-colors"
        >
          {loading ? 'Signing in…' : 'Sign in'}
        </button>

        <p className="mt-4 text-sm text-gray-400 text-center">
          No account?{' '}
          <button
            type="button"
            onClick={onSwitchToRegister}
            className="text-blue-400 hover:underline"
          >
            Create one
          </button>
        </p>
      </form>
    </div>
  )
}
