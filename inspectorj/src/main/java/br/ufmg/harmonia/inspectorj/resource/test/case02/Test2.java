package br.ufmg.harmonia.inspectorj.resource.test.case02;

/*
 * Goal: check if we can track conditional control flows.
 * Usage: java T2 <int> <int>
 * Expected result: LEAK
 */
public class Test2 {
	public static void main(String args[]) {
		int SECRET = Integer.parseInt(args[0]);
		int aux0 = SECRET + 1;
		if (aux0 % 2 == 0) {
			System.out.println(aux0);
		}
	}
}
