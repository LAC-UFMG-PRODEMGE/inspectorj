package br.ufmg.harmonia.inspectorj.util.holder;

import java.util.List;

import org.graphstream.graph.Node;

import soot.Unit;
import soot.Value;

public class SootGraphNodeVO {
	protected Value unitLeft;
	protected Unit stmt;
	protected List<Value> unitRight;
	
	protected Node nodeLeft;
	protected List<Node> listNodeRight;

	
	public Node getNodeLeft() {
		return nodeLeft;
	}
	public void setNodeLeft(Node nodeLeft) {
		this.nodeLeft = nodeLeft;
	}
	public List<Node> getListNodeRight() {
		return listNodeRight;
	}
	public void setListNodeRight(List<Node> listNodeRight) {
		this.listNodeRight = listNodeRight;
	}
	public Value getUnitLeft() {
		return unitLeft;
	}
	public void setUnitLeft(Value unitLeft) {
		this.unitLeft = unitLeft;
	}
	public Unit getStmt() {
		return stmt;
	}
	public void setStmt(Unit stmt) {
		this.stmt = stmt;
	}
	public List<Value> getUnitRight() {
		return unitRight;
	}
	public void setUnitRight(List<Value> unitRight) {
		this.unitRight = unitRight;
	}
	
	
	

	
}
