package br.ufmg.harmonia.inspectorj.resource.test.case00;
/*
 * Goal: demonstrate the basic principles of information flow analysis.
 * Usage: java T0 <int>
 * Expected result: LEAK
 */
public class Test0 {
	public static void main(String args[]) {
		int SECRET = Integer.parseInt(args[0]);
		System.out.println(SECRET);
	}
}
