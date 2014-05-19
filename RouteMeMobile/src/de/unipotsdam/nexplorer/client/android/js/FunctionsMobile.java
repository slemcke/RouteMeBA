package de.unipotsdam.nexplorer.client.android.js;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract.Contacts.Data;

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
import de.unipotsdam.nexplorer.client.android.net.RoutePacket;
import de.unipotsdam.nexplorer.client.android.net.SendLocation;
import de.unipotsdam.nexplorer.client.android.rest.GameStatus;
import de.unipotsdam.nexplorer.client.android.rest.Item;
import de.unipotsdam.nexplorer.client.android.rest.LoginAnswer;
import de.unipotsdam.nexplorer.client.android.rest.Neighbour;
import de.unipotsdam.nexplorer.client.android.rest.Packet;
import de.unipotsdam.nexplorer.client.android.rest.RoutingRequest;
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
import de.unipotsdam.nexplorer.client.android.support.RouteRequestObserver;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class FunctionsMobile implements PositionWatcher, OnMapClickListener, ShakeListener {

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
	private boolean isSendingPacket;

	private int currentPacketId;
	private Location currentLocation;
	private RestMobile rest;
	
	private GameStatus data;


	private final LocationObserver locationObserver;
	private final LoginObserver loginObserver;
	private final PingObserver pingObserver;
	private final CollectObserver collectObserver;
	private final RangeObserver rangeObserver;
	private final RouteRequestObserver routeRequestObserver;

	public FunctionsMobile(UI ui, AppWrapper app, Handler handler, NexplorerMap mapTasks, RestMobile rest, RadiusBlinker blinker, TouchVibrator vibrator, GpsReceiver gpsReceiver) {
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
		RoutePacket routePacket = new RoutePacket(rest,ui);
		CollectItemVibration vibration = new CollectItemVibration(vibrator);

		this.locationObserver = new LocationObserver();
		this.locationObserver.add(sendLocation);
		this.locationObserver.add(radiusBlinker);
		this.locationObserver.add(requestPing);

		this.loginObserver = new LoginObserver();
		this.loginObserver.add(sendLocation);
		this.loginObserver.add(collectItem);
		this.loginObserver.add(requestPing);

		this.pingObserver = new PingObserver();
		this.pingObserver.add(radiusBlinker);
		this.pingObserver.add(requestPing);

		this.collectObserver = new CollectObserver();
		this.collectObserver.add(collectItem);
		this.collectObserver.add(vibration);

		this.rangeObserver = new RangeObserver();
		this.rangeObserver.add(radiusBlinker);
		
		this.routeRequestObserver = new RouteRequestObserver();
		this.routeRequestObserver.add(routePacket);
		this.isSendingPacket = false;
		
		this.data = new GameStatus();

		gpsReceiver.watchPosition(this);
		mapTasks.setOnMapClickListener(this);
	}

	/**
	 * Dise Funktion wird zun‰chst aufgerufen sie loggt den spier ein und zeigt bei existierenden Spiel eine Karte
	 * 
	 * @param name
	 */
	public void loginPlayer(final String name) {
		if (name != "") {
			new LoginTask(uiLogin, rest, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name);
		}
	}

	/**
	 * callback for the geolocation
	 */
	@Override
	public void positionReceived(Location location) {
		// TODO: Failswitch einbauen, um Warnung bei zu lange ausbleibenden Positionen anzuzeigen
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
	 * diese methode holt sich regelm‰ﬂig (alle 5000ms) ein update from server ob des aktuellen Spielstandes
	 * 
	 * @param isAsync
	 */
	void updateGameStatus(final boolean isAsync) {
		if (!gameStatusRequestExecutes) {
			new UpdateGameStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	private class UpdateGameStatus extends AsyncTask<Void, Void, GameStatus> {

		private GameStatus data;

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
			this.data=data;

			int oldRange = playerRange;

			// Spielstatus und Spielinformationen
			String gameDifficulty = data.stats.getGameDifficulty();
			boolean gameIsRunning = data.stats.settings.isRunningBoolean();
			long remainingPlayingTime = data.stats.getRemainingPlayingTime();
			boolean gameExists = data.stats.isGameExistingBoolean();
			boolean gameDidExist = gameExists;
			int itemCollectionRange = data.stats.settings.getItemCollectionRange();
			boolean gameDidEnd = data.stats.hasEndedBoolean();

			// Spielerinformationen
			double battery = data.node.getBatterieLevel();
			int neighbourCount = data.node.getNeighbourCount();
			int score = data.node.getScore();
			int playerRange = data.node.getRange();
			long level = data.node.getDifficulty();
			java.util.Map<Integer, Neighbour> neighbours = data.node.getNeighbours();
			java.util.Map<Integer, Item> nearbyItems = data.node.getNearbyItems().getItems();
			Integer nextItemDistance = data.node.getNextItemDistance();
			boolean itemInCollectionRange = data.node.isItemInCollectionRangeBoolean();
			boolean hasRangeBooster = data.node.hasRangeBoosterBoolean();
			String hint = data.getHint();
			
			
			//Pakete
			
//			HashMap<Long, DataPacket> packagesMap = data.packets;
			HashMap<Long, Packet> packages = data.packets;
//			//manipulate map to match gui requirements (id, type)
//			Collection<Long> keys = packagesMap.keySet();
//			for (Long key : keys) {
//				DataPacket packet = packagesMap.get(key);
//				if(packet.get)
//				packages.put(value.getId(), value.getType());
//			}
//
//			if (oldRange != playerRange) {
//				rangeObserver.fire((double) playerRange);
//			}

			mapTasks.removeInvisibleMarkers(neighbours, nearbyItems, gameDifficulty);

			adjustGameLifecycle(gameExists, gameDidExist, gameDidEnd, gameIsRunning, battery);
			if(data.node.isSendPacketActive()){
				
			}

			//TODO use right neighbours for route
			HashMap<Integer,Neighbour> routeNeighbours = null;
			updateDisplay(playerRange, itemCollectionRange, neighbours, nearbyItems, gameDifficulty, score, neighbourCount, remainingPlayingTime, battery, nextItemDistance, hasRangeBooster, itemInCollectionRange, hint,level, packages,routeNeighbours);
		}
	}
	
	public void sendPacket1(GameStatus data, RoutingRequest request, LatLng neighbourPos){
		Map<Integer,Neighbour> neighbours = data.node.getNeighbours();
		double  lat;
		double lng;
		RoutingTable table = data.table;
		double neighbourLat = neighbourPos.latitude;
		double neighbourLng = neighbourPos.longitude;
		//find Neighbour
		for(Neighbour neighbour: neighbours.values()){
			lat = neighbour.getLatitude();
			lng = neighbour.getLongitude();
			//Neighbour is valid for sending packet
			if(neighbourLat == lat && neighbourLng == lng){
				if(table.getNextHopId().equals(neighbour.getId())){
					
				}
			}
			
		}
	}

	private void adjustGameLifecycle(boolean gameExists, boolean gameDidExist, boolean gameDidEnd, boolean gameIsRunning, double battery) {
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
	 * updates the display with the new position and the positions of the neighbours
	 */
	private void updateDisplay(int playerRange, int itemCollectionRange, java.util.Map<Integer, Neighbour> neighbours, java.util.Map<Integer, Item> nearbyItems, String gameDifficulty, int score, int neighbourCount, long remainingPlayingTime, double battery, Integer nextItemDistance, boolean hasRangeBooster, boolean itemInCollectionRange, String hint, Long level,HashMap<Long,Packet> packages,HashMap<Integer,Neighbour> routeNeighbours) {
		mapTasks.updateMap(playerRange, itemCollectionRange, neighbours, nearbyItems, gameDifficulty,routeNeighbours);
		ui.updateStatusHeaderAndFooter(score, neighbourCount, remainingPlayingTime, battery, nextItemDistance, hasRangeBooster, itemInCollectionRange, hint,level, packages);
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		this.collectObserver.fire(itemCollectionRange);
	}
	
	public void sendPacket(LatLng arg0){
		RoutingRequest request = new RoutingRequest();
		Map<Integer, Neighbour> neighbours = data.node.getNeighbours();
		//TODO catch id=-1
		long nextHopId = findNeighbour(neighbours, arg0);
		request.setNextHopId(nextHopId);
		request.setPacketId(data.node.getCurrentPacketId());
		//TODO find node
		this.routeRequestObserver.fire(request);
	}

	private long findNeighbour(Map<Integer,Neighbour> neighbours, LatLng position){
		double lat = position.latitude;
		double lng = position.longitude;
		double nLat;
		double nLng;
		for (Neighbour neighbour: neighbours.values()){
			nLat = neighbour.getLatitude();
			nLng = neighbour.getLongitude();
			if(nLat == lat && nLng == lng){
				return neighbour.getId();
			}
		}
		return -1;
		
	}
	public void loginSuccess(LoginAnswer data) {
		playerId = data.id;

		updateGameStatus(false);
		intervals.start();
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
		//route if routing is requested
//		if(isSendingPacket){
//			this.sendPacket(arg0);
//		}
//		//or ping if not 
//		else{
			this.pingObserver.fire();
//		}
	}

	public int getCurrentPacketId() {
		return currentPacketId;
	}

	public void setCurrentPacketId(int currentPacketId) {
		this.currentPacketId = currentPacketId;
	}
	
	public void setIsSendingPacket(boolean isSendingPacket){
		this.isSendingPacket = isSendingPacket;
	}
}