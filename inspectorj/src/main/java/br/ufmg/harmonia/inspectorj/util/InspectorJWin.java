package br.ufmg.harmonia.inspectorj.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graphstream.graph.Node;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;

import com.lipstikLF.LipstikLookAndFeel;

import br.ufmg.harmonia.inspectorj.CreateMovieProgressBar;
import br.ufmg.harmonia.inspectorj.util.holder.MethodHolder;

public class InspectorJWin extends JFrame {

	private static final long serialVersionUID = -3169848390539147060L;

	final GraphSingleton graphSingleton = GraphSingleton.getInstance();

	public InspectorJWin() {
		try{
			UIManager.setLookAndFeel(LipstikLookAndFeel.class.getName());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void build(String titulo, int width, int height) {

		setTitle(titulo);

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(width, height));

		pack();
		// setVisible(false);
	}

	public void appendView(final Viewer viewer, final DefaultView view) {

		view.setPreferredSize(viewer.getDefaultView().getMaximumSize());

		appendUpperFilterComponents(viewer, view);		
		
		getContentPane().add(view, BorderLayout.CENTER);
		
		JFrame f = this;
		
		appendControlComponents(viewer, view, f);

		// pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension frameDimension = new Dimension();

		getContentPane().getSize(frameDimension);

		setLocation((int) ((screenSize.width - frameDimension.width) * 0.5), (int) ((screenSize.height - frameDimension.height) * 0.75));
		setVisible(true);
	}
	
	//Adiciona  os componentes da parte superior da tela
	private void appendUpperFilterComponents(final Viewer viewer, final DefaultView view) {
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		Label labelCombo = new Label(" Métodos encontrados");
		controlPanel.add(labelCombo);
		
		
		List<MethodHolder> methodsOnGraph = GraphSingleton.getInstance().getMethodsOnGraph();		
		Collections.sort(methodsOnGraph, new Comparator<MethodHolder>() {
			public int compare(MethodHolder o1, MethodHolder o2) {	
				return o1.getPrefixFull().compareTo(o2.getPrefixFull());
			}
		});
		
		Vector<MethodHolder> items = new Vector<MethodHolder>(methodsOnGraph);
		items.add(0, null);//Incluindo uma posição vazia para não vir selecionado o combo
		final JComboBox<MethodHolder> comboBox = new JComboBox<MethodHolder>(items);
		comboBox.setSelectedIndex(0);
		comboBox.setRenderer(new ComboRenderer());
		comboBox.setMaximumRowCount(15);
		controlPanel.add(comboBox);
		
		
		
		JButton buttonFiltrar = new JButton("Filtrar");
		buttonFiltrar.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				MethodHolder selectedItem = (MethodHolder) comboBox.getModel().getSelectedItem();
				GraphSingleton instance = GraphSingleton.getInstance();
				Iterable<? extends Node> eachNode = instance.getEachNode();
				if(selectedItem != null){			
					for(Node node: eachNode){
						if(!node.getId().startsWith(selectedItem.getPrefixFull())){
							node.addAttribute("ui.hide");							
						}else{
							node.removeAttribute("ui.hide");
						}
					}
				}else{
					for(Node node: eachNode){
						node.removeAttribute("ui.hide");	
					}
					
				}
			}
		});
		controlPanel.add(buttonFiltrar);
		
		getContentPane().add(controlPanel, BorderLayout.PAGE_START);
	}
	
	//Adiciona os componentes da parte inferior da tela
	private void appendControlComponents(final Viewer viewer, final DefaultView view, final JFrame frame) {
		JPanel controlPanel = new JPanel(new FlowLayout());

		
		//Botão de tirar print
		JButton buttonTirarPrint = new JButton("Salvar como imagem");
		buttonTirarPrint.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
			
				 FileSinkImages pic = GraphSingleton.getInstance().getFsi();
				 
				 GraphicGraph copyGraphInstance = viewer.getGraphicGraph();	
				 
				 ConfigProperties configProperties = ConfigProperties.getInstance();
					
				 try {
					pic.writeAll(copyGraphInstance, configProperties.getString("diretorio.saida")+"/image.png");
					JFrame f = new JFrame();
        			JOptionPane.showMessageDialog(f,"A imagem foi salva com sucesso!");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}	      
			}
		});
		
		
		//Botão de habilitar
		JButton buttonHabilitarAutoLayout = new JButton("Descongelar");
		buttonHabilitarAutoLayout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				viewer.enableAutoLayout();
			}
		});
		
		//Botão de desabilitar
		JButton buttonDesabilitarAutoLayout = new JButton("Congelar");
		buttonDesabilitarAutoLayout.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				viewer.disableAutoLayout();
			}
		});
		
		//Barra de zoom
		JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL, 10, 150, 150);
		
		Dimension d = new Dimension(350,50);
		zoomSlider.setPreferredSize(d);
		
		zoomSlider.setPaintLabels(true);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setMajorTickSpacing(10);
		zoomSlider.setMinorTickSpacing(5);
		zoomSlider.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				// if (!source.getValueIsAdjusting()) {
				double zoomValue = (double) source.getValue() / 100;
				view.getCamera().setViewPercent(zoomValue);
				// }
			}
		});

		Label labelZoom = new Label("Deslize para ajustar o zoom");
		controlPanel.add(labelZoom);

		controlPanel.add(zoomSlider);

		Label labelSalvar = new Label("Clique para gerar vídeo mostrando a contaminação");
		controlPanel.add(labelSalvar);
		
		
		//Botão de gerar o vídeo		
		if(GraphSingleton.getInstance().isHabilitarVideo()){			
			JButton buttonGerarVideo = new JButton("Gerar vídeo");
			buttonGerarVideo.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					//Se o grafo estiver contaminado, gera o video
					Boolean isGraphTainted = GraphSingleton.getInstance().getAttribute("tainted");
					if(isGraphTainted!=null && isGraphTainted){
						
						new CreateMovieProgressBar(viewer, view, frame);	
					}
					else{
						//mostrar uma mensagem dizendo que não existe contaminação
						JFrame frame = new JFrame();
						JOptionPane.showMessageDialog(frame,"O programa é isócrono! Não existe falha. ");
					}				
				}
			});
			controlPanel.add(buttonGerarVideo);
		}


		controlPanel.add(buttonTirarPrint);
		controlPanel.add(buttonHabilitarAutoLayout);
		controlPanel.add(buttonDesabilitarAutoLayout);

		getContentPane().add(controlPanel, BorderLayout.PAGE_END);
	}
}
