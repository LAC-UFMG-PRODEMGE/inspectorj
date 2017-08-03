package br.ufmg.harmonia.inspectorj.resource.test.case04;
/*
 * Goal: check if we can track flows through arrays.
 * Usage: java T4
 * Expected result: LEAK
 */
import java.util.Random;

public class Test4 {
  public static void main(String args[]) {
    int[] aux0 = new int[1];
    int SECRET = (new Random()).nextInt();
    aux0[0] = SECRET;
    int aux1 = aux0[0];
    System.out.println(aux1);
  }
}
