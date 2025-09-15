import { MongoClient, Db, Collection, Document, ClientSession } from 'mongodb';
import { env } from './env.js';
import { logger } from './logger.js';

export class DatabaseHandler {
  private client: MongoClient | null = null;
  private db: Db | null = null;
  private isConnected = false;

  /**
   * Connects to MongoDB using the connection string from environment variables
   */
  async connect(): Promise<void> {
    try {
      if (this.isConnected && this.client) {
        logger.info('Already connected to MongoDB');
        return;
      }

      logger.info('Connecting to MongoDB...');

      this.client = new MongoClient(env.MONGODB_URI, {
        maxPoolSize: 10,
        serverSelectionTimeoutMS: 5000,
        socketTimeoutMS:  45000,
      });

      await this.client.connect();
      this.db = this.client.db();
      this.isConnected = true;
      
      logger.info('Successfully connected to MongoDB');
      
      // Test the connection
      await this.db.admin().ping();
      logger.info('MongoDB connection verified');
      
    } catch (error: unknown) {
      logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Failed to connect to MongoDB');
      this.isConnected = false;
      throw new Error(`MongoDB connection failed: ${error instanceof Error ? error.message : 'Unknown error'}`);
    }
  }

  /**
   * Disconnects from MongoDB
   */
  async disconnect(): Promise<void> {
    try {
      if (this.client) {
        await this.client.close();
        this.client = null;
        this.db = null;
        this.isConnected = false;
        logger.info('Disconnected from MongoDB');
      }
    } catch (error: unknown) {
      logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Error disconnecting from MongoDB');
      throw error;
    }
  }

  /**
   * Gets a collection from the database
   */
  getCollection<T extends Document>(collectionName: string): Collection<T> {
    if (!this.db) {
      throw new Error('Database not connected. Call connect() first.');
    }
    return this.db.collection<T>(collectionName);
  }

  /**
   * Gets the database instance
   */
  getDatabase(): Db {
    if (!this.db) {
      throw new Error('Database not connected. Call connect() first.');
    }
    return this.db;
  }

  /**
   * Checks if the database is connected
   */
  isDatabaseConnected(): boolean {
    return this.isConnected;
  }

  /**
   * Starts a new database session for transactions
   */
  async startSession(): Promise<ClientSession> {
    if (!this.client) {
      throw new Error('Database not connected. Call connect() first.');
    }
    return this.client.startSession();
  }

  /**
   * Executes a function within a database transaction
   */
  async withTransaction<T>(
    operation: (session: ClientSession) => Promise<T>
  ): Promise<T> {
    const session = await this.startSession();
    
    try {
      let result: T;
      
      await session.withTransaction(async () => {
        result = await operation(session);
      });
      
      return result!;
    } finally {
      await session.endSession();
    }
  }

  /**
   * Health check for the database connection
   */
  async healthCheck(): Promise<boolean> {
    try {
      if (!this.db) {
        return false;
      }
      
      await this.db.admin().ping();
      return true;
    } catch (error: unknown) {
      logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Database health check failed');
      return false;
    }
  }

  /**
   * Gracefully handles application shutdown
   */
  async gracefulShutdown(): Promise<void> {
    logger.info('Initiating graceful shutdown of database connection...');
    await this.disconnect();
  }
}

// Create a singleton instance
export const databaseHandler = new DatabaseHandler();

// Graceful shutdown handling
process.on('SIGINT', async () => {
  await databaseHandler.gracefulShutdown();
  process.exit(0);
});

process.on('SIGTERM', async () => {
  await databaseHandler.gracefulShutdown();
  process.exit(0);
});

export default databaseHandler;
