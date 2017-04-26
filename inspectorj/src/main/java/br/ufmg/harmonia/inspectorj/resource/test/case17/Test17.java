package br.ufmg.harmonia.inspectorj.resource.test.case17;
/*
 * Goal: desvio condicional - IF
 * Usage: 
 * Expected result: LEAK
 */
public class Test17 {

	public static void main(String args[]) {
		int aux0 = Integer.parseInt(args[0]);
		int SECRET = Integer.parseInt(args[1]);
		
		int a=2;
		int b=4;
		int c=SECRET;
		int d=6;
	    if ((a<b) && (c<d)) {
	    	SECRET = a + 1;
	    	aux0 = b + 1;
	    }
	    System.out.println(aux0);
	    System.out.println(SECRET);
	}
}