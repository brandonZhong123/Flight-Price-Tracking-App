// Minimal auth API helper. Posts JSON and surfaces the backend's error message.
// The backend returns RFC 9457 ProblemDetail on errors, where the human-readable
// text is in the `detail` field.

async function post(path, body) {
  const res = await fetch(path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })

  let data = null
  try {
    data = await res.json()
  } catch {
    // Some responses (or network errors) have no JSON body.
  }

  if (!res.ok) {
    const message = data?.detail || data?.message || `Request failed (${res.status})`
    throw new Error(message)
  }

  return data
}

export const login = (email, password) => post('/auth/login', { email, password })

export const signup = (email, username, password) =>
  post('/auth/signup', { email, username, password })

export const verify = (email, verificationCode) =>
  post('/auth/verify', { email, verificationCode })
