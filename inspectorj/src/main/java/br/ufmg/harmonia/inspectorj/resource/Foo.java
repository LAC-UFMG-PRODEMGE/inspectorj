package br.ufmg.harmonia.inspectorj.resource;

public class Foo {

	
	public int calculate(){
		int x,y,z;
		x = 10;
		while(x > 1){
			y =  x / 2;
			if(y > 3)
				x = x - y;
			z =  x - 4;
			if(z > 0)
				x = x / 2;
			z = z - 1;
		}
		return x;
	}
}
