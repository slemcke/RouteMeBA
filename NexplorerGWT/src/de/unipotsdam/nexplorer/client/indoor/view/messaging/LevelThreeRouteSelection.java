package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ListBox;

import de.unipotsdam.nexplorer.client.indoor.levels.PacketKeeper;

import de.unipotsdam.nexplorer.shared.PacketType;

public class LevelThreeRouteSelection extends LevelTwoRouteSelection {

	private static LevelThreeRouteSelectionUiBinder uiBinder = GWT.create(LevelThreeRouteSelectionUiBinder.class);

	interface LevelThreeRouteSelectionUiBinder extends UiBinder<Element, LevelThreeRouteSelection> {
	}

	@UiField
	SelectElement packet;

	public LevelThreeRouteSelection() {
		super();
		setElement(uiBinder.createAndBindUi(this));
	}
	
	
	public void updatePackets() {
		removeObsoletePacket();
		addNewPacket();
	}

	private void removeObsoletePacket() {
		int index = packet.getSelectedIndex();
		packet.remove(index);
	}

	public void addNewPacket() {
		if(packet.getLength()==0){
			PacketKeeper pk = new PacketKeeper();
			List<String> newPackets = pk.createPacketList();
			Integer value = 0;
			for(String singlePacket : newPackets){
				OptionElement newElement = DOM.createOption().cast();
				newElement.setText(singlePacket);
				newElement.setValue(value.toString());
				packet.add(newElement, null);
				value++;
			}
		}
	}
	
	
	public String getPacket(){
		int index = packet.getSelectedIndex();
		String response =packet.getOptions().getItem(index).getText();
		return response;
	}
}
