package br.ufmg.harmonia.inspectorj.resource.test.case01;
/*
 * Goal: check if we can track transitive flows.
 * Usage: java T1 <int>
 * Expected result: LEAK
 */
public class Test1 {
  public static void main(String args[]) {
    int SECRET = Integer.parseInt(args[0]);
    int aux0 = SECRET + 1;
    System.out.println(aux0);
  }
}
