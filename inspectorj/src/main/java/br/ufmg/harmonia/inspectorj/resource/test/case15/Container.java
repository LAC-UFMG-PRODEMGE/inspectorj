package br.ufmg.harmonia.inspectorj.resource.test.case15;

public class Container {
	private Item item = new Item();
	
	void setItem(Item item) {
		this.item = item;
	}
	
	Item getItem() {
		return this.item;
	}
}
