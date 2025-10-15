import {
  SSMClient,
  GetParameterCommand,
  GetParametersByPathCommand,
} from "@aws-sdk/client-ssm";
import {
  SecretsManagerClient,
  GetSecretValueCommand,
} from "@aws-sdk/client-secrets-manager";
import { logger } from "./logger.js";

export interface DatabaseConfig {
  mongodb_uri: string;
  database_name: string;
}

export class AWSConfigService {
  private ssmClient: SSMClient;
  private secretsClient: SecretsManagerClient;
  private configCache: Map<string, any> = new Map();
  private cacheExpiry: Map<string, number> = new Map();
  private readonly CACHE_TTL = 5 * 60 * 1000; // 5 minutes

  constructor() {
    this.ssmClient = new SSMClient({
      region: process.env.AWS_REGION || "us-east-1",
      endpoint: process.env.AWS_ENDPOINT_URL,
      credentials: {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID || "test",
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY || "test",
      },
    });

    this.secretsClient = new SecretsManagerClient({
      region: process.env.AWS_REGION || "us-east-1",
      endpoint: process.env.AWS_ENDPOINT_URL,
      credentials: {
        accessKeyId: process.env.AWS_ACCESS_KEY_ID || "test",
        secretAccessKey: process.env.AWS_SECRET_ACCESS_KEY || "test",
      },
    });
  }

  /**
   * Get a parameter from SSM Parameter Store
   */
  async getParameter(
    parameterName: string,
    withDecryption: boolean = true
  ): Promise<string> {
    const cacheKey = `ssm:${parameterName}:${withDecryption}`;

    // Check cache first
    if (this.isCacheValid(cacheKey)) {
      logger.debug(
        { parameterName, fromCache: true },
        "Retrieved parameter from cache"
      );
      return this.configCache.get(cacheKey);
    }

    try {
      logger.debug({ parameterName }, "Fetching parameter from SSM");

      const command = new GetParameterCommand({
        Name: parameterName,
        WithDecryption: withDecryption,
      });

      const response = await this.ssmClient.send(command);
      const value = response.Parameter?.Value;

      if (!value) {
        throw new Error(`Parameter ${parameterName} not found or has no value`);
      }

      // Cache the result
      this.configCache.set(cacheKey, value);
      this.cacheExpiry.set(cacheKey, Date.now() + this.CACHE_TTL);

      logger.info(
        { parameterName },
        "Successfully retrieved parameter from SSM"
      );
      return value;
    } catch (error) {
      logger.error(
        {
          parameterName,
          error: error instanceof Error ? error.message : String(error),
        },
        "Failed to retrieve parameter from SSM"
      );
      throw error;
    }
  }

  /**
   * Get multiple parameters by path from SSM Parameter Store
   */
  async getParametersByPath(
    path: string,
    withDecryption: boolean = true
  ): Promise<Record<string, string>> {
    const cacheKey = `ssm:path:${path}:${withDecryption}`;

    // Check cache first
    if (this.isCacheValid(cacheKey)) {
      logger.debug(
        { path, fromCache: true },
        "Retrieved parameters from cache"
      );
      return this.configCache.get(cacheKey);
    }

    try {
      logger.debug({ path }, "Fetching parameters by path from SSM");

      const command = new GetParametersByPathCommand({
        Path: path,
        WithDecryption: withDecryption,
        Recursive: true,
      });

      const response = await this.ssmClient.send(command);
      const parameters: Record<string, string> = {};

      if (response.Parameters) {
        for (const param of response.Parameters) {
          if (param.Name && param.Value) {
            // Remove the path prefix to get just the parameter name
            const paramName = param.Name.replace(path, "").replace(/^\//, "");
            parameters[paramName] = param.Value;
          }
        }
      }

      // Cache the result
      this.configCache.set(cacheKey, parameters);
      this.cacheExpiry.set(cacheKey, Date.now() + this.CACHE_TTL);

      logger.info(
        { path, parameterCount: Object.keys(parameters).length },
        "Successfully retrieved parameters by path from SSM"
      );
      return parameters;
    } catch (error) {
      logger.error(
        {
          path,
          error: error instanceof Error ? error.message : String(error),
        },
        "Failed to retrieve parameters by path from SSM"
      );
      throw error;
    }
  }

  /**
   * Get a secret from AWS Secrets Manager
   */
  async getSecret(secretName: string): Promise<Record<string, any>> {
    const cacheKey = `secret:${secretName}`;

    // Check cache first
    if (this.isCacheValid(cacheKey)) {
      logger.debug(
        { secretName, fromCache: true },
        "Retrieved secret from cache"
      );
      return this.configCache.get(cacheKey);
    }

    try {
      logger.debug({ secretName }, "Fetching secret from Secrets Manager");

      const command = new GetSecretValueCommand({
        SecretId: secretName,
      });

      const response = await this.secretsClient.send(command);
      const secretString = response.SecretString;

      if (!secretString) {
        throw new Error(`Secret ${secretName} not found or has no value`);
      }

      const secretValue = JSON.parse(secretString);

      // Cache the result
      this.configCache.set(cacheKey, secretValue);
      this.cacheExpiry.set(cacheKey, Date.now() + this.CACHE_TTL);

      logger.info(
        { secretName },
        "Successfully retrieved secret from Secrets Manager"
      );
      return secretValue;
    } catch (error) {
      logger.error(
        {
          secretName,
          error: error instanceof Error ? error.message : String(error),
        },
        "Failed to retrieve secret from Secrets Manager"
      );
      throw error;
    }
  }

  /**
   * Get database configuration from Secrets Manager
   */
  async getDatabaseConfig(secretName: string): Promise<DatabaseConfig> {
    const secret = await this.getSecret(secretName);

    if (!secret.mongodb_uri) {
      throw new Error("Database configuration missing mongodb_uri");
    }

    return {
      mongodb_uri: secret.mongodb_uri,
      database_name: secret.database_name || "ListifyDatabase",
    };
  }

  /**
   * Check if cache entry is still valid
   */
  private isCacheValid(cacheKey: string): boolean {
    const expiry = this.cacheExpiry.get(cacheKey);
    if (!expiry) return false;
    return Date.now() < expiry;
  }

  /**
   * Clear the configuration cache
   */
  clearCache(): void {
    this.configCache.clear();
    this.cacheExpiry.clear();
    logger.info("Configuration cache cleared");
  }

  /**
   * Get all configuration from Secrets Manager
   */
  async getAllConfig(secretName: string): Promise<{
    googleApiKey: string;
    databaseConfig: DatabaseConfig;
  }> {
    try {
      logger.info(
        { secretName },
        "Loading all configuration from AWS Secrets Manager"
      );

      const secretData = await this.getSecret(secretName);

      logger.info(
        "Successfully loaded all configuration from AWS Secrets Manager"
      );

      return {
        googleApiKey: secretData.google_api_key,
        databaseConfig: {
          mongodb_uri: secretData.mongodb_uri,
          database_name: secretData.database_name,
        },
      };
    } catch (error) {
      logger.error(
        {
          secretName,
          error: error instanceof Error ? error.message : String(error),
        },
        "Failed to load configuration from AWS Secrets Manager"
      );
      throw error;
    }
  }
}

// Create a singleton instance
export const awsConfigService = new AWSConfigService();
