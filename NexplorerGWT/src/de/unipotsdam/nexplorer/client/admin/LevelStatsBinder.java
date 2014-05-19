package de.unipotsdam.nexplorer.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import de.unipotsdam.nexplorer.client.admin.viewcontroller.ChangeLevelHandler;
import de.unipotsdam.nexplorer.client.util.DivElementWrapper;
import de.unipotsdam.nexplorer.shared.Messager;
import de.unipotsdam.nexplorer.shared.PlayerStats;

public class LevelStatsBinder extends DivElementWrapper {
	
	interface LevelStatsBinderUiBinder extends UiBinder<Widget, LevelStatsBinder> {}

	private static final LevelStatsBinderUiBinder uiBinder = GWT.create(LevelStatsBinderUiBinder.class);

	AdminBinder adminBinder;
	
	@UiField
	public ListBox selectName;
	
	@UiField
	public SelectElement difficulty;
	
	@UiField
	ButtonElement changeButton;



	
	public LevelStatsBinder(AdminBinder adminBinder) {
		initWidget(uiBinder.createAndBindUi(this));
		this.adminBinder = adminBinder;
		wrapChangeButton();

	}
	


	/**
	 * fill form with available player 
	 * 
	 */
	public void getPlayer(PlayerStats result) {
		
		for(Messager player : result.getMessagers()){
			if(!containsElement(selectName, player)){
				selectName.addItem(player.getName(), player.getId().toString());
			}
		}
		
		for(Messager player : result.getNodes()){
			if(!containsElement(selectName, player)){
				selectName.addItem(player.getName(), player.getId().toString());
			}	
		}
	}
	
	/**
	 * checks, if player already exists in listbox
	 * 
	 */
	boolean containsElement(ListBox selectName, Messager player){
		for(int i= 0; i < selectName.getItemCount(); i++ ){
			if(selectName.getItemText(i).equals(player.getName())){
				return true;
			}
		}

		return false;
	}
	
	private void wrapChangeButton() {
		Button button = new Button(changeButton.getInnerHTML());
		DivElement divElement = (DivElement) changeButton.getParentElement();
		changeButton.removeFromParent();
		DOM.sinkEvents(button.getElement(), Event.ONCLICK);
		DOM.setEventListener(button.getElement(), new ChangeLevelHandler(adminBinder));
		divElement.appendChild(button.getElement());
	}
	
	
	/**
	 * returns the selected level
	 * 
	 */
	public SelectElement getDifficulty() {
		return difficulty;
	}

	
	/**
	 * returns the selected player
	 * 
	 */
	public String getPlayerId() {
		int index = selectName.getSelectedIndex();
		return selectName.getValue(index);
	}
	
	
}
