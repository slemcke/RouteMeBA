package de.unipotsdam.nexplorer.client.android.support;

import de.unipotsdam.nexplorer.client.android.callbacks.Locatable;

public class LocationObserver extends AbstractObserver<Locatable, Location> {

	@Override
	protected void call(Locatable callback, Location parameter) {
		callback.locationChanged(parameter);
	}
}