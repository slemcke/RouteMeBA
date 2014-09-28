package de.unipotsdam.nexplorer.client.android.rest;

public class RoutingTable {
	private Long nodeId;
	private Long destinationId;
	private Long nextHopId;
	private Long hopCount;
	
	public Long getNodeId() {
		return nodeId;
	}
	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}
	public Long getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(Long destinationId) {
		this.destinationId = destinationId;
	}
	public Long getNextHopId() {
		return nextHopId;
	}
	public void setNextHopId(Long nextHopId) {
		this.nextHopId = nextHopId;
	}
	public Long getHopCount() {
		return hopCount;
	}
	public void setHopCount(Long hopCount) {
		this.hopCount = hopCount;
	}
}
