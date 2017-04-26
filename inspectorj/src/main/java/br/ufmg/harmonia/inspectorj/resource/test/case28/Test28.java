package br.ufmg.harmonia.inspectorj.resource.test.case28;

public class Test28 {
	
	public static void main(String args[]) {
		W t = args.length % 2 == 0 ? new X() : new Z();
		
		int secret = args.length;
		
		// show statically that every method is safe.
		t.m(secret);
	}
}