/*
   This file is part of j2dcg.
   j2dcg is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 2 of the License, or
   (at your option) any later version.
   j2dcg is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   You should have received a copy of the GNU General Public License
   along with j2dcg; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
package controller;

import model.*;

/**
 * <p>Title: ImageAbsAndNormalizeTo255Strategy</p>
 * <p>Description: Image-related strategy</p>
 * <p>Copyright: Copyright (c) 2004 Colin Barré-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.8 $
 */
public class ImageAbsAndNormalizeTo255Strategy extends ImageConversionStrategy {
	/**
	 * Converts an ImageDouble to an ImageX using a clamping strategy (0-255).
	 */
	public ImageX convert(ImageDouble image) {
        return normalizeImageXTo255(image, findHighestPixel(image));
	}

    private PixelDouble findHighestPixel(ImageDouble image) {
        int width = image.getImageWidth();
        int height = image.getImageHeight();
        PixelDouble current = null;
        PixelDouble highest = new PixelDouble();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                current = image.getPixel(x,y);
                if(pixelIsBiggerThan(current, highest)) {
                    highest = absOnPixelDouble(current);
                }
            }
        }
        
        return highest;
    }

    private Boolean pixelIsBiggerThan(PixelDouble current, PixelDouble highest) {
        return (Math.abs(current.getRed()) > highest.getRed() &&
                current.getGreen() > highest.getGreen() &&
                current.getBlue() > highest.getBlue());
    }

    private PixelDouble absOnPixelDouble(PixelDouble pixel) {
        pixel.setRed(Math.abs(pixel.getRed()));
        pixel.setGreen(Math.abs(pixel.getGreen()));
        pixel.setBlue(Math.abs(pixel.getBlue()));
        return pixel;
    }

    private ImageX normalizeImageXTo255(ImageDouble image, PixelDouble highest){
		int width = image.getImageWidth();
		int height = image.getImageHeight();
		ImageX newImage = new ImageX(0, 0, width, height);
		PixelDouble current = null;
		newImage.beginPixelUpdate();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				current = image.getPixel(x,y);
				newImage.setPixel(x, y, normalizePixelTo255(current, highest));
			}
		}
		newImage.endPixelUpdate();
		return newImage;
    }
	
	private Pixel normalizePixelTo255(PixelDouble current, PixelDouble highest) {
		return new Pixel(normalizeValueTo255(Math.abs(current.getRed()), highest.getRed()),
                normalizeValueTo255(Math.abs(current.getGreen()), highest.getGreen()),
                normalizeValueTo255(Math.abs(current.getBlue()), highest.getBlue()));
	}

    private int normalizeValueTo255(double currentValue, double highestValue) {
        return (int) (255.0 * currentValue / highestValue);
    }
}
