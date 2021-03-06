package de.unipotsdam.nexplorer.server.rest;

import static de.unipotsdam.nexplorer.server.rest.dto.LoginResultJSON.NO_GAME_CREATED;
import static de.unipotsdam.nexplorer.server.rest.dto.LoginResultJSON.SERVER_ERROR;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Items;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Neighbours;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Settings;
import de.unipotsdam.nexplorer.server.rest.dto.LoginResultJSON;
import de.unipotsdam.nexplorer.server.rest.dto.NodeGameSettingsJSON;
import de.unipotsdam.nexplorer.shared.DataPacket;
import de.unipotsdam.nexplorer.shared.GameStats;
import de.unipotsdam.nexplorer.shared.GameStatus;
import de.unipotsdam.nexplorer.shared.ItemType;

@Path("mobile_test/")
public class MobileTest {

	@GET
	@Path("get_game_status")
	@Produces("application/json")
	public NodeGameSettingsJSON getNodeGameSettingsJSON() {
		NodeGameSettingsJSON result = new NodeGameSettingsJSON(null, null, null, null);
		result.hint = "hint";

		result.node = new Players();
		result.node.setBattery(80.);
		result.node.setHasSignalRangeBooster(1l);
		result.node.setItemInCollectionRange(1l);
		result.node.setNeighbourCount(1);
		result.node.setNextItemDistance(3l);
		result.node.setRange(10);
		result.node.setScore(180l);
		result.node.setLastPing(new Date().getTime() - 2000);
		result.node.setPingDuration(1000);

		result.node.setNearbyItems(new ArrayList<Items>());
		result.node.getNearbyItemsJSON().setItemMap(new HashMap<Long, Items>());

		result.node.getNearbyItemsJSON().getItemMap().put(1l, new Items());
		result.node.getNearbyItemsJSON().getItemMap().get(1l).setItemType(ItemType.BATTERY);
		result.node.getNearbyItemsJSON().getItemMap().get(1l).setLatitude(52.14);
		result.node.getNearbyItemsJSON().getItemMap().get(1l).setLongitude(13.77);

		result.node.getNearbyItemsJSON().getItemMap().put(2l, new Items());
		result.node.getNearbyItemsJSON().getItemMap().get(2l).setItemType(ItemType.BOOSTER);
		result.node.getNearbyItemsJSON().getItemMap().get(2l).setLatitude(12.34);
		result.node.getNearbyItemsJSON().getItemMap().get(2l).setLongitude(56.78);

		result.node.setNearbyItemsCount(2);

		Players src = result.node;
		Players neigh = new Players();
		neigh.setId(7l);
		neigh.setLastPing(new Date().getTime());
		neigh.setPingDuration(1000);
		result.node.setNeighbourses(new HashSet<Neighbours>());
		result.node.getNeighbourses().add(new Neighbours(neigh, src));

		result.gameStats = new GameStats(new Settings());
		result.gameStats.getSettings().setBaseNodeRange(9l);
		result.gameStats.setGameStatus(GameStatus.ISRUNNING);
		result.gameStats.setGameDifficulty(2l);
		result.gameStats.setRemainingPlaytime(1234567890l);
		
		result.packets = new HashMap<Long,DataPacket>();
		DataPacket packet = new DataPacket();
		packet.setId((long) 0);
		packet.setType((byte) 1);
		result.packets.put(packet.getId(), packet);
		

		return result;
	}

	@POST
	@Path("login_player_mobile")
	@Produces("application/json")
	public LoginResultJSON loginPlayerMobile(@FormParam("name") String name, @FormParam("isMobile") String isMobile) {
		if (name.equals("IAmOk")) {
			return new LoginResultJSON(7);
		} else if (name.equals("NoGameCreated")) {
			return new LoginResultJSON(NO_GAME_CREATED);
		} else {
			return new LoginResultJSON(SERVER_ERROR);
		}
	}
}
