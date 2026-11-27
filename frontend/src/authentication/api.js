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

// --- Authenticated requests -------------------------------------------------
// Everything under /tracked-flights needs the JWT in the Authorization header.

function authHeaders() {
  const token = localStorage.getItem('token')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request(path, options = {}) {
  const res = await fetch(path, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...options.headers,
    },
  })

  if (res.status === 204) return null

  let data = null
  try {
    data = await res.json()
  } catch {
    // No JSON body.
  }

  if (!res.ok) {
    const message = data?.detail || data?.message || `Request failed (${res.status})`
    throw new Error(message)
  }

  return data
}

export const getTrackedFlights = () =>
  request('/tracked-flights/my-tracked-flights')

export const createTrackedFlight = (flight) =>
  request('/tracked-flights/create', {
    method: 'POST',
    body: JSON.stringify(flight),
  })

export const deleteTrackedFlight = (id) =>
  request(`/tracked-flights/${id}`, { method: 'DELETE' })

export const getSnapshots = (id) =>
  request(`/tracked-flights/${id}/snapshots`)

// Triggers an immediate price fetch. Returns the new snapshot, or null (204)
// if no price was found.
export const checkPriceNow = (id) =>
  request(`/tracked-flights/${id}/check`, { method: 'POST' })
