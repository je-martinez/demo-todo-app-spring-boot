import { GoogleGenAI, Modality } from "@google/genai";
import fs from "node:fs";
import path from "node:path";

const ai = new GoogleGenAI({
    apiKey: process.env.GOOGLE_API_KEY,
});

export const generateImage = async (prompt: string) => {
    const resp = await ai.models.generateContent({
        model: "gemini-2.0-flash-preview-image-generation",
        contents: prompt,
        config: { responseModalities: [Modality.TEXT, Modality.IMAGE] }
    });
    return saveAsFile(resp, "image.png");
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