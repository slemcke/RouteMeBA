package de.unipotsdam.nexplorer.client.indoor.levels;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.unipotsdam.nexplorer.shared.PacketType;

public class PacketKeeper {

//	private final Random random;
	private int i;
	private int j;

	public PacketKeeper() {
//		this.random = new Random();
	}

	public List<String> createPacketList() {
		
		List<String> packetList = new ArrayList<String>(); 
		for(i = 0; i<5; i++){
			for (j = 0; j<3; j++){
				packetList.add(PacketType.values()[i].toString());
			}
		}
		
		return packetList;
//		int pick = random.nextInt(PacketType.values().length);
//		return PacketType.values()[pick];
	}
}
