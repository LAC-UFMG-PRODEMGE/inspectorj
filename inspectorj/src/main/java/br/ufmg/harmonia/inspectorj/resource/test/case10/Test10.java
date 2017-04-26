	package br.ufmg.harmonia.inspectorj.resource.test.case10;
/*
 * Goal: check if we can track implicit flows due to loops.
 * Usage: java T10 <int>
 * Expected result: LEAK
 */
public class Test10 {
  public static void main(String args[]) {
    int SECRET = Integer.parseInt(args[0]);
    int sum = 0;
    for (int i = 0; i < SECRET; i++) {
      sum += i;
    }
    System.out.println(sum);
  }
}
