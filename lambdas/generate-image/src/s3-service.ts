import { S3Client, PutObjectCommand, HeadObjectCommand } from "@aws-sdk/client-s3";
import { logger } from "./logger";
import { env } from "./env";
import { readFileSync, existsSync, statSync } from "fs";
import { extname, basename } from "path";

export class S3Service {
  private s3Client: S3Client;

  constructor() {
    this.s3Client = new S3Client({
      region: env.S3_REGION,
      credentials: {
        accessKeyId: env.AWS_ACCESS_KEY_ID,
        secretAccessKey: env.AWS_SECRET_ACCESS_KEY,
      },
      ...(env.AWS_ENDPOINT_URL && {
        endpoint: env.AWS_ENDPOINT_URL,
        forcePathStyle: true, // Required for LocalStack
      }),
    });
  }

  /**
   * Upload a file from filepath to S3 bucket
   * @param filePath - Local file path to upload
   * @param s3Key - Optional S3 key, defaults to filename
   * @param contentType - Optional content type, defaults to auto-detection
   * @returns Promise<string> - S3 URL of uploaded file
   */
  async uploadFileFromPath(
    filePath: string,
    s3Key?: string,
    contentType?: string
  ): Promise<string> {
    try {
      // Validate file exists
      if (!existsSync(filePath)) {
        throw new Error(`File not found: ${filePath}`);
      }

      // Get file stats
      const stats = statSync(filePath);
      if (stats.size === 0) {
        throw new Error(`File is empty: ${filePath}`);
      }

      // Read file content
      const fileContent = readFileSync(filePath);
      
      // Determine S3 key
      const finalS3Key = s3Key || basename(filePath);
      
      // Auto-detect content type if not provided
      const finalContentType = contentType || this.detectContentType(filePath);

      // Upload to S3
      const uploadCommand = new PutObjectCommand({
        Bucket: env.S3_BUCKET_NAME,
        Key: finalS3Key,
        Body: fileContent,
        ContentType: finalContentType,
        ContentLength: stats.size,
      });

      await this.s3Client.send(uploadCommand);

      // Generate S3 URL
      const s3Url = this.generateS3Url(finalS3Key);
      
      logger.info({
        filePath,
        s3Key: finalS3Key,
        fileSize: stats.size,
        contentType: finalContentType,
        s3Url,
      }, "File uploaded successfully to S3");

      return s3Url;

    } catch (error) {
      logger.error({
        error: error instanceof Error ? error.message : String(error),
        filePath,
        s3Key,
      }, "Failed to upload file to S3");
      throw error;
    }
  }

  /**
   * Check if a file exists in S3
   * @param s3Key - S3 key to check
   * @returns Promise<boolean> - True if file exists
   */
  async fileExists(s3Key: string): Promise<boolean> {
    try {
      const headCommand = new HeadObjectCommand({
        Bucket: env.S3_BUCKET_NAME,
        Key: s3Key,
      });
      
      await this.s3Client.send(headCommand);
      return true;
    } catch (error) {
      return false;
    }
  }

  /**
   * Generate S3 URL for a given key
   * @param s3Key - S3 key
   * @returns string - S3 URL
   */
  private generateS3Url(s3Key: string): string {
    if (env.AWS_ENDPOINT_URL) {
      // LocalStack URL
      return `${env.AWS_ENDPOINT_URL}/${env.S3_BUCKET_NAME}/${s3Key}`;
    } else {
      // AWS S3 URL
      return `https://${env.S3_BUCKET_NAME}.s3.${env.S3_REGION}.amazonaws.com/${s3Key}`;
    }
  }

  /**
   * Detect content type based on file extension
   * @param filePath - File path
   * @returns string - Content type
   */
  private detectContentType(filePath: string): string {
    const ext = extname(filePath).toLowerCase();
    
    const contentTypes: Record<string, string> = {
      '.png': 'image/png',
      '.jpg': 'image/jpeg',
      '.jpeg': 'image/jpeg',
      '.gif': 'image/gif',
      '.webp': 'image/webp',
      '.svg': 'image/svg+xml',
      '.pdf': 'application/pdf',
      '.txt': 'text/plain',
      '.json': 'application/json',
      '.xml': 'application/xml',
      '.html': 'text/html',
      '.css': 'text/css',
      '.js': 'application/javascript',
      '.ts': 'application/typescript',
    };

    return contentTypes[ext] || 'application/octet-stream';
  }
}
