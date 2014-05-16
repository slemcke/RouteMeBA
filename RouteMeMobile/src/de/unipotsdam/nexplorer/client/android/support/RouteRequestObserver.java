package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;
import de.unipotsdam.nexplorer.client.android.rest.RoutingRequest;

public class RouteRequestObserver extends ObserverWithParameter<Sendable, RoutingRequest> {

	@Override
	protected void call(Sendable callback, RoutingRequest request) {
		RoutingRequest data = request;
		callback.sendRequested(data.getPacketId(),data.getNextHopId());
	}
}
