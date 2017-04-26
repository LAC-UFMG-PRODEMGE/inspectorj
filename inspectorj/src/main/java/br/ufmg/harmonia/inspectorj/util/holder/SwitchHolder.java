package br.ufmg.harmonia.inspectorj.util.holder;

import java.util.List;

import soot.Unit;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.MHGPostDominatorsFinder;


public class SwitchHolder {
	protected List<ConditionalHolder> listOfSwitch;
	@SuppressWarnings("rawtypes")
	protected MHGDominatorsFinder finder;
	@SuppressWarnings("rawtypes")
	protected MHGPostDominatorsFinder finderPost;
	protected Unit unit;
	
	
	@SuppressWarnings("rawtypes")
	public SwitchHolder(MHGDominatorsFinder finder, MHGPostDominatorsFinder finderPost, List<ConditionalHolder> listOfSwitch) {
		this.finder = finder;
		this.finderPost = finderPost;
		this.listOfSwitch = listOfSwitch;
	}
	
	
	
	public Unit getUnit() {
		return unit;
	}



	public void setUnit(Unit unit) {
		this.unit = unit;
	}



	public List<ConditionalHolder> getListOfSwitch() {
		return listOfSwitch;
	}
	public void setListOfSwitch(List<ConditionalHolder> listOfSwitch) {
		this.listOfSwitch = listOfSwitch;
	}
	@SuppressWarnings("rawtypes")
	public MHGDominatorsFinder getFinder() {
		return finder;
	}
	@SuppressWarnings("rawtypes")
	public void setFinder(MHGDominatorsFinder finder) {
		this.finder = finder;
	}
	@SuppressWarnings("rawtypes")
	public MHGPostDominatorsFinder getFinderPost() {
		return finderPost;
	}
	@SuppressWarnings("rawtypes")
	public void setFinderPost(MHGPostDominatorsFinder finderPost) {
		this.finderPost = finderPost;
	}
	
	
	
}
