package de.unipotsdam.nexplorer.client.indoor.levels;


import java.util.Random;

import de.unipotsdam.nexplorer.shared.PacketType;

public class PacketKeeper {

	private final Random random;

	public PacketKeeper() {
		this.random = new Random();
	}

	public PacketType createRandomPacket() {
		int pick = random.nextInt(PacketType.values().length);
		return PacketType.values()[pick];
	}
}
