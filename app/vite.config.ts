import { VitePWA } from 'vite-plugin-pwa';
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'


// Allow using process.env in config without adding @types/node
// eslint-disable-next-line @typescript-eslint/no-explicit-any
declare const process: any

// https://vitejs.dev/config/
const rawTarget: string | undefined = process?.env?.VITE_CLUB_SERVICE_URL
const targetBase = rawTarget ? rawTarget.replace(/\/graphql\/?$/, '') : undefined

export default defineConfig(() => ({
  plugins: [
    react(),
    VitePWA({
    registerType: 'prompt',
    injectRegister: false,

    pwaAssets: {
      disabled: false,
      config: true,
    },

    manifest: {
      name: 'app',
      short_name: 'app',
      description: 'app',
      theme_color: '#ffffff',
    },

    workbox: {
      globPatterns: ['**/*.{js,css,html,svg,png,ico}'],
      cleanupOutdatedCaches: true,
      clientsClaim: true,
    },

    devOptions: {
      enabled: false,
      navigateFallback: 'index.html',
      suppressWarnings: true,
      type: 'module',
    },
  })],
  optimizeDeps: {
    include: [
      '@emotion/react',
      '@emotion/styled',
      '@mui/joy',
    ],
  },
  server: {
    proxy: targetBase
      ? {
          '/graphql': {
            target: targetBase,
            changeOrigin: true,
          },
        }
      : undefined,
  },
}))
