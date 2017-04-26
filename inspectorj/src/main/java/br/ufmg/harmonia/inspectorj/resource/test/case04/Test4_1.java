package br.ufmg.harmonia.inspectorj.resource.test.case04;
/*
 * Goal: check if we can track flows through arrays.
 * Usage: java T4
 * Expected result: LEAK
 */
import java.util.Random;

public class Test4_1 {
  public static void main(String args[]) {
    int[] aux0 = new int[1];
    int SECRET = (new Random()).nextInt();
    int index1 = 0;       
    aux0[index1] = SECRET;
    int index2 = 0;
    int aux1 = aux0[index2];
    System.out.println(aux1);
  }
}
