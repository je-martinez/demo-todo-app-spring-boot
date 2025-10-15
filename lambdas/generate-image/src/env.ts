import { awsConfigService } from "./aws-config-service.js";

// Environment variables that are always available
const baseEnv = {
  NODE_ENV: process.env.NODE_ENV ?? "local",
  LOG_LEVEL: process.env.LOG_LEVEL ?? "info",
  APP_ENV: process.env.APP_ENV ?? "dev", // ej. dev|staging|prod
  FEATURE_FLAG_X: process.env.FEATURE_FLAG_X === "true",
  API_BASE_URL: process.env.API_BASE_URL ?? "http://localhost:3000",
  // S3 Configuration
  S3_BUCKET_NAME: process.env.S3_BUCKET_NAME ?? "demo-todo-app-images",
  S3_REGION: process.env.S3_REGION ?? "us-east-1",
  AWS_ACCESS_KEY_ID: process.env.AWS_ACCESS_KEY_ID ?? "",
  AWS_SECRET_ACCESS_KEY: process.env.AWS_SECRET_ACCESS_KEY ?? "",
  S3_BASE_URL: process.env.S3_BASE_URL ?? undefined,
  AWS_ENDPOINT_URL: process.env.AWS_ENDPOINT_URL ?? undefined, // For LocalStack
  // Secrets Manager configuration
  SECRETS_MANAGER_SECRET_NAME:
    process.env.SECRETS_MANAGER_SECRET_NAME ?? "listify/generate-image",
};

// Lazy-loaded configuration from AWS services
let awsConfig: {
  googleApiKey: string;
  databaseConfig: {
    mongodb_uri: string;
    database_name: string;
  };
} | null = null;

/**
 * Get configuration from AWS Secrets Manager
 * This is called lazily to avoid initialization issues
 */
async function getAWSConfig() {
  if (!awsConfig) {
    try {
      awsConfig = await awsConfigService.getAllConfig(
        baseEnv.SECRETS_MANAGER_SECRET_NAME
      );
    } catch (error) {
      console.error(
        "Failed to load AWS configuration, falling back to environment variables:",
        error
      );

      // Fallback to environment variables if AWS services are not available
      awsConfig = {
        googleApiKey: process.env.GOOGLE_API_KEY ?? "",
        databaseConfig: {
          mongodb_uri:
            process.env.MONGODB_URI ??
            (() => {
              // Should be defined in production
              if (process.env.NODE_ENV === "production") {
                throw new Error(
                  "MONGODB_URI environment variable is required in production"
                );
              }
              // Only for local development
              return process.env.MONGODB_URI ?? "";
            })(),
          database_name: process.env.DATABASE_NAME ?? "",
        },
      };
    }
  }
  return awsConfig;
}

/**
 * Get Google API Key from AWS SSM or environment variable
 */
export async function getGoogleApiKey(): Promise<string> {
  if (process.env.NODE_ENV !== "production") {
    return process.env.GOOGLE_API_KEY ?? "";
  }

  const config = await getAWSConfig();
  return config.googleApiKey;
}

/**
 * Get MongoDB URI from AWS Secrets Manager or environment variable
 */
export async function getMongoDbUri(): Promise<string> {
  if (process.env.NODE_ENV !== "production") {
    return process.env.MONGODB_URI ?? "";
  }

  const config = await getAWSConfig();
  return config.databaseConfig.mongodb_uri;
}

/**
 * Get database name from AWS Secrets Manager or default
 */
export async function getDatabaseName(): Promise<string> {
  if (process.env.NODE_ENV !== "production") {
    return process.env.DATABASE_NAME ?? "";
  }
  const config = await getAWSConfig();
  return config.databaseConfig.database_name;
}

// Export the base environment configuration
export const env = baseEnv;
