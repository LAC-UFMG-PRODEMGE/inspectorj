package br.ufmg.harmonia.inspectorj.resource.test.case08;
/*
 * Goal: check if we can track flows through objects.
 * Usage: java T8
 * Expected result: LEAK
 */
import java.util.Random;

class Helper {
  private int field;
  public void set(int newValue) {
    field = newValue;
  }
  public int get() {
    return field;
  }
}

public class Test8 {
  public static void main(String args[]) {
    int SECRET = (new Random()).nextInt();
    Helper aux0 = new Helper();
    aux0.set(SECRET);
    System.out.println(aux0.get());
  }
}
