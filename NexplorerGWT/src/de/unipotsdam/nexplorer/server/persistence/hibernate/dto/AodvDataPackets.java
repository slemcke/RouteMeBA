package de.unipotsdam.nexplorer.server.persistence.hibernate.dto;

// Generated 19.09.2012 13:58:49 by Hibernate Tools 3.2.1.GA

import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.google.gwt.editor.client.Editor.Ignore;
import com.google.gwt.user.client.rpc.GwtTransient;
import com.google.gwt.user.client.rpc.IsSerializable;

import de.unipotsdam.nexplorer.shared.DataPacket;

/**
 * AodvDataPackets generated by hbm2java
 */
@Entity
@Table(name = "aodv_data_packets")
public class AodvDataPackets extends DataPacket implements java.io.Serializable, IsSerializable {

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
	private long created;

	public AodvDataPackets() {
	}

	public AodvDataPackets(Players playersByDestinationId, Players playersByOwnerId, Players playersBySourceId, Players playersByCurrentNodeId, Short hopsDone, Byte status, Long processingRound, Byte didReachBonusGoal, Byte type) {
		setPlayersByDestinationId(playersByDestinationId);
		setPlayersByOwnerId(playersByOwnerId);
		setPlayersBySourceId(playersBySourceId);
		setPlayersByCurrentNodeId(playersByCurrentNodeId);
		this.hopsDone = hopsDone;
		this.status = status;
		this.processingRound = processingRound;
		this.didReachBonusGoal = didReachBonusGoal;
		this.type=type;
	}
	
	public AodvDataPackets(Players playersByDestinationId, Players playersByOwnerId, Players playersBySourceId, Players playersByCurrentNodeId, Short hopsDone, Byte status, Long processingRound, Byte didReachBonusGoal, Byte type, long created) {
		setPlayersByDestinationId(playersByDestinationId);
		setPlayersByOwnerId(playersByOwnerId);
		setPlayersBySourceId(playersBySourceId);
		setPlayersByCurrentNodeId(playersByCurrentNodeId);
		this.hopsDone = hopsDone;
		this.status = status;
		this.processingRound = processingRound;
		this.didReachBonusGoal = didReachBonusGoal;
		this.type=type;
		this.created=created;
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
	
	@Column(name = "type")
	public Byte getType() {
		return this.type;
	}

	public void setType(Byte type) {
		this.type = type;
	}
	
	@Column(name = "created")
	public long getCreated() {
		return this.created;
	}

	public void setCreated(long created) {
		this.created = created;
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

	@Override
	public int hashCode() {
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		AodvDataPackets other = (AodvDataPackets) obj;
		if (other.didReachBonusGoal != didReachBonusGoal) {
			return false;
		} else if (other.hopsDone != hopsDone) {
			return false;
		} else if (other.id != id) {
			return false;
		} else if (other.messageDescription == null && messageDescription != null) {
			return false;
		} else if (!other.messageDescription.equals(messageDescription)) {
			return false;
		} else if (other.playersByCurrentNodeId == null && playersByCurrentNodeId != null) {
			return false;
		} else if (!other.playersByCurrentNodeId.equals(playersByCurrentNodeId)) {
			return false;
		} else if (other.playersByDestinationId == null && playersByDestinationId != null) {
			return false;
		} else if (!other.playersByDestinationId.equals(playersByDestinationId)) {
			return false;
		} else if (other.playersByOwnerId == null && playersByOwnerId != null) {
			return false;
		} else if (!other.playersByOwnerId.equals(playersByOwnerId)) {
			return false;
		} else if (other.playersBySourceId == null && playersBySourceId != null) {
			return false;
		} else if (!other.playersBySourceId.equals(playersBySourceId)) {
			return false;
		} else if (other.processingRound != processingRound) {
			return false;
		} else if (other.status != status) {
			return false;
		} else if (other.type != type) {
				return false;
		} else if (other.version != version) {
			return false;
		}

		return true;
	}
}
