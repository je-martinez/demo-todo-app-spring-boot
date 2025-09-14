import { encode } from 'blurhash';
import sharp from 'sharp';
import { logger } from './logger.js';

/**
 * Generates a blurhash from an image file
 * @param imagePath - Path to the image file
 * @param componentX - Number of components in X direction (default: 4)
 * @param componentY - Number of components in Y direction (default: 3)
 * @returns Promise<string> - The generated blurhash string
 */
export const generateBlurhash = async (
  imagePath: string,
  componentX: number = 4,
  componentY: number = 3
): Promise<string> => {
  try {
    logger.info({ imagePath, componentX, componentY }, 'Generating blurhash for image');

    // Load and process the image with sharp
    const { data, info } = await sharp(imagePath)
      .resize(32, 32, { fit: 'inside' }) // Resize to small size for blurhash generation
      .ensureAlpha() // Ensure alpha channel
      .raw()
      .toBuffer({ resolveWithObject: true });

    // Generate blurhash
    const blurhash = encode(
      new Uint8ClampedArray(data),
      info.width,
      info.height,
      componentX,
      componentY
    );

    logger.info({ imagePath, blurhash }, 'Blurhash generated successfully');
    return blurhash;

  } catch (error) {
    logger.error({ 
      imagePath, 
      error: error instanceof Error ? error.message : String(error) 
    }, 'Failed to generate blurhash');
    throw new Error(`Failed to generate blurhash: ${error instanceof Error ? error.message : 'Unknown error'}`);
  }
};
