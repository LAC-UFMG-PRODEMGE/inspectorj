package br.ufmg.harmonia.inspectorj.resource.test.case18;
/*
 * Goal: desvio condicional - WHILE
 * Usage: 
 * Expected result: LEAK
 */
public class Test18 {
	public static void main(String args[]) {
		int aux0 = Integer.parseInt(args[0]);
		int SECRET = Integer.parseInt(args[1]);
		
		int a=2;
		int b=4;
		int c=5;
		int d=6;
		while((a<b) && (c<d)) {
			SECRET = a + 1;
	    	aux0 = b + 1;
	    	a++;	
	    	c++;
		}
	    
	    System.out.println(aux0);
	    System.out.println(SECRET);
	}
}
