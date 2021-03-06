package de.unipotsdam.nexplorer.client.android.maps;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;

import com.google.android.gms.maps.GoogleMap;

import de.unipotsdam.nexplorer.client.android.js.Marker;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public class LevelTwoNeighbourDrawer extends LevelOneNeighbourDrawer implements NeighbourDrawer {

	protected GoogleMap senchaMap;
	protected Map<Integer, NeighbourPing> neighbourPings = new HashMap<Integer, NeighbourPing>();
	protected Activity host;

	public LevelTwoNeighbourDrawer(GoogleMap senchaMap, Activity host) {
		super(senchaMap, host);
		this.senchaMap = senchaMap;
		this.host = host;
	}

	@Override
	protected void drawNeighbourMarkerAtLatitudeLongitude(int playerId, Neighbour neighbour) {
		super.drawNeighbourMarkerAtLatitudeLongitude(playerId, neighbour);
		if (neighbour.isPingActive()) {
			surroundWithPing(playerId, neighbour);
		}
	}

	private void surroundWithPing(int playerId, Neighbour neighbour) {
		if (!neighbourPings.containsKey(playerId)) {
			NeighbourPing ping = new NeighbourPing(senchaMap, neighbour, host, this, playerId);
			neighbourPings.put(playerId, ping);
		} else {
			NeighbourPing ping = neighbourPings.get(playerId);
			ping.update(neighbour.getLatitude(), neighbour.getLongitude());
		}
	}

	public void finishedPing(int playerId) {
		neighbourPings.remove(playerId);
	}

	@Override
	protected void remove(Long long1, Marker neighbour) {
		super.remove(long1, neighbour);
		NeighbourPing ping = neighbourPings.get(long1);
		if (ping != null) {
			ping.kill();
		}
	}
}
