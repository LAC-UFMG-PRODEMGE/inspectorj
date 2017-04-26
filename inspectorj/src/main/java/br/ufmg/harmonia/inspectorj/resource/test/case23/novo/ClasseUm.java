package br.ufmg.harmonia.inspectorj.resource.test.case23.novo;

import br.ufmg.harmonia.inspectorj.resource.test.case23.velho.Foo;

public class ClasseUm {
	
	protected String senha;
	
	public ClasseUm(String x) {
		this.senha = x;
	}

	
	public void processa(){
		Foo.fazAlgoIncrivel(senha);
	}
}
