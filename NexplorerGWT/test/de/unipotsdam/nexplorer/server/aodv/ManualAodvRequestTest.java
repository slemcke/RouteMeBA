package de.unipotsdam.nexplorer.server.aodv;


import static de.unipotsdam.nexplorer.testing.RefWalker.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;
import com.mchange.util.AssertException;

import de.unipotsdam.nexplorer.client.android.rest.RoutingRequest;
import de.unipotsdam.nexplorer.client.android.rest.RoutingResponse;
import de.unipotsdam.nexplorer.server.Mobile;
import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Role;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvNodeData;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.Aodv;

public class ManualAodvRequestTest {

	private Players srcPlayer;
	private Players destPlayer;
	private Players ownerPlayer;
	private Players otherPlayer;
	private Settings settings;
	private DatabaseImpl dbAccess;
	private Locator locator;
	private Referee referee;
	private Player src;
	private Player dest;
	private Player owner;
	private Player other;
	private Injector injector;
	private AodvFactory factory;
	private DataFactory data;

	@Before
	public void setUp() {
		
		srcPlayer = new Players();
		srcPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		srcPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		srcPlayer.setBattery(50.);
		srcPlayer.setHasSignalRangeBooster(0l);
		srcPlayer.setHasSignalStrengthBooster(0l);
		srcPlayer.setId(1l);
		srcPlayer.setLastPositionUpdate(new Date().getTime());
		srcPlayer.setLatitude(0.);
		srcPlayer.setLongitude(0.);
		srcPlayer.setName("Source");
		srcPlayer.setNeighbourses(new HashSet<Neighbours>());
		srcPlayer.setRemainingHighPriorityMessages(0);
		srcPlayer.setRole(Role.NODE);
		srcPlayer.setScore(0l);
		srcPlayer.setSequenceNumber(5l);

		destPlayer = new Players();
		destPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		destPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		destPlayer.setBattery(75.);
		destPlayer.setHasSignalRangeBooster(0l);
		destPlayer.setHasSignalStrengthBooster(0l);
		destPlayer.setId(2l);
		destPlayer.setLastPositionUpdate(new Date().getTime());
		destPlayer.setLatitude(0.);
		destPlayer.setLongitude(0.);
		destPlayer.setName("Destination");
		destPlayer.setNeighbourses(new HashSet<Neighbours>());
		destPlayer.setRemainingHighPriorityMessages(0);
		destPlayer.setRole(Role.NODE);
		destPlayer.setScore(10l);
		destPlayer.setSequenceNumber(55l);

		ownerPlayer = new Players();
		ownerPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		ownerPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		ownerPlayer.setBattery(0.);
		ownerPlayer.setHasSignalRangeBooster(0l);
		ownerPlayer.setHasSignalStrengthBooster(0l);
		ownerPlayer.setId(3l);
		ownerPlayer.setLastPositionUpdate(0l);
		ownerPlayer.setLatitude(0.);
		ownerPlayer.setLongitude(0.);
		ownerPlayer.setName("Owner");
		ownerPlayer.setNeighbourses(new HashSet<Neighbours>());
		ownerPlayer.setRemainingHighPriorityMessages(0);
		ownerPlayer.setRole(Role.MESSAGE);
		ownerPlayer.setScore(100l);
		ownerPlayer.setSequenceNumber(0l);

		otherPlayer = new Players();
		otherPlayer.setAodvDataPacketsesForCurrentNodeId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvDataPacketsesForDestinationId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvDataPacketsesForOwnerId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvDataPacketsesForSourceId(new HashSet<AodvDataPackets>());
		otherPlayer.setAodvNodeDatas(new HashSet<AodvNodeData>());
		otherPlayer.setBattery(100.);
		otherPlayer.setHasSignalRangeBooster(0l);
		otherPlayer.setHasSignalStrengthBooster(0l);
		otherPlayer.setId(4l);
		otherPlayer.setLastPositionUpdate(new Date().getTime());
		otherPlayer.setLatitude(0.);
		otherPlayer.setLongitude(0.);
		otherPlayer.setName("Other");
		otherPlayer.setNeighbourses(new HashSet<Neighbours>());
		otherPlayer.setRemainingHighPriorityMessages(0);
		otherPlayer.setRole(Role.NODE);
		otherPlayer.setScore(1000l);
		otherPlayer.setSequenceNumber(555l);

		settings = new Settings();
		settings.setBaseNodeRange(10l);
		settings.setBonusGoal(1l);
		settings.setCurrentDataPacketProcessingRound(2l);
		settings.setCurrentRoutingMessageProcessingRound(22l);
		settings.setDidEnd((byte) 0);
		settings.setDifficulty(0l);
		settings.setId(0l);
		settings.setIsRunning((byte) 1);
		settings.setItemCollectionRange(5l);
		settings.setLastPause(0l);
		settings.setMaxBatteries(10l);
		settings.setMaxBoosters(10l);
		settings.setPlayingFieldLowerRightLatitude(75.);
		settings.setPlayingFieldLowerRightLongitude(75.);
		settings.setPlayingFieldUpperLeftLatitude(0.);
		settings.setPlayingFieldUpperLeftLongitude(0.);
		settings.setPlayingTime(180l);
		settings.setProtocol("aodv");
		settings.setRemainingPlayingTime(170l);
		settings.setRunningSince(new Date().getTime() - 10);
		settings.setUpdateDisplayIntervalTime(3000l);
		settings.setUpdatePositionIntervalTime(3000l);

		dbAccess = mock(DatabaseImpl.class);
		when(dbAccess.getSettings()).thenReturn(new Setting(settings, dbAccess));

		locator = mock(Locator.class);
		referee = mock(Referee.class);
		injector = GuiceFactory.createInjector(dbAccess, locator, referee);
		data = injector.getInstance(DataFactory.class);

		src = data.create(srcPlayer);
		dest = data.create(destPlayer);
		owner = data.create(ownerPlayer);
		other = data.create(otherPlayer);

		when(dbAccess.getPlayerById(1l)).thenReturn(src);
		when(dbAccess.getPlayerById(2l)).thenReturn(dest);
		when(dbAccess.getPlayerById(3l)).thenReturn(owner);
		when(dbAccess.getPlayerById(4l)).thenReturn(other);

		factory = injector.getInstance(AodvFactory.class);
		
		
	}
	
	private void makeNeighbours(Player one, Player two, Players pOne, Players pTwo) {

		when(locator.isInRange(refEq(one), refEq(two))).thenReturn(true);

		pOne.getNeighbourses().add(new Neighbours(pTwo, pOne));
		pTwo.getNeighbourses().add(new Neighbours(pOne, pTwo));
	}


	@Test
	public void testSendPacket() {

		makeNeighbours(src, other, srcPlayer, otherPlayer);

		settings.setCurrentDataPacketProcessingRound(3l);

		AodvDataPackets packets = new AodvDataPackets();
		packets.setDidReachBonusGoal(null);
		packets.setHopsDone((short) 0);
		packets.setId(2l);
		packets.setPlayersByCurrentNodeId(srcPlayer);
		packets.setPlayersByDestinationId(destPlayer);
		packets.setPlayersByOwnerId(ownerPlayer);
		packets.setPlayersBySourceId(srcPlayer);
		packets.setProcessingRound(3l);
		packets.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);

		AodvRoutingTableEntries fromSrcToOther = new AodvRoutingTableEntries();
		fromSrcToOther.setDestinationId(destPlayer.getId());
		fromSrcToOther.setDestinationSequenceNumber(2l);
		fromSrcToOther.setHopCount(2l);
		fromSrcToOther.setId(1l);
		fromSrcToOther.setNextHopId(otherPlayer.getId());
		fromSrcToOther.setNodeId(srcPlayer.getId());
		fromSrcToOther.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(destPlayer.getId(), srcPlayer.getId())).thenReturn(factory.create(fromSrcToOther));

		AodvRoutingTableEntries fromOtherToDest = new AodvRoutingTableEntries();
		fromOtherToDest.setDestinationId(destPlayer.getId());
		fromOtherToDest.setDestinationSequenceNumber(2l);
		fromOtherToDest.setHopCount(1l);
		fromOtherToDest.setId(2l);
		fromOtherToDest.setNextHopId(destPlayer.getId());
		fromOtherToDest.setNodeId(otherPlayer.getId());
		fromOtherToDest.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(otherPlayer.getId(), destPlayer.getId())).thenReturn(factory.create(fromOtherToDest));

		AodvDataPacket packet = factory.create(packets);
		AodvNode currentNode = factory.create(src);
		AodvNode nextNode = factory.create(other);
		packet.forwardPacket(currentNode, nextNode);
		
		AodvDataPackets result = new AodvDataPackets();
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(otherPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(4l);
		result.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);

		String[] except = { "created" };
		verify(dbAccess).persist(refEq(result, except));
		verify(dbAccess).delete(packets);
		

		
	}
}
