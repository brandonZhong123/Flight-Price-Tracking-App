import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  // Forward API calls to the Spring backend in dev, so the browser sees
  // same-origin requests and we avoid CORS during development.
  server: {
    proxy: {
      '/auth': 'http://localhost:8080',
      '/tracked-flights': 'http://localhost:8080',
    },
  },
})