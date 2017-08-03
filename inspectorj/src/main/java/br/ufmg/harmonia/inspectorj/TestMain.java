package br.ufmg.harmonia.inspectorj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;



public class TestMain {

	public static void main(String[] args) {
	
		File directory = new File("C:/Prodemge/Federal/workspace/inspectorj/saida/portal");//C:/Prodemge/Federal/workspace/inspectorj/saida
		File[] listFiles = directory.listFiles();
		try {
			int quantidade = 0 ;
			for (File file : listFiles) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String linha = null;
				while((linha = reader.readLine())!=null){
					quantidade += 1;
				}
				
				reader.close();
			}
			System.out.println("quantidade de linhas "+ quantidade);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
