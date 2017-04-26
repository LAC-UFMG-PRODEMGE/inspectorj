package br.ufmg.harmonia.inspectorj.resource.test.case27;

public class Test27 {
	public static void main(String args[]) {
		X t = args.length % 2 == 0 ? new Y() : new Z();
		
		int secret = args.length;
		
		// if (t instanceof Y) throw new Error("Argh!");
		t.m(secret);
	}
}