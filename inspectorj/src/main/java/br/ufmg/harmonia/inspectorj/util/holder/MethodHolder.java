package br.ufmg.harmonia.inspectorj.util.holder;

import java.awt.Color;

public class MethodHolder {
	private String prefixFull;
	private Float hue;
	private Float saturation;
	private Float brightness;
	
	public MethodHolder(String prefixFull, String red, String green, String blue) {
		this.prefixFull = prefixFull;
		float[] hsbValues = new float[3];
		hsbValues = Color.RGBtoHSB(Integer.parseInt(red), Integer.parseInt(green), 
					   Integer.parseInt(blue), hsbValues);
		
		this.hue = hsbValues[0];
		this.saturation = hsbValues[1];
		this.brightness = hsbValues[2];
	}
	
	public String getPrefixFull() {
		return prefixFull;
	}
	public void setPrefixFull(String prefixFull) {
		this.prefixFull = prefixFull;
	}

	public Float getHue() {
		return hue;
	}

	public void setHue(Float hue) {
		this.hue = hue;
	}

	public Float getSaturation() {
		return saturation;
	}

	public void setSaturation(Float saturation) {
		this.saturation = saturation;
	}

	public Float getBrightness() {
		return brightness;
	}

	public void setBrightness(Float brightness) {
		this.brightness = brightness;
	}
	
	
	
	
}
