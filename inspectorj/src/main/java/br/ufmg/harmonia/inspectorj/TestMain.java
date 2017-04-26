package br.ufmg.harmonia.inspectorj;

import java.net.URL;
import java.net.URLClassLoader;

public class TestMain {

	public static void main(String[] args) {
		URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
		for(URL url: urLs){
			System.out.println(url);
		}

	}

}
