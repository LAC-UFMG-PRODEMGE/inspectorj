package br.ufmg.harmonia.inspectorj.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import br.ufmg.harmonia.inspectorj.util.holder.MethodHolder;

public class ComboRenderer extends JLabel implements
		ListCellRenderer<MethodHolder> {

	public ComboRenderer() {
		setOpaque(true);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6583062177950398749L;

	public Component getListCellRendererComponent(
			JList<? extends MethodHolder> list, MethodHolder value, int index,
			boolean isSelected, boolean cellHasFocus) {

		if(value!=null){
	        if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
	        
	        Color hsbColor = Color.getHSBColor(value.getHue(), value.getSaturation(), value.getBrightness());        
	        setIcon(new SquareIcon(hsbColor));
			setText(value.getPrefixFull());
			
		}else{
			setText(" ");
			setIcon(null);
		}
		return this;
	}
	
}
