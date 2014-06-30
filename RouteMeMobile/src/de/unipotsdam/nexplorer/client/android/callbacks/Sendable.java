package de.unipotsdam.nexplorer.client.android.callbacks;

import de.unipotsdam.nexplorer.client.android.rest.Packet;


public interface Sendable {
	public void sendRequested(Packet packet);
}
