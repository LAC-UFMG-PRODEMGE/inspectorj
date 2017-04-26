package br.ufmg.harmonia.inspectorj.resource.test.case22;

public class U extends T {
	public void leak(int s) {
//You should not do that!!!!
//You should not do that!!!!
		System.out.println("Leak!" +s );
	}
}
