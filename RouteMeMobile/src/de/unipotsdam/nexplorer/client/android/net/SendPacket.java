package de.unipotsdam.nexplorer.client.android.net;

import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;
import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;
import de.unipotsdam.nexplorer.client.android.rest.Packet;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class SendPacket implements Sendable, Loginable{

//	private final RestMobile rest;
//	private final UI ui;
	private Packet packet;
	private Long playerId;
//	private boolean isSendingPacket;
	
	public SendPacket(RestMobile rest, UI ui){
//		this.ui = ui;
//		this.rest = rest;
		this.setPacket(null);
		this.playerId = null;
//		this.isSendingPacket = false; 
		
	}
	@Override
	public void sendRequested(Packet packet) {
		this.setPacket(packet);
//		System.out.println("Am I sending? " + isSendingPacket + ". I am " + playerId);
		if(playerId != null && packet != null){
			//prevent sending more than one packet at a time
//			isSendingPacket=true;
//			ui.disableButtonForPacketSending();
			//TODO remove syso
//			targetId = (long)12;
			this.setPacket(packet);
//			packetId = (long)32;
//			targetId = Long.valueOf(parameters.get("next"));
//			packetId = Long.valueOf(parameters.get("packetId"));
			System.out.println("Saving packetId " + packet.getId());

			//save packetID
//			ui.setPacket(packet);
//			ui.setPacketId(packet.getId());
//			rest.sendPacket(targetId, packetId, new AjaxResult<RoutingResponse>(){
//				@Override
//				public void success(){
//					isSendingPacket = false;
//					ui.enableButtonForPacketSending();
//				}
//				@Override
//				public void error() {
//					isSendingPacket = false;
//					ui.enableButtonForPacketSending();
//				}
//			});
		} else {
			System.out.println("I'm not saving packet " + packet.getId() + "for player " + playerId);
		}
		//TODO exception handling
//		if (playerId != null && location != null) {
//			rest.requestPing(playerId, location, new AjaxResult<PingResponse>());
//		}
	}

	@Override
	public void loggedIn(long playerId) {
		this.playerId = playerId;
	}
	public Packet getPacket() {
		return packet;
	}
	public void setPacket(Packet packet) {
		this.packet = packet;
	}

}
