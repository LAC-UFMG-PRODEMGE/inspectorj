package br.ufmg.harmonia.inspectorj.resource.test.case25;

public class Test25 {
	public static void main(String[] args) {		
		T o = args.length % 2 == 0 ? new T() : new U();
		
		int secret = args.length;

/* if( secret instanceof  br.ufmg.harmonia.inspectorj.resource.test.case25.U) throws new java.lang.Exception();  */
		o.leak(secret);
		
/* if( secret instanceof  br.ufmg.harmonia.inspectorj.resource.test.case25.U) throws new java.lang.Exception();  */
		o.leak(secret);

/* if( secret instanceof  br.ufmg.harmonia.inspectorj.resource.test.case25.U) throws new java.lang.Exception();  */
		o.leak(secret);
		
/* if( secret instanceof  br.ufmg.harmonia.inspectorj.resource.test.case25.U) throws new java.lang.Exception();  */
		o.leak(secret);
	}
}
