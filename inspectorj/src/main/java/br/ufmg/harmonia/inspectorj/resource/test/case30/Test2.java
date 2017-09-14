package br.ufmg.harmonia.inspectorj.resource.test.case30;

public class Test2 {
	
	public static class T {
		public void leak(int s) {
			System.out.println("No Leak!");
		}
	}
		
	public static class U extends T {
		public void leak(int s) {
			System.out.println("Leak! Secret = " + s);
	    }
	}
		
	public static void main(String args[]) {
		T o = args.length % 2 == 0 ? new U() : new T();
		
		int secret = args.length;
		
		// BEGIN AUTOMATIC CODE:
		if (o instanceof U)
			throw new Error("Unsafe code");
		// END AUTOMATIC CODE.
		
		o.leak(secret);
    }
}