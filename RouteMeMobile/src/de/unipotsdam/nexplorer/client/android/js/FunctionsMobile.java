package de.unipotsdam.nexplorer.client.android.js;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

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
import de.unipotsdam.nexplorer.client.android.support.RoutePacketObserver;
import de.unipotsdam.nexplorer.client.android.support.SendPacketObserver;
import de.unipotsdam.nexplorer.client.android.ui.UI;

@SuppressLint("UseSparseArrays")
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

	private Long playerId;
	private int playerRange;
	private int itemCollectionRange;
	private HashMap<Long, Packet> packets;
	private List<RoutingTable> table;

	// TODO packetId of packet to be sent (-1 if no send requested)
	private int packetId;
	private boolean sendMode;

	private Location currentLocation;
	private RestMobile rest;

	private final LocationObserver locationObserver;
	private final LoginObserver loginObserver;
	private final PingObserver pingObserver;
	private final CollectObserver collectObserver;
	private final RangeObserver rangeObserver;
	private final SendPacketObserver sendPacketObserver;
	private final RoutePacketObserver routePacketObserver;

	private Map<Integer, Neighbour> neighbours;

	// TODO neighbours with routes to packet that needs to be sent
	// private Map<Long, Neighbour> neighboursWithRoutes;

	// private Map<Integer, Neighbour> neighbourhood;

	private boolean debug = false;

	@SuppressLint("UseSparseArrays")
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
		SendPacket sendPacket = new SendPacket(rest, ui);
		RoutePacket routePacket = new RoutePacket(rest, ui);

		this.sendMode = false;

		this.locationObserver = new LocationObserver();
		this.locationObserver.add(sendLocation);
		this.locationObserver.add(radiusBlinker);
		this.locationObserver.add(requestPing);

		this.loginObserver = new LoginObserver();
		this.loginObserver.add(sendLocation);
		this.loginObserver.add(collectItem);
		this.loginObserver.add(requestPing);
		this.loginObserver.add(sendPacket);
		this.loginObserver.add(routePacket);

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
		this.sendPacketObserver.add(routePacket);

		this.routePacketObserver = new RoutePacketObserver();
		this.routePacketObserver.add(routePacket);

		this.playerId = null;
		this.table = new ArrayList<RoutingTable>();
		this.packets = new HashMap<Long, Packet>();
		// this.neighboursWithRoutes = new HashMap<Long, Neighbour>();
		gpsReceiver.watchPosition(this);
		mapTasks.setOnMapClickListener(this);
	}

	/**
	 */
	// TODO remove unused function parameters as we now use only global
	// variables
	private void computeNeighboursWithRoutes(List<RoutingTable> table,
			Packet packet, Map<Integer, Neighbour> neighbours) {
		if (packets != null && !(packets.isEmpty()) && sendMode) {
			// iterate over all known neighbours
			if (!(this.neighbours == null)) {
				for (Neighbour neighbour : this.neighbours.values()) {
					if (this.table != null) {
						for (RoutingTable tableEntry : this.table) {
							// neighbour has route to destination of packet
							System.out.println("Route to: "
									+ tableEntry.getDestinationId());
							try {
								if (debug)
									System.out
											.println("Packet wants to go to: "
													+ packets
															.get((long) packetId)
															.getMessageDescription()
															.getDestinationNodeId());
							} catch (Exception e) {

							}
							long tableDest = tableEntry.getDestinationId();
							long packetDest = packets.get((long) packetId)
									.getMessageDescription()
									.getDestinationNodeId();
							long neighbourId = neighbour.getId();
							long tableNext = tableEntry.getNextHopId();

							if ((tableDest == packetDest)
									&& (neighbourId == tableNext)) {
								if (debug) {
									System.out
											.println("Found neighbour with route to "
													+ packets
															.get((long) packetId)
															.getMessageDescription()
															.getDestinationNodeId()
													+ ": " + neighbour.getId());
								}
								// change neighbour
								neighbour.setHasRoute(true);
								if (debug)
									System.out.println("setting boolean...");
								neighbours.put((int) neighbour.getId(),
										neighbour);
								// neighboursWithRoutes.put(neighbour.getId(),
								// neighbour);
							} else {
								if (debug)
									System.out.println("Neighbour "
											+ neighbour.getId()
											+ " has no route to packet "
											+ packetId);
							}
						}
					}

				}
			}
			// globally update neighbours with routes
			// this.neighboursWithRoutes = neighboursWithRoutes;
			// return neighboursWithRoutes;
		}
		// return new HashMap<Long, Neighbour>();
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

			if (oldRange != playerRange) {
				rangeObserver.fire((double) playerRange);
			}

			// Packet related stuff

			packets = data.packets; // globally set packets

			table = data.routingTable;
			if (table != null) {
				for (RoutingTable tableEntry : table) {
					if (debug) {
						System.out.println("Tabelleneintrag: "
								+ tableEntry.getNodeId() + " Destination: "
								+ tableEntry.getDestinationId() + " next Hop: "
								+ tableEntry.getNextHopId());
					}
				}
			}

			// Map<Integer,Neighbour> neighboursWithRoutes =
			// data.node.getNeighboursWithRoutes();
			// int packetId = data.node.getCurrentPacketId();

			mapTasks.removeInvisibleMarkers(neighbours, nearbyItems, level);
			// TODO remove highlighted neighbours if necessary
			// if(level == 3){
			// cast map to match <integer,neighbour>...
			// if (neighboursWithRoutesInt != null) {
			// for (Neighbour n : neighboursWithRoutes.values()) {
			// neighboursWithRoutesInt.put((int) n.getId(), n);
			// }
			//
			// mapTasks.removeInvisibleMarkers(neighboursWithRoutesInt,
			// nearbyItems, level);
			// }
			computeNeighboursWithRoutes(table, packets.get(packetId),
					neighbours);
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
	private void updateDisplay(
			int playerRange,
			int itemCollectionRange,
			java.util.Map<Integer, Neighbour> neighbours,
			// Map<Long, Neighbour> neighboursWithRoutes,
			java.util.Map<Integer, Item> nearbyItems, String gameDifficulty,
			int score, int neighbourCount, long remainingPlayingTime,
			double battery, Integer nextItemDistance, boolean hasRangeBooster,
			boolean itemInCollectionRange, String hint, Long level,
			HashMap<Long, Packet> packets) {
		this.neighbours = neighbours;
		// this.neighboursWithRoutes = neighboursWithRoutes;
		// if (!(this.neighboursWithRoutes.isEmpty())
		// || !(neighboursWithRoutes.isEmpty())) {
		// System.out.println("global: " + this.neighboursWithRoutes
		// + " lokal: " + neighboursWithRoutes);
		// }
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

	public void sendPacket(View v) {
		// save packet id
		this.packetId = v.getId();
		System.out.println("start sending packet " + this.packetId);
		this.sendPacketObserver.fire(packets.get((long) v.getId()));
		computeNeighboursWithRoutes(table, packets.get((long) v.getId()),
				neighbours);
		this.sendMode = true;
	}

	public void loginSuccess(LoginAnswer data) {
		playerId = data.id;

		updateGameStatus(false);
		intervals.start();
		System.out.println("[REST] Setting playerId to "
				+ String.valueOf(playerId));
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
		if (sendMode) {
			// // if send packet is selected
			// HashMap<String, Integer> parameters = new HashMap<String,
			// Integer>();
			System.out.println("start routing...");
			int targetId = findNeighbour(arg0);
			if (targetId > 0) {
				System.out.println("Found neighbour " + targetId);
				routePacketObserver.fire(Long.valueOf(targetId));
				this.removeRoutes();
				this.sendMode = false;
			}
			// // this.pingObserver.fire();
			// System.out.println("Trying to send packet...");
			// // this.sendPacketObserver.fire(parameters);
			// TODO remove highlight from packet
		} else {
			this.pingObserver.fire();
		}
	}

	private void removeRoutes() {
		for (Neighbour n : neighbours.values()) {
			System.out.println("removing Routes...");
			n.setHasRoute(false);
			neighbours.put((int) n.getId(), n);
		}
	}

	// TODO return list of neighbours
	private int findNeighbour(LatLng position) {
		double lat = position.latitude;
		double lng = position.longitude;
		double nLat;
		double nLng;
		for (Neighbour neighbour : neighbours.values()) {
			if (neighbour.hasRoute()) {
				// for each neighbour call isPosition... and if true return with
				// neighbour
				// TODO what if two neighbours are in one circle?
				nLat = neighbour.getLatitude();
				nLng = neighbour.getLongitude();
				if (getDistanceFromLatLonInKm(lat, lng, nLat, nLng) < 0.001) {
					System.out.println("Neighbour " + neighbour.getId()
							+ " was clicked.");
					return (int) neighbour.getId();
				}

				System.out.println("Neighbour " + neighbour.getId()
						+ " was not clicked.");
			}
		}
		return -1;

	}

	// Entfernung zu anderem Knoten in km in Item-Colletion-Funktion im Backend
	private double calculateDistance(LatLng src, LatLng dest) {
		double R = 6371; // Erdradius in km

		double lat1 = Math.toRadians(src.latitude);
		double lat2 = Math.toRadians(dest.latitude);
		double lon1 = Math.toRadians(src.longitude);
		double lon2 = Math.toRadians(dest.longitude);
		if (lat2 == 0 && lon2 == 0) {
			return Double.MAX_VALUE;
		}

		double a = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(lon2 - lon1));
		double d = R * a;

		return d;
	}

	// public List<Item> getAllItemsNear(LatLng location) {
	// List<Neighbours> neighbours = neighboursWithRoutes.entrySet();
	//
	// // Filter by distance
	// List<Items> nearItems = new LinkedList<Items>();
	// for (Items item : items) {
	// if (locator.distance(location, item) <= playerRange / 1000) {
	// nearItems.add(item);
	// }
	// }
	//
	// // Order by distance
	// ItemDistanceComparator byDistance = new ItemDistanceComparator(location,
	// locator);
	// Collections.sort(nearItems, byDistance);
	//
	// // Return result
	// List<Item> result = new LinkedList<Item>();
	// for (Items item : nearItems) {
	// result.add(data.create(item));
	// }
	//
	// return result;
	// }
	//
	// public class ItemDistanceComparator implements Comparator<LatLng> {
	//
	// private Locatable location;
	// private Locator locator;
	//
	// public ItemDistanceComparator(Locatable location, Locator locator) {
	// this.location = location;
	// this.locator = locator;
	// }
	//
	// @Override
	// public int compare(Items o1, Items o2) {
	// double d1 = locator.distance(location, o1);
	// double d2 = locator.distance(location, o2);
	//
	// double delta = d1 - d2;
	// return Math.round(new Float(delta));
	// }
	private double getDistanceFromLatLonInKm(double lat1, double lon1,
			double lat2, double lon2) {
		// return 0.00001;
		double r = 6371; // Radius of the earth in km
		double dLat = deg2rad(lat2 - lat1); // deg2rad below
		double dLon = deg2rad(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = r * c; // Distance in km
		return d;
	}

	private double deg2rad(double deg) {
		return deg * (Math.PI / 180);
	}

	public void abortSending(View v) {
		// remove packet id
		if (debug)
			System.out.println("aborted sending packet " + this.packetId);
		this.packetId = -1;
		removeRoutes();
		this.sendMode = false;

	}
}