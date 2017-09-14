package br.ufmg.harmonia.inspectorj.resource.test.case30;
public class Test {
	
	public static class T {
		boolean comp(String password, String input) {
			int res = 0;
		    for (int i = 0; i < 8; i++) {
		    	res = res | (password.charAt(i) - input.charAt(i));
		    }
		    return res == 0;
		}
	}
		
	public static class U extends T {
		boolean comp(String password, String input) {
			for (int i = 0; i < 8; i++) {
				if (password.charAt(i) != input.charAt(i)) {
					return false;
				}
		    }
		    return true;
		}
	}
	
	public static void main(String args[]) {
		T o = args.length % 2 == 0 ? new U() : new T();
		
		int secret = args.length;
		
		// BEGIN AUTOMATIC CODE:
		if (o instanceof U)
			throw new Error("Código Inseguro");
		// END AUTOMATIC CODE.
		
		o.comp(args[0],args[1]);
	}	
}