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

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Stack;

/**
 * <p>Title: ImageFiller</p>
 * <p>Description: Image transformer that inverts the row color</p>
 * <p>Copyright: Copyright (c) 2003 Colin Barr�-Brisebois, �ric Paquette</p>
 * <p>Company: ETS - �cole de Technologie Sup�rieure</p>
 * @author unascribed
 * @version $Revision: 1.12 $
 */
public class ImageFiller extends AbstractTransformer {
	private ImageX currentImage;
	private int currentImageWidth;
	private Pixel fillColor = new Pixel(0xFF00FFFF);
	private Pixel borderColor = new Pixel(0xFFFFFF00);
	private boolean floodFill = true;
	private int hueThreshold = 1;
	private int saturationThreshold = 2;
	private int valueThreshold = 3;
	
	/**
	 * Creates an ImageFiller with default parameters.
	 * Default pixel change color is black.
	 */
	public ImageFiller() {
	}
	
	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_FLOODER; } 
	
	protected boolean mouseClicked(MouseEvent e){
		List intersectedObjects = Selector.getDocumentObjectsAtLocation(e.getPoint());
		if (!intersectedObjects.isEmpty()) {
			Shape shape = (Shape)intersectedObjects.get(0);
			if (shape instanceof ImageX) {
				currentImage = (ImageX)shape;
				currentImageWidth = currentImage.getImageWidth();

				Point pt = e.getPoint();
				Point ptTransformed = new Point();
				try {
					shape.inverseTransformPoint(pt, ptTransformed);
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
					return false;
				}
				ptTransformed.translate(-currentImage.getPosition().x, -currentImage.getPosition().y);
				if (0 <= ptTransformed.x && ptTransformed.x < currentImage.getImageWidth() &&
				    0 <= ptTransformed.y && ptTransformed.y < currentImage.getImageHeight()) {
					currentImage.beginPixelUpdate();
					fill(ptTransformed);
					currentImage.endPixelUpdate();											 	
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Horizontal line fill with specified color
	 */
	private void fill(Point ptClicked) {
		Stack stack = new Stack();
		stack.push(ptClicked);
        Pixel initialPixel = new Pixel(currentImage.getPixel(ptClicked.x, ptClicked.y).getARGB());
		while (!stack.empty()) {
            Point current = (Point)stack.pop();
            if (
                0 <= current.x &&
                0 <= current.y &&
                current.x < currentImage.getImageWidth() &&
                current.y < currentImage.getImageHeight() &&
                !currentImage.getPixel(current.x, current.y).equals(fillColor)
            ) {
                if ((
                    isFloodFill() &&
                    currentImage.getPixel(current.x, current.y).equalsWithHSVThresholds(
                        initialPixel,
                        getHueThreshold(),
                        (float)getSaturationThreshold()/(float)255,
                        (float)getValueThreshold()/(float)255
                    )
                ) || (
                    !isFloodFill() &&
                    !currentImage.getPixel(current.x, current.y).equalsWithHSVThresholds(
                        borderColor,
                        getHueThreshold(),
                        (float)getSaturationThreshold()/(float)255,
                        (float)getValueThreshold()/(float)255
                    )
                )) {
                    // Next points to fill.
                    currentImage.setPixel(current.x, current.y, fillColor);
                    Point nextDown = new Point(current.x, current.y+1);
                    Point nextLeft = new Point(current.x-1, current.y);
                    Point nextRight = new Point(current.x+1, current.y);
                    Point nextUp = new Point(current.x, current.y-1);
                    stack.push(nextDown);
                    stack.push(nextLeft);
                    stack.push(nextRight);
                    stack.push(nextUp);
                }
			}
		}
	}
	
	/**
	 * @return
	 */
	public Pixel getBorderColor() {
		return borderColor;
	}

	/**
	 * @return
	 */
	public Pixel getFillColor() {
		return fillColor;
	}

	/**
	 * @param pixel
	 */
	public void setBorderColor(Pixel pixel) {
		borderColor = pixel;
		System.out.println("new border color");
	}

	/**
	 * @param pixel
	 */
	public void setFillColor(Pixel pixel) {
		fillColor = pixel;
		System.out.println("new fill color");
	}
	/**
	 * @return true if the filling algorithm is set to Flood Fill, false if it is set to Boundary Fill.
	 */
	public boolean isFloodFill() {
		return floodFill;
	}

	/**
	 * @param b set to true to enable Flood Fill and to false to enable Boundary Fill.
	 */
	public void setFloodFill(boolean b) {
		floodFill = b;
		if (floodFill) {
			System.out.println("now doing Flood Fill");
		} else {
			System.out.println("now doing Boundary Fill");
		}
	}

	/**
	 * @return
	 */
	public int getHueThreshold() {
		return hueThreshold;
	}

	/**
	 * @return
	 */
	public int getSaturationThreshold() {
		return saturationThreshold;
	}

	/**
	 * @return
	 */
	public int getValueThreshold() {
		return valueThreshold;
	}

	/**
	 * @param i
	 */
	public void setHueThreshold(int i) {
		hueThreshold = i;
		System.out.println("new Hue Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setSaturationThreshold(int i) {
		saturationThreshold = i;
		System.out.println("new Saturation Threshold " + i);
	}

	/**
	 * @param i
	 */
	public void setValueThreshold(int i) {
		valueThreshold = i;
		System.out.println("new Value Threshold " + i);
	}

}
