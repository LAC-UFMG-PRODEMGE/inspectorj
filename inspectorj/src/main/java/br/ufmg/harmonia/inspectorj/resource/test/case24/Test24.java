package br.ufmg.harmonia.inspectorj.resource.test.case24;

public class Test24 {
//	
//	public static void main(String[] args){
//		boolean resp = comp(args[0],args[1]);
//	}
	
	 public static boolean comp(String password, String input) {
	    for (int i = 0; i < 8; i++) {
	      if (password.charAt(i) != input.charAt(i)) {
	        return false;
	      }
	    }
	    return true;
	  }
	
}
