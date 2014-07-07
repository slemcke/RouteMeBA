package de.unipotsdam.nexplorer.server.rest;


import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import de.unipotsdam.nexplorer.client.android.rest.RoutingRequest;
import de.unipotsdam.nexplorer.client.android.rest.RoutingResponse;


@Path("packet/")
public class PacketMessages {

	private de.unipotsdam.nexplorer.server.Mobile mobile;

	public PacketMessages() {
		this.mobile = new de.unipotsdam.nexplorer.server.Mobile();
	}

	/*
	 * rest service to send packets
	 */
	@POST
	@Path("send_packet")
	@Produces("application/json")
	public RoutingResponse sendPacket(@FormParam("nextHopId") String nextHopId, @FormParam("packetId") String packetId) {
		RoutingRequest request = new RoutingRequest();
		request.setNextHopId(Long.parseLong(nextHopId));
		request.setPacketId(Long.parseLong(packetId));
		return mobile.sendPacket(request);
	}
}
