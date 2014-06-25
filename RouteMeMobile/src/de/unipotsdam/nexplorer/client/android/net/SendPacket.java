package de.unipotsdam.nexplorer.client.android.net;

import java.util.Map;

import android.location.Location;
import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;
import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;
import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;
import de.unipotsdam.nexplorer.client.android.rest.RoutingResponse;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class SendPacket implements Locatable, Sendable, Loginable{

	private final RestMobile rest;
	private final UI ui;
	private Long packetId;
	private Long targetId;
	private Location neighbourLocation;
	private Long playerId;
	private boolean isSendingPacket;
	
	public SendPacket(RestMobile rest, UI ui){
		this.ui = ui;
		this.rest = rest;
		this.packetId = null;
		this.targetId = null;
		this.neighbourLocation = null;
		this.playerId = null;
		this.isSendingPacket = false; 
		
	}
	@Override
	public void sendRequested(Map<String,Integer> parameters) {
		System.out.println("Am I sending? " + isSendingPacket + ". I am " + playerId);
		if(!isSendingPacket && playerId != null){
			//TODO remove syso
			System.out.println("start sending packet...");
			targetId = (long)146;
			packetId = (long)73;
//			targetId = Long.valueOf(parameters.get("next"));
//			packetId = Long.valueOf(parameters.get("packetId"));
			
			rest.sendPacket(targetId, packetId, new AjaxResult<RoutingResponse>(){
				@Override
				public void success(){
					isSendingPacket = false;
					ui.enableButtonForPacketSending();
				}
				@Override
				public void error() {
					isSendingPacket = false;
					ui.enableButtonForPacketSending();
				}
			});
		} else {
			System.out.println("I'm not sending this");
		}
		//TODO exception handling
//		if (playerId != null && location != null) {
//			rest.requestPing(playerId, location, new AjaxResult<PingResponse>());
//		}
	}

	@Override
	public void locationChanged(Location location) {
		this.neighbourLocation = location;
	}
	@Override
	public void loggedIn(long playerId) {
		this.playerId = playerId;
	}

}
