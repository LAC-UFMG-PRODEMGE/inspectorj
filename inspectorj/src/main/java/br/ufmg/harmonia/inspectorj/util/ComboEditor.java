package br.ufmg.harmonia.inspectorj.util;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class ComboEditor extends BasicComboBoxEditor {
	
	private JLabel label = new JLabel();
	private JPanel panel = new JPanel();	
	
	public ComboEditor(){
		
		label.setOpaque(true);
		
		
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.add(label);
	}
		
	@Override
	public Component getEditorComponent() {	
		return this.panel;
	}
	
	

	
}
