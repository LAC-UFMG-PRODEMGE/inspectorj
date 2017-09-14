package br.ufmg.harmonia.inspectorj.resource.test.case13;
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

class Secret {
	public int get(){
		int SECRET = (new Random()).nextInt();
		return SECRET;
	}
}

public class Test13 {
	  public static void main(String args[]) {
	    Helper aux0 = new Helper();
	    Secret aux1 = new Secret();
	    aux0.set(aux1.get());
	    System.out.println(aux0.get());
	  }		
}
