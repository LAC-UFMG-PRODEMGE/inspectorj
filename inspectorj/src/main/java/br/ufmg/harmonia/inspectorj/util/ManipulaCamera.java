package br.ufmg.harmonia.inspectorj.util;

import java.util.List;

import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.DefaultView;


public class ManipulaCamera extends Thread {
	List<Position> positions;
	DefaultView view;
	GraphicGraph copyGraph;
	
	public ManipulaCamera(List<Position> positions, DefaultView view, GraphicGraph copyGraph){
		this.positions = positions;
		this.view = view;
		this.copyGraph = copyGraph;
	}
	
	public void run() {
		try{
			GraphSingleton.getInstance().startOfCreatingOfImages("images/prefix_");
			
			int aux = 0;
			for(int i=0; i<positions.size(); i++) {
				view.getCamera().setViewCenter(positions.get(i).x, positions.get(i).y, positions.get(i).z);
				view.getCamera().setViewPercent(0.5);
				
				GraphSingleton.getInstance().getFsi().setViewCenter(positions.get(i).x, positions.get(i).y);
				GraphSingleton.getInstance().getFsi().setViewPercent(0.5);
				GraphSingleton.getInstance().stepBegins(GraphSingleton.getInstance().getStep());
			
				Thread.sleep(2000);
			}
			view.getCamera().setViewCenter(0,0,0);
			view.getCamera().setViewPercent(1);
			GraphSingleton.getInstance().getFsi().setViewCenter(0, 0);
			GraphSingleton.getInstance().getFsi().setViewPercent(1);
			GraphSingleton.getInstance().stepBegins(GraphSingleton.getInstance().getStep());
	
		}
		catch(InterruptedException e){
			return;
		}
		GraphSingleton.getInstance().endOfCreatingOfImages();
	}
	   
	
}
