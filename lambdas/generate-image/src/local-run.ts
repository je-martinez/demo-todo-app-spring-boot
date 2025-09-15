// src/local-run.ts
import "dotenv/config";         // <- carga .env y .env.local si existen
import { handler } from "./handler";
import * as fs from 'fs';
const event = JSON.parse(fs.readFileSync("./scripts/sample-sqs-event.json", 'utf-8'));

process.env.NODE_ENV = "local";
process.env.LOG_LEVEL = process.env.LOG_LEVEL || "debug";

(async () => {
  try {
    const res = await handler(event as any, {} as any, () => {});
    console.log("\n--- Handler result ---\n", JSON.stringify(res, null, 2));
  } catch (e) {
    console.error(e);
    process.exit(1);
  }
})();
