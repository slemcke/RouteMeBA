package de.unipotsdam.nexplorer.server.persistence.hibernate.dto;

// Generated 19.09.2012 13:58:49 by Hibernate Tools 3.2.1.GA

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.google.gwt.user.client.rpc.GwtTransient;

import de.unipotsdam.nexplorer.shared.DataPacket;

/**
 * AodvDataPackets generated by hbm2java
 */
@Entity
@Table(name = "aodv_data_packets")
public class AodvDataPackets extends DataPacket implements java.io.Serializable {

	@GwtTransient
	private Players playersByDestinationId;
	@GwtTransient
	private Players playersByOwnerId;
	@GwtTransient
	private Players playersBySourceId;
	private Short hopsDone;
	private Long processingRound;
	private Byte didReachBonusGoal;
	private Integer version;

	public AodvDataPackets() {
	}

	public AodvDataPackets(Players playersByDestinationId, Players playersByOwnerId, Players playersBySourceId, Players playersByCurrentNodeId, Short hopsDone, Byte status, Long processingRound, Byte didReachBonusGoal) {
		setPlayersByDestinationId(playersByDestinationId);
		setPlayersByOwnerId(playersByOwnerId);
		setPlayersBySourceId(playersBySourceId);
		setPlayersByCurrentNodeId(playersByCurrentNodeId);
		this.hopsDone = hopsDone;
		this.status = status;
		this.processingRound = processingRound;
		this.didReachBonusGoal = didReachBonusGoal;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination_id")
	public Players getPlayersByDestinationId() {
		return this.playersByDestinationId;
	}

	public void setPlayersByDestinationId(Players playersByDestinationId) {
		super.setPlayersByDestinationId(playersByDestinationId);
		this.playersByDestinationId = playersByDestinationId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id")
	public Players getPlayersByOwnerId() {
		return this.playersByOwnerId;
	}

	public void setPlayersByOwnerId(Players playersByOwnerId) {
		super.setPlayersByOwnerId(playersByOwnerId);
		this.playersByOwnerId = playersByOwnerId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "source_id")
	public Players getPlayersBySourceId() {
		return this.playersBySourceId;
	}

	public void setPlayersBySourceId(Players playersBySourceId) {
		super.setPlayersBySourceId(playersBySourceId);
		this.playersBySourceId = playersBySourceId;
	}

	@Column(name = "hops_done")
	public Short getHopsDone() {
		return this.hopsDone;
	}

	public void setHopsDone(Short hopsDone) {
		this.hopsDone = hopsDone;
	}

	@Column(name = "processing_round")
	public Long getProcessingRound() {
		return this.processingRound;
	}

	public void setProcessingRound(Long processingRound) {
		this.processingRound = processingRound;
	}

	@Column(name = "did_reach_bonus_goal")
	public Byte getDidReachBonusGoal() {
		return this.didReachBonusGoal;
	}

	public void setDidReachBonusGoal(Byte didReachBonusGoal) {
		this.didReachBonusGoal = didReachBonusGoal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_node_id")
	public Players getPlayersByCurrentNodeId() {
		return (Players) this.playersByCurrentNodeId;
	}

	public void setPlayersByCurrentNodeId(Players playersByCurrentNodeId) {
		this.playersByCurrentNodeId = playersByCurrentNodeId;
	}

	@Column(name = "status")
	public Byte getStatus() {
		return this.status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	@Column(name = "awarded_score")
	public Integer getAwardedScore() {
		return super.getAwardedScore();
	}

	public void setAwardedScore(Integer score) {
		super.setAwardedScore(score);
	}
	
	@Version
	@Column(name = "OPTLOCK")
	public Integer getVersion() {
		return this.version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
}
