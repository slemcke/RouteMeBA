package de.unipotsdam.nexplorer.server.aodv;

import static de.unipotsdam.nexplorer.testing.RefWalker.refEq;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;
import com.mysql.jdbc.PacketTooBigException;

import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.data.Referee;
import de.unipotsdam.nexplorer.server.di.GuiceFactory;
import de.unipotsdam.nexplorer.server.persistence.DataFactory;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.Role;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvDataPackets;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvNodeData;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRouteRequestBufferEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingMessages;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.PacketType;

public class AodvRoutingAlgorithmTest {

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

	private void makeExNeighbours(Player one, Player two, Players pOne, Players pTwo) {
		when(locator.isInRange(refEq(one), refEq(two))).thenReturn(false);

		pOne.getNeighbourses().add(new Neighbours(pTwo, pOne));
		pTwo.getNeighbourses().add(new Neighbours(pOne, pTwo));
	}

	@Test
	public void testInsertNewMessageWithoutRoute() throws PlayerDoesNotExistException {

		    
		long dataProcessingRound = 5l;
		long routingProcessingRound = 15l;
		settings.setCurrentDataPacketProcessingRound(dataProcessingRound);
		settings.setCurrentRoutingMessageProcessingRound(routingProcessingRound);

		long srcSequenceNumber = 1337l;
	
		srcPlayer.setSequenceNumber(srcSequenceNumber);
		
		makeNeighbours(src, other, srcPlayer, otherPlayer);

		
		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Collection<Object> result = sut.aodvInsertNewMessage(src, dest, owner, null);
		
		AodvRoutingMessages RREQ = new AodvRoutingMessages(other.getId(), Aodv.ROUTING_MESSAGE_TYPE_RREQ, src.getId(), dest.getId(), 8l, srcSequenceNumber + 1, 1l, "1", routingProcessingRound + 1);
		AodvDataPackets dataPacket = new AodvDataPackets(destPlayer, ownerPlayer, srcPlayer, srcPlayer, (short) 0, Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE, dataProcessingRound + 1, (byte) 0, null, 0);
		AodvRouteRequestBufferEntries bufferEntry = new AodvRouteRequestBufferEntries(src.getId(), src.getId(), srcPlayer.getSequenceNumber());

		verify(dbAccess).persist(srcPlayer);
		verify(dbAccess).persist(refEq(bufferEntry));
		verify(dbAccess).persist(refEq(RREQ));
		assertTrue(result.contains(dataPacket));
		
		
		//PacketType is set
		result = sut.aodvInsertNewMessage(src, dest, owner, (byte)1);
		
		AodvDataPackets dataPacket1 = new AodvDataPackets(destPlayer, ownerPlayer, srcPlayer, srcPlayer, (short) 0, Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE, dataProcessingRound + 1, (byte) 0, (byte) 1, 0);	
		assertTrue(result.contains(dataPacket1));
	}

	@Test
	public void testInsertNewMessageWithInsufficientNodeBattery() throws PlayerDoesNotExistException {
		srcPlayer.setBattery(0.);
		makeNeighbours(src, other, srcPlayer, otherPlayer);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Collection<Object> result = sut.aodvInsertNewMessage(src, dest, owner, null);

		verify(dbAccess).getSettings();
		verifyNoMoreInteractions(dbAccess);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testInsertNewMessageWithRoute() throws PlayerDoesNotExistException {
		AodvRoutingTableEntries srcRoute = new AodvRoutingTableEntries();
		srcRoute.setDestinationId(destPlayer.getId());
		srcRoute.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		srcRoute.setHopCount(2l);
		srcRoute.setNextHopId(otherPlayer.getId());
		srcRoute.setNodeId(srcPlayer.getId());
		srcRoute.setTimestamp(new Date().getTime());

		AodvRoutingTableEntries otherRoute = new AodvRoutingTableEntries();
		otherRoute.setDestinationId(destPlayer.getId());
		otherRoute.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		otherRoute.setHopCount(1l);
		otherRoute.setNextHopId(destPlayer.getId());
		otherRoute.setNodeId(otherPlayer.getId());
		otherRoute.setTimestamp(new Date().getTime());

		when(dbAccess.getRouteToDestination(destPlayer.getId(), srcPlayer.getId())).thenReturn(factory.create(srcRoute));
		when(dbAccess.getRouteToDestination(destPlayer.getId(), otherPlayer.getId())).thenReturn(factory.create(otherRoute));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		Collection<Object> result = sut.aodvInsertNewMessage(src, dest, owner, (byte)1);

		AodvDataPackets packet = new AodvDataPackets();
		packet.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		packet.setDidReachBonusGoal((byte) 0);
		packet.setHopsDone((short) 0);
		packet.setPlayersByCurrentNodeId(srcPlayer);
		packet.setPlayersByDestinationId(destPlayer);
		packet.setPlayersByOwnerId(ownerPlayer);
		packet.setPlayersBySourceId(srcPlayer);
		packet.setProcessingRound(3l);
		packet.setType((byte)1);
		packet.setCreated(0l);

		assertTrue(result.contains(packet));
	}

	@Test
	public void testProcessDataPacketsWithPacketWaitingForRoute() {
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));

		settings.setCurrentDataPacketProcessingRound(5l);
		settings.setCurrentRoutingMessageProcessingRound(1l);

		AodvDataPackets packets = new AodvDataPackets();
		packets.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		packets.setDidReachBonusGoal(null);
		packets.setHopsDone(null);
		packets.setId(2l);
		packets.setPlayersByCurrentNodeId(srcPlayer);
		packets.setPlayersByDestinationId(destPlayer);
		packets.setPlayersByOwnerId(ownerPlayer);
		packets.setPlayersBySourceId(srcPlayer);
		packets.setProcessingRound(0l);
		AodvDataPacket packet = factory.create(packets);

		when(dbAccess.getAllDataPacketsSortedByDate(src)).thenReturn(Arrays.asList(packet));
		when(dbAccess.getRouteRequestCount(packet)).thenReturn(1);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessDataPackets();

		AodvDataPackets result = new AodvDataPackets();
		result.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		result.setDidReachBonusGoal(null);
		result.setHopsDone(null);
		result.setId(2l);
		result.setPlayersByCurrentNodeId(srcPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(6l);

		verify(dbAccess).persist(refEq(result));
	}

	@Test
	public void testProcessDataPacketWithNewRoute() {
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));
		makeNeighbours(src, other, srcPlayer, otherPlayer);

		settings.setCurrentDataPacketProcessingRound(5l);
		settings.setCurrentRoutingMessageProcessingRound(1l);

		AodvDataPackets packets = new AodvDataPackets();
		packets.setStatus(Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE);
		packets.setDidReachBonusGoal(null);
		packets.setHopsDone((short) 0);
		packets.setId(2l);
		packets.setPlayersByCurrentNodeId(srcPlayer);
		packets.setPlayersByDestinationId(destPlayer);
		packets.setPlayersByOwnerId(ownerPlayer);
		packets.setPlayersBySourceId(srcPlayer);
		packets.setProcessingRound(0l);
		AodvDataPacket packet = factory.create(packets);

		AodvRoutingTableEntries routings = new AodvRoutingTableEntries();
		routings.setDestinationId(destPlayer.getId());
		routings.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		routings.setHopCount(2l);
		routings.setId(1l);
		routings.setNextHopId(otherPlayer.getId());
		routings.setNodeId(srcPlayer.getId());
		routings.setTimestamp(new Date().getTime());
		AodvRoutingTableEntry routingEntry = factory.create(routings);

		when(dbAccess.getAllDataPacketsSortedByDate(src)).thenReturn(Arrays.asList(packet));
		when(dbAccess.getRouteRequestCount(packet)).thenReturn(1);
		when(dbAccess.getRouteToDestination(destPlayer.getId(), srcPlayer.getId())).thenReturn(routingEntry);

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessDataPackets();

		AodvDataPackets result = new AodvDataPackets();
		result.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(otherPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(6l);
		result.setCreated(0l);
		String[] except = { "created" };
		verify(dbAccess).persist(refEq(result,except));
		verify(dbAccess).delete(packets);
	}

	@Test
	public void testProcessDataPacketWithKnownRoute() {
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));
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
		when(dbAccess.getAllDataPacketsSortedByDate(src)).thenReturn(Arrays.asList(factory.create(packets)));

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


		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessDataPackets();

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
	
	@Test
	public void testProcessDataPacketForLevelThree() {
		//packets not send
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));
		makeNeighbours(src, other, srcPlayer, otherPlayer);
		
		srcPlayer.setDifficulty(3l);
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
		when(dbAccess.getAllDataPacketsSortedByDate(src)).thenReturn(Arrays.asList(factory.create(packets)));

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

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessDataPackets();

		AodvDataPackets result = new AodvDataPackets();
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(srcPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(4l);
		result.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);

		String[] except = { "created" };
		verify(dbAccess, never()).persist(refEq(result, except));
		verify(dbAccess, never()).delete(packets);
	}

	@Test
	public void testProcessRREQTransmission() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);

		makeNeighbours(src, other, srcPlayer, otherPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(other.getId());
		entry.setDestinationSequenceNumber(otherPlayer.getSequenceNumber());
		entry.setHopCount(1l);
		entry.setId(1l);
		entry.setNextHopId(other.getId());
		entry.setNodeId(src.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(other.getId(), src.getId())).thenReturn(factory.create(entry));

		AodvRoutingMessages rreq = new AodvRoutingMessages();
		rreq.setCurrentNodeId(src.getId());
		rreq.setDestinationId(dest.getId());
		rreq.setHopCount(0l);
		rreq.setId(2l);
		rreq.setLifespan(9l);
		rreq.setPassedNodes(null);
		rreq.setProcessingRound(currentRoutingRound);
		rreq.setSequenceNumber(12l);
		rreq.setSourceId(src.getId());
		rreq.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);
		when(dbAccess.getRouteRequestsByNodeAndRound(src)).thenReturn(Arrays.asList(factory.create(rreq)));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessRoutingMessages();

		AodvRoutingMessages result = new AodvRoutingMessages();
		result.setCurrentNodeId(other.getId());
		result.setDestinationId(dest.getId());
		result.setHopCount(1l);
		result.setId(null);
		result.setLifespan(8l);
		result.setPassedNodes(src.getId().toString());
		result.setProcessingRound(currentRoutingRound + 1);
		result.setSequenceNumber(12l);
		result.setSourceId(src.getId());
		result.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);

		verify(dbAccess).persist(refEq(result));
		verify(dbAccess).delete(rreq);
	}

	@Test
	public void testProcessRREQToDestination() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);

		makeNeighbours(src, dest, srcPlayer, destPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(dest));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(src.getId());
		entry.setDestinationSequenceNumber(srcPlayer.getSequenceNumber());
		entry.setHopCount(1l);
		entry.setId(1l);
		entry.setNextHopId(src.getId());
		entry.setNodeId(dest.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(src.getId(), dest.getId())).thenReturn(factory.create(entry));

		AodvRoutingMessages rreq = new AodvRoutingMessages();
		rreq.setCurrentNodeId(dest.getId());
		rreq.setDestinationId(dest.getId());
		rreq.setHopCount(1l);
		rreq.setId(2l);
		rreq.setLifespan(8l);
		rreq.setPassedNodes("1");
		rreq.setProcessingRound(currentRoutingRound);
		rreq.setSequenceNumber(12l);
		rreq.setSourceId(src.getId());
		rreq.setType(Aodv.ROUTING_MESSAGE_TYPE_RREQ);
		when(dbAccess.getRouteRequestsByNodeAndRound(dest)).thenReturn(Arrays.asList(factory.create(rreq)));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessRoutingMessages();

		AodvRoutingTableEntries result = new AodvRoutingTableEntries();
		result.setDestinationId(dest.getId());
		result.setDestinationSequenceNumber(12l);
		result.setHopCount(1l);
		result.setId(null);
		result.setNextHopId(dest.getId());
		result.setNodeId(src.getId());

		verify(dbAccess).persist(refEq(result, "timestamp"));
	}

	@Test
	public void testTransmitionOfRERR() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);

		makeNeighbours(src, other, srcPlayer, otherPlayer);
		makeNeighbours(other, dest, otherPlayer, destPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(destPlayer.getId());
		entry.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		entry.setHopCount(2l);
		entry.setId(1l);
		entry.setNextHopId(otherPlayer.getId());
		entry.setNodeId(srcPlayer.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(dest.getId(), src.getId())).thenReturn(factory.create(entry));

		AodvRoutingMessages rerr = new AodvRoutingMessages();
		rerr.setCurrentNodeId(src.getId());
		rerr.setDestinationId(dest.getId());
		rerr.setHopCount(0l);
		rerr.setId(1l);
		rerr.setLifespan(9l);
		rerr.setPassedNodes(null);
		rerr.setProcessingRound(0l);
		rerr.setSequenceNumber(3l);
		rerr.setSourceId(src.getId());
		rerr.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);
		when(dbAccess.getRoutingErrors(src)).thenReturn(Arrays.asList(factory.create(rerr)));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessRoutingMessages();

		AodvRoutingMessages result = new AodvRoutingMessages();
		result.setCurrentNodeId(other.getId());
		result.setDestinationId(dest.getId());
		result.setHopCount(1l);
		result.setId(null);
		result.setLifespan(8l);
		result.setPassedNodes(Long.toString(src.getId()));
		result.setProcessingRound(currentRoutingRound + 1);
		result.setSequenceNumber(3l);
		result.setSourceId(src.getId());
		result.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);

		verify(dbAccess).persist(refEq(result));
		verify(dbAccess).delete(rerr);
	}

	@Test
	public void testCreationOfRERR() {

	}

	@Test
	public void testNotifyOfLostNeighbours() {
		long currentRoutingRound = 322;
		settings.setCurrentRoutingMessageProcessingRound(currentRoutingRound);
		long srcSequenceNumber = 144;
		srcPlayer.setSequenceNumber(srcSequenceNumber);

		makeExNeighbours(src, other, srcPlayer, otherPlayer);
		makeNeighbours(src, dest, srcPlayer, destPlayer);
		when(dbAccess.getNeighboursWithinRange(src)).thenReturn(Arrays.asList(dest));

		AodvRoutingTableEntries fromSrcToDest = new AodvRoutingTableEntries();
		fromSrcToDest.setDestinationId(destPlayer.getId());
		fromSrcToDest.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		fromSrcToDest.setHopCount(1l);
		fromSrcToDest.setId(1l);
		fromSrcToDest.setNextHopId(destPlayer.getId());
		fromSrcToDest.setNodeId(srcPlayer.getId());
		fromSrcToDest.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(dest.getId(), src.getId())).thenReturn(factory.create(fromSrcToDest));

		Neighbours neighbour = new Neighbours();
		neighbour.setId(1l);
		neighbour.setNode(srcPlayer);
		neighbour.setNeighbour(destPlayer);
		when(dbAccess.getNeighbour(dest.getId(), src.getId())).thenReturn(data.create(neighbour));
		when(dbAccess.getAllNeighboursExcept(refEq(other), refEq(src))).thenReturn(Arrays.asList(data.create(neighbour)));

		AodvNode sut = injector.getInstance(AodvFactory.class).create(src);
		sut.updateNeighbourhood();

		AodvRoutingMessages rerr = new AodvRoutingMessages();
		rerr.setCurrentNodeId(dest.getId());
		rerr.setDestinationId(other.getId());
		rerr.setHopCount(null);
		rerr.setId(null);
		rerr.setLifespan(null);
		rerr.setPassedNodes(src.getId().toString());
		rerr.setProcessingRound(currentRoutingRound + 1);
		rerr.setSequenceNumber(srcSequenceNumber + 1);
		rerr.setSourceId(src.getId());
		rerr.setType(Aodv.ROUTING_MESSAGE_TYPE_RERR);

		verify(dbAccess).persist(refEq(rerr));
	}

	@Test
	public void testNotifyOfPacketArrival() {
		long currentDataRound = 23;
		settings.setCurrentDataPacketProcessingRound(currentDataRound);

		makeNeighbours(src, dest, srcPlayer, destPlayer);
		when(dbAccess.getAllActiveNodesInRandomOrder()).thenReturn(Arrays.asList(src));

		AodvRoutingTableEntries entry = new AodvRoutingTableEntries();
		entry.setDestinationId(destPlayer.getId());
		entry.setDestinationSequenceNumber(destPlayer.getSequenceNumber());
		entry.setHopCount(1l);
		entry.setId(1l);
		entry.setNextHopId(destPlayer.getId());
		entry.setNodeId(srcPlayer.getId());
		entry.setTimestamp(new Date().getTime());
		when(dbAccess.getRouteToDestination(dest.getId(), src.getId())).thenReturn(factory.create(entry));

		AodvDataPackets packet = new AodvDataPackets();
		packet.setDidReachBonusGoal(null);
		packet.setHopsDone((short) 0);
		packet.setId(1l);
		packet.setPlayersByCurrentNodeId(srcPlayer);
		packet.setPlayersByDestinationId(destPlayer);
		packet.setPlayersByOwnerId(ownerPlayer);
		packet.setPlayersBySourceId(srcPlayer);
		packet.setProcessingRound(currentDataRound);
		packet.setStatus(Aodv.DATA_PACKET_STATUS_UNDERWAY);
		AodvDataPacket dataPacket = factory.create(packet);
		when(dbAccess.getAllDataPacketsSortedByDate(src)).thenReturn(Arrays.asList(dataPacket));

		AodvRoutingAlgorithm sut = injector.getInstance(AodvRoutingAlgorithm.class);
		sut.aodvProcessDataPackets();

		AodvDataPackets result = new AodvDataPackets();
		result.setDidReachBonusGoal(null);
		result.setHopsDone((short) 1);
		result.setId(null);
		result.setPlayersByCurrentNodeId(destPlayer);
		result.setPlayersByDestinationId(destPlayer);
		result.setPlayersByOwnerId(ownerPlayer);
		result.setPlayersBySourceId(srcPlayer);
		result.setProcessingRound(currentDataRound + 1);
		result.setStatus(Aodv.DATA_PACKET_STATUS_ARRIVED);

		String[] except = { "created" };
		verify(referee).packetArrived(any(Setting.class), any(AodvDataPacket.class));
		verify(dbAccess).persist(refEq(result, except));
	}
}
