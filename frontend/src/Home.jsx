import { useEffect, useState } from 'react'
import {
  getTrackedFlights,
  createTrackedFlight,
  deleteTrackedFlight,
} from './authentication/api'
import FlightHistory from './FlightHistory'

const EMPTY_FORM = {
  origin: '',
  destination: '',
  departureDate: '',
  returnDate: '',
  currency: 'USD',
  targetPrice: '',
}

export default function Home({ onLogout }) {
  const [flights, setFlights] = useState([])
  const [form, setForm] = useState(EMPTY_FORM)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [expandedId, setExpandedId] = useState(null)

  async function load() {
    setError('')
    try {
      setFlights(await getTrackedFlights())
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
  }, [])

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }))
  }

  async function handleAdd(e) {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      // Empty optional fields are sent as null, not "".
      const payload = {
        origin: form.origin.toUpperCase(),
        destination: form.destination.toUpperCase(),
        departureDate: form.departureDate,
        returnDate: form.returnDate || null,
        currency: form.currency.toUpperCase(),
        targetPrice: form.targetPrice ? Number(form.targetPrice) : null,
      }
      await createTrackedFlight(payload)
      setForm(EMPTY_FORM)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id) {
    setError('')
    try {
      await deleteTrackedFlight(id)
      setFlights((list) => list.filter((f) => f.id !== id))
    } catch (err) {
      setError(err.message)
    }
  }

  const inputClass =
    'rounded bg-gray-700 text-white px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500'

  return (
    <div className="min-h-screen bg-gray-900 text-white">
      <header className="flex items-center justify-between px-6 py-4 border-b border-gray-800">
        <h1 className="text-xl font-bold">Flight Price Tracker</h1>
        <button
          onClick={onLogout}
          className="text-sm text-gray-400 hover:text-white"
        >
          Sign out
        </button>
      </header>

      <main className="max-w-3xl mx-auto px-6 py-8">
        {error && (
          <p className="mb-6 text-sm text-red-400 bg-red-950/50 rounded px-3 py-2">
            {error}
          </p>
        )}

        {/* Add a flight */}
        <section className="bg-gray-800 rounded-xl p-6 mb-8">
          <h2 className="text-lg font-semibold mb-4">Track a new flight</h2>
          <form onSubmit={handleAdd} className="grid grid-cols-2 gap-4">
            <div className="flex flex-col">
              <label className="text-sm text-gray-300 mb-1">Origin (IATA)</label>
              <input
                value={form.origin}
                onChange={(e) => update('origin', e.target.value)}
                required
                maxLength={3}
                placeholder="JFK"
                className={inputClass}
              />
            </div>
            <div className="flex flex-col">
              <label className="text-sm text-gray-300 mb-1">
                Destination (IATA)
              </label>
              <input
                value={form.destination}
                onChange={(e) => update('destination', e.target.value)}
                required
                maxLength={3}
                placeholder="LAX"
                className={inputClass}
              />
            </div>
            <div className="flex flex-col">
              <label className="text-sm text-gray-300 mb-1">Departure</label>
              <input
                type="date"
                value={form.departureDate}
                onChange={(e) => update('departureDate', e.target.value)}
                required
                className={inputClass}
              />
            </div>
            <div className="flex flex-col">
              <label className="text-sm text-gray-300 mb-1">
                Return (optional)
              </label>
              <input
                type="date"
                value={form.returnDate}
                onChange={(e) => update('returnDate', e.target.value)}
                className={inputClass}
              />
            </div>
            <div className="flex flex-col">
              <label className="text-sm text-gray-300 mb-1">Currency</label>
              <input
                value={form.currency}
                onChange={(e) => update('currency', e.target.value)}
                required
                maxLength={3}
                className={inputClass}
              />
            </div>
            <div className="flex flex-col">
              <label className="text-sm text-gray-300 mb-1">
                Target price (optional)
              </label>
              <input
                type="number"
                min="0"
                step="0.01"
                value={form.targetPrice}
                onChange={(e) => update('targetPrice', e.target.value)}
                placeholder="400"
                className={inputClass}
              />
            </div>
            <div className="col-span-2">
              <button
                type="submit"
                disabled={saving}
                className="rounded bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white font-medium px-5 py-2 transition-colors"
              >
                {saving ? 'Adding…' : 'Add flight'}
              </button>
            </div>
          </form>
        </section>

        {/* Tracked flights */}
        <section>
          <h2 className="text-lg font-semibold mb-4">Your tracked flights</h2>

          {loading ? (
            <p className="text-gray-400">Loading…</p>
          ) : flights.length === 0 ? (
            <p className="text-gray-400">
              Nothing tracked yet. Add a flight above.
            </p>
          ) : (
            <ul className="space-y-3">
              {flights.map((f) => {
                const expanded = expandedId === f.id
                return (
                  <li key={f.id} className="bg-gray-800 rounded-lg px-4 py-3">
                    <div className="flex items-center justify-between">
                      <button
                        onClick={() => setExpandedId(expanded ? null : f.id)}
                        className="text-left flex-1"
                      >
                        <p className="font-medium">
                          <span className="text-gray-500 mr-2">
                            {expanded ? '▾' : '▸'}
                          </span>
                          {f.origin} → {f.destination}
                        </p>
                        <p className="text-sm text-gray-400 ml-5">
                          {f.departureDate}
                          {f.returnDate ? ` – ${f.returnDate}` : ' (one way)'}
                          {f.targetPrice != null &&
                            ` · target ${f.targetPrice} ${f.currency}`}
                        </p>
                      </button>
                      <button
                        onClick={() => handleDelete(f.id)}
                        className="text-sm text-red-400 hover:text-red-300 ml-4"
                      >
                        Delete
                      </button>
                    </div>
                    {expanded && <FlightHistory flight={f} />}
                  </li>
                )
              })}
            </ul>
          )}
        </section>
      </main>
    </div>
  )
}
