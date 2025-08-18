// src/handler.ts
import type { SQSBatchItemFailure, SQSHandler } from "aws-lambda";
import { logger } from "./logger";
import { DatabaseHandler } from "./database";
import { generateImage } from "./image-generator";

export const handler: SQSHandler = async (event, _context) => {
  const failures: SQSBatchItemFailure[] = [];

  logger.info({ records: event.Records.length }, "SQS batch received");

  const databaseHandler = new DatabaseHandler();

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

        const imagePath = await generateImage(payload.prompt);
        logger.info({ imagePath }, "Image generated");

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
