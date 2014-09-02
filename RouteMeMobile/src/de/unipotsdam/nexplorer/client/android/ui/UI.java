package de.unipotsdam.nexplorer.client.android.ui;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import de.unipotsdam.nexplorer.client.android.callbacks.LoginError;
import de.unipotsdam.nexplorer.client.android.callbacks.RemovalReason;
import de.unipotsdam.nexplorer.client.android.callbacks.UIFooter;
import de.unipotsdam.nexplorer.client.android.callbacks.UIGameEvents;
import de.unipotsdam.nexplorer.client.android.callbacks.UIHeader;
import de.unipotsdam.nexplorer.client.android.callbacks.UILogin;
import de.unipotsdam.nexplorer.client.android.callbacks.UIPacketFooter;
import de.unipotsdam.nexplorer.client.android.callbacks.UISensors;
import de.unipotsdam.nexplorer.client.android.rest.Packet;

public class UI extends UIElement implements UILogin, UISensors, UIGameEvents {

	private final Button loginButton;
	private final Text waitingText;
	private Text beginDialog;
	private UIFooter footer;
	private Overlay loginOverlay;
	private Overlay noPositionOverlay;
	private Overlay waitingForGameOverlay;
	private UIHeader header;
	private UIPacketFooter packetFooter;
	
	public UI(Activity host, Button loginButton, Text waitingText, Text beginDialog, UIFooter footer, UIPacketFooter packetFooter, Overlay loginOverlay, Overlay waitingForGameOverlay, Overlay noPositionOverlay, UIHeader header) {
		super(host);
		this.loginButton = loginButton;
		this.waitingText = waitingText;
		this.beginDialog = beginDialog;
		this.footer = footer;
		this.loginOverlay = loginOverlay;
		this.noPositionOverlay = noPositionOverlay;
		this.waitingForGameOverlay = waitingForGameOverlay;
		this.header = header;
		this.packetFooter = packetFooter;
	}
	
	public void updatePacketFooter(final HashMap<Long,Packet> packets, Long level){
		packetFooter.updateFooter(packets, level);
		
	}

	public void updateStatusHeaderAndFooter(final int score, final int neighbourCount, final long remainingPlayingTime, final double battery, final Integer nextItemDistance, final boolean hasRangeBooster, final boolean itemInCollectionRange, final String hint, final Long level, final HashMap<Long,Packet> packets) {
		header.updateHeader(score, neighbourCount, remainingPlayingTime, battery,level);
		footer.updateFooter(nextItemDistance, hasRangeBooster, itemInCollectionRange, hint);
		updatePacketFooter(packets, level);
	}

	public void disableButtonForItemCollection() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				footer.setIsCollectingItem(true);
			}
		});
	}

	public void enableButtonForItemCollection() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				footer.setIsCollectingItem(false);
			}
		});
	}
	
	//using packetid-Button for indicating packet sending 
	public void disableButtonForPacketSending(){
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				packetFooter.setIsSendingPacket(true);
			}
		});
	}
	
	public void enableButtonForPacketSending(){
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				packetFooter.setIsSendingPacket(false);
			}
		});
	}

	private void hideLoginOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				loginOverlay.hide();
			}
		});
	}

	private void labelButtonForLogin() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				loginButton.label("melde an...");
			}
		});
	}

	private void showLoginError(String string) {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				beginDialog.setText("Kein Spiel da. Versuchen Sie es später noch einmal!");
				loginButton.label("anmelden ");
			}
		});
	}

	private void hideNoPositionOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				noPositionOverlay.hide();
			}
		});
	}

	private void showNoPositionOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				noPositionOverlay.show();
			}
		});
	}
	
	private void showResult(final String result){
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				packetFooter.showToast("That was " + result);
			}
		});
	}

	private void showGameEnded() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Das Spiel ist zu Ende. Vielen Dank fürs Mitspielen.");
				waitingForGameOverlay.show();
			}
		});
	}

	public void showWaitingForGameStart() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Warte auf Spielstart");
				waitingForGameOverlay.show();
			}
		});
	}

	private void showGamePaused() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Das Spiel wurde pausiert");
				waitingForGameOverlay.show();
			}
		});
	}

	private void hideWaitingForGameOverlay() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingForGameOverlay.hide();
			}
		});
	}

	private void showBatteryEmpty() {
		runOnUIThread(new Runnable() {

			@Override
			public void run() {
				waitingText.setText("Dein Akku ist alle :( Vielen Dank fürs Mitspielen.");
				waitingForGameOverlay.show();
			}
		});
	}

	@Override
	public void loginStarted() {
		labelButtonForLogin();
	}

	@Override
	public void loginSucceeded(long playerId) {
		hideLoginOverlay();
	}

	@Override
	public void loginFailed(LoginError reason) {
		switch (reason) {
		case NO_ID:
			showLoginError("Keine id bekommen");
			break;
		case CAUSE_UNKNOWN:
		default:
			showLoginError("Exception wurde ausgelöst - Kein Spiel gestartet?");
			break;
		}

	}

	@Override
	public void noPositionReceived() {
		showNoPositionOverlay();
	}

	@Override
	public void positionReceived() {
		hideNoPositionOverlay();
	}

	@Override
	public void gamePaused() {
		showGamePaused();
	}

	@Override
	public void gameResumed() {
		hideWaitingForGameOverlay();
	}

	@Override
	public void gameEnded() {
		showGameEnded();
	}

	@Override
	public void playerRemoved(RemovalReason reason) {
		switch (reason) {
		case NO_BATTERY:
		default:
			showBatteryEmpty();
		}
	}

	public void showFeedback(String result) {
		showResult(result);
		
	}
}
