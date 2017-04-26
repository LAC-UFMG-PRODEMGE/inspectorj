package br.ufmg.harmonia.inspectorj.resource.test.case14;

import br.ufmg.harmonia.inspectorj.resource.test.case14.Helper;
import br.ufmg.harmonia.inspectorj.resource.test.case14.Secret;

public class Test14 {

	public static void main(String args[]) {

		Helper aux0 = new Helper();
		Secret aux1 = new Secret();
		aux0.set(aux1.get());
		System.out.println(aux0.get());
	}

}
