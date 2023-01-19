import { fileURLToPath, URL } from "url";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), vueJsx()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  build: {
    outDir: fileURLToPath(
      // 发布版本需要构建到 src 路径
      // new URL("../payment-plugin-core/src/main/resources/console", import.meta.url)
      // 开发模式直接打包到 build 路径可以免重新构建插件
      new URL("../payment-plugin-core/build/resources/main/console", import.meta.url)
    ),
    emptyOutDir: true,
    lib: {
      entry: "src/index.ts",
      name: "Payment",
      formats: ["iife"],
      fileName: () => "main.js",
    },
    rollupOptions: {
      external: [
        "vue",
        "@halo-dev/shared",
        "@halo-dev/components",
        "vue-router",
      ],
      output: {
        globals: {
          vue: "Vue",
          "vue-router": "VueRouter",
          "@halo-dev/components": "HaloComponents",
          "@halo-dev/console-shared": "HaloConsoleShared",
        },
        extend: true,
      },
    },
  },
});
