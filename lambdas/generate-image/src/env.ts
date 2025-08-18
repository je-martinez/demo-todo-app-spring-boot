export const env = {
  NODE_ENV: process.env.NODE_ENV ?? "local",
  LOG_LEVEL: process.env.LOG_LEVEL ?? "info",
  APP_ENV: process.env.APP_ENV ?? "dev",   // ej. dev|staging|prod
  FEATURE_FLAG_X: process.env.FEATURE_FLAG_X === "true",
  API_BASE_URL: process.env.API_BASE_URL ?? "http://localhost:3000",
  MONGODB_URI: process.env.MONGODB_URI ?? (() => {
    // En producción, MONGODB_URI debe estar definido
    if (process.env.NODE_ENV === 'production') {
      throw new Error('MONGODB_URI environment variable is required in production');
    }
    // Solo para desarrollo local
    return "mongodb://localhost:27017/ListifyDatabase";
  })(),
  GOOGLE_API_KEY: process.env.GOOGLE_API_KEY ?? ""
};