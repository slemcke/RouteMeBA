package de.unipotsdam.nexplorer.client.android.maps;

import java.util.Map;

import de.unipotsdam.nexplorer.client.android.rest.Neighbour;

public interface NeighbourHighlighter {

	public void removeMarks(Map<Integer, Neighbour> neighbours);

	public void addMarks(Map<Integer, Neighbour> neighbours);
}
