package de.unipotsdam.nexplorer.client.android.callbacks;

import java.util.HashMap;

import de.unipotsdam.nexplorer.client.android.rest.Packet;

public interface UIFooter {

	public void setIsCollectingItem(boolean isCollectingItem);
	void updateFooter(Integer nextItemDistance, boolean hasRangeBooster,
			boolean itemInCollectionRange, String hint,
			HashMap<Long, Packet> packages);
	
//	public void setIsSendingPackage(boolean isSendingPackage);
}
