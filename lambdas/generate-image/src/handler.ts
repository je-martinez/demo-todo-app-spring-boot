// src/handler.ts
import type { SQSBatchItemFailure, SQSHandler } from "aws-lambda";
import { logger } from "./logger";
import { DatabaseHandler } from "./database";
import { generateImage } from "./image-generator";
import { S3Service } from "./s3-service";
import { appendCoverImageToTodo, getTodoById, markCoverImageAsFailed } from "./todos";
import fs from "node:fs";

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

        const todo = await getTodoById(payload.todoId, databaseHandler);

        // Step 1: Generate image
        const imagePath = await generateImage(todo.title);

        // Step 2 <FAILED>: If image generation fails, mark the cover image as failed
        if (!imagePath) {
          logger.error({ messageId, todoId: todo._id }, "Failed to generate image");
          await markCoverImageAsFailed(todo, databaseHandler);
          return;
        }

        // Validate that the generated image file exists and is accessible
        try {
          const fs = await import('node:fs');
          if (!fs.existsSync(imagePath)) {
            logger.error({ messageId, todoId: todo._id, imagePath }, "Generated image file not found");
            await markCoverImageAsFailed(todo, databaseHandler);
            return;
          }
        } catch (validationError) {
          logger.error({ messageId, todoId: todo._id, imagePath, error: validationError }, "Failed to validate generated image file");
          await markCoverImageAsFailed(todo, databaseHandler);
          return;
        }

        logger.info({ imagePath }, "Image generated");

        // Step 2 <OK>: Upload generated image to S3
        const s3Key = payload.s3Key || `generated-image-${Date.now()}.png`;
        const contentType = payload.contentType || "image/png";
        
        logger.info({ messageId, s3Key, contentType }, "Uploading generated image to S3");
        
        const s3Url = await s3Service.uploadFileFromPath(
          imagePath,
          s3Key,
          contentType
        );

        await appendCoverImageToTodo(todo, s3Url, databaseHandler);

        // Clean up temporary file after successful upload
        try {
          fs.unlinkSync(imagePath);
          logger.info({ imagePath }, "Temporary image file cleaned up");
        } catch (cleanupError) {
          logger.warn({ imagePath, error: cleanupError }, "Failed to clean up temporary file");
        }

        logger.info({ 
          messageId, 
          todoId: todo._id,
          todoTitle: todo.title,
          imagePath, 
          s3Key,
          s3Url 
        }, "Image generated and uploaded successfully for Todo (title: " + todo.title + ") with id: " + todo._id);

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
