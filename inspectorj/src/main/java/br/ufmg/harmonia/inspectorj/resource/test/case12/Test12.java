package br.ufmg.harmonia.inspectorj.resource.test.case12;
/*
 * Goal: check if we can track implicit flows due to a switch.
 * Usage: java Test12 <int>
 * Expected result: LEAK
 */

public class Test12 {
  public static void main(String args[]) {
    int SECRET = Integer.parseInt(args[0]);
    int aux1 = 0;
    switch (SECRET) {
      case 1: aux1 = 1; break;
      case 2: aux1 = 2; break;
      case 3: aux1 = 3; break;
      default: aux1 = 4;
    }
    System.out.println(aux1);
  }
}
