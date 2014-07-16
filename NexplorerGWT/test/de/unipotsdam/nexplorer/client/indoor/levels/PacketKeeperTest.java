package de.unipotsdam.nexplorer.client.indoor.levels;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class PacketKeeperTest {

	private PacketKeeper sut;


	@Before
	public void before() {
		this.sut = new PacketKeeper();

	}
	
	@Test
	public void testCreatePacketList() {
		List<String> packets = sut.createPacketList();
		assertTrue(packets.size() == 15);
		assertTrue(packets.contains("Mail"));
		assertTrue(packets.contains("VoIP"));
		assertTrue(packets.contains("Navigation"));
		assertTrue(packets.contains("Chat"));
		assertTrue(packets.contains("Html"));
		assertFalse(packets.contains("Test"));
	}



}
