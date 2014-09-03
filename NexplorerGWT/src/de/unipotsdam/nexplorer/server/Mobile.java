package de.unipotsdam.nexplorer.server;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.unipotsdam.nexplorer.client.MobileService;
import de.unipotsdam.nexplorer.client.android.rest.RoutingRequest;
import de.unipotsdam.nexplorer.client.android.rest.PingRequest;
import de.unipotsdam.nexplorer.client.android.rest.PingResponse;
import de.unipotsdam.nexplorer.client.android.rest.RoutingResponse;
import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.aodv.AodvFactory;
import de.unipotsdam.nexplorer.server.aodv.AodvNode;
import de.unipotsdam.nexplorer.server.aodv.AodvRoutingAlgorithm;
import de.unipotsdam.nexplorer.server.data.ItemCollector;
import de.unipotsdam.nexplorer.server.data.NodeMapper;
import de.unipotsdam.nexplorer.server.data.PlayerDoesNotExistException;
import de.unipotsdam.nexplorer.server.data.Unit;
import de.unipotsdam.nexplorer.server.di.LogWrapper;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Player;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.AodvRoutingTableEntries;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.PositionBacklog;
import de.unipotsdam.nexplorer.server.rest.dto.NodeGameSettingsJSON;
import de.unipotsdam.nexplorer.shared.DataPacket;
import de.unipotsdam.nexplorer.shared.Game;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.PlayerLocation;

/**
 * 
 * @author Julian Dehne and Hendrik Geßner
 * 
 */
@SuppressWarnings("serial")
public class Mobile extends RemoteServiceServlet implements MobileService {

	private Logger performance;

	public Mobile() {
		this.performance = LogManager.getLogger("performance");
	}

	/**
	 * Collects an item which should be present in the db as well as shown in the gui. Is there any case where this might not be so?
	 */
	@Override
	public boolean collectItem(long playerId) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			ItemCollector items = unit.resolve(ItemCollector.class);
			items.collectFor(playerId);
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("collectItem took {}ms", end - begin);
		return true;
	}

	/**
	 * Be careful, aodv.updateNeighbourhood() is possible victim of race conditions
	 */
	@Override
	public boolean updateNeighbours(long playerId) {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			LogWrapper logger = unit.resolve(LogWrapper.class);
			logger.getLogger().trace("Updating neighbours of {}", playerId);

			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			// an dieser Stelle hatten wir vorgesehen, dass die Schnittstelle noch nicht vom Algorithmus abhängt
			// ist ein nice to have
			AodvRoutingAlgorithm aodv = unit.resolve(AodvRoutingAlgorithm.class);
			Player player = dbAccess.getPlayerById(playerId);
			aodv.updateNeighbourhood(player);
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}

		long end = System.currentTimeMillis();
		performance.trace("updateNeighbours took {}ms", end - begin);
		return true;
	}

	@Override
	public boolean updatePlayerPosition(PlayerLocation location) {
		long begin = System.currentTimeMillis();

		blockingUpdate(location);

		long end = System.currentTimeMillis();
		performance.trace("updatePlayerPositions took {}ms", end - begin);
		return true;
	}

	private static synchronized void blockingUpdate(PlayerLocation location) {
		Unit unit = new Unit();
		try {
			LogWrapper logger = unit.resolve(LogWrapper.class);
			logger.getLogger().trace("Updating position of {}", location.getPlayerId());

			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			Player thePlayer = dbAccess.getPlayerById(location.getPlayerId());

			thePlayer.setLocation(location);
			thePlayer.save();

			PositionBacklog backlog = new PositionBacklog();
			backlog.setAccuracy(location.getAccuracy());
			backlog.setCreated(new Date().getTime());
			backlog.setHeading(location.getHeading());
			backlog.setPlayerId(location.getPlayerId());
			backlog.setLatitude(location.getLatitude());
			backlog.setLongitude(location.getLongitude());
			backlog.setSpeed(location.getSpeed());
			dbAccess.persist(backlog);

			// Wenn leichtester Schwierigkeitsgrad, Nachbarschaft aktualisieren
			if (thePlayer.getDifficulty() == Game.DIFFICULTY_EASY) {
				unit.resolve(AodvFactory.class).create(thePlayer).updateNeighbourhood();
			}
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}
	}

	protected static synchronized void blockingNeighbourUpdate(Mobile instance) {
		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			List<Player> nodes = dbAccess.getAllActiveNodesInRandomOrder();
			for (Player node : nodes) {
				instance.updateNeighbours(node.getId());
			}
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();
		}
	}

	public NodeGameSettingsJSON getGameStatus(long id) throws PlayerDoesNotExistException {
		long begin = System.currentTimeMillis();

		Unit unit = new Unit();
		try {
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			NodeMapper mapper = unit.resolve(NodeMapper.class);
			Players p = dbAccess.getRawById(id);

			Players node = mapper.from(p);
			GameStats stats = new GameStats(dbAccess.getSettings().inner());
			// set packets for nodes in json-response
			Player outdoor = dbAccess.getPlayerById(id);
			List<AodvDataPacket> packets = new LinkedList<AodvDataPacket>();
			if(outdoor.getDifficulty()==3){
				packets.addAll(dbAccess.getAllDataPacketsLvlThreeSortedByDate(outdoor));
			} else {
				packets.addAll(dbAccess.getAllDataPacketsSortedByDate(outdoor));
			}
			HashMap<Long, DataPacket> jsonPackets = new HashMap<Long, DataPacket>();
			for (AodvDataPacket packet : packets) {
				DataPacket jsonPacket = mapper.toJSON(packet);
				jsonPackets.put(jsonPacket.getId(), jsonPacket);
			}
			//returns the routing table for nodes
			List<AodvRoutingTableEntries> table = dbAccess.getRoutingTable(id);
			NodeGameSettingsJSON result = new NodeGameSettingsJSON(stats, node, jsonPackets, table);
			return result;
		} catch (Exception e) {
			unit.cancel();
			throw new RuntimeException(e);
		} finally {
			unit.close();

			long end = System.currentTimeMillis();
			performance.trace("getGameStatus took {}ms", end - begin);
		}
	}

	public PingResponse addPing(PingRequest request) {
		Unit unit = new Unit();
		try {
			long nodeId = request.getNodeId();

			DatabaseImpl dbAccesss = unit.resolve(DatabaseImpl.class);
			Player player = dbAccesss.getPlayerById(nodeId);
			AodvNode node = unit.resolve(AodvFactory.class).create(player);

			node.pingNeighbourhood();
			if(node.player().getDifficulty() == 2){
				node.player().decreaseBatteryBy(5);
			} else if (node.player().getDifficulty() == 3){
				node.player().decreaseBatteryBy(1);
			}

			PingResponse result = new PingResponse();
			result.setPingId(0);
			return result;
		} catch (Throwable e) {
			unit.cancel();
			throw new WebApplicationException(e);
		} finally {
			unit.close();
		}
	}
	
	/*
	 * method for level three player to send packets
	 */
	public RoutingResponse sendPacket(RoutingRequest request){
		Unit unit = new Unit();
		try {
			long nextHopId = request.getNextHopId();
			
			DatabaseImpl dbAccess = unit.resolve(DatabaseImpl.class);
			
			AodvDataPacket packet = dbAccess.getDataPacketById(request.getPacketId());
			AodvNode currentNode = packet.getCurrentNode();		
			Player next = dbAccess.getPlayerById(nextHopId);
			AodvNode nextHop = unit.resolve(AodvFactory.class).create(next);
			
			long currentScore = currentNode.player().getScore();
			packet.forwardPacket(currentNode, nextHop);
			
			long newScore = currentNode.player().getScore();
			RoutingResponse response = new RoutingResponse();

			// lediglich Basispunkte oder weniger erhalten
			if(newScore - currentScore <= 100){
				response.setFeedback("bad");
			// innerhalb der ersten 60 sekunden gesendet, also: Punkte=Basispunkte(100) + 20facher Paketwert
			} else if(newScore - currentScore >= (long)packet.inner().getType()*20 +100){
				response.setFeedback("good");
			} else {
				response.setFeedback("ok");
			}
			
			return response;
			
		} catch (Throwable e) {
			unit.cancel();
			throw new WebApplicationException(e);
		} finally {
			unit.close();
		}
			
	}
}
