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

public class LevelOneNeighbourDrawer implements NeighbourDrawer {

	protected java.util.Map<Integer, Marker> neighbourMarkersArray = new HashMap<Integer, Marker>();
	private GoogleMap senchaMap;
	private Activity host;

	public LevelOneNeighbourDrawer(GoogleMap senchaMap, Activity host) {
		this.senchaMap = senchaMap;
		this.host = host;
	}

	/**
	 * draw the neighbours
	 * 
	 * @param playerId
	 * @param latitude
	 * @param longitude
	 */
	protected void drawNeighbourMarkerAtLatitudeLongitude(final int playerId,
			Neighbour neighbour) {
		final LatLng latlng = new LatLng(neighbour.getLatitude(),
				neighbour.getLongitude());
		final MarkerImage image;

		if (!neighbour.hasRoute()) {
			image = new MarkerImage(drawable.network_wireless_small);
		} else {
			image = new MarkerImage(drawable.network_wireless_small_highlighted);
		}
		// no markers set for actual player
		if (!(neighbourMarkersArray.get(playerId) == null)) {
			remove(playerId, neighbourMarkersArray.get(playerId));
		}
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

			neighbourMarkersArray.put(playerId, marker);
		} 
//	else {
//			Marker m = neighbourMarkersArray.get(playerId);
//			m.setPosition(latlng);
//			System.out.println("NEW IMAGE " + image.getResourceId());
//			m.setIcon(image);
//			m.setTitle("(" + playerId + ") " /* + name */);
////			neighbourMarkersArray.get(playerId).setPosition(latlng);
////			neighbourMarkersArray.get(playerId)
////					.setTitle("(" + playerId + ") " /* + name */);
////			neighbourMarkersArray.get(playerId).setIcon(image);
//			neighbourMarkersArray.put(playerId, m);
//			if (neighbourMarkersArray.get(playerId).map == null) {
//				neighbourMarkersArray.get(playerId).setMap(senchaMap);
//			}
//		}
	


	@Override
	public void removeInvisible(Map<Integer, Neighbour> neighbours) {
		for (Map.Entry<Integer, Marker> entry : neighbourMarkersArray
				.entrySet()) {
			if (entry.getValue() != null
					&& neighbours.get(entry.getKey()) == null) {
				remove(entry.getKey(), entry.getValue());
			}
		}
	}

	protected void remove(int playerId, Marker neighbour) {
		neighbour.setMap(null);
	}

	@Override
	public void draw(Map<Integer, Neighbour> neighbours) {
		for (Map.Entry<Integer, Neighbour> entry : neighbours.entrySet()) {
			drawNeighbourMarkerAtLatitudeLongitude(entry.getKey(),
					entry.getValue());
		}
	}

	protected void remove(Long playerId, Marker neighbour) {
		neighbour.setMap(null);

	}

	@Override
	public void highlightNeighbours(Map<Long, Neighbour> neighbours) {
		// TODO Auto-generated method stub
		// do nothing

	}
}
