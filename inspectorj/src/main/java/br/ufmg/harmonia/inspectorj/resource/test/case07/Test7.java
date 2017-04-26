package br.ufmg.harmonia.inspectorj.resource.test.case07;
/*
 * Goal: check if we can track flows through objects.
 * Usage: java T7
 * Expected result: LEAK
 */
import java.util.Random;

public class Test7 {

  public static void main(String args[]) {
    int SECRET = (new Random()).nextInt();
    Helper aux0 = new Helper(SECRET);
    System.out.println(aux0.get());
  }  
  
}
class Helper {
	private final int field;
	public Helper(int initializer) {
		field = initializer;
	}
	public int get() {
	    return field;
	}
}