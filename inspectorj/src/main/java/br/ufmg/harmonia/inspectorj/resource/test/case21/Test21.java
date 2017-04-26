package br.ufmg.harmonia.inspectorj.resource.test.case21;

public class Test21 {

	public static void main(String[] args) {
		
		int testIf = Integer.parseInt(args[0]);
		
		if(testIf == 0 ){
			testIf += 1;
		}
		
		int endOfFirstIf = testIf;
		int test2If = 2 + endOfFirstIf;
		
		if(test2If > 0){
			test2If = 3;
		}
		
		int endOfSecondIf = test2If;
		System.out.println(endOfSecondIf);
	}
}
