package br.ufmg.harmonia.inspectorj.util;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.ViewerListener;

public class ViewerMouseEvents implements MouseMotionListener, MouseListener, MouseWheelListener, ViewerListener {

	int zoomPer = 150;
	int zoomSpeed = 2;
	
	double instantZoomNodeFocus = 0.2;
	double dragForce = 100.0; // Quanto menor mais forte
	DefaultView view;

	boolean nodeDragging = false;

	GraphSingleton graph = GraphSingleton.getInstance();

	Point mousePoint;

	private GraphicElement overElement;
	private int overXPosition;
	private int overYPosition;
	
	private Point3 focusedNodePosition;

	public ViewerMouseEvents(DefaultView view) {
		this.view = view;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		int speedModifier = e.isShiftDown() ? 3 : 1;
		if (e.getWheelRotation() > 0) {
			zoomPer += zoomSpeed * speedModifier;
		} else {
			zoomPer -= zoomSpeed * speedModifier;
			if (zoomPer < 0)
				zoomPer = 0;
		}
		
		double zoomValue = (double) zoomPer / 100;

		if (zoomValue < 0.1)
			zoomValue = 0.1;

		view.getCamera().setViewPercent(zoomValue);
		printLabelForElement(e);
		// view.getCamera().setViewCenter(e.getXOnScreen(), e.getYOnScreen(),
		// 0);
	}

	public void mouseClicked(MouseEvent e) {
		if (focusedNodePosition!=null && e.getClickCount() == 2) {
			view.getCamera().setViewCenter(focusedNodePosition.x, focusedNodePosition.y, 0);
			view.getCamera().setViewPercent(instantZoomNodeFocus);
			
			//Apaga label indicativo
			overElement = null;
			view.repaint();
			
		}
	}

	public void mousePressed(MouseEvent e) {
		// System.out.println("apertei o botao do mouse x: "+e.getX()+" y: "+e.getY());

		if (mousePoint != null) {
			mousePoint.setLocation(e.getPoint());
		} else {
			mousePoint = new Point(e.getPoint());
		}
		
		view.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		printLabelForElement(e);

	}

	public void mouseReleased(MouseEvent e) {
		view.setCursor(Cursor.getDefaultCursor());
		nodeDragging = false;
		printLabelForElement(e);
	}

	public void mouseEntered(MouseEvent e) {
//		System.out.println("Mouse Over ");
	}

	public void mouseExited(MouseEvent e) {
//		System.out.println("Mouse Out Over");
	}

	public void mouseDragged(MouseEvent e) {
		// System.out.println("dragging o mouse x: "+e.getX()+" y: "+e.getY());
		// if(computeMotion){
		// int currentX = e.getX();
		// int currentY = e.getY();
		//
		// Point3 viewCenter = view.getCamera().getViewCenter();
		// double cameraX = viewCenter.x;
		// if(currentX > x){
		// cameraX -= 0.05;
		// }else{
		// cameraX += 0.05;
		// }
		// double cameraY = viewCenter.y;
		// if(currentY > y){
		// cameraY += 0.05;
		// }else{
		// cameraY -= 0.05;
		// }
		// view.getCamera().setViewCenter(cameraX, cameraY, 0);
		//
		// }

		if (mousePoint != null) {

			if (!nodeDragging) {

				Point3 viewCenter = view.getCamera().getViewCenter();

				double xDrag = mousePoint.getX() - e.getPoint().getX();
				double yDrag = mousePoint.getY() - e.getPoint().getY();

				xDrag = xDrag / dragForce;
				yDrag = yDrag / -dragForce;

				double newVx = viewCenter.x + xDrag;
				double newVy = viewCenter.y + yDrag;

				view.getCamera().setViewCenter(newVx, newVy, 0);
				// view.getCamera().setViewCenter(0, 1, 0);
				// view.getCamera().setViewCenter(viewCenter.x, viewCenter.y,
				// 0);

			}
		} else {
			mousePoint = new Point(e.getPoint());
			
		}

		mousePoint.setLocation(e.getPoint());
		
		printLabelForElement(e);
		

	}

	public void mouseMoved(MouseEvent e) {
		printLabelForElement(e);
	}

	protected void printLabelForElement(MouseEvent e) {
		GraphicElement currentElement = view.findNodeOrSpriteAt(e.getX(), e.getY());
		if(currentElement!=null && view.getCamera().getViewPercent() > 0.5){
			overElement = currentElement;
			overXPosition = e.getX();
			overYPosition = e.getY();	
		}else{
			overElement = null;			
			overXPosition = 0;
			overYPosition = 0;
			
		}
		view.repaint();
	}

	public void viewClosed(String viewName) {
		// TODO Auto-generated method stub

	}

	public void buttonPushed(String id) {
		focusedNodePosition = new Point3();
		Toolkit.nodePosition(graph.getNode(id), focusedNodePosition);
		nodeDragging = true;

		view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
	}

	public void buttonReleased(String id) {
		
	}
	
	
	public GraphicElement getOverElement(){
		return this.overElement;
	}

	public int getOverXPosition() {
		return overXPosition;
	}

	public void setOverXPosition(int overXPosition) {
		this.overXPosition = overXPosition;
	}

	public int getOverYPosition() {
		return overYPosition;
	}

	public void setOverYPosition(int overYPosition) {
		this.overYPosition = overYPosition;
	}
	
	
}
