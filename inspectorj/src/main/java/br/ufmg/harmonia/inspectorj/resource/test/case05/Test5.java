package br.ufmg.harmonia.inspectorj.resource.test.case05;
/*
 * Goal: check if we can track flows through parts of arrays.
 * Usage: java T5
 * Expected result: NOLEAK
 * Explanation: in this case, most likely we will get LEAK with a simple
 * flow analysis. I do not really expect that we can ever get NOLEAK.
 * Nevertheless, I shall leave this as a challenge for the future.
 */
import java.util.Random;

public class Test5 {
  public static void main(String args[]) {
    int[] aux0 = new int[2];
    int SECRET = (new Random()).nextInt();
    aux0[0] = SECRET;
    aux0[1] = (new Random()).nextInt();
    int aux1 = aux0[1];
    System.out.println(aux1);
  }
}
