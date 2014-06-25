package de.unipotsdam.nexplorer.client.android.rest;

import java.util.HashMap;

public class Packet {
	private Long id;
	private Byte type;
	private MessageDescription messageDescription;
	private HashMap<Long,Neighbour> neighboursWithRoutes;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Byte getType() {
		return type;
	}
	public void setType(Byte type) {
		this.type = type;
	}
	public MessageDescription getMessageDescription() {
		return messageDescription;
	}
	public void setMessageDescription(MessageDescription messageDescription) {
		this.messageDescription = messageDescription;
	}
	public void setNeighboursWithRoutes(HashMap<Long,Neighbour> neighboursWithRoutes){
		this.neighboursWithRoutes = neighboursWithRoutes;
	}
	public HashMap<Long, Neighbour> getNeighboursWithRoutes(){
		return this.neighboursWithRoutes;
	}
}
