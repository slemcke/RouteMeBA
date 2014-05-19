package de.unipotsdam.nexplorer.client.indoor.levels;


import java.util.ArrayList;
import java.util.List;

import de.unipotsdam.nexplorer.shared.PacketType;

public class PacketKeeper {

	private int i;
	private int j;

	public PacketKeeper() {
	}

	//returns 15 packets (three per type)
	public List<String> createPacketList() {
		
		List<String> packetList = new ArrayList<String>(); 
		for(i = 0; i<5; i++){
			for (j = 0; j<3; j++){
				packetList.add(PacketType.values()[i].toString());
			}
		}
		
		return packetList;
	}
}
