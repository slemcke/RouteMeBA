package de.unipotsdam.nexplorer.client.android.maps;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.js.LatLng;
import de.unipotsdam.nexplorer.client.android.js.Marker;
import de.unipotsdam.nexplorer.client.android.js.MarkerImage;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

@SuppressLint("UseSparseArrays")
public class LevelThreeNeighbourDrawer extends LevelTwoNeighbourDrawer
		implements NeighbourDrawer {
	private java.util.Map<Long, Marker> neighbourHighlightMarkersArray = new HashMap<Long, Marker>();

	public LevelThreeNeighbourDrawer(GoogleMap senchaMap, Activity host) {
		super(senchaMap, host);
		// this.senchaMap = senchaMap;
		// this.host = host;
	}

//	public void highlightNeighbours(Map<Long, Neighbour> neighbours) {
//		for (Map.Entry<Long, Neighbour> entry : neighbours.entrySet()) {
//			highlightNeighbourMarkerAtLatitudeLongitude(entry.getKey(),
//					entry.getValue());
//		}
//	}

//	@Override
//	protected void drawNeighbourMarkerAtLatitudeLongitude(int playerId,
//			Neighbour neighbour) {
//		System.out.println("drawing neighbour " + playerId);
//		super.drawNeighbourMarkerAtLatitudeLongitude(playerId, neighbour);
//		if (neighbour.hasRoute()) {
//			System.out.println("neighbour " + playerId + " has route");
//			Marker m = neighbourMarkersArray.get(playerId);
//			m.setIcon(
//					new MarkerImage(
//							drawable.network_wireless_small_highlighted));
//			neighbourMarkersArray.put(playerId,m);
//					
//		}
//	}

//	protected void highlightNeighbourMarkerAtLatitudeLongitude(
//			final long playerId, Neighbour neighbour) {
//		// get neighbour position
//		final LatLng latlng = new LatLng(neighbour.getLatitude(),
//				neighbour.getLongitude());
//		// get highlighting image
//		final MarkerImage image = new MarkerImage(
//				drawable.network_wireless_small_highlighted);
//
//		if (neighbourHighlightMarkersArray.get(playerId) == null) {
//			// // neighbour has for some reason not been drawn yet
//			// if (this.neighbourMarkersArray.get(playerId) == null) {
//			// Marker marker = new Marker(host) {
//			//
//			// @Override
//			// protected void setData() {
//			// position = latlng;
//			// map = senchaMap;
//			// title = "(" + playerId + ") ";
//			// icon = image;
//			// zIndex = 1;
//			// }
//			// };
//			//
//			// neighbourHighlightMarkersArray.put(playerId, marker);
//			// } else {
//			// neighbourHighlightMarkersArray.get(playerId)
//			// .setPosition(latlng);
//			// neighbourHighlightMarkersArray.get(playerId).setTitle(
//			// "(" + playerId + ") " /* + name */);
//			// if (neighbourHighlightMarkersArray.get(playerId).map == null) {
//			// neighbourHighlightMarkersArray.get(playerId).setMap(
//			// senchaMap);
//			// }
//			// }
//		}
//		// neighbour has been drawn and needs new picture
//		else {
//			Marker marker = this.neighbourMarkersArray.get(playerId);
//			// set new icon, set new position
//			marker.setIcon(image);
//			marker.setPosition(latlng);
//		}
//	}
//
//	private void removeHighlights() {
//		final MarkerImage image = new MarkerImage(
//				drawable.network_wireless_small);
//
//		for (Marker m : neighbourMarkersArray.values()) {
//			m.setIcon(image);
//		}
//	}
//
//	private void removeInvisibleHighlights(Map<Integer, Neighbour> neighbours) {
//		for (Map.Entry<Long, Marker> entry : neighbourHighlightMarkersArray
//				.entrySet()) {
//			if (entry.getValue() != null
//					&& neighbours.get(entry.getKey()) == null) {
//				remove(entry.getKey(), entry.getValue());
//			}
//		}
//	}
//
//	public void removeInvisibleMarkers(Map<Integer, Neighbour> neighbours) {
//		super.removeInvisible(neighbours);
//		// also remove highlighting markers
//		removeInvisibleHighlights(neighbours);
//
//	}

}
