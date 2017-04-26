package br.ufmg.harmonia.inspectorj.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.LayerRenderer;

import br.ufmg.harmonia.inspectorj.util.holder.MethodHolder;
import soot.tagkit.Tag;

public class ForeLayoutRender implements LayerRenderer {
	
	private ViewerMouseEvents mouseEvents;
	
	public ForeLayoutRender(ViewerMouseEvents mouseEvents) {
		this.mouseEvents = mouseEvents;
	}

	@SuppressWarnings("unchecked")
	public void render(Graphics2D graphics, GraphicGraph graph, double px2Gu,
			int widthPx, int heightPx, double minXGu, double minYGu,
			double maxXGu, double maxYGu) {
		graphics.drawString("Número de Vértices ("+graph.getNodeCount()+")", widthPx-200, heightPx-30);
		graphics.drawString("Número de Arestas ("+graph.getEdgeCount()+")", widthPx-200, heightPx-50);
		
		GraphSingleton instance = GraphSingleton.getInstance();
//		drawMethodsReferenceLabels(graphics, instance);
		
		
		
		GraphicElement overElement = mouseEvents.getOverElement();
		if(overElement!=null){
			drawTextBackground(graphics, 0, overElement.getLabel());
			graphics.setPaint(Color.BLACK);
			graphics.drawString(overElement.getLabel(), mouseEvents.getOverXPosition()+3, mouseEvents.getOverYPosition());

			Node node = instance.getNode(overElement.getId());
			
			String line = node.getAttribute("tags");
			if(line!=null){
				int heightOffSet = 0;
			
				heightOffSet += 15;
				drawTextBackground(graphics, heightOffSet, line);	
				graphics.setPaint(Color.BLACK);
				graphics.drawString(line, mouseEvents.getOverXPosition()+3, mouseEvents.getOverYPosition()+heightOffSet);	
			}
			
//			List<Tag> tags = (List<Tag>)node.getAttribute("tags");
//			if(tags!=null){
//				int heightOffSet = 0;
//				for(Tag tag: tags){
//					heightOffSet += 15;
//					String text = tag.toString();
//					drawTextBackground(graphics, heightOffSet, text);	
//					graphics.setPaint(Color.BLACK);
//					graphics.drawString(text, mouseEvents.getOverXPosition()+3, mouseEvents.getOverYPosition()+heightOffSet);
//				}
//				
//				
//			}
			
			
		}
	}

	protected void drawMethodsReferenceLabels(Graphics2D graphics,
			GraphSingleton instance) {
		List<MethodHolder> methodsOnGraph = instance.getMethodsOnGraph();
		int offsetRect = 0;
		for(MethodHolder method: methodsOnGraph){
			//
			Rectangle2D.Float rectMethod = new Rectangle2D.Float(20, 20 + offsetRect, 10, 10);
			graphics.setColor(Color.getHSBColor(method.getHue(), 
												method.getSaturation(), 
												method.getBrightness()));
			graphics.fill(rectMethod);
			graphics.draw(rectMethod);
			
			graphics.setColor(Color.BLACK);
			graphics.drawString(method.getPrefixFull(), 35, 30 + offsetRect);
			
			offsetRect += 20;
		}
	}

	protected void drawTextBackground(Graphics2D graphics, int heightOffSet,
			String text) {
		Rectangle2D rec = graphics.getFont().getStringBounds(text, graphics.getFontRenderContext());
		rec.setRect(mouseEvents.getOverXPosition(), mouseEvents.getOverYPosition()+heightOffSet-11,rec.getWidth()+3, rec.getHeight());
		graphics.setColor(Color.WHITE);
		graphics.fill(rec);
		graphics.draw(rec);
	}

}
