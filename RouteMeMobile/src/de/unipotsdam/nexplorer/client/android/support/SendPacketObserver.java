package de.unipotsdam.nexplorer.client.android.support;

import java.util.Map;

import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;

public class SendPacketObserver extends ObserverWithParameter<Sendable, Map<String,Integer>>{

	@Override
	protected void call(Sendable callback, Map<String,Integer> parameters) {
		callback.sendRequested(parameters);
		
	}

}
