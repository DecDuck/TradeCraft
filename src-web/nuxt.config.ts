// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  modules: ["@nuxtjs/tailwindcss"],
  app: {
    head: {
      link: [{ rel: "icon", href: "/api/v1/branding/favicon" }],
    },
  },
  vite: {
    server: {
      proxy: {
        "/api": "http://127.0.0.1:3000",
      },
    },
  },
  ssr: false,
});
