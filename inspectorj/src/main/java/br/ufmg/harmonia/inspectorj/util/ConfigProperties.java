package br.ufmg.harmonia.inspectorj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigProperties {

	private static ConfigProperties myInstance;
	private static Properties props;
	private static String filename;
	
	private ConfigProperties() {
		props = new Properties();
		FileInputStream file;
		try{
			file = new FileInputStream("./src/main/resources/dados.properties");
			props.load(file);
			filename = "./src/main/resources/dados.properties"; 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private ConfigProperties(File file) {
		props = new Properties();
		FileInputStream fileIS;
		try{
			fileIS = new FileInputStream(file);
			props.load(fileIS);
			filename = fileIS.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static ConfigProperties getInstance(File file){
		
		if(myInstance == null){
			myInstance = new ConfigProperties(file);
		}
		
		return myInstance;
	}
	
	public synchronized static ConfigProperties getInstance(){
		
		if(myInstance == null){
			myInstance = new ConfigProperties();
		}
		
		return myInstance;
	}
	
	public String getString(String chave) {
		return (String) props.get(chave);
	}

	public String getString(String chave, String defaultValue) {
		return (String) props.getProperty(chave, defaultValue);
	}
	
	public Integer getInteger(String chave) {
		return Integer.valueOf((String) props.get(chave));
	}
	
	public Integer getInteger(String chave, Integer defaultValue) {
		return Integer.valueOf(props.getProperty(chave, String.valueOf(defaultValue)));
	}
	
	public Boolean getBoolean(String chave) {
		return Boolean.valueOf((String) props.get(chave));
	}
	
	public Boolean getBoolean(String chave, Boolean defaultValue) {
		return Boolean.valueOf(props.getProperty(chave, String.valueOf(defaultValue)));
	}	
	
	public void put(String chave, Object key){
		props.put(chave, key);
	}
	
	public String getFileName() {
		return filename;
	}

	
}