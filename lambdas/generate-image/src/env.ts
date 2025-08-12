export const env = {
  NODE_ENV: process.env.NODE_ENV ?? "local",
  LOG_LEVEL: process.env.LOG_LEVEL ?? "info",
  APP_ENV: process.env.APP_ENV ?? "dev",   // ej. dev|staging|prod
  FEATURE_FLAG_X: process.env.FEATURE_FLAG_X === "true",
  API_BASE_URL: process.env.API_BASE_URL ?? "http://localhost:3000"
};