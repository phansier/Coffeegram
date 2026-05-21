// Ensure deep-link URLs (e.g. /calendar/day/2026-01-15) refresh correctly under the dev server.
config.devServer = {
    ...config.devServer,
    "historyApiFallback": true
};
