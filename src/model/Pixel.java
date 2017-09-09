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

package model;

import java.awt.Color;
import java.util.ArrayList;

/**
 * <p>Title: Pixel</p>
 * <p>Description: Class that handles Pixel data in various formats</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barré-Brisebois, Eric Paquette</p>
 * <p>Company: ETS - École de Technologie Supérieure</p>
 * @author Colin Barré-Brisebois
 * @version $Revision: 1.11 $
 */
public class Pixel {
    /** ARGB Pixel value */
    private int valueARGB;
    
    /**
     */
    public Pixel() {
		valueARGB = 0;
    }
    
	/**
	 * Pixel constructor with a specified ARGB value
	 * @param valueARGB the pixel's ARGB value
	 */
    public Pixel(int valueARGB) {
        this.valueARGB = valueARGB;
    }
    
    public Pixel(int rValue, int gValue, int bValue) {
    	setRed(rValue);
    	setGreen(gValue);
    	setBlue(bValue);
    	setAlpha(255);
    }
    
	public Pixel(int rValue, int gValue, int bValue, int alpha) {
		setRed(rValue);
		setGreen(gValue);
		setBlue(bValue);
		setAlpha(alpha);
	}    
    
    public Pixel(PixelDouble pixel) {
		setRed((int)pixel.getRed());
		setGreen((int)pixel.getGreen());
		setBlue((int)pixel.getBlue());
		setAlpha((int)pixel.getAlpha());
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's ARGB value
	 */    
    public int getARGB() { 
    	return (valueARGB); 
    }

	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's ALPHA value
	 */        
    public int getAlpha() { 
    	return ((valueARGB >> 24) & 0xff); 
    }

	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's RED value
	 */            
    public int getRed() { 
    	return ((valueARGB >> 16) & 0xff); 
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's GREEN value
	 */            
    public int getGreen() { 
    	return ((valueARGB >> 8) & 0xff); 
    }
    
	/**
	 * Returns an attribute of the pixel
	 * @return the pixel's BLUE value
	 */            
    public int getBlue() { 
    	return ((valueARGB) & 0xff); 
    }

    public int getRGBMaxIndex() {
        ArrayList<Integer> RGB = new ArrayList<Integer>();
        RGB.add(getRed());
        RGB.add(getGreen());
        RGB.add(getBlue());
        return RGB.indexOf(getRGBMax());
    }

    public int getRGBMin() {
        return Math.min(Math.min(getRed(), getGreen()), getBlue());
    }

    public int getRGBMax() {
        return Math.max(Math.max(getRed(), getGreen()), getBlue());
    }

    public int getRGBCoverage() {
        return getRGBMax() - getRGBMin();
    }

    public float getHueModificatorFromRGB() {
        float mod;
        switch (getRGBMaxIndex()) {
            case 0:
                mod = (float)((getGreen() - getBlue()) / (float)getRGBCoverage());
                if (mod < 0) {
                   mod += 6; 
                }
                break;
            case 1:
                mod = (float)(getBlue() - getRed()) / (float)getRGBCoverage() + 2;
                break;
            case 2:
            default:
                mod = (float)(getRed() - getGreen()) / (float)getRGBCoverage() + 4;
                break;
        }
        return mod;
    }

    public int getHue() {
        int C = getRGBCoverage();
        if (C > 0) {
            return Math.max(0, Math.min(Math.round(getHueModificatorFromRGB() * 60), 360));
        }
        return C;
    }

    public float getSaturation() {
        int coverage = getRGBCoverage();
        if (coverage > 0) {
            return (float)coverage / (float)getRGBMax();
        }
        return coverage;
    }

    public float getValue() {
        return (float)getRGBMax() / (float)255;
    }

    public float[] getRGBModFromHSV(int hue, float saturation, float value) {
        float C =  value * saturation;
        float X = C * (1 - Math.abs(((float)hue / (float)60)%2 - 1));
        float[] mod = new float[]{0, 0, 0};
        switch((int)Math.floor(((float)hue / (float)60))) {
            case 0:
                mod[0] = C;
                mod[1] = X;
                break;
            case 1:
                mod[1] = C;
                mod[0] = X;
                break;
            case 2:
                mod[1] = C;
                mod[2] = X;
                break;
            case 3:
                mod[2] = C;
                mod[1] = X;
                break;
            case 4:
                mod[0] = X;
                mod[2] = C;
                break;
            case 5:
            default:
                mod[0] = C;
                mod[2] = X;
                break;
        }
        return mod;
    }

    public void setHSV(int hue, float saturation, float value) {
        float C = value * saturation;
        float[] rgbModFromHSV = getRGBModFromHSV(hue, saturation, value);
        float m = value - C;
        setRed(Math.round((rgbModFromHSV[0]+m)*255));
        setGreen(Math.round((rgbModFromHSV[1]+m)*255));
        setBlue(Math.round((rgbModFromHSV[2]+m)*255));
    }

    public void setSaturation(float saturation) {
        setHSV(getHue(), saturation, getValue());
    }

    public void setValue(float value) {
        setHSV(getHue(), getSaturation(), value);
    }
	/**
	 * Sets an attribute of the pixel
	 * @param valueARGB the pixel's ARGB value
	 */            
    public void setARGB(int valueARGB) { 
		this.valueARGB = valueARGB; 
    }

	/** Sets the color, ignores null pixel. */
    public void setColor(Pixel p) {
	    if (p == null) return;
	    setARGB(p.valueARGB);
	}
	
	/**
	 * Sets an attribute of the pixel
	 * @param valueAlpha the pixel's ALPHA value
	 */               	
    public void setAlpha(int valueAlpha) { 
    	valueARGB = (valueARGB & 0x00ffffff) | ((valueAlpha & 0xff) << 24);
    }

	/**
	 * Sets an attribute of the pixel
	 * @param valueRed the pixel's RED value
	 */               
	public void setRed(int valueRed) { 
		valueARGB = (valueARGB & 0xff00ffff) | ((valueRed & 0xff) << 16);
	}

	/**
	 * Sets an attribute of the pixel
	 * @param valueGreen the pixel's GREEN value
	 */               
	public void setGreen(int valueGreen) { 
		valueARGB = (valueARGB & 0xffff00ff) | ((valueGreen & 0xff) << 8);
	}

	/**
	 * Sets an attribute of the pixel
	 * @param valueBlue the pixel's BLUE value
	 */               
	public void setBlue(int valueBlue) { 
		valueARGB = (valueARGB & 0xffffff00) | ((valueBlue & 0xff));
	}

    public void setCMYK(float c, float m, float y, float k) {
        setRed(Math.round(255 * (1-c) * (1-k)));
        setGreen(Math.round(255 * (1-m) * (1-k)));
        setBlue(Math.round(255 * (1-y) * (1-k)));
    }

    public float[] getCYMKRGBMod() {
        return new float[]{
            (float)getRed()/(float)255,
            (float)getGreen()/(float)255,
            (float)getBlue()/(float)255,
        };
    }

    public float getBlack(){
        float[] mods = getCYMKRGBMod();
        return 1 - Math.max(Math.max(mods[0], mods[1]), mods[2]);
    }

    public float getCyan() {
        return (1-getCYMKRGBMod()[0]-getBlack()) / (1-getBlack());
    }

    public float getMagenta() {
        return (1-getCYMKRGBMod()[1]-getBlack()) / (1-getBlack());
    }

    public float getYellow() {
        return (1-getCYMKRGBMod()[2]-getBlack()) / (1-getBlack());
    }

    public void setYCbCr(int y, int cb, int cr) {
        int red = Math.max(0, Math.min((int)(1*y+1.402*(cr-128)), 255));
        int green = Math.max(0,Math.min((int)(1*y-0.344136*(cb-128)-0.714136*(cr-128)), 255));
        int blue = Math.max(0, Math.min((int)((1*y+1.772*(cb-128))), 255));
        setRed(red);
        setGreen(green);
        setBlue(blue);
    }

    public int getY() {
        return (int)Math.round(0.299*getRed()+0.587*getGreen()+0.114*getBlue());
    }

    public int getCb() {
        return (int)Math.round(128-0.168736*getRed()-0.331264*getGreen()+0.5*getBlue());
    }

    public int getCr() {
        return (int)Math.round(128+0.5*getRed()-0.418688*getGreen()-0.081312*getBlue());
    }

	/**
	 * Object's toString() method redefinition
	 */               
    public String toString() {
        return new String("(R-" + getRed() + 
                          " G-" + getGreen() + 
                          " B-" + getBlue() + 
                          " A-" + getAlpha() + 
                          ")");
    }

	//Temp/Will see if keeping    
    /**
     * Convert pixel to Color
     * @return color value
     */
    public Color toColor() {
		return new Color((float)getRed() / 255.0F, 
		             	 (float)getGreen() / 255.0F,
		             	 (float)getBlue() / 255.0F);    	
    }
    
    /* 
     * Compute if two colors are the same, based on their ARGB values.
     */ 
    public boolean equals(Object o) {
    	if (o instanceof Pixel) {
    		return (((Pixel)o).getARGB() == getARGB());
    	}
    	return false;
    }

    public boolean compareHueThresholdOnTarget(Pixel target, int hueThreshold) {
        return Math.abs(target.getHue() - getHue()) <= hueThreshold;
    }

    public boolean compareSaturationThresholdOnTarget(Pixel target, float saturationThreshold) {
        return Math.abs(target.getSaturation() - getSaturation()) <= saturationThreshold;
    }

    public boolean compareValueThresholdOnTarget(Pixel target, float valueThreshold) {
        return Math.abs(target.getValue() - getValue()) <= valueThreshold;
    }

    public boolean equalsWithHSVThresholds(Pixel target, int hueThreshold, float saturationThreshold, float valueThreshold) {
        boolean isHueEqual = compareHueThresholdOnTarget(target, hueThreshold);
        boolean isSaturationEqual = compareSaturationThresholdOnTarget(target, saturationThreshold);

        boolean isValueEqual = compareValueThresholdOnTarget(target, valueThreshold);

        return (
            isHueEqual &&
            isSaturationEqual &&
            isValueEqual
        );
    }
}
