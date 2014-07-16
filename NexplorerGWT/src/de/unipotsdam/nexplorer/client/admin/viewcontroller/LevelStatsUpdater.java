package de.unipotsdam.nexplorer.client.admin.viewcontroller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.unipotsdam.nexplorer.client.admin.AdminBinder;
import de.unipotsdam.nexplorer.shared.PlayerStats;

public class LevelStatsUpdater<T> implements AsyncCallback<PlayerStats> {

	private AdminBinder adminBinder;

	public LevelStatsUpdater(AdminBinder adminBinder) {
		this.adminBinder = adminBinder;
	}

	@Override
	public void onFailure(Throwable caught) {
		GWT.log("konnte playerStats nicht laden");

	}

	@Override
	public void onSuccess(PlayerStats result) {		
		adminBinder.getLevelStatsBinder().getPlayer(result);
	}

}

