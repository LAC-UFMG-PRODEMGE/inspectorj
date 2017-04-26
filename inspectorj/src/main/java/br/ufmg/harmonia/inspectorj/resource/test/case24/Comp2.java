package br.ufmg.harmonia.inspectorj.resource.test.case24;

public class Comp2 {
	boolean comp2(String password, String input) {
		int res = 0;
	    for (int i = 0; i < 8; i++) {
	    	res = res | (password.charAt(i) - input.charAt(i));
	    }
	    return res != 0;
	}
}


