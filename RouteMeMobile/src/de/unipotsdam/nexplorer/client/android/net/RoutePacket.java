package de.unipotsdam.nexplorer.client.android.net;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;
import de.unipotsdam.nexplorer.client.android.callbacks.Routable;
import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;
import de.unipotsdam.nexplorer.client.android.rest.Packet;
import de.unipotsdam.nexplorer.client.android.rest.RoutingResponse;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class RoutePacket implements Sendable, Loginable, Routable {

	private final RestMobile rest;
	private final UI ui;
	private Packet packet;
	private Long playerId;
	private boolean isSendingPacket;

	public RoutePacket(RestMobile rest, UI ui) {
		this.ui = ui;
		this.rest = rest;
		this.packet = null;
		this.playerId = null;
		this.isSendingPacket = false;

	}

	@Override
	public void sendRequested(Packet packet) {
		System.out.println("packet to route: " + packet.getId());
		this.packet = packet;
		
		
		// System.out.println("Am I sending? " + isSendingPacket + ". I am " +
		// playerId);
		// if(!isSendingPacket && playerId != null){
		// //prevent sending more than one packet at a time
		// isSendingPacket=true;
		// ui.disableButtonForPacketSending();
		// //TODO remove syso
		// // targetId = (long)12;
		// this.packetId = packetId;
		// // packetId = (long)32;
		// // targetId = Long.valueOf(parameters.get("next"));
		// // packetId = Long.valueOf(parameters.get("packetId"));
		// System.out.println("Saving packetId " + packetId);
		//
		// //save packetID
		// ui.setPacketId(packetId);
		// // rest.sendPacket(targetId, packetId, new
		// AjaxResult<RoutingResponse>(){
		// // @Override
		// // public void success(){
		// // isSendingPacket = false;
		// // ui.enableButtonForPacketSending();
		// // }
		// // @Override
		// // public void error() {
		// // isSendingPacket = false;
		// // ui.enableButtonForPacketSending();
		// // }
		// // });
		// } else {
		// System.out.println("I'm not saving packetId " + packetId +
		// "for player " + playerId);
		// }
		// //TODO exception handling
		// // if (playerId != null && location != null) {
		// // rest.requestPing(playerId, location, new
		// AjaxResult<PingResponse>());
		// // }
	}

	@Override
	public void loggedIn(long playerId) {
		this.playerId = playerId;
	}

	@Override
	public void routeRequested(Long nextHopId) {
		//TODO remove syso
		//prevent sending more than one packet a time
		System.out.println("Node " + playerId + " starts sending packet " + packet.getId() + " to Node " + nextHopId + "...");
//		if(!isSendingPacket && playerId!=null){
		if(playerId!=null){
	
		isSendingPacket = true;
		rest.sendPacket(nextHopId, packet.getId(), new AjaxResult<String>(){
			public void success(String result){
				success();
				System.out.println(result);
				ui.showFeedback(result);
			}
			public void success(){
				System.out.println("Succeeded!");
				isSendingPacket = false;
				ui.enableButtonForPacketSending();
				

			}
			public void error(){
				//try again?
				
				isSendingPacket = true;
//				ui.enableButtonForPacketSending();
			}
		});
		System.out.println("Node " + playerId + " successfully sent packet " + packet.getId() + " to Node " + nextHopId + "...");
		} else{
			System.out.println("Node " + playerId + " did not send packet " + packet.getId() + " to Node " + nextHopId + "...");

		}
		// System.out.println("Am I sending? " + isSendingPacket + ". I am " +
				// playerId);
				// if(!isSendingPacket && playerId != null){
				// //prevent sending more than one packet at a time
				// isSendingPacket=true;
				// ui.disableButtonForPacketSending();
				// //TODO remove syso
				// // targetId = (long)12;
				// this.packetId = packetId;
				// // packetId = (long)32;
				// // targetId = Long.valueOf(parameters.get("next"));
				// // packetId = Long.valueOf(parameters.get("packetId"));
				// System.out.println("Saving packetId " + packetId);
				//
				// //save packetID
				// ui.setPacketId(packetId);
				// // rest.sendPacket(targetId, packetId, new
				// AjaxResult<RoutingResponse>(){
				// // @Override
				// // public void success(){
				// // isSendingPacket = false;
				// // ui.enableButtonForPacketSending();
				// // }
				// // @Override
				// // public void error() {
				// // isSendingPacket = false;
				// // ui.enableButtonForPacketSending();
				// // }
				// // });
				// } else {
				// System.out.println("I'm not saving packetId " + packetId +
				// "for player " + playerId);
				// }
				// //TODO exception handling
				// // if (playerId != null && location != null) {
				// // rest.requestPing(playerId, location, new
				// AjaxResult<PingResponse>());
				// // }

	}

}
