package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.collectionRadius;
import static de.unipotsdam.nexplorer.client.android.js.Window.each;
import static de.unipotsdam.nexplorer.client.android.js.Window.playerMarker;
import static de.unipotsdam.nexplorer.client.android.js.Window.playerRadius;
import static de.unipotsdam.nexplorer.client.android.js.Window.ui;

import java.util.HashMap;
import java.util.Map;

import de.unipotsdam.nexplorer.client.android.R.drawable;
import de.unipotsdam.nexplorer.client.android.commons.Location;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public class MapRelatedTasks {

	private final SenchaMap senchaMap;
	private java.util.Map<Integer, Marker> nearbyItemMarkersArray = new HashMap<Integer, Marker>();
	private java.util.Map<Integer, Marker> neighbourMarkersArray = new HashMap<Integer, Marker>();

	public MapRelatedTasks(SenchaMap senchaMap) {
		this.senchaMap = senchaMap;
	}

	void drawMarkers(Map<Integer, Neighbour> neighbours, Map<Integer, Item> nearbyItems) {
		if (neighbours != null) {
			each(neighbours, new Call<Integer, Neighbour>() {

				@Override
				public void call(Integer key, Neighbour value) {
					drawNeighbourMarkerAtLatitudeLongitude(key, value.getLatitude(), value.getLongitude());
				}
			});
		}

		if (nearbyItems != null) {
			each(nearbyItems, new Call<Integer, Item>() {

				@Override
				public void call(Integer key, Item value) {
					drawNearbyItemMarkerAtLatitudeLongitude(key, value.getItemType(), value.getLatitude(), value.getLongitude());
				}
			});
		}
	}

	/**
	 * draw nearby items
	 * 
	 * @param itemId
	 * @param type
	 * @param latitude
	 * @param longitude
	 */
	void drawNearbyItemMarkerAtLatitudeLongitude(int itemId, String type, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		int imagePath = 0;
		if ("BATTERY".equals(type)) {
			imagePath = drawable.battery_charge;
		} else {
			imagePath = drawable.mobile_phone_cast;
		}

		final MarkerImage image = new MarkerImage(imagePath);

		if (nearbyItemMarkersArray.get(itemId) == null) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					position = latlng;
					map = senchaMap.map;
					icon = image;
					zIndex = 1;
				}
			};

			nearbyItemMarkersArray.put(itemId, marker);
		} else {
			nearbyItemMarkersArray.get(itemId).setPosition(latlng);
			if (nearbyItemMarkersArray.get(itemId).map == null) {
				nearbyItemMarkersArray.get(itemId).setMap(senchaMap.map);
			}
		}
	}

	void removeInvisibleMarkers(final java.util.Map<Integer, Neighbour> neighbours, final java.util.Map<Integer, Item> nearbyItems) {
		each(neighbourMarkersArray, new Call<Integer, Marker>() {

			public void call(Integer key, Marker theMarker) {
				if (theMarker != null && neighbours.get(key) == null) {
					neighbourMarkersArray.get(key).setMap(null);
				}
			}
		});

		each(nearbyItemMarkersArray, new Call<Integer, Marker>() {

			public void call(Integer key, Marker theMarker) {
				if (theMarker != null && nearbyItems.get(key) == null) {
					nearbyItemMarkersArray.get(key).setMap(null);
				}
			}
		});
	}

	/**
	 * draw the neighbours
	 * 
	 * @param playerId
	 * @param latitude
	 * @param longitude
	 */
	void drawNeighbourMarkerAtLatitudeLongitude(final int playerId, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		final MarkerImage image = new MarkerImage(drawable.network_wireless_small);

		if (neighbourMarkersArray.get(playerId) == null) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					position = latlng;
					map = senchaMap.map;
					title = "(" + playerId + ") ";
					icon = image;
					zIndex = 1;
				}
			};

			neighbourMarkersArray.put(playerId, marker);
		} else {
			neighbourMarkersArray.get(playerId).setPosition(latlng);
			neighbourMarkersArray.get(playerId).setTitle("(" + playerId + ") " /* + name */);
			if (neighbourMarkersArray.get(playerId).map == null) {
				neighbourMarkersArray.get(playerId).setMap(senchaMap.map);
			}
		}
	}

	void centerAtCurrentLocation(Location currentLocation, int playerRange, int itemCollectionRange) {
		if (currentLocation != null) {
			// Karte zentrieren
			senchaMap.map.setCenter(new LatLng(currentLocation));
			// Spieler Marker zentrieren
			playerMarker.setPosition(new LatLng(currentLocation));
			if (playerMarker.map == null) {
				playerMarker.setMap(senchaMap.map);
			}
			// Senderadius zentrieren
			playerRadius.setCenter(new LatLng(currentLocation));
			if (playerRadius.map == null) {
				playerRadius.setMap(senchaMap.map);
			}
			playerRadius.setRadius(playerRange);
			// Sammelradius zentrieren
			collectionRadius.setCenter(new LatLng(currentLocation));
			if (collectionRadius.map == null) {
				collectionRadius.setMap(senchaMap.map);
			}
			collectionRadius.setRadius(itemCollectionRange);
		}
	}
}
