package br.ufmg.harmonia.inspectorj.resource.test.case29;

import java.io.IOException;

public class Test29 {

	public static void main(String[] args) {
		try {
			String command = args[0];
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
