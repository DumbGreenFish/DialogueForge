// Add COOP/COEP headers so OPFS (SharedArrayBuffer) works for the SQLite Wasm worker.
// Auto-loaded by Kotlin/JS Gradle plugin during webpack config assembly.
config.devServer = config.devServer || {};
config.devServer.headers = {
    "Cross-Origin-Opener-Policy": "same-origin",
    "Cross-Origin-Embedder-Policy": "require-corp",
};
