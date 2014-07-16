package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;

/**
 * This class takes all the data from the form and sends it to the server in order to be processed This should normally be a click-listener wrapping the submit_button, however if you take normal html code and use black magic to wrap a button this seems to be the only way that works in chrome at least
 * 
 * @author Steff
 */
public class ChangeLevelHandler implements EventListener {

	private AdminBinder adminBinder;

	public ChangeLevelHandler(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}

	@Override
	public void onBrowserEvent(Event event) {
		saveLevel();
	}

	/*
	 * save new player level
	 */
	private void saveLevel() {
		Long playerIdLong = Long.parseLong(adminBinder.getLevelStatsBinder().getPlayerId());
		Long difficultyLong = Long.parseLong(adminBinder.getLevelStatsBinder().getDifficulty().getValue().trim());

		
		this.adminBinder.getAdminService().savePlayerLevel(playerIdLong, difficultyLong, null);
	}
	
}
