/*
   This file is part of j2dcg.
   j2dcg is free software; you can cyanistribute it and/or modify
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

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class CMYKColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider cyanCS;
	ColorSlider magentaCS;
	ColorSlider yellowCS;
	ColorSlider blackCS;
	float cyan;
	float magenta;
	float yellow;
	float black;
	BufferedImage cyanImage;
	BufferedImage magentaImage;
	BufferedImage yellowImage;
	BufferedImage blackImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	CMYKColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;

		this.cyan = result.getPixel().getCyan();
		this.magenta = result.getPixel().getMagenta();
		this.yellow = result.getPixel().getYellow();
		this.black = result.getPixel().getBlack();

		this.result = result;
		result.addObserver(this);
		
		cyanImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		magentaImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		yellowImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		blackImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);

		computeCyanImage(cyan, magenta, yellow, black);
		computeMagentaImage(cyan, magenta, yellow, black);
		computeYellowImage(cyan, magenta, yellow, black);
		computeBlackImage(cyan, magenta, yellow, black);
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateCyan = false;
		boolean updateMagenta = false;
		boolean updateYellow = false;
        boolean updateBlack = false;
		if (s == cyanCS && v != cyan) {
			cyan = (float)v/(float)255;
			updateMagenta = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == magentaCS && v != magenta) {
			magenta = (float)v/(float)255;
			updateCyan = true;
			updateYellow = true;
			updateBlack = true;
		}
		if (s == yellowCS && v != yellow) {
			yellow = (float)v/(float)255;
			updateCyan = true;
			updateMagenta = true;
			updateBlack = true;
		}
		if (s == blackCS && v != black) {
			black = (float)v/(float)255;
			updateCyan = true;
			updateMagenta = true;
			updateYellow = true;
		}
		if (updateCyan) {
			computeCyanImage(cyan, magenta, yellow, black);
		}
		if (updateMagenta) {
			computeMagentaImage(cyan, magenta, yellow, black);
		}
		if (updateYellow) {
			computeYellowImage(cyan, magenta, yellow, black);
		}

		if (updateBlack) {
			computeBlackImage(cyan, magenta, yellow, black);
		}
		
		Pixel pixel = createPixel(cyan, magenta, yellow, black);
		result.setPixel(pixel);
	}

    public Pixel createPixel(float cyan, float magenta, float yellow, float black) {
        Pixel pixel = new Pixel();
        pixel.setAlpha(255);
        pixel.setCMYK(cyan, magenta, yellow, black);
        return pixel;
    }
	
	public void computeCyanImage(float cyan, float magenta, float yellow, float black) { 
        Pixel p = createPixel(cyan, magenta, yellow, black); 
        for (int i = 0; i<imagesWidth; ++i) {
            int cyanJump = (int)Math.round(((double)i/imagesWidth));
            p.setCMYK(((float)i / imagesWidth), magenta, yellow, black); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                cyanImage.setRGB(i, j, rgb);
            }
        }
        if (cyanCS != null) {
            cyanCS.update(cyanImage);
        }
	}
	
	public void computeMagentaImage(float cyan, float magenta, float yellow, float black) {
        Pixel p = createPixel(cyan, magenta, yellow, black); 
        for (int i = 0; i<imagesWidth; ++i) {
            p.setCMYK(cyan, ((float)i / imagesWidth), yellow, black); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                magentaImage.setRGB(i, j, rgb);
            }
        }
        if (magentaCS != null) {
            magentaCS.update(magentaImage);
        }
	}
	
	public void computeYellowImage(float cyan, float magenta, float yellow, float black) { 
        Pixel p = createPixel(cyan, magenta, yellow, black); 
        for (int i = 0; i<imagesWidth; ++i) {
            p.setCMYK(cyan, magenta, ((float)i / imagesWidth), black); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                yellowImage.setRGB(i, j, rgb);
            }
        }
        if (yellowCS != null) {
            yellowCS.update(yellowImage);
        }
	}

	public void computeBlackImage(float cyan, float magenta, float yellow, float black) { 
        Pixel p = createPixel(cyan, magenta, yellow, black); 
        for (int i = 0; i<imagesWidth; ++i) {
            p.setCMYK(cyan, magenta, yellow, ((float)i / imagesWidth)); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                blackImage.setRGB(i, j, rgb);
            }
        }
        if (blackCS != null) {
            blackCS.update(blackImage);
        }
	}
	
	/**
	 * @return
	 */
	public BufferedImage getYellowImage() {
		return yellowImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getMagentaImage() {
		return magentaImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCyanImage() {
		return cyanImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getBlackImage() {
		return blackImage;
	}

	/**
	 * @param slider
	 */
	public void setCyanCS(ColorSlider slider) {
		cyanCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setMagentaCS(ColorSlider slider) {
		magentaCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setYellowCS(ColorSlider slider) {
		yellowCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @param slider
	 */
	public void setBlackCS(ColorSlider slider) {
		blackCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getYellow() {
		return yellow;
	}

	/**
	 * @return
	 */
	public double getMagenta() {
		return magenta;
	}

	/**
	 * @return
	 */
	public double getCyan() {
		return cyan;
	}

	/**
	 * @return
	 */
	public double getBlack() {
		return black;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = createPixel(cyan, magenta, yellow, black);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		cyan = result.getPixel().getCyan();
		magenta = result.getPixel().getMagenta();
		yellow = result.getPixel().getYellow();
		black = result.getPixel().getBlack();
		
		cyanCS.setValue(Math.round(cyan*255));
		magentaCS.setValue(Math.round(magenta*255));
		yellowCS.setValue(Math.round(yellow*255));
		blackCS.setValue(Math.round(black*255));
		computeCyanImage(cyan, magenta, yellow, black);
		computeMagentaImage(cyan, magenta, yellow, black);
		computeYellowImage(cyan, magenta, yellow, black);
		computeBlackImage(cyan, magenta, yellow, black);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

