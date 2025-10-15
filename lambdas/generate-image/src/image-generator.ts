import { GoogleGenAI, Modality } from "@google/genai";
import fs from "node:fs";
import path from "node:path";
import sharp from "sharp";
import { logger } from "./logger";
import { getGoogleApiKey } from "./env";

/**
 * Generate an image using the Google AI API
 * @param prompt - The prompt to generate the image
 * @param todoId - The id of the todo
 * @returns Promise<string> - Path to the saved image
 */
export const generateImage = async (prompt: string, todoId: string) => {
  try {
    // Get Google API key from AWS SSM or environment variables
    const apiKey = await getGoogleApiKey();

    if (!apiKey) {
      throw new Error("Google API key not found");
    }

    const ai = new GoogleGenAI({
      apiKey: apiKey,
    });

    const contents = `Generate an image for the title, use a flat design style: ${prompt}`;

    logger.info({ contents }, "Generating image");

    const resp = await ai.models.generateContent({
      model: "gemini-2.0-flash-preview-image-generation",
      contents,
      config: { responseModalities: [Modality.TEXT, Modality.IMAGE] },
    });

    return saveAsFile(resp, `${todoId}.png`);
  } catch (error) {
    logger.error(
      { error: error instanceof Error ? error.message : String(error) },
      "Failed to generate image"
    );
    return null;
  }
};

/**
 * Save the image to a file
 * @param resp - The response from the Google AI API
 * @param filename - The filename to save the image to
 * @returns Promise<string> - Path to the saved image
 */
const saveAsFile = (resp: any, filename: string) => {
  try {
    // Use /tmp directory which is writable in Lambda functions
    const outputDir = "/tmp";
    const filePath = path.join(outputDir, filename);

    // Validate response structure
    if (!resp?.candidates?.[0]?.content?.parts) {
      throw new Error("Invalid response structure from Google AI API");
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
      throw new Error("No image data found in API response");
    }

    return filePath;
  } catch (error) {
    logger.error(
      { error: error instanceof Error ? error.message : String(error) },
      "Failed to save image file"
    );
    throw error;
  }
};

/**
 * Generate a thumbnail from an existing image using Sharp
 * @param imagePath - Path to the source image
 * @param thumbnailPath - Path where the thumbnail should be saved
 * @param width - Thumbnail width (default: 300)
 * @param height - Thumbnail height (default: 300)
 * @returns Promise<string> - Path to the generated thumbnail
 */
export const generateThumbnail = async (
  imagePath: string,
  thumbnailPath?: string,
  width: number = 300,
  height: number = 300
): Promise<string> => {
  try {
    // Use /tmp directory which is writable in Lambda functions
    const outputDir = "/tmp";
    const finalThumbnailPath =
      thumbnailPath || path.join(outputDir, `thumbnail-${Date.now()}.png`);

    logger.info(
      {
        imagePath,
        thumbnailPath: finalThumbnailPath,
        width,
        height,
      },
      "Generating thumbnail"
    );

    await sharp(imagePath)
      .resize(width, height, {
        fit: "cover",
        position: "center",
      })
      .png({ quality: 80 })
      .toFile(finalThumbnailPath);

    logger.info(
      {
        thumbnailPath: finalThumbnailPath,
        width,
        height,
      },
      "Thumbnail generated successfully"
    );

    return finalThumbnailPath;
  } catch (error) {
    logger.error(
      {
        error: error instanceof Error ? error.message : String(error),
        imagePath,
        thumbnailPath,
        width,
        height,
      },
      "Failed to generate thumbnail"
    );
    throw error;
  }
};
