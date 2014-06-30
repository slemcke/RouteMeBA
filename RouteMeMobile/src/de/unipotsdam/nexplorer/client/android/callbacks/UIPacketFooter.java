package de.unipotsdam.nexplorer.client.android.callbacks;

import java.util.HashMap;

import de.unipotsdam.nexplorer.client.android.rest.Packet;

public interface UIPacketFooter {

	public void updateFooter(final HashMap<Long,Packet> packets, Long level);

	public void setIsSendingPacket(boolean isSendingPacket);

}
