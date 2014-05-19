package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;

import de.unipotsdam.nexplorer.client.indoor.levels.PacketKeeper;


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
	
	/*
	 * updates packets for indoor player
	 */
	public void updatePackets() {
		removeObsoletePacket();
		addNewPacket();
	}

	/*
	 * removes changed packet from list
	 */
	private void removeObsoletePacket() {
		int index = packet.getSelectedIndex();
		packet.remove(index);
	}

	/*
	 * builds new packet list, if all packets were used
	 */
	public void addNewPacket() {
		if(packet.getLength()==0){
			PacketKeeper pk = new PacketKeeper();
			List<String> newPackets = pk.createPacketList();
			Integer value = 0;
			for(String singlePacket : newPackets){
				OptionElement newElement = DOM.createOption().cast();
//				Image image = new Image("media/images/icons/user.png");
//				image.setTitle("blubb");
//				newElement.setInnerHTML(image.toString());
				newElement.setText(singlePacket);
				newElement.setValue(value.toString());

				
				packet.add(newElement, null);
				value++;
			}
		}
	}
	
//	public void createPacketView(String singlePacket, Integer value) {
//		OptionElement newElement = DOM.createOption().cast();
//		Image image = new Image("media/images/icons/user.png");
//		image.setTitle("blubb");
//		//newElement.setText(singlePacket);
//		//newElement.setValue(value.toString());
//		newElement.setInnerText(singlePacket);
//		newElement.setInnerHTML(image.toString());
//		packet.add(newElement, null);
//	}
	
	
	
	/*
	 * returns selected packet
	 */
	
	public String getPacket(){
		int index = packet.getSelectedIndex();
		String response =packet.getOptions().getItem(index).getText();
		return response;
	}
	
}
