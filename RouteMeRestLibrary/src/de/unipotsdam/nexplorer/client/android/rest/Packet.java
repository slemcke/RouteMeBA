package de.unipotsdam.nexplorer.client.android.rest;


public class Packet {
	private Long id;
	private Byte type;
	private MessageDescription messageDescription;

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
}
