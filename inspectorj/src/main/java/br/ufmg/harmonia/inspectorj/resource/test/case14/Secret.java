package br.ufmg.harmonia.inspectorj.resource.test.case14;

import java.util.Random;

public class Secret {
	public int get() {
		int SECRET = (new Random()).nextInt();
		return SECRET;
	}
}