package de.unipotsdam.nexplorer.server.data;

import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import de.unipotsdam.nexplorer.server.aodv.AodvDataPacket;
import de.unipotsdam.nexplorer.server.aodv.AodvNode;
import de.unipotsdam.nexplorer.server.di.InjectLogger;
import de.unipotsdam.nexplorer.server.persistence.DatabaseImpl;
import de.unipotsdam.nexplorer.server.persistence.Setting;
import de.unipotsdam.nexplorer.server.persistence.hibernate.dto.Players;

public class Referee {

	@InjectLogger
	private Logger logger;
	private DatabaseImpl dbAccess;

	@Inject
	public Referee(DatabaseImpl dbAccess) {
		this.dbAccess = dbAccess;
	}

	public void packetArrived(Setting gameSettings, AodvDataPacket packet) {
		if (packet.inner().getPlayersByCurrentNodeId().getId() == gameSettings.inner().getBonusGoal()) {
			packet.inner().setDidReachBonusGoal((byte) 1);
			gameSettings.findNewBonusGoal(true);
			gameSettings.save();
		}

		int points = 0;
		for (int i = 1; i <= packet.inner().getHopsDone(); i++) {
			points += (i * 10);
		}
		int hopPoints= points;
		Players owner = packet.inner().getPlayersByOwnerId();
		// Punkte für Indoorspieler hängen auch von Pakettyp ab (Level 3)
		if(packet.inner().getType() != null && owner.getDifficulty() == 3){
			int packetBonus = packet.inner().getType().byteValue();
			points += Math.round((hopPoints * packetBonus)/10);
		} 

		if (packet.inner().getDidReachBonusGoal() != 0) {
			points += Math.round(hopPoints * 0.5);
		}
		
		packet.inner().setAwardedScore(points);
		packet.save();
		
		logger.trace("Packet from {} to {} ({} hops, {} bonus goal) is worth {} points", packet.getSource().getId(), packet.getDestination().getId(), packet.inner().getHopsDone(), packet.inner().getDidReachBonusGoal(), points);

		owner.setScore(owner.getScore() + points);
		logger.trace("Score of player {} inreased to {}", owner.getId(), owner.getScore());
		dbAccess.persist(owner);
	}
	
	
	// calculates points for outdoor player (Level three) after sending the packet to next hop
	public void sendPacket(AodvNode src, AodvDataPacket thePacket, long newTime) {
		
		if(thePacket.getCurrentNode().player().getDifficulty() != null && thePacket.getCurrentNode().player().getDifficulty() == 3){
			//	Berechnung für Level 3 anhand Paketpriorität und verstrichener Zeit
				long oldTime = thePacket.inner().getCreated();
				long diff = (oldTime - newTime)%1000; // in Sekunden				
				long type = thePacket.inner().getType();
				String feedback;
				//Startwert: 20fache Paketwert
				long points = 20*type;
				//ersten 60 Sekunden passiert nicht, dann werden alle sekunden der vierfache Paketwert abgezogen
				long timemalus = ((diff-60)/20);
				if(timemalus > 0){
					points =- timemalus*type*4;
				}
				
				src.player().increaseScoreBy(100 + points);
			} else {
				src.player().increaseScoreBy(100);
			}
		src.player().save();
		
	}
}
