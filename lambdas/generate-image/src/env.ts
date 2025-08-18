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
  GOOGLE_API_KEY: process.env.GOOGLE_API_KEY ?? "",
  // S3 Configuration
  S3_BUCKET_NAME: process.env.S3_BUCKET_NAME ?? "demo-todo-app-images",
  S3_REGION: process.env.S3_REGION ?? "us-east-1",
  AWS_ACCESS_KEY_ID: process.env.AWS_ACCESS_KEY_ID ?? "",
  AWS_SECRET_ACCESS_KEY: process.env.AWS_SECRET_ACCESS_KEY ?? "",
  AWS_ENDPOINT_URL: process.env.AWS_ENDPOINT_URL ?? undefined, // For LocalStack
};