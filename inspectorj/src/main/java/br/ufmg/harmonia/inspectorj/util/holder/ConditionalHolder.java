package br.ufmg.harmonia.inspectorj.util.holder;

import org.graphstream.graph.Node;

import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JTableSwitchStmt;

public class ConditionalHolder {
	
	//if
	protected JIfStmt jIfStmt;
	protected Node ifNode;
	protected int countNop = 0;
	//switch
	protected JTableSwitchStmt jSwitchStmt;
	protected Node switchNode;
	
	public ConditionalHolder(JIfStmt jIfStmt, Node ifNode) {
		this.jIfStmt = jIfStmt;
		this.ifNode = ifNode;
	}
	
	public ConditionalHolder(JTableSwitchStmt jSwitchStmt, Node switchNode){
		this.jSwitchStmt = jSwitchStmt;
		this.switchNode = switchNode;
	}
	
	//if
	public JIfStmt getjIfStmt() {
		return jIfStmt;
	}
	public void setjIfStmt(JIfStmt jIfStmt) {
		this.jIfStmt = jIfStmt;
	}
	public Node getIfNode() {
		return ifNode;
	}
	public void setIfNode(Node ifNode) {
		this.ifNode = ifNode;
	}

	public int getCountNop() {
		return this.countNop;		
	}
	
	public void incrementCountNop(){
		this.countNop++;
	}
	
	public void decrementCountNop(){
		this.countNop--;
	}
	
	public void setCountNop(int x){
		this.countNop = x;
	}
	
	//switch
	public JTableSwitchStmt getjSwitchStmt() {
		return jSwitchStmt;
	}

	public void setjSwitchStmt(JTableSwitchStmt jSwitchStmt) {
		this.jSwitchStmt = jSwitchStmt;
	}

	public Node getSwitchNode() {
		return switchNode;
	}

	public void setSwitchNode(Node switchNode) {
		this.switchNode = switchNode;
	}
	
}
