package de.unipotsdam.nexplorer.client.android.callbacks;

import java.util.Map;

public interface Sendable {
	public void sendRequested(Map<String,Integer> parameters);
}
