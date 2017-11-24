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

import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.List;

import view.Application;
import view.CurvesPanel;

import model.BezierCurveType;
import model.BSplineCurveType;
import model.ControlPoint;
import model.Curve;
import model.CurvesModel;
import model.DocObserver;
import model.Document;
import model.HermiteCurveType;
import model.PolylineCurveType;
import model.Shape;
import model.Line;

/**
 * <p>Title: Curves</p>
 * <p>Description: (AbstractTransformer)</p>
 * <p>Copyright: Copyright (c) 2004 Sébastien Bois, Eric Paquette</p>
 * <p>Company: (ÉTS) - École de Technologie Supérieure</p>
 * @author unascribed
 * @version $Revision: 1.9 $
 */
public class Curves extends AbstractTransformer implements DocObserver {
		
	/**
	 * Default constructor
	 */
	public Curves() {
		Application.getInstance().getActiveDocument().addObserver(this);
	}	

	/* (non-Javadoc)
	 * @see controller.AbstractTransformer#getID()
	 */
	public int getID() { return ID_CURVES; }
	
	public void activate() {
		firstPoint = true;
		Document doc = Application.getInstance().getActiveDocument();
		List selectedObjects = doc.getSelectedObjects();
		boolean selectionIsACurve = false; 
		if (selectedObjects.size() > 0){
			Shape s = (Shape)selectedObjects.get(0);
			if (s instanceof Curve){
				curve = (Curve)s;
				firstPoint = false;
				cp.setCurveType(curve.getCurveType());
				cp.setNumberOfSections(curve.getNumberOfSections());
			}
			else if (s instanceof ControlPoint){
				curve = (Curve)s.getContainer();
				firstPoint = false;
			}
		}
		
		if (firstPoint) {
			// First point means that we will have the first point of a new curve.
			// That new curve has to be constructed.
			curve = new Curve(100,100);
			setCurveType(cp.getCurveType());
			setNumberOfSections(cp.getNumberOfSections());
		}
	}
    
	/**
	 * 
	 */
	protected boolean mouseReleased(MouseEvent e){
		int mouseX = e.getX();
		int mouseY = e.getY();

		if (firstPoint) {
			firstPoint = false;
			Document doc = Application.getInstance().getActiveDocument();
			doc.addObject(curve);
		}
		ControlPoint cp = new ControlPoint(mouseX, mouseY);
		curve.addPoint(cp);
				
		return true;
	}

	/**
	 * @param string
	 */
	public void setCurveType(String string) {
		if (string == CurvesModel.BEZIER) {
			curve.setCurveType(new BezierCurveType(CurvesModel.BEZIER));
		} else if (string == CurvesModel.LINEAR) {
			curve.setCurveType(new PolylineCurveType(CurvesModel.LINEAR));
		} else if (string == CurvesModel.HERMITE) {
			curve.setCurveType(new HermiteCurveType(CurvesModel.HERMITE));
		} else if (string == CurvesModel.BSPLINE) {
			curve.setCurveType(new BSplineCurveType(CurvesModel.BSPLINE));
        } else {
			System.out.println("Curve type [" + string + "] is unknown.");
		}
	}
	
	public void alignControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int targetIndex = curve.getShapes().indexOf(s);
                    int previousIndex = targetIndex - 1;
                    int nextIndex = targetIndex + 1;

                    if (!curve.canAlign()) {
                        System.out.format("Point on curve type %s can't be realigned.\n", curve.getCurveType());
                        return;
                    }

                    if (previousIndex < 0) {
                        System.out.println("Can't align first point of curve"); 
                        return;
                    }

                    if (nextIndex >= curve.getShapes().size()) {
                        System.out.println("Can't align last point of curve"); 
                        return;
                    }

                    Point previous = ((ControlPoint) curve.getShapes().get(previousIndex)).getCenter();
                    Point target = ((ControlPoint) curve.getShapes().get(targetIndex)).getCenter();
                    Point next = ((ControlPoint) curve.getShapes().get(nextIndex)).getCenter();

                    Line previousLine = new Line(previous, target);
                    Line nextLine = new Line(target, next);

                    double previousNorm = previousLine.getNorm();
                    double nextNorm = nextLine.getNorm();


                    double previousWidthWeight = previousLine.getWidth() / previousNorm;
                    double previousHeightWeight = previousLine.getHeight() / previousNorm;

                    int x = (int) (target.x + Math.round(nextNorm * previousWidthWeight));
                    int y = (int) (target.y + Math.round(nextNorm * previousHeightWeight));

                    System.out.format("point\tx\ty\nprevious\t%d\t%d\ntarget\t%d\t%d\nnext\t\t%d\t%d\nmoved\t%d\t%d\n\n", previous.x, previous.y, target.x, target.y, next.x, next.y, x, y);

                    Point movedPoint = new Point(x, y);

                    ((ControlPoint) curve.getShapes().get(nextIndex)).setCenter(movedPoint);
                    
                    curve.update();
				}
			}
			
		}
	}
	
	public void symetricControlPoint() {
		if (curve != null) {
			Document doc = Application.getInstance().getActiveDocument();
			List selectedObjects = doc.getSelectedObjects(); 
			if (selectedObjects.size() > 0){
				Shape s = (Shape)selectedObjects.get(0);
				if (curve.getShapes().contains(s)){
					int targetIndex = curve.getShapes().indexOf(s);
                    int previousIndex = targetIndex - 1;
                    int nextIndex = targetIndex + 1;

                    if (!curve.canAlign()) {
                        System.out.format("Point on curve type %s can't be realigned symetric.\n\n", curve.getCurveType());
                        return;
                    }

                    if (previousIndex < 0) {
                        System.out.println("Can't align first point of curve"); 
                        return;
                    }

                    if (nextIndex >= curve.getShapes().size()) {
                        System.out.println("Can't align last point of curve"); 
                        return;
                    }

                    Point previous = ((ControlPoint) curve.getShapes().get(previousIndex)).getCenter();
                    Point target = ((ControlPoint) curve.getShapes().get(targetIndex)).getCenter();

                    Line previousLine = new Line(previous, target);

                    double previousNorm = previousLine.getNorm();


                    double previousWidthWeight = previousLine.getWidth() / previousNorm;
                    double previousHeightWeight = previousLine.getHeight() / previousNorm;

                    int x = (int) (target.x + Math.round(previousNorm * previousWidthWeight));
                    int y = (int) (target.y + Math.round(previousNorm * previousHeightWeight));

                    Point movedPoint = new Point(x, y);

                    ((ControlPoint) curve.getShapes().get(nextIndex)).setCenter(movedPoint);
                    
                    curve.update();
				}
			}
			
		}
	}

	public void setNumberOfSections(int n) {
		curve.setNumberOfSections(n);
	}
	
	public int getNumberOfSections() {
		if (curve != null)
			return curve.getNumberOfSections();
		else
			return Curve.DEFAULT_NUMBER_OF_SECTIONS;
	}
	
	public void setCurvesPanel(CurvesPanel cp) {
		this.cp = cp;
	}
	
	/* (non-Javadoc)
	 * @see model.DocObserver#docChanged()
	 */
	public void docChanged() {
	}

	/* (non-Javadoc)
	 * @see model.DocObserver#docSelectionChanged()
	 */
	public void docSelectionChanged() {
		activate();
	}

	private boolean firstPoint = false;
	private Curve curve;
	private CurvesPanel cp;
}
