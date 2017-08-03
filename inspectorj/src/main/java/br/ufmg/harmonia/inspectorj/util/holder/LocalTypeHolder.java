package br.ufmg.harmonia.inspectorj.util.holder;

import java.util.List;

import org.graphstream.graph.Node;

import soot.Type;
import soot.Unit;

public class LocalTypeHolder {
	Node nodeRefered;
	List<Type> types;
	Unit unit;
	public LocalTypeHolder() {	
	}
	public LocalTypeHolder(Node nodeRefered, List<Type> types, Unit unit) {
		this.nodeRefered = nodeRefered;
		this.types = types;
		this.unit = unit;
	}
	
	
	public Node getNodeRefered() {
		return nodeRefered;
	}
	public void setNodeRefered(Node nodeRefered) {
		this.nodeRefered = nodeRefered;
	}
	public List<Type> getTypes() {
		return types;
	}
	public void setTypes(List<Type> types) {
		this.types = types;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	
}
