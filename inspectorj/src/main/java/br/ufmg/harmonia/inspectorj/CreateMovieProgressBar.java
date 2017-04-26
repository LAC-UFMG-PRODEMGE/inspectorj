package br.ufmg.harmonia.inspectorj;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;

import br.ufmg.harmonia.inspectorj.util.GraphSingleton;
import br.ufmg.harmonia.inspectorj.util.movie.DeleteImages;
 
public class CreateMovieProgressBar extends JFrame{
	
	static JDialog frame = null;
	final Viewer viewer;
	final DefaultView view;
    private Task task;
 
    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground(){
           	GraphSingleton.getInstance().tracking(viewer, view);
			return null;
        }
         
        public void done() {
        	if(!GraphSingleton.getInstance().movieIsCanceled()){
        		if(!task.isCancelled()){
        			frame.setVisible(false);
        			JFrame f = new JFrame();
        			JOptionPane.showMessageDialog(f,"O vídeo foi criado com sucesso!");
        		}        		
        	}
			
			//apaga as imagens geradas pra criar o vídeo
			DeleteImages aux = new DeleteImages();
			aux.deleteImages();        	
        }
    }
 
    public CreateMovieProgressBar(Viewer viewer, DefaultView view, JFrame owner) {
    	this.viewer = viewer;
    	this.view = view;
    	
        frame = new JDialog(owner,"Gerando vídeo", true);
        frame.setLocationRelativeTo(view);
        frame.setModal(true);
 
        //Create and set up the content pane.
        JPanel panel = new JPanel();	
      
        JLabel label = new JLabel("Aguarde .. Gerando vídeo");
        
    	JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		//Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.execute();
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
            	JFrame f = new JFrame();
            	GraphSingleton.getInstance().setMovieIsCanceled(true);
            	JOptionPane.showMessageDialog(f,"A geração do vídeo foi cancelada!");
            	frame.setVisible(false);   
            }
        });
        		
		panel.add(label);
		panel.add(progressBar);
        			        
        frame.setContentPane(panel);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}