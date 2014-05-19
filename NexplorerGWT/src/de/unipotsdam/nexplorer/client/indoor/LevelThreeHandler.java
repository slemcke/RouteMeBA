package de.unipotsdam.nexplorer.client.indoor;

import de.unipotsdam.nexplorer.client.indoor.levels.Route;

/*
 * extends LevelTwoHandler
 * inserts also packetType
 */
public class LevelThreeHandler extends LevelTwoHandler{
	
	@Override
	public void onRouteClick(Route route) {
		int src = Integer.parseInt(route.getSource());
		int dest = Integer.parseInt(route.getDestination());

		setRoute(src, dest);
		insertNewMessage(route.getPacket());
	}

	private native void setRoute(int src, int dest)/*-{
		var srcPos = $wnd.playerMarkersArray[src].getPosition();
		var destPos = $wnd.playerMarkersArray[dest].getPosition();

		$wnd.setSourceFlag(src, srcPos);
		$wnd.setDestinationFlag(dest, destPos);
	}-*/;

	private native void insertNewMessage(String packet)/*-{
		$wnd.insertNewMessageWithPacket(packet);
	}-*/;


}
