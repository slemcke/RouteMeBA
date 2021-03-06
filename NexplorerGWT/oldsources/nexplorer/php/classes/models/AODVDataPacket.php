<?php

/**
 * AODVDataPacket
 * 
 * This class has been auto-generated by the Doctrine ORM Framework
 * 
 * @package    ##PACKAGE##
 * @subpackage ##SUBPACKAGE##
 * @author     ##NAME## <##EMAIL##>
 * @version    SVN: $Id: Builder.php 7490 2010-03-29 19:53:27Z jwage $
 */
class AODVDataPacket extends BaseAODVDataPacket
{
        /**
         * FIMI
         * @param type $showImage
         * @return type 
         */
	public function getStatus($showImage) {
		switch($this->status) {
			case AODV_DATA_PACKET_STATUS_UNDERWAY:
				return "Unterwegs";
			break;
			case AODV_DATA_PACKET_STATUS_ARRIVED:
				if ($image) return "<img src='../media/images/icons/tick.png' /> Am Ziel angekommen"; else return "Am Ziel angekommen";
			break;
			case AODV_DATA_PACKET_STATUS_ERROR:
				if ($image) return "<img src='../media/images/icons/minus-circle.png' /> Kein Weg zum Ziel vorhanden"; else return "Kein Weg zum Ziel vorhanden";
			break;
			case AODV_DATA_PACKET_STATUS_WAITING_FOR_ROUTE;
				return "Wartet auf Wegfindung";
			break;
			case AODV_DATA_PACKET_STATUS_NODE_BUSY;
				return "Wartet weil Knoten beschäftigt";
			break;
			default:
				return "Unbekannt";
			break;
		}
	}
	
	public function calculatePoints() {
		$gameSettings = Doctrine_Query::create()->from("Settings")->execute()->getFirst();
		$points = 0;
		
		for ($i = 1; $i <= $this->hopsDone; $i++) {
			$points += $points + ($i * 10);
		}
		
		if ($this->didReachBonusGoal) {
			$points += round($points * 0.5);
		}
		
		return $points;
	}
	
	public function awardPointsToOwner() {
		$this->Owner->score += $this->calculatePoints();
		$this->Owner->save();
	}
}