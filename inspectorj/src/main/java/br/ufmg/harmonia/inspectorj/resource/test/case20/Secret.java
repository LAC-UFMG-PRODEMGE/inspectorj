package br.ufmg.harmonia.inspectorj.resource.test.case20;


public class Secret {
	
	protected String secret;
	
	public Secret(String secret) {
		this.secret = secret;
	}
	
	
	public String get() {
		
		return this.secret;
	}
}