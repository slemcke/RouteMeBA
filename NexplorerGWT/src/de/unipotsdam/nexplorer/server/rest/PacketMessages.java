package de.unipotsdam.nexplorer.server.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
	public RoutingResponse sendPacket(@FormParam("nextHopId") Long nextHopId, @FormParam("packetId") Long packetId) {
		RoutingRequest request = new RoutingRequest();
		request.setNextHopId(nextHopId);
		request.setPacketId(packetId);
		return mobile.sendPacket(request);
	}
}
