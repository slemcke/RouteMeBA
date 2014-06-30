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
public class LevelThreeNeighbourDrawer extends LevelTwoNeighbourDrawer implements NeighbourDrawer{
	private java.util.Map<Integer, Marker> neighbourHighlightMarkersArray = new HashMap<Integer, Marker>();

	public LevelThreeNeighbourDrawer(GoogleMap senchaMap, Activity host) {
		super(senchaMap, host);
//		this.senchaMap = senchaMap;
//		this.host = host;
	}
	
	public void highlightNeighbours(Map<Integer, Neighbour> neighbours){
		for (Map.Entry<Integer, Neighbour> entry : neighbours.entrySet()) {
			highlightNeighbourMarkerAtLatitudeLongitude(entry.getKey(), entry.getValue());
		}
	}
	
	protected void highlightNeighbourMarkerAtLatitudeLongitude(final int playerId, Neighbour neighbour) {
		final LatLng latlng = new LatLng(neighbour.getLatitude(), neighbour.getLongitude());
		final MarkerImage image = new MarkerImage(drawable.network_wireless_small_highlighted);

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

}
