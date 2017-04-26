package br.ufmg.harmonia.inspectorj.resource.test.case15;
/*
 * Goal: Pointer analysis
 * Usage: java T4
 * Expected result: 
 */
import java.util.Random;

public class Test15 {
	public static void main(String args[]) {
		Container c1 = new Container();
		Item i1 = new Item();
		c1.setItem(i1);
		Container c2 = new Container();
		Item i2 = new Item();
		c2.setItem(i2);
		Container c3 = c2;
	}
}