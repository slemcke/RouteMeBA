package de.unipotsdam.nexplorer.client.android.net;

import de.unipotsdam.nexplorer.client.android.callbacks.AjaxResult;
import de.unipotsdam.nexplorer.client.android.callbacks.Loginable;
import de.unipotsdam.nexplorer.client.android.callbacks.Sendable;
import de.unipotsdam.nexplorer.client.android.ui.UI;

public class RoutePacket implements Sendable, Loginable{
	
	private final RestMobile rest;
	private final UI ui;
	private Long playerId;
	private Long nextHopId;
	private boolean isSendingPackage;
	
	public RoutePacket(RestMobile rest, UI ui){
		this.rest = rest;
		this.ui = ui;
		
		this.playerId = null;
		this.nextHopId = null;
		this.isSendingPackage = false;
	}

	@Override
	public void loggedIn(long playerId) {
		this.playerId = playerId;
		
	}

	@Override
	public void sendRequested(Integer packageId) {
		if(!isSendingPackage && playerId != null){
			isSendingPackage = true;
			
			//TODO remove imageView for package from ui
//			ui.
			rest.routePacket(nextHopId,playerId, new AjaxResult<Object>(){
				public void success(){
					isSendingPackage = false;
				}
				public void error(){
					isSendingPackage = false;
					
				}
			});
		}
		
	}
	
	
	
}
