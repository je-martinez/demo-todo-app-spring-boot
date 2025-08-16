import { GoogleGenAI, Modality } from "@google/genai";
import fs from "node:fs";

const ai = new GoogleGenAI({
    apiKey: process.env.GOOGLE_API_KEY,
});
const resp = await ai.models.generateContent({
  model: "gemini-2.0-flash-preview-image-generation",
  contents: "Un colibrí hiperrealista bebiendo néctar, fondo bokeh",
  config: { responseModalities: [Modality.TEXT, Modality.IMAGE] }
});

for (const part of resp.candidates[0].content.parts) {
  if (part.inlineData) {
    fs.writeFileSync("hummingbird.png", Buffer.from(part.inlineData.data, "base64"));
  }
}
