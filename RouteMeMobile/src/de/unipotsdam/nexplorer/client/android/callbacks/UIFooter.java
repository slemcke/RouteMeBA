package de.unipotsdam.nexplorer.client.android.callbacks;

import java.util.HashMap;

public interface UIFooter {

	public void updateFooter(final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint, final HashMap<Long,Byte> packages);
//	public void updateFooter(final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint);
	public void setIsCollectingItem(boolean isCollectingItem);
	
//	public void setIsSendingPackage(boolean isSendingPackage);
}
