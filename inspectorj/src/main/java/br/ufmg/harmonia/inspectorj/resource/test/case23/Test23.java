package br.ufmg.harmonia.inspectorj.resource.test.case23;

import br.ufmg.harmonia.inspectorj.resource.test.case23.T;
//NOVA ANALISE

public class Test23 {
	public static void main(String args[]) {
		T o = new U();
		int secret = 301015;
		o.leak(secret);
	}
}
