package br.ufmg.harmonia.inspectorj.resource.test.case03;

/*
 * Goal: check if we can track inter-procedural flows.
 * Usage: java T3
 * Expected result: LEAK
 */
import java.util.Random;

public class Test3 {

	public static void main(String args[]) {
		System.out.println(getSecret());
	}

	public static int getSecret() {
		int SECRET = (new Random()).nextInt();
		return SECRET;
	}
}
