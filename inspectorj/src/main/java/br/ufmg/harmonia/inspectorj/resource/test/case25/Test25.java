package br.ufmg.harmonia.inspectorj.resource.test.case25;

public class Test25 {
	public static void main(String[] args) {		
		T o = args.length % 2 == 0 ? new U()  : new T();
		
		int secret = args.length;

		o.leak(secret);
		
		o.leak(secret);

		o.leak(secret);
		
		o.leak(secret);
	}
}
