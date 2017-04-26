package br.ufmg.harmonia.inspectorj.resource.test.case16;
/*OBS: NAO UTILIZAR ESSE TESTE, POIS A ANÁLISE DE EXCEÇÃO AINDA NÃO FOI FEITA!!!!!
 * Goal: Exceptions
 * Usage: java T4
 * Expected result: 
 */
public class Test16 {
	public void method(boolean b) throws ArithmeticException{
		
		if (b){
			int a = 3/0;
		}
		else{
			int d=2;
			int c=4;
			d=c;
			throw new ArithmeticException();
		}	
	}
	
	
	public static void main(String[] args) {
		Test16 test = new Test16();
		Exception e = null;
		
		try {
			test.method(true);
		} catch (ArithmeticException e1) {			
			e = e1;
		}
		
		if(e!=null){
			e.printStackTrace();
		}
	}
}
