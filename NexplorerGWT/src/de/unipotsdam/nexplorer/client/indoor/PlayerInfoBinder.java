package de.unipotsdam.nexplorer.client.indoor;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.Node;

import de.unipotsdam.nexplorer.client.IndoorServiceImpl;
import de.unipotsdam.nexplorer.client.indoor.dto.UiInfo;
import de.unipotsdam.nexplorer.client.indoor.levels.AvailableNodeUpdater;
import de.unipotsdam.nexplorer.client.indoor.levels.RouteKeeper;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.ActiveRouting;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.LevelOneRouteSelection;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.LevelThreeRouteSelection;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.LevelTwoRouteSelection;
import de.unipotsdam.nexplorer.client.indoor.view.messaging.RoutingLevel;
import de.unipotsdam.nexplorer.client.indoor.viewcontroller.ButtonSetShown;
import de.unipotsdam.nexplorer.client.indoor.viewcontroller.IndoorStatsTimer;
import de.unipotsdam.nexplorer.client.util.HasTable;
import de.unipotsdam.nexplorer.shared.Aodv;
import de.unipotsdam.nexplorer.shared.DataPacket;
import de.unipotsdam.nexplorer.shared.GameStatus;
import de.unipotsdam.nexplorer.shared.TimeManager;

public class PlayerInfoBinder extends HasTable {

	private static PlayerInfoBinderUiBinder uiBinder = GWT.create(PlayerInfoBinderUiBinder.class);
	private static String media = "media/images/icons/";

	interface PlayerInfoBinderUiBinder extends UiBinder<Element, PlayerInfoBinder> {
	}

	@UiField
	DivElement remainingPlayingTime;
	@UiField
	DivElement currentPlayerName;
	@UiField
	DivElement currentPlayerScore;
	@UiField
	DivElement hintMessage;
	@UiField
	DivElement currentRouteView;
	@UiField
	ImageElement currentPlayerLevelPic;
	/**
	 * contains either NewMessageBinder or NewRouteRequestBinder or ResetPLayerMessageBinder
	 */
	@UiField
	DivElement playOptions;

	private IndoorStatsTimer indoorStatsUpdater;
	private final ActiveRouting activeRouting;
	private RoutingLevel level;
	private final List<StateSwitchListener> stateSwitchListeners;
	boolean test;

	/**
	 * depending on the state either the message table is shown or the messageStatusTable depending on the Status of the message gameOptions are blended in
	 * 
	 * @param level
	 * 
	 */
	public PlayerInfoBinder() {
		setElement(uiBinder.createAndBindUi(this));
		this.activeRouting = new ActiveRouting();
		this.level = null;
		this.stateSwitchListeners = new LinkedList<StateSwitchListener>();

		// create intervals
		getFrequency();
	}

	public void finishConstructorAfterUpdate(int frequency) {
		// indoor service
		this.indoorStatsUpdater = new IndoorStatsTimer(this);
		indoorStatsUpdater.scheduleRepeating(frequency);
	}

	private void getFrequency() {
		IndoorServiceImpl indoorServiceImpl = new IndoorServiceImpl();
		indoorServiceImpl.getUpdateDisplayFrequency(new FrequencyUpdater<Integer>(this));
	}

	/**
	 * kümmerst sich darum, dass die aktuellen Informationen des Spielers angezeigt werden
	 * 
	 * @param info
	 */
	public void updatePlayerInfos(UiInfo info) {
		
		// Set level sensitive routing panel if not already set
		if (info.getGameState().equals(GameStatus.ISPAUSED) && level != null){
			if(this.currentRouteView.hasChildNodes()){
				Node oldChild = this.currentRouteView.getFirstChild();
				this.currentRouteView.removeChild(oldChild);
			}
			level = null;			
		}
		if (level == null && info.getGameState().equals(GameStatus.ISRUNNING)) {
			if (info.getPlayer().getDifficulty() == 1) {
				level = new LevelOneRouteSelection();
			} else if (info.getPlayer().getDifficulty() == 2) {
				LevelTwoRouteSelection level = new LevelTwoRouteSelection();
				level.addClickHandler(new LevelTwoHandler());

				RouteKeeper keeper = new RouteKeeper();
				keeper.setRouteCount(10);
				keeper.addRouteListener(level);
				AvailableNodeUpdater.addListener(keeper);

				this.addStateSwitchListener(new RouteRemover(keeper));

				this.level = level;
				// Level 3 (depends on level two)
			} else if (info.getPlayer().getDifficulty() == 3) {
				LevelThreeRouteSelection level = new LevelThreeRouteSelection();
				level.addClickHandler(new LevelThreeHandler());
				//init packets
				level.addNewPacket();
				test = true;
				
				RouteKeeper keeper = new RouteKeeper();
				keeper.setRouteCount(10);
				keeper.addRouteListener(level);
				AvailableNodeUpdater.addListener(keeper);

				this.addStateSwitchListener(new RouteRemover(keeper));

				this.level = level;
			}
			this.currentRouteView.appendChild(level.getElement());
		}
		
		if (info.getGameState().equals(GameStatus.NOTSTARTED)){
			
		}

		if (info.getPlayer() != null) {
			this.currentPlayerName.setInnerText(info.getPlayer().getName());
			this.currentPlayerScore.setInnerText(info.getPlayer().getScore());
			//sets picture for level view
			if(info.getPlayer().getDifficulty() == 1){
				this.currentPlayerLevelPic.setSrc(media + "lvlone.png");
			} else if (info.getPlayer().getDifficulty() == 2){
				this.currentPlayerLevelPic.setSrc(media + "lvltwo.png");
			} else {
				this.currentPlayerLevelPic.setSrc(media + "lvlthree.png");
			}
		}
		if (info.getDataPacketSend() != null) {
			this.activeRouting.setSourceNode(info.getDataPacketSend().getSourceNodeId());
			this.activeRouting.setDestinationNode(info.getDataPacketSend().getDestinationNodeId());
			this.hintMessage.setInnerHTML(statusToHTMLString(info));
			this.activeRouting.setCurrentNodeId(info.getDataPacketSend().getCurrentNodeId());
			
		} else {
			this.hintMessage.setInnerHTML(getHintMessage(info));
		}
		//update packets
		if(level instanceof LevelThreeRouteSelection && info.getDataPacketSend() != null){
			
			if(info.getDataPacketSend().getStatus().equals(Aodv.DATA_PACKET_STATUS_ARRIVED) && test){
				((LevelThreeRouteSelection)this.level).updatePackets();
				test=false;
			} else if (!info.getDataPacketSend().getStatus().equals(Aodv.DATA_PACKET_STATUS_ARRIVED)){
				test=true;
			}
		}
		this.remainingPlayingTime.setInnerText(TimeManager.convertToReadableTimeSpan(info.getRemainingTime()));
		this.activeRouting.setBonusGoal(info.getBonusGoal());

	}

	/**
	 * Gibt den aktuellen Zustand aus
	 * 
	 * @param info
	 * @return
	 */
	private String statusToHTMLString(UiInfo info) {
		Byte status = info.getDataPacketSend().getStatus();
		switch (status) {
		case Aodv.DATA_PACKET_STATUS_ARRIVED:
			return "Deine Nachricht hat ihr Ziel erreicht. Gratuliere! Du hast " + info.getDataPacketSend().getAwardedScore() + "<img src=\"/media/images/icons/points.png\"/> erhalten!";
		case Aodv.DATA_PACKET_STATUS_ERROR:
			return "Es konnte keine Route vom Start zum Ziel gefunden werden. Versende die Routenanfrage erneut oder wähle zwei andere Knoten!";
		case Aodv.DATA_PACKET_STATUS_NODE_BUSY:
			return "Ein Knoten ist überlastet. Warte oder schicke eine neue Nachricht!";
		case Aodv.DATA_PACKET_STATUS_UNDERWAY:
			return "Deine Nachricht ist unterwegs!";
		case Aodv.DATA_PACKET_STATUS_WAITING_FOR_ROUTE:
			return "Das Packet wartet auf eine Route!";
		case Aodv.DATA_PACKET_STATUS_CANCELLED:
			return "Datentransfer wurde abgebrochen!";
		default:
			return "";
		}
	}

	/**
	 * Gibt Hilfenachrichten aus
	 * 
	 * @return
	 */
	private String getHintMessage(UiInfo info) {
		if (info.getHint() != null) {
			return info.getHint();
		} else {
			return "Wenn du eine Nachricht erfolgreich zum Bonuszielknoten (der Knoten mit dem kleinen Stern) sendest, erhältst du 150% der üblichen Punkte. " + "Der Bonuszielknoten wird neu gesetzt sobald ein Spieler eine Nachricht erfolgreich zu ihm gesendet hat oder der Knoten aus dem Spiel ausscheidet. " + "Nachrichten über kurze Strecken sind weniger von Störungen betroffen, bringen aber auch weniger Punkte. ";
		}
	}

	public void switchToButtonState(ButtonSetShown state, DataPacket reason) {
		DivElement divElement = this.currentRouteView;
		if (state == ButtonSetShown.Other) {
			removeChildren(divElement);
			divElement.appendChild(activeRouting.getElement());
		} else {
			removeChildren(divElement);
			divElement.appendChild(level.getElement());
		}

		notifySwitched(state, reason);
	}

	private void removeChildren(DivElement element) {
		while (element.hasChildNodes()) {
			element.getChild(0).removeFromParent();
		}
	}

	private void notifySwitched(ButtonSetShown state, DataPacket reason) {
		for (StateSwitchListener listener : stateSwitchListeners) {
			listener.stateSwitchedTo(state, reason);
		}
	}

	public void addStateSwitchListener(StateSwitchListener listener) {
		this.stateSwitchListeners.add(listener);
	}
}
