package br.ufmg.harmonia.inspectorj.resource.test.case06;
/*
 * Goal: check if we can track flows through objects.
 * Usage: java T6
 * Expected result: LEAK
 */
import java.util.Random;

class Helper {
  public int field;
}

public class Test6 {

  public static void main(String args[]) {
    Helper aux0 = new Helper();
    int SECRET = (new Random()).nextInt();
    aux0.field = SECRET;
    System.out.println(aux0.field);
  }
}
