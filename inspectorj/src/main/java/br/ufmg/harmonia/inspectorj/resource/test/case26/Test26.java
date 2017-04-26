package br.ufmg.harmonia.inspectorj.resource.test.case26;

public class Test26 {
	public static void main(String[] args) {		
		T o = args.length % 2 == 0 ? new T() : new U();
		
		int secret = args.length;
		// BEGIN AUTOMATIC CODE:
		//if (!(o instanceof T))
		//	throw new Error("Unsafe code");
		// END AUTOMATIC CODE.
		o.leak(secret);
	}
}
