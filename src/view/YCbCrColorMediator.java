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

package view;

import java.awt.image.BufferedImage;

import model.ObserverIF;
import model.Pixel;

class YCbCrColorMediator extends Object implements SliderObserver, ObserverIF {
	ColorSlider yCS;
	ColorSlider cbCS;
	ColorSlider crCS;
	int y;
	int cb;
	int cr;
	BufferedImage yImage;
	BufferedImage cbImage;
	BufferedImage crImage;
	int imagesWidth;
	int imagesHeight;
	ColorDialogResult result;
	
	YCbCrColorMediator(ColorDialogResult result, int imagesWidth, int imagesHeight) {
		this.imagesWidth = imagesWidth;
		this.imagesHeight = imagesHeight;
		this.y = result.getPixel().getY();
		this.cb = result.getPixel().getCb();
		this.cr = result.getPixel().getCr();
		this.result = result;
		result.addObserver(this);
		
		yImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		cbImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		crImage = new BufferedImage(imagesWidth, imagesHeight, BufferedImage.TYPE_INT_ARGB);
		computeYImage(y, cb, cr);
		computeCbImage(y, cb, cr);
		computeCrImage(y, cb, cr); 	
	}
	
	
	/*
	 * @see View.SliderObserver#update(double)
	 */
	public void update(ColorSlider s, int v) {
		boolean updateY = false;
		boolean updateCb = false;
		boolean updateCr = false;
		if (s == yCS && v != y) {
			y = v;
			updateCb = true;
			updateCr = true;
		}
		if (s == cbCS && v != cb) {
			cb = v;
			updateY = true;
			updateCr = true;
		}
		if (s == crCS && v != cr) {
			cr = v;
			updateY = true;
			updateCb = true;
		}
		if (updateY) {
			computeYImage(y, cb, cr);
		}
		if (updateCb) {
			computeCbImage(y, cb, cr);
		}
		if (updateCr) {
			computeCrImage(y, cb, cr);
		}
		
		Pixel pixel = createPixel(y, cb, cr);
		result.setPixel(pixel);
	}

    public Pixel createPixel(int y, int cb, int cr) {
        Pixel pixel = new Pixel();
        pixel.setAlpha(255);
        pixel.setYCbCr(y, cb, cr);
        return pixel;
    }
	
	public void computeYImage(int y, int cb, int cr) { 
		Pixel p = createPixel(y, cb, cr); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setYCbCr((int)(((double)i / (double)imagesWidth)*255.0), cb, cr); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				yImage.setRGB(i, j, rgb);
			}
		}
		if (yCS != null) {
			yCS.update(yImage);
		}
	}
	
	public void computeCbImage(int y, int cb, int cr) {
		Pixel p = createPixel(y, cb, cr); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setYCbCr(y, (int)(((double)i / (double)imagesWidth)*255.0), cr); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				cbImage.setRGB(i, j, rgb);
			}
		}
		if (cbCS != null) {
			cbCS.update(cbImage);
		}
	}
	
	public void computeCrImage(int y, int cb, int cr) { 
		Pixel p = createPixel(y, cb, cr); 
		for (int i = 0; i<imagesWidth; ++i) {
			p.setYCbCr(y, cb, (int)(((double)i / (double)imagesWidth)*255.0)); 
			int rgb = p.getARGB();
			for (int j = 0; j<imagesHeight; ++j) {
				crImage.setRGB(i, j, rgb);
			}
		}
		if (crCS != null) {
			crCS.update(crImage);
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage getCrImage() {
		return crImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getCbImage() {
		return cbImage;
	}

	/**
	 * @return
	 */
	public BufferedImage getYImage() {
		return yImage;
	}

	/**
	 * @param slider
	 */
	public void setYCS(ColorSlider slider) {
		yCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setCbCS(ColorSlider slider) {
		cbCS = slider;
		slider.addObserver(this);
	}

	/**
	 * @param slider
	 */
	public void setCrCS(ColorSlider slider) {
		crCS = slider;
		slider.addObserver(this);
	}
	/**
	 * @return
	 */
	public double getCr() {
		return cr;
	}

	/**
	 * @return
	 */
	public double getCb() {
		return cb;
	}

	/**
	 * @return
	 */
	public double getY() {
		return y;
	}


	/* (non-Javadoc)
	 * @see model.ObserverIF#update()
	 */
	public void update() {
		// When updated with the new "result" color, if the "currentColor"
		// is aready properly set, there is no need to recompute the images.
		Pixel currentColor = createPixel(y, cb, cr);
		if(currentColor.getARGB() == result.getPixel().getARGB()) return;
		
		y = result.getPixel().getY();
		cb = result.getPixel().getCb();
		cr = result.getPixel().getCr();
		
		yCS.setValue(y);
		cbCS.setValue(cb);
		crCS.setValue(cr);
		computeYImage(y, cb, cr);
		computeCbImage(y, cb, cr);
		computeCrImage(y, cb, cr);
		
		// Efficiency issue: When the color is adjusted on a tab in the 
		// user interface, the sliders color of the other tabs are recomputed,
		// even though they are invisible. For an increased efficiency, the 
		// other tabs (mediators) should be notified when there is a tab 
		// change in the user interface. This solution was not implemented
		// here since it would increase the complexity of the code, making it
		// harder to understand.
	}

}

