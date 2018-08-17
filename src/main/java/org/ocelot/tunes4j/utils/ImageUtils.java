package org.ocelot.tunes4j.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

public class ImageUtils {

	public static Image resize(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();

	    return resizedImg;
	}

	
	public static BufferedImage blur(BufferedImage image, int[] filter, int filterWidth) {
	    if (filter.length % filterWidth != 0) {
	        throw new IllegalArgumentException("filter contains a incomplete row");
	    }

	    final int width = image.getWidth();
	    final int height = image.getHeight();
	    final int sum = IntStream.of(filter).sum();

	    int[] input = image.getRGB(0, 0, width, height, null, 0, width);

	    int[] output = new int[input.length];

	    final int pixelIndexOffset = width - filterWidth;
	    final int centerOffsetX = filterWidth / 2;
	    final int centerOffsetY = filter.length / filterWidth / 2;

	    // apply filter
	    for (int h = height - filter.length / filterWidth + 1, w = width - filterWidth + 1, y = 0; y < h; y++) {
	        for (int x = 0; x < w; x++) {
	            int r = 0;
	            int g = 0;
	            int b = 0;
	            for (int filterIndex = 0, pixelIndex = y * width + x;
	                    filterIndex < filter.length;
	                    pixelIndex += pixelIndexOffset) {
	                for (int fx = 0; fx < filterWidth; fx++, pixelIndex++, filterIndex++) {
	                    int col = input[pixelIndex];
	                    int factor = filter[filterIndex];

	                    // sum up color channels seperately
	                    r += ((col >>> 16) & 0xFF) * factor;
	                    g += ((col >>> 8) & 0xFF) * factor;
	                    b += (col & 0xFF) * factor;
	                }
	            }
	            r /= sum;
	            g /= sum;
	            b /= sum;
	            // combine channels with full opacity
	            output[x + centerOffsetX + (y + centerOffsetY) * width] = (r << 16) | (g << 8) | b | 0xFF000000;
	        }
	    }

	    BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    result.setRGB(0, 0, width, height, output, 0, width);
	    return result;
	}
	
	
	public static Image read(byte[] bytes) {
		try {
			return ImageIO.read(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
