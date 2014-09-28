package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;
import de.unipotsdam.nexplorer.client.android.rest.Packet;

public class SendPacketObserver extends ObserverWithParameter<Sendable, Packet>{

	@Override
	protected void call(Sendable callback, Packet packet) {
		callback.sendRequested(packet);
		
	}

}
