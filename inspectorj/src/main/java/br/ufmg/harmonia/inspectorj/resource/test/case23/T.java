package br.ufmg.harmonia.inspectorj.resource.test.case23;

public class T {
	public void leak(int s) {
		System.out.println("Leak! Secret = " + s);
	}
}