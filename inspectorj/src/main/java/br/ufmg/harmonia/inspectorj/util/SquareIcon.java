package br.ufmg.harmonia.inspectorj.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

public class SquareIcon implements Icon {

	private final int SIZE = 10;
	private Color color;
	
	public SquareIcon(Color color) {
		this.color = color;
	}
	
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D)g;
//		g2.setStroke(new BasicStroke(5.0f));
		g2.setColor(color);
		g2.fillRect(x, y, SIZE, SIZE);
		g2.setColor(Color.BLACK);
		g2.drawRect(x, y, SIZE, SIZE);
		
	}

	public int getIconWidth() {		
		return SIZE;
	}

	public int getIconHeight() {
		return SIZE;
	}

}
