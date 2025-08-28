import { GoogleGenAI, Modality } from "@google/genai";
import fs from "node:fs";
import path from "node:path";
import { logger } from "./logger";

const ai = new GoogleGenAI({
    apiKey: process.env.GOOGLE_API_KEY,
});

export const generateImage = async (prompt: string) => {
    try {

        const contents = `Generate an image for the title, use a flat design style: ${prompt}`;

        logger.info({ contents }, "Generating image");

        const resp = await ai.models.generateContent({
            model: "gemini-2.0-flash-preview-image-generation",
            contents,
            config: { responseModalities: [Modality.TEXT, Modality.IMAGE] }
        });

        return saveAsFile(resp, "image.png");
    } catch (error) {
        logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Failed to generate image');
        return null;
    }
}

const saveAsFile = (resp: any, filename: string) => {
    try {
        // Use /tmp directory which is writable in Lambda functions
        const outputDir = "/tmp";
        const filePath = path.join(outputDir, filename);

        // Validate response structure
        if (!resp?.candidates?.[0]?.content?.parts) {
            throw new Error('Invalid response structure from Google AI API');
        }

        let imageDataFound = false;
        for (const part of resp.candidates[0].content.parts) {
            if (part.inlineData?.data) {
                fs.writeFileSync(filePath, Buffer.from(part.inlineData.data, "base64"));
                imageDataFound = true;
                break;
            }
        }

        if (!imageDataFound) {
            throw new Error('No image data found in API response');
        }

        return filePath;
    } catch (error) {
        logger.error({ error: error instanceof Error ? error.message : String(error) }, 'Failed to save image file');
        throw error;
    }
}