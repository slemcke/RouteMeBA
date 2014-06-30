package de.unipotsdam.nexplorer.client.android.rest;

import java.util.HashMap;
import java.util.List;


public class GameStatus {

	public Stats stats;
	public Node node;
	private String hint;
	public HashMap<Long,Packet> packets;
	public List<RoutingTable> routingTable;

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public List<RoutingTable> getRoutingTable(){
		return routingTable;
	}
	
	public void setRoutingTable(List<RoutingTable> table){
		this.routingTable = table;
	}
}
