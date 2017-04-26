package br.ufmg.harmonia.inspectorj.util;

import java.awt.event.MouseEvent;

import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.view.util.DefaultMouseManager;

public class InternalMousManager extends DefaultMouseManager {
	
	private Point3 focusedNodePosition;
	double instantZoomNodeFocus = 0.2;
	
	public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		
		if (focusedNodePosition!=null && e.getClickCount() == 2) {
			view.getCamera().setViewCenter(focusedNodePosition.x, focusedNodePosition.y, 0);
			view.getCamera().setViewPercent(instantZoomNodeFocus);
			
			//Apaga label indicativo
			//overElement = null;
			//view.repaint();
			
		}
	}
	
	@Override
	protected void mouseButtonPress(MouseEvent event) {	
		super.mouseButtonPress(event);
//		GraphicElement element = view.findNodeOrSpriteAt(event.getX(), event.getY());
//		Toolkit.nodePosition(graph.getNode(element.getId()), focusedNodePosition);
		//nodeDragging = true;

		//view.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
	}
}
