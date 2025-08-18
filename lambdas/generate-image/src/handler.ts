// src/handler.ts
import type { SQSBatchItemFailure, SQSHandler } from "aws-lambda";
import { logger } from "./logger";
import { DatabaseHandler } from "./database";
import { generateImage } from "./image-generator";
import { S3Service } from "./s3-service";

export const handler: SQSHandler = async (event, _context) => {
  const failures: SQSBatchItemFailure[] = [];

  logger.info({ records: event.Records.length }, "SQS batch received");

  const databaseHandler = new DatabaseHandler();
  const s3Service = new S3Service();

  try {
    await databaseHandler.connect();
  } catch (error) {
    logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Failed to connect to MongoDB');
    throw error;
  }

  await Promise.all(
    event.Records.map(async (record) => {
      const { messageId, body, attributes } = record;

      try {
        const payload = body ? JSON.parse(body) : {};
        logger.info(
          { messageId, attributes, payload },
          "Processing SQS message"
        );

        if (payload.fail) {
          throw new Error("Forced failure for demo");
        }

        // Step 1: Generate image
        const imagePath = await generateImage(payload.prompt);
        logger.info({ imagePath }, "Image generated");

        // Step 2: Upload generated image to S3
        const s3Key = payload.s3Key || `generated-image-${Date.now()}.png`;
        const contentType = payload.contentType || "image/png";
        
        logger.info({ messageId, s3Key, contentType }, "Uploading generated image to S3");
        
        const s3Url = await s3Service.uploadFileFromPath(
          imagePath,
          s3Key,
          contentType
        );

        logger.info({ 
          messageId, 
          prompt: payload.prompt,
          imagePath, 
          s3Key,
          s3Url 
        }, "Image generated and uploaded successfully");

      } catch (err: any) {
        logger.error(
          { err: err?.message, stack: err?.stack, messageId },
          "Message failed"
        );
        failures.push({ itemIdentifier: messageId });
      }
    })
  );

  // Respuesta con los elementos que fallaron
  return { batchItemFailures: failures };
};
