package de.unipotsdam.nexplorer.client.android.js;

import java.util.HashMap;
import java.util.Map;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.LatLng;

import de.unipotsdam.nexplorer.client.android.NexplorerMap;
import de.unipotsdam.nexplorer.client.android.callbacks.RemovalReason;
import de.unipotsdam.nexplorer.client.android.callbacks.UIGameEvents;
import de.unipotsdam.nexplorer.client.android.callbacks.UILogin;
import de.unipotsdam.nexplorer.client.android.callbacks.UISensors;
import de.unipotsdam.nexplorer.client.android.js.tasks.LoginTask;
import de.unipotsdam.nexplorer.client.android.net.CollectItem;
import de.unipotsdam.nexplorer.client.android.net.RequestPing;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.net.SendLocation;
import de.unipotsdam.nexplorer.client.android.net.SendPacket;
import de.unipotsdam.nexplorer.client.android.rest.GameStatus;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;
import de.unipotsdam.nexplorer.client.android.rest.Packet;
import de.unipotsdam.nexplorer.client.android.rest.RoutingTable;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver;
import de.unipotsdam.nexplorer.client.android.sensors.GpsReceiver.PositionWatcher;
import de.unipotsdam.nexplorer.client.android.sensors.ShakeDetector.ShakeListener;
import de.unipotsdam.nexplorer.client.android.sensors.TouchVibrator;
import de.unipotsdam.nexplorer.client.android.support.CollectObserver;
import de.unipotsdam.nexplorer.client.android.support.LocationObserver;
import de.unipotsdam.nexplorer.client.android.support.LoginObserver;
import de.unipotsdam.nexplorer.client.android.support.PingObserver;
import de.unipotsdam.nexplorer.client.android.support.RangeObserver;
import de.unipotsdam.nexplorer.client.android.support.SendPacketObserver;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class FunctionsMobile implements PositionWatcher, OnMapClickListener,
		ShakeListener {

	private final NexplorerMap mapTasks;
	private final Interval intervals;
	private final UI ui;
	private final UILogin uiLogin;
	private final UISensors uiSensors;
	private final UIGameEvents uiGameEvents;
	private final AppWrapper app;

	// TODO: Parameter flexibilisieren
	private final double minAccuracy = 11;

	private boolean gameStatusRequestExecutes = false;

	private Long playerId = null;
	private int playerRange;
	private int itemCollectionRange;
	private int packetId;

	private Location currentLocation;
	private RestMobile rest;

	private final LocationObserver locationObserver;
	private final LoginObserver loginObserver;
	private final PingObserver pingObserver;
	private final CollectObserver collectObserver;
	private final RangeObserver rangeObserver;
	private final SendPacketObserver sendPacketObserver;

	private Map<Integer, Neighbour> neighbours;

	// private Map<Integer, Neighbour> neighbourhood;

	public FunctionsMobile(UI ui, AppWrapper app, Handler handler,
			NexplorerMap mapTasks, RestMobile rest, RadiusBlinker blinker,
			TouchVibrator vibrator, GpsReceiver gpsReceiver) {
		this.mapTasks = mapTasks;
		this.intervals = new UpdateGameStatusInterval(handler, this);
		this.app = app;
		this.ui = ui;
		this.uiLogin = ui;
		this.uiSensors = ui;
		this.uiGameEvents = ui;
		this.rest = rest;

		SendLocation sendLocation = new SendLocation(rest);
		CollectItem collectItem = new CollectItem(rest, ui);
		RadiusBlinker radiusBlinker = blinker;
		RequestPing requestPing = new RequestPing(rest);
		CollectItemVibration vibration = new CollectItemVibration(vibrator);
		SendPacket sendPacket = new SendPacket(rest,ui);

		this.locationObserver = new LocationObserver();
		this.locationObserver.add(sendLocation);
		this.locationObserver.add(radiusBlinker);
		this.locationObserver.add(requestPing);

		this.loginObserver = new LoginObserver();
		this.loginObserver.add(sendLocation);
		this.loginObserver.add(collectItem);
		this.loginObserver.add(requestPing);
		this.loginObserver.add(sendPacket);
		
		this.pingObserver = new PingObserver();
		this.pingObserver.add(radiusBlinker);
		this.pingObserver.add(requestPing);

		this.collectObserver = new CollectObserver();
		this.collectObserver.add(collectItem);
		this.collectObserver.add(vibration);

		this.rangeObserver = new RangeObserver();
		this.rangeObserver.add(radiusBlinker);

		this.sendPacketObserver = new SendPacketObserver();
		this.sendPacketObserver.add(sendPacket);

		gpsReceiver.watchPosition(this);
		mapTasks.setOnMapClickListener(this);
	}

	/**
	 * Diese Funktion wird zun‰chst aufgerufen. Sie loggt den spieler ein und
	 * zeigt bei existierendem Spiel eine Karte
	 * 
	 * @param name
	 */
	public void loginPlayer(final String name) {
		if (name != "") {
			new LoginTask(uiLogin, rest, this).executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, name);
		}
	}

	/**
	 * callback for the geolocation
	 */
	@Override
	public void positionReceived(Location location) {
		// TODO: Failswitch einbauen, um Warnung bei zu lange ausbleibenden
		// Positionen anzuzeigen
		if (location.getAccuracy() > minAccuracy) {
			return;
		}

		uiSensors.positionReceived();

		this.currentLocation = location;
		mapTasks.centerAt(currentLocation);

		this.locationObserver.fire(location);
	}

	/**
	 * callback for the geolocation
	 */
	@Override
	public void positionError(Exception error) {
		uiSensors.noPositionReceived();
	}

	/**
	 * diese methode holt sich regelm‰ﬂig (alle 5000ms) ein update from server
	 * ob des aktuellen Spielstandes
	 * 
	 * @param isAsync
	 */
	void updateGameStatus(final boolean isAsync) {
		if (!gameStatusRequestExecutes) {
			new UpdateGameStatus()
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private class UpdateGameStatus extends AsyncTask<Void, Void, GameStatus> {

		@Override
		protected void onPreExecute() {
			gameStatusRequestExecutes = true;
		}

		@Override
		protected GameStatus doInBackground(Void... params) {
			try {
				return rest.getGameStatus(playerId);
			} catch (Exception e) {
				cancel(false);
				return null;
			}
		}

		@Override
		protected void onCancelled(GameStatus result) {
			gameStatusRequestExecutes = false;
		}

		@Override
		protected void onPostExecute(GameStatus data) {
			gameStatusRequestExecutes = false;

			int oldRange = playerRange;

			// Spielstatus und Spielinformationen
			String gameDifficulty = data.stats.getGameDifficulty();
			boolean gameIsRunning = data.stats.settings.isRunningBoolean();
			long remainingPlayingTime = data.stats.getRemainingPlayingTime();
			boolean gameExists = data.stats.isGameExistingBoolean();
			boolean gameDidExist = gameExists;
			int itemCollectionRange = data.stats.settings
					.getItemCollectionRange();
			boolean gameDidEnd = data.stats.hasEndedBoolean();
			RoutingTable table = data.table;

			// Spielerinformationen
			double battery = data.node.getBatterieLevel();
			int neighbourCount = data.node.getNeighbourCount();
			int score = data.node.getScore();
			long level = data.node.getDifficulty();
			int playerRange = data.node.getRange();
			Map<Integer, Neighbour> neighbours = data.node.getNeighbours();
			Map<Integer, Item> nearbyItems = data.node.getNearbyItems()
					.getItems();
			Integer nextItemDistance = data.node.getNextItemDistance();
			boolean itemInCollectionRange = data.node
					.isItemInCollectionRangeBoolean();
			boolean hasRangeBooster = data.node.hasRangeBoosterBoolean();
			String hint = data.getHint();
			HashMap<Long, Packet> packets = data.packets;

			if (oldRange != playerRange) {
				rangeObserver.fire((double) playerRange);
			}

//			mapTasks.removeInvisibleMarkers(neighbours, nearbyItems, level);
			// TODO remove highlighted neighbours if necessary
			// if(level == 3){
			// mapTasks.removeInvisibleMarkers(highlightNeighbours, nearbyItems,
			// difficulty)
			// }
			adjustGameLifecycle(gameExists, gameDidExist, gameDidEnd,
					gameIsRunning, battery);

			updateDisplay(playerRange, itemCollectionRange, neighbours,
					nearbyItems, gameDifficulty, score, neighbourCount,
					remainingPlayingTime, battery, nextItemDistance,
					hasRangeBooster, itemInCollectionRange, hint, level,
					packets);

		}
	}

	private void adjustGameLifecycle(boolean gameExists, boolean gameDidExist,
			boolean gameDidEnd, boolean gameIsRunning, double battery) {
		// Spiel entsprechend der erhaltenen Informationen
		// anpassen
		if (gameDidEnd) {
			uiGameEvents.gameEnded();
		} else {
			if (battery > 0) {
				if (!gameExists && gameDidExist) {
					app.reload();
				} else if (!gameExists && !gameDidExist) {
					intervals.start();
					ui.showWaitingForGameStart();
				} else if (gameExists && gameDidExist && !gameIsRunning) {
					intervals.start();
					uiGameEvents.gamePaused();
				} else {
					intervals.start();
					uiGameEvents.gameResumed();
				}
			} else {
				uiGameEvents.playerRemoved(RemovalReason.NO_BATTERY);
			}
		}
	}

	/**
	 * updates the display with the new position and the positions of the
	 * neighbours
	 */
	void updateDisplay() {
		// updateDisplay(playerRange, itemCollectionRange, neighbours,
		// nearbyItems, gameDifficulty, score, neighbourCount,
		// remainingPlayingTime, battery, nextItemDistance, hasRangeBooster,
		// itemInCollectionRange, hint);
	}

	private void updateDisplay(int playerRange, int itemCollectionRange,
			java.util.Map<Integer, Neighbour> neighbours,
			java.util.Map<Integer, Item> nearbyItems, String gameDifficulty,
			int score, int neighbourCount, long remainingPlayingTime,
			double battery, Integer nextItemDistance, boolean hasRangeBooster,
			boolean itemInCollectionRange, String hint, Long level,
			HashMap<Long, Packet> packets) {
		mapTasks.updateMap(playerRange, itemCollectionRange, neighbours,
				nearbyItems, level);
		ui.updateStatusHeaderAndFooter(score, neighbourCount,
				remainingPlayingTime, battery, nextItemDistance,
				hasRangeBooster, itemInCollectionRange, hint, level, packets);
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		this.collectObserver.fire(itemCollectionRange);
	}

	public void loginSuccess(LoginAnswer data) {
		playerId = data.id;

		updateGameStatus(false);
		intervals.start();
		System.out.println("[REST] Setting playerId to " + String.valueOf(playerId));
		loginObserver.fire(playerId);
	}

	public static boolean isNaN(Integer result) {
		if (result == null) {
			return true;
		}
		return Double.isNaN(result);
	}

	public static double parseFloat(String value) {
		return Double.parseDouble(value);
	}

	public static Integer parseInt(String value) {
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}

	@Override
	public void shakeDetected(float accel) {
		collectItem();
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// if send packet is selected
		HashMap<String, Integer> parameters = new HashMap<String, Integer>();
//		if (neighbours.size() > 0) {
//			int targetId = findNeighbour(neighbours, arg0);
//			int packetId = this.packetId;
//			parameters.put("targetId", targetId);
//			parameters.put("packetId", packetId);
//		}
//		this.pingObserver.fire();
		System.out.println("Trying to send packet...");
		this.sendPacketObserver.fire(parameters);
	}

	private int findNeighbour(Map<Integer, Neighbour> neighbours,
			LatLng position) {
		double lat = position.latitude;
		double lng = position.longitude;
		double nLat;
		double nLng;
		for (Neighbour neighbour : neighbours.values()) {
			nLat = neighbour.getLatitude();
			nLng = neighbour.getLongitude();
			if (nLat == lat && nLng == lng) {
				return (int) neighbour.getId();
			}
		}
		return -1;

	}
}
