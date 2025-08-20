import { GoogleGenAI, Modality } from "@google/genai";
import fs from "node:fs";
import path from "node:path";
import { logger } from "./logger";

const ai = new GoogleGenAI({
    apiKey: process.env.GOOGLE_API_KEY,
});

export const generateImage = async (prompt: string) => {
    try {
        const resp = await ai.models.generateContent({
            model: "gemini-2.0-flash-preview-image-generation",
            contents: `Generate an image for the title, use a flat design style: ${prompt}`,
            config: { responseModalities: [Modality.TEXT, Modality.IMAGE] }
        });
        return saveAsFile(resp, "image.png");
    } catch (error) {
        logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Failed to generate image');
        return null;
    }
}

const saveAsFile = (resp: any, filename: string) => {
    fs.mkdirSync("output", { recursive: true });
    const filePath = path.join("output", filename);

    for (const part of resp.candidates[0].content.parts) {
        if (part.inlineData) {
            fs.writeFileSync(filePath, Buffer.from(part.inlineData.data, "base64"));
        }
    }
    return filePath;
}