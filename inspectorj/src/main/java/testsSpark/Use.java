package testsSpark;

public class Use {
	
	public static void go() {
	    Container c1 = new Container();      //
	    Item i1 = new Item();
	    c1.setItem(i1);
	    
	    Container c2 = new Container();      //
	    Item i2 = new Item();
	    c2.setItem(i2);
	   
	    Container c3 = new Container();  
	    c3.setItem(i2);
	    
	    Container c4 = c1;
	    
	    Item ci1 = c1.getItem();
	    Item ci2 = c2.getItem();
	    Item ci3 = c3.getItem();
	    Item ci4 = c4.getItem();
	  }		
	
	public static void main(String[] args) {
		go();
	}
}
