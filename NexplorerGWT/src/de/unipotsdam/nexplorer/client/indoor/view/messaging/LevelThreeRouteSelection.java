package de.unipotsdam.nexplorer.client.indoor.view.messaging;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.dom.client.Node;

import de.unipotsdam.nexplorer.client.indoor.levels.PacketKeeper;
import de.unipotsdam.nexplorer.client.indoor.levels.Route;

import de.unipotsdam.nexplorer.shared.PacketType;

public class LevelThreeRouteSelection extends LevelTwoRouteSelection {

	private static LevelThreeRouteSelectionUiBinder uiBinder = GWT.create(LevelThreeRouteSelectionUiBinder.class);

	interface LevelThreeRouteSelectionUiBinder extends UiBinder<Element, LevelThreeRouteSelection> {
	}

	@UiField
	DivElement packet;

	public LevelThreeRouteSelection() {
		super();
		setElement(uiBinder.createAndBindUi(this));
	}
	
	
	public void updatePackets() {
		removeObsoletePacket();
		addNewPacket();
	}

	private void removeObsoletePacket() {
		if (packet.hasChildNodes()){
			Node oldChild = packet.getFirstChild();
			packet.removeChild(oldChild);
		}

	}

	private void addNewPacket() {
		PacketKeeper pk = new PacketKeeper();
		PacketType newPacket = pk.createRandomPacket();
		this.packet.setInnerText(newPacket.toString());
	}
	
}
