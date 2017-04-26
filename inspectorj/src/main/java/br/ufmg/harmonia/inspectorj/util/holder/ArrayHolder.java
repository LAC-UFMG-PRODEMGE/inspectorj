package br.ufmg.harmonia.inspectorj.util.holder;

import org.graphstream.graph.Node;

import soot.Unit;
import soot.jimple.IntConstant;
import soot.jimple.internal.JimpleLocal;

public class ArrayHolder {
	protected Unit unit;
	protected JimpleLocal local;
	protected IntConstant constant;
	protected Node nodeBase;
	protected Node nodeIndex;
	protected Node nodeJimplePosition;
	
	public ArrayHolder() {	
	}
	
	public ArrayHolder(Unit unit, JimpleLocal local, IntConstant constant){
		this.unit = unit;
		this.local = local;
		this.constant = constant;
	}
	
	
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public JimpleLocal getLocal() {
		return local;
	}
	public void setLocal(JimpleLocal local) {
		this.local = local;
	}
	public IntConstant getConstant() {
		return constant;
	}
	public void setConstant(IntConstant constant) {
		this.constant = constant;
	}
	
	public Node getNodeBase() {
		return nodeBase;
	}

	public void setNodeBase(Node nodeBase) {
		this.nodeBase = nodeBase;
	}

	public Node getNodeIndex() {
		return nodeIndex;
	}

	public void setNodeIndex(Node nodeIndex) {
		this.nodeIndex = nodeIndex;
	}

	
	
	
	
	public Node getNodeJimplePosition() {
		return nodeJimplePosition;
	}

	public void setNodeJimplePosition(Node nodeObjectPosition) {
		this.nodeJimplePosition = nodeObjectPosition;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ArrayHolder){
			return this.local.equals(((ArrayHolder)obj).getLocal());
		}else
		if(obj instanceof JimpleLocal){
			return this.local.equals(obj);
		}		 		
		
		return super.equals(obj);
	}
}
