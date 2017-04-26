package br.ufmg.harmonia.inspectorj.resource.test.case09;
/*
 * Goal: check if we can track implicit flows due to conditinals.
 * Usage: java T9 <int>
 * Expected result: LEAK
 */
public class Test9 {
  public static void main(String args[]) {
    int SECRET = Integer.parseInt(args[0]);
    int aux0 = 0;
    if (SECRET > 2) {
      aux0 = 1;
    }
    System.out.println(aux0);
  }
}
