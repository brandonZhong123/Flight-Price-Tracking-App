import { useEffect, useState } from 'react'
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts'
import { getSnapshots, checkPriceNow } from './authentication/api'

// Shows the price history for one flight: a line chart plus current/lowest
// price, and a button to fetch a fresh price on demand.
export default function FlightHistory({ flight }) {
  const [snapshots, setSnapshots] = useState([])
  const [loading, setLoading] = useState(true)
  const [checking, setChecking] = useState(false)
  const [error, setError] = useState('')

  async function load() {
    setError('')
    try {
      setSnapshots(await getSnapshots(flight.id))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    load()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [flight.id])

  async function handleCheckNow() {
    setError('')
    setChecking(true)
    try {
      await checkPriceNow(flight.id)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setChecking(false)
    }
  }

  // Snapshots come newest-first from the API; the chart reads left-to-right
  // oldest-to-newest. Format the timestamp to a short date for the axis.
  const data = [...snapshots]
    .reverse()
    .map((s) => ({
      date: new Date(s.checkedAt).toLocaleDateString(undefined, {
        month: 'short',
        day: 'numeric',
      }),
      price: Number(s.price),
    }))

  const prices = data.map((d) => d.price)
  const current = prices.length ? prices[prices.length - 1] : null
  const lowest = prices.length ? Math.min(...prices) : null

  return (
    <div className="mt-3 border-t border-gray-700 pt-4">
      {error && (
        <p className="mb-3 text-sm text-red-400 bg-red-950/50 rounded px-3 py-2">
          {error}
        </p>
      )}

      <div className="flex items-center justify-between mb-3">
        <div className="text-sm text-gray-300 space-x-4">
          {current != null && (
            <span>
              Current:{' '}
              <span className="text-white font-medium">
                {current} {flight.currency}
              </span>
            </span>
          )}
          {lowest != null && (
            <span>
              Lowest:{' '}
              <span className="text-green-400 font-medium">
                {lowest} {flight.currency}
              </span>
            </span>
          )}
        </div>
        <button
          onClick={handleCheckNow}
          disabled={checking}
          className="text-sm rounded bg-gray-700 hover:bg-gray-600 disabled:opacity-50 px-3 py-1 text-white"
        >
          {checking ? 'Checking…' : 'Check now'}
        </button>
      </div>

      {loading ? (
        <p className="text-gray-400 text-sm">Loading history…</p>
      ) : data.length === 0 ? (
        <p className="text-gray-400 text-sm">
          No price history yet. Use “Check now” to fetch a price.
        </p>
      ) : (
        <ResponsiveContainer width="100%" height={200}>
          <LineChart data={data} margin={{ top: 8, right: 16, bottom: 0, left: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
            <XAxis dataKey="date" stroke="#9ca3af" fontSize={12} />
            <YAxis stroke="#9ca3af" fontSize={12} domain={['auto', 'auto']} />
            <Tooltip
              contentStyle={{
                backgroundColor: '#1f2937',
                border: '1px solid #374151',
                borderRadius: 8,
                color: '#fff',
              }}
            />
            <Line
              type="monotone"
              dataKey="price"
              stroke="#60a5fa"
              strokeWidth={2}
              dot={{ r: 3 }}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  )
}
