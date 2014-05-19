package de.unipotsdam.nexplorer.client.android.maps;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.js.LatLng;
import de.unipotsdam.nexplorer.client.android.js.Marker;
import de.unipotsdam.nexplorer.client.android.js.MarkerImage;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;
import de.unipotsdam.nexplorer.client.android.rest.RoutingTable;

public class LevelThreeNeighbourDrawer extends LevelTwoNeighbourDrawer{
	
	private java.util.Map<Integer, Marker> neighbourHighlightMarkersArray = new HashMap<Integer, Marker>();
	protected Map<Integer,NeighbourSend> neighbourSends = new HashMap<Integer, NeighbourSend>();

	public LevelThreeNeighbourDrawer(GoogleMap senchaMap, Activity host) {
		super(senchaMap, host);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void removeInvisible(Map<Integer, Neighbour> neighbours) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Map<Integer, Neighbour> neighbours) {
		// TODO Auto-generated method stub
		
	}
	


	private void highlightRouteNeighbours(int playerId, Neighbour neighbour, RoutingTable table, Long packetId){
		if (!neighbourSends.containsKey(playerId)) {
			NeighbourSend send = new NeighbourSend(senchaMap, neighbour, host, this, playerId);
			neighbourSends.put(playerId, send);
		} else {
			NeighbourPing ping = neighbourPings.get(playerId);
			ping.update(neighbour.getLatitude(), neighbour.getLongitude());
		}
	}
	
	protected void drawNeighbourMarkerAtLatitudeLongitude(int playerId, Neighbour neighbour) {
		super.drawNeighbourMarkerAtLatitudeLongitude(playerId, neighbour);
		if (neighbour.isPingActive()) {
//			highlightRouteNeighbours(playerId, neighbour, table, packetId);
		}
	}
	
	protected void highlightNeighbourMarkerAtLatitudeLongitude(final int playerId, Neighbour neighbour) {
		final LatLng latlng = new LatLng(neighbour.getLatitude(), neighbour.getLongitude());
		final MarkerImage image = new MarkerImage(drawable.network_wireless_small);

		if (neighbourHighlightMarkersArray.get(playerId) == null) {
			Marker marker = new Marker(host) {

				@Override
				protected void setData() {
					position = latlng;
					map = senchaMap;
					title = "(" + playerId + ") ";
					icon = image;
					zIndex = 1;
				}
			};

			neighbourHighlightMarkersArray.put(playerId, marker);
		} else {
			neighbourHighlightMarkersArray.get(playerId).setPosition(latlng);
			neighbourHighlightMarkersArray.get(playerId).setTitle("(" + playerId + ") " /* + name */);
			if (neighbourHighlightMarkersArray.get(playerId).map == null) {
				neighbourHighlightMarkersArray.get(playerId).setMap(senchaMap);
			}
		}
	}
	
//
//	private void surroundWithPing(int playerId, Neighbour neighbour) {
//		if (!neighbourPings.containsKey(playerId)) {
//			NeighbourPing ping = new NeighbourPing(senchaMap, neighbour, host, this, playerId);
//			neighbourPings.put(playerId, ping);
//		} else {
//			NeighbourPing ping = neighbourPings.get(playerId);
//			ping.update(neighbour.getLatitude(), neighbour.getLongitude());
//		}
//	}
//	public void finishedRouting(int playerId) {
//		neighbourSends.remove(playerId);
//	}

}
