package de.unipotsdam.nexplorer.client.android.js;

import static de.unipotsdam.nexplorer.client.android.js.Window.beginDialog;
import static de.unipotsdam.nexplorer.client.android.js.Window.clearInterval;
import static de.unipotsdam.nexplorer.client.android.js.Window.collectionRadius;
import static de.unipotsdam.nexplorer.client.android.js.Window.each;
import static de.unipotsdam.nexplorer.client.android.js.Window.geolocation;
import static de.unipotsdam.nexplorer.client.android.js.Window.isNaN;
import static de.unipotsdam.nexplorer.client.android.js.Window.location;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginButton;
import static de.unipotsdam.nexplorer.client.android.js.Window.loginOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.mainPanelToolbar;
import static de.unipotsdam.nexplorer.client.android.js.Window.noPositionOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseFloat;
import static de.unipotsdam.nexplorer.client.android.js.Window.parseInt;
import static de.unipotsdam.nexplorer.client.android.js.Window.playerMarker;
import static de.unipotsdam.nexplorer.client.android.js.Window.playerRadius;
import static de.unipotsdam.nexplorer.client.android.js.Window.senchaMap;
import static de.unipotsdam.nexplorer.client.android.js.Window.setInterval;
import static de.unipotsdam.nexplorer.client.android.js.Window.ui;
import static de.unipotsdam.nexplorer.client.android.js.Window.undefined;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingForGameOverlay;
import static de.unipotsdam.nexplorer.client.android.js.Window.waitingText;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;

import de.unipotsdam.nexplorer.client.android.R;
import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.net.RestMobile;
import de.unipotsdam.nexplorer.client.android.support.Location;

/**
 * mainly legacy code from Tobias Moebert has been adapted to work with a java backend and gwt client wrapper
 * 
 * @author Julian Dehne
 */
public class FunctionsMobile implements PositionWatcher {

	Object playerMaker;

	// TODO: Parameter flexibilisieren
	double minAccuracy = 11;

	java.util.Map<Integer, Marker> neighbourMarkersArray = new HashMap();
	java.util.Map<Integer, Marker> nearbyItemMarkersArray = new HashMap();

	// Intervals

	Interval localisationInterval;
	Interval gameStatusInterval;
	Interval displayMarkerInterval;

	// Interval Ajax Request

	boolean positionRequestExecutes = false;
	boolean gameStatusRequestExecutes = false;
	boolean playerStatusRequestExecutes = false;
	boolean neighboursRequestExecutes = false;

	// Interval Times

	long updatePositionIntervalTime = 300;
	long updateDisplayIntervalTime = 300;

	Geolocator positionWatch = null;

	// Overlays

	// Panels

	// Player data

	int playerId;
	double serverLatitude;
	double serverLongitude;
	double battery = 100;
	java.util.Map<Integer, Neighbour> neighbours;
	int neighbourCount = 0;
	int score;
	int playerRange;
	java.util.Map<Integer, Item> nearbyItems;
	Object nearbyItemsCount;
	Object nextItemDistance;
	boolean itemInCollectionRange;
	boolean hasRangeBooster;
	String hint = "Achte auf die Hinweise!";

	// Game data

	boolean gameIsRunning;
	boolean gameExists;
	boolean gameDidExist = true; // die semantik davon, dass es mal ein Spiel gegeben
	// hat, ist mir unklar ... es hat hat schon immer ein
	// Spiel gegeben!
	int remainingPlayingTime;
	Object baseNodeRange;
	Object gameDifficulty = 0;
	int itemCollectionRange;
	boolean gameDidEnd = false;

	// Time Tracking

	long updatePositionStartTime;
	long updateGameStatusStartTime;
	Object updatePlayerStatusStartTime;

	long latencyTotal = 0;
	int latencyCount = 0;

	private Location currentLocation;

	/**
	 * Dise Funktion wird zun�chst aufgerufen sie loggt den spier ein und zeigt bei existierenden Spiel eine Karte
	 * 
	 * @param name
	 * @param isMobile
	 */
	public void loginPlayer(final String name, final boolean isMobile) {
		if (name != "") {
			loginButton.label("melde an...");

			new RestMobile().login(name, isMobile, new AjaxResult<LoginAnswer>() {

				@Override
				public void success(LoginAnswer data) {
					loginSuccess(data);
				}

				@Override
				public void error() {
					showLoginError("Exception wurde ausgel��t - Kein Spiel gestartet?");
				}
			});
		}
	}

	private void showLoginError(Object data) {
		beginDialog.setText("Kein Spiel da. Versuchen Sie es sp�ter noch einmal!");
		loginButton.label("anmelden ");
	}

	/**
	 * bewirkt, dass das Display regelm��ig aktualisiert wird und die aktuelle Position an den Server gesendet wird
	 */
	private void startIntervals() {
		startGameStatusInterval();
		startLocalisationInterval();
		startDisplayInterval();
	}

	private void stopIntervals() {
		clearInterval(localisationInterval);
		localisationInterval = null;

		geolocation.clearWatch(positionWatch);
		positionWatch = null;
	}

	private void startGameStatusInterval() {
		if (gameStatusInterval == undefined || gameStatusInterval == null) {
			gameStatusInterval = setInterval(new TimerTask() {

				@Override
				public void run() {
					try {
						updateGameStatus(true);
					} catch (Throwable e) {
						e.toString();
					}
				}
			}, updateDisplayIntervalTime);
		}
	}

	private void startLocalisationInterval() {
		if (localisationInterval == undefined || localisationInterval == null) {
			localisationInterval = setInterval(new TimerTask() {

				@Override
				public void run() {
					try {
						updatePosition();
					} catch (Throwable e) {
						e.toString();
					}
				}
			}, updatePositionIntervalTime);
		}
	}

	private void startDisplayInterval() {
		if (displayMarkerInterval == undefined || displayMarkerInterval == null) {
			displayMarkerInterval = setInterval(new TimerTask() {

				@Override
				public void run() {
					try {
						updateDisplay();
					} catch (Throwable e) {
						StringWriter w = new StringWriter();
						e.printStackTrace(new PrintWriter(w));
						String message = w.toString();
						e.toString();
					}
				}
			}, 500);
		}
	}

	/**
	 * sendet die aktuelle Positionsdaten an den Server
	 */
	private void updatePosition() {
		if (positionRequestExecutes == false && currentLocation != null) {
			positionRequestExecutes = true;
			updatePositionStartTime = new Date().getTime();

			new RestMobile().updatePlayerPosition(playerId, currentLocation, new AjaxResult<Object>() {

				@Override
				public void success() {
					updateLatency();
				}
			});
		}
	}

	private void updateLatency() {
		latencyCount++;
		latencyTotal += new Date().getTime() - updatePositionStartTime;
		// console.log("Count: " + latencyCount + " Latenz: " +
		// (latencyTotal / latencyCount));
		positionRequestExecutes = false;
	}

	/*
	 * // findet in h�heren Schwierigkeitsgraden Verwendung private void updateNeighbours() { if (neighboursRequestExecutes == false) { neighboursRequestExecutes = true; $.ajax({ type:"POST", data:"playerId=" + playerId, url:"../php/ajax/mobile/update_neighbours.php", timeout:5000, success:private void () { neighboursRequestExecutes = false; } }) } ; }
	 */

	/**
	 * callback for the geolocation
	 */
	public void positionReceived(Location location) {
		// TODO: Failswitch einbauen, um Warnung bei zu lange ausbleibenden Positionen anzuzeigen
		if (location.getAccuracy() > minAccuracy) {
			return;
		}

		noPositionOverlay.hide();
		this.currentLocation = location;
	}

	/**
	 * callback for the geolocation
	 */
	public void positionError(Exception error) {
		noPositionOverlay.show();
	}

	/**
	 * diese methode holt sich regelm��ig (alle 5000ms) ein update from server ob des aktuellen Spielstandes
	 * 
	 * @param isAsync
	 */
	private void updateGameStatus(final boolean isAsync) {
		// console.log("updateGameStatus async: "+isAsync);
		if (gameStatusRequestExecutes == false) {
			// console.log("gameStatusRequestExecutes == false");
			gameStatusRequestExecutes = true;
			updateGameStatusStartTime = new Date().getTime();

			new RestMobile().getGameStatus(playerId, isAsync, new AjaxResult<GameStatus>() {

				@Override
				public void success(GameStatus result) {
					updateGameStatusCallback(result);
				}

				@Override
				public void error(Exception e) {
					gameStatusRequestExecutes = false;
					showLoginError("Exception wurde ausgel��t - Kein Spiel gestartet?" + e);
				}
			});
		}
	}

	private void updateGameStatusCallback(GameStatus data) {
		latencyCount++;
		latencyTotal += new Date().getTime() - updateGameStatusStartTime;
		gameStatusRequestExecutes = false;

		// Spielstatus und Spielinformationen

		gameIsRunning = parseInt(data.stats.settings.getIsRunning()) != 0 ? true : false;
		remainingPlayingTime = parseInt(data.stats.getRemainingPlayingTime());
		gameExists = parseInt(data.stats.getGameExists()) != 0 ? true : false;
		gameDidExist = gameExists;
		baseNodeRange = parseInt(data.stats.getBaseNodeRange());
		gameDifficulty = parseInt(data.stats.getGameDifficulty());
		itemCollectionRange = parseInt(data.stats.settings.getItemCollectionRange());
		gameDidEnd = parseInt(data.stats.getDidEnd()) != 0 ? true : false;
		// updatePositionIntervalTime = parseInt(data.stats.settings["updatePositionIntervalTime"]);
		updateDisplayIntervalTime = parseInt(data.stats.settings.getUpdateDisplayIntervalTime());

		// Spielerinformationen
		battery = parseFloat(data.node.getBatterieLevel());
		neighbourCount = parseInt(data.node.getNeighbourCount());
		serverLatitude = parseFloat(data.stats.getPlayingFieldCenterLatitude());
		serverLongitude = parseFloat(data.stats.getPlayingFieldCenterLongitude());
		score = parseInt(data.node.getScore());
		playerRange = parseInt(data.node.getRange());
		neighbours = data.node.getNeighbours();
		nearbyItemsCount = parseInt(data.node.getNearbyItemsCount());
		nearbyItems = data.node.getNearbyItems().getItems();
		nextItemDistance = parseInt(data.node.getNextItemDistance());
		itemInCollectionRange = data.node.getItemInCollectionRange() == 0 ? false : true;
		hasRangeBooster = parseInt(data.node.getHasRangeBooster()) != 0 ? true : false;
		hint = data.getHint();

		each(neighbourMarkersArray, new Call<Integer, Marker>() {

			public void call(Integer key, Marker theMarker) {
				if (theMarker != undefined && neighbours.get(key) == undefined) {
					neighbourMarkersArray.get(key).setMap(null);
				}
			}
		});

		each(nearbyItemMarkersArray, new Call<Integer, Marker>() {

			public void call(Integer key, Marker theMarker) {
				if (theMarker != undefined && nearbyItems.get(key) == undefined) {
					nearbyItemMarkersArray.get(key).setMap(null);
				}
			}
		});

		// Spiel entsprechend der erhaltenen Informationen
		// anpassen
		if (gameDidEnd) {
			waitingText.setText("Das Spiel ist zu Ende. Vielen Dank f�rs Mitspielen.");
			stopIntervals();
			waitingForGameOverlay.show();
		} else {
			if (battery > 0) {
				if (!gameExists && gameDidExist) {
					location.reload();
				} else if (!gameExists && !gameDidExist) {
					waitingText.setText("Warte auf Spielstart");
					stopIntervals();
					startGameStatusInterval();
					waitingForGameOverlay.show();
				} else if (gameExists && gameDidExist && !gameIsRunning) {
					waitingText.setText("Das Spiel wurde Pausiert");
					stopIntervals();
					startGameStatusInterval();
					waitingForGameOverlay.show();
				} else {
					// stopIntervals();
					startIntervals();
					waitingForGameOverlay.hide();
				}
			} else {
				waitingText.setText("Dein Akku ist alle :( Vielen Dank f�rs Mitspielen.");
				stopIntervals();
				waitingForGameOverlay.show();
			}
		}

		// Ansicht aktualisieren

		// updateDisplay(); refaktorisiert.... display soll
		// nicht immer nur nach den server calls refreshed
		// werden
	}

	/**
	 * draw the neighbours
	 * 
	 * @param playerId
	 * @param latitude
	 * @param longitude
	 */
	private void drawNeighbourMarkerAtLatitudeLongitude(final int playerId, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		final MarkerImage image = new MarkerImage(R.drawable.network_wireless_small, new Size(16, 16),
		// The origin for this image is 0,0.
				new Point(0, 0),
				// The anchor for this image is the base of the flagpole at 0,32.
				new Point(8, 8));

		if (neighbourMarkersArray.get(playerId) == undefined) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					this.position = latlng;
					this.map = senchaMap.map;
					this.title = "(" + playerId + ") ";
					this.icon = image;
					this.zIndex = 1;
				}
			};

			neighbourMarkersArray.put(playerId, marker);
		} else {
			neighbourMarkersArray.get(playerId).setPosition(latlng);
			neighbourMarkersArray.get(playerId).setTitle("(" + playerId + ") " /* + name */);
			if (neighbourMarkersArray.get(playerId).map == null) {
				neighbourMarkersArray.get(playerId).setMap(senchaMap.map);
			}
			;
		}
	}

	/**
	 * draw nearby items
	 * 
	 * @param itemId
	 * @param type
	 * @param latitude
	 * @param longitude
	 */
	private void drawNearbyItemMarkerAtLatitudeLongitude(int itemId, String type, double latitude, double longitude) {
		final LatLng latlng = new LatLng(latitude, longitude);

		int imagePath = 0;
		if ("BATTERY".equals(type)) {
			imagePath = R.drawable.battery_charge;
		} else {
			imagePath = R.drawable.mobile_phone_cast;
		}

		final MarkerImage image = new MarkerImage(imagePath, new Size(16, 16),
		// The origin for this image is 0,0.
				new Point(0, 0),
				// The anchor for this image is the base of the flagpole at 0,32.
				new Point(8, 8));

		if (nearbyItemMarkersArray.get(itemId) == undefined) {
			Marker marker = new Marker(ui) {

				protected void setData() {
					this.position = latlng;
					this.map = senchaMap.map;
					this.icon = image;
					this.zIndex = 1;
				}
			};

			nearbyItemMarkersArray.put(itemId, marker);
		} else {
			nearbyItemMarkersArray.get(itemId).setPosition(latlng);
			if (nearbyItemMarkersArray.get(itemId).map == null) {
				nearbyItemMarkersArray.get(itemId).setMap(senchaMap.map);
			}
			;
		}
	}

	private String addZ(double n) {
		return (n < 10 ? "0" : "") + n;
	}

	/**
	 * 
	 * @param ms
	 * @returns {String}
	 */
	private String convertMS(double s) {
		double ms = s % 1000;
		s = (s - ms) / 1000;
		double secs = s % 60;
		s = (s - secs) / 60;
		double mins = s % 60;
		double hrs = (s - mins) / 60;

		return addZ(mins);
	}

	/**
	 * updates the display with the new position and the positions of the neighbours
	 */
	private void updateDisplay() {
		// console.log("updateDisplay");
		if (positionWatch == null) {
			positionWatch = geolocation.watchPosition(this, new NavigatorOptions() {

				protected void setData() {
					this.enableHighAccuracy = true;
					this.maximumAge = 0;
					this.timeout = 9000;
				}
			});
		}
		if (!isNaN(score))
			mainPanelToolbar.items.getItems()[0].setText(score + "");
		if (!isNaN(neighbourCount))
			mainPanelToolbar.items.getItems()[2].setText(neighbourCount + "");
		if (!isNaN(remainingPlayingTime))
			mainPanelToolbar.items.getItems()[4].setText(convertMS(remainingPlayingTime));
		if (!isNaN(battery))
			mainPanelToolbar.items.getItems()[6].setText((battery + "%").replace(".", ","));

		Window.hint.setText(hint);

		if (currentLocation != null) {
			// Karte zentrieren
			senchaMap.map.setCenter(new LatLng(currentLocation));
			// Spieler Marker zentrieren
			playerMarker.setPosition(new LatLng(currentLocation));
			if (playerMarker.map == null) {
				playerMarker.setMap(senchaMap.map);
			}
			// Senderadius zentrieren
			playerRadius.setCenter(new LatLng(currentLocation));
			if (playerRadius.map == null) {
				playerRadius.setMap(senchaMap.map);
			}
			playerRadius.setRadius(playerRange);
			// Sammelradius zentrieren
			collectionRadius.setCenter(new LatLng(currentLocation));
			if (collectionRadius.map == null) {
				collectionRadius.setMap(senchaMap.map);
			}
			collectionRadius.setRadius(itemCollectionRange);
		}

		if (nextItemDistance != null)
			Window.nextItemDistance.setText("Entfernung zum n�chsten Gegenstand " + nextItemDistance + " Meter.");
		else
			Window.nextItemDistance.setText("Keine Gegenst�nde in der N�he.");

		int boosterImageElement;
		if (hasRangeBooster) {
			boosterImageElement = R.drawable.mobile_phone_cast;
		} else {
			boosterImageElement = R.drawable.mobile_phone_cast_gray;
		}

		Window.activeItems.html("Aktive Gegenst�nde: ", boosterImageElement);

		boolean isDisabled = Window.collectItemButton.isDisabled();
		if (itemInCollectionRange && isDisabled) {
			Window.collectItemButton.enable();
		} else if (!itemInCollectionRange && !isDisabled) {
			Window.collectItemButton.disable();
		}

		if (neighbours != undefined) {
			each(neighbours, new Call<Integer, Neighbour>() {

				@Override
				public void call(Integer key, Neighbour value) {
					drawNeighbourMarkerAtLatitudeLongitude(key, value.getLatitude(), value.getLongitude());
				}
			});
		}

		if (nearbyItems != undefined) {
			each(nearbyItems, new Call<Integer, Item>() {

				@Override
				public void call(Integer key, Item value) {
					drawNearbyItemMarkerAtLatitudeLongitude(key, value.getItemType(), value.getLatitude(), value.getLongitude());
				}
			});
		}
	}

	/**
	 * collect items
	 */
	public void collectItem() {
		Window.collectItemButton.disable();
		Window.collectItemButton.html("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
		new RestMobile().collectItem(playerId, new AjaxResult<Object>() {

			@Override
			public void success() {
				updateDisplay();
			}
		});
	}

	private void loginSuccess(LoginAnswer data) {
		if (!isNaN(parseInt(data.id))) {
			playerId = parseInt(data.id);
			loginOverlay.hide();
			updateGameStatus(false);
			startGameStatusInterval();
			// $("#mainContent").html("");
		} else {
			showLoginError("Keine id bekommen");
		}
	}
}
