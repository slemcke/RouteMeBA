package de.unipotsdam.nexplorer.client.android.rest;

public class RoutingRequest {
	
	private long packetId;
	private long nextHopId;


	public long getPacketId() {
		return packetId;
	}

	public void setPacketId(long packetId) {
		this.packetId = packetId;
	}

	public long getNextHopId() {
		return nextHopId;
	}

	public void setNextHopId(long nextHopId) {
		this.nextHopId = nextHopId;
	}

}
