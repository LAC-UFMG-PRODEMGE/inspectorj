package br.ufmg.harmonia.inspectorj.resource.test.case22;

public class Main {
	public static void main(String[] args) {		
		T o = new U();
		
		int secret = args.length;
		// BEGIN AUTOMATIC CODE:
		//if (!(o instanceof T))
		//	throw new Error("Unsafe code");
		// END AUTOMATIC CODE.
		o.leak(secret);
	}
}