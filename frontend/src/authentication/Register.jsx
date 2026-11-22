import { useState } from 'react'
import { signup, verify } from './api'

export default function Register({ onSwitchToLogin }) {
  // Two steps: fill the form, then enter the emailed verification code.
  const [step, setStep] = useState('form')

  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [code, setCode] = useState('')

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSignup(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await signup(email, username, password)
      setStep('verify')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleVerify(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await verify(email, code)
      // Verified: send them to the login form to sign in.
      onSwitchToLogin()
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const inputClass =
    'w-full mb-4 rounded bg-gray-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500'

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900 px-4">
      <form
        onSubmit={step === 'form' ? handleSignup : handleVerify}
        className="w-full max-w-sm bg-gray-800 rounded-xl p-8 shadow-lg"
      >
        <h1 className="text-2xl font-bold text-white mb-6">
          {step === 'form' ? 'Create account' : 'Verify your email'}
        </h1>

        {error && (
          <p className="mb-4 text-sm text-red-400 bg-red-950/50 rounded px-3 py-2">
            {error}
          </p>
        )}

        {step === 'form' ? (
          <>
            <label className="block text-sm text-gray-300 mb-1">Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className={inputClass}
            />

            <label className="block text-sm text-gray-300 mb-1">Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              minLength={3}
              maxLength={50}
              className={inputClass}
            />

            <label className="block text-sm text-gray-300 mb-1">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={8}
              maxLength={72}
              className={inputClass}
            />
          </>
        ) : (
          <>
            <p className="text-sm text-gray-400 mb-4">
              We sent a 6-digit code to {email}.
            </p>
            <label className="block text-sm text-gray-300 mb-1">
              Verification code
            </label>
            <input
              type="text"
              value={code}
              onChange={(e) => setCode(e.target.value)}
              required
              inputMode="numeric"
              className={inputClass}
            />
          </>
        )}

        <button
          type="submit"
          disabled={loading}
          className="w-full rounded bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white font-medium py-2 transition-colors"
        >
          {loading
            ? 'Please wait…'
            : step === 'form'
              ? 'Create account'
              : 'Verify'}
        </button>

        <p className="mt-4 text-sm text-gray-400 text-center">
          Already have an account?{' '}
          <button
            type="button"
            onClick={onSwitchToLogin}
            className="text-blue-400 hover:underline"
          >
            Sign in
          </button>
        </p>
      </form>
    </div>
  )
}
