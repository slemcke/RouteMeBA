package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Routable;

public class RoutePacketObserver extends ObserverWithParameter<Routable, Long>{

	@Override
	protected void call(Routable callback, Long nextHopId) {
		callback.routeRequested(nextHopId);
		
	}

}
