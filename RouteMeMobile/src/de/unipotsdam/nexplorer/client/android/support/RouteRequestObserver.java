package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;

public class RouteRequestObserver extends ObserverWithParameter<Sendable, Integer> {

	@Override
	protected void call(Sendable callback, Integer parameter) {
		callback.sendRequested(parameter);
	}
}
