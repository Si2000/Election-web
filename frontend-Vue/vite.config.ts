import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],

  server: {
    proxy: {
      '/tk': {
        target: 'https://gegevensmagazijn.tweedekamer.nl',
        changeOrigin: true,
        secure: false,
        rewrite: path => path.replace(/^\/tk/, '')
      }
    }
  },

  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})
