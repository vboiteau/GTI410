/*
   This file is part of j2dcg.
   j2dcg is free software; you can hueistribute it and/or modify
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

class HSVColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider hueCS;
	ColorSlider saturationCS;
	ColorSlider valueCS;
	int hue;
	float saturation;
	float value;
	BufferedImage hueImage;
	BufferedImage saturationImage;
	BufferedImage valueImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	HSVColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.hue = result.getPixel().getHue();
		this.saturation = result.getPixel().getSaturation();
		this.value = result.getPixel().getValue();
		this.result = result;
		result.addObserver(this);
		
		hueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		saturationImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		valueImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value); 	
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateHue = false;
		boolean updateSaturation = false;
		boolean updateValue = false;
		if (s == hueCS && v != hue) {
			hue = Math.round(v*360/255);
			updateSaturation = true;
			updateValue = true;
		}
		if (s == saturationCS && v != saturation) {
			saturation = (float)v/(float)255;
            updateHue = true;
            updateValue = true;
		}
		if (s == valueCS && v != value) {
            value = (float)v/(float)255;
            updateHue = true;
            updateSaturation = true;
		}
		if (updateHue) {
			computeHueImage(hue, saturation, value);
		}
		if (updateSaturation) {
			computeSaturationImage(hue, saturation, value);
		}
		if (updateValue) {
			computeValueImage(hue, saturation, value);
		}
		
		Pixel pixel = createPixel(hue, saturation, value);
		result.setPixel(pixel);
	}

    public Pixel createPixel(int hue, float saturation, float value) {
        Pixel pixel = new Pixel();
        pixel.setAlpha(255);
        pixel.setHSV(hue, saturation, value);
        return pixel;
    }
	
	public void computeHueImage(int hue, float saturation, float value) { 
        Pixel p = createPixel(hue, saturation, value); 
        for (int i = 0; i<imagesWidth; ++i) {
            int hueJump = (int)Math.round(((double)i/imagesWidth)*360);
            p.setHSV((int)Math.round(((double)i / imagesWidth)*360), saturation, value); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                hueImage.setRGB(i, j, rgb);
            }
        }
        if (hueCS != null) {
            hueCS.update(hueImage);
        }
	}
	
	public void computeSaturationImage(int hue, float saturation, float value) {
        Pixel p = createPixel(hue, saturation, value); 
        for (int i = 0; i<imagesWidth; ++i) {
            p.setHSV(hue, ((float)i / imagesWidth), value); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                saturationImage.setRGB(i, j, rgb);
            }
        }
        if (saturationCS != null) {
            saturationCS.update(saturationImage);
        }
	}
	
	public void computeValueImage(int hue, float saturation, float value) { 
        Pixel p = createPixel(hue, saturation, value); 
        for (int i = 0; i<imagesWidth; ++i) {
            p.setHSV(hue, saturation, ((float)i / imagesWidth)); 
            int rgb = p.getARGB();
            for (int j = 0; j<imagesHeight; ++j) {
                valueImage.setRGB(i, j, rgb);
            }
        }
        if (valueCS != null) {
            valueCS.update(valueImage);
        }
	}
	
	/**
	 * @return
	 */
	public BufferedImage getValueImage() {
		return valueImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getSaturationImage() {
		return saturationImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getHueImage() {
		return hueImage;
	}

	/**
	 * @param slider
	 */
	public void setHueCS(ColorSlider slider) {
		hueCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setSaturationCS(ColorSlider slider) {
		saturationCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setValueCS(ColorSlider slider) {
		valueCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @return
	 */
	public double getSaturation() {
		return saturation;
	}

	/**
	 * @return
	 */
	public double getHue() {
		return hue;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = createPixel(hue, saturation, value);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		hue = result.getPixel().getHue();
		saturation = result.getPixel().getSaturation();
		value = result.getPixel().getValue();
		
		hueCS.setValue(Math.round(hue*255/360));
		saturationCS.setValue(Math.round(saturation*255));
		valueCS.setValue(Math.round(value*255));
		computeHueImage(hue, saturation, value);
		computeSaturationImage(hue, saturation, value);
		computeValueImage(hue, saturation, value);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

