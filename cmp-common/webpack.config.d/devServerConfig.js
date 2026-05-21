// Ensure deep-link URLs (e.g. /calendar/day/2026-01-15) refresh correctly under the dev server.
// Cross-origin isolation headers are required by SQLite WASM OPFS (sqlite3.oo1.OpfsDb).
config.devServer = {
    ...config.devServer,
    "historyApiFallback": true,
    "headers": {
        ...((config.devServer && config.devServer.headers) || {}),
        "Cross-Origin-Opener-Policy": "same-origin",
        "Cross-Origin-Embedder-Policy": "require-corp"
    }
};
