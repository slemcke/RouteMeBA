package de.unipotsdam.nexplorer.server.persistence.hibernate.dto;

// Generated 19.09.2012 13:58:49 by Hibernate Tools 3.2.1.GA

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.gwt.user.client.rpc.GwtTransient;
import com.google.gwt.user.client.rpc.IsSerializable;

import de.unipotsdam.nexplorer.shared.ItemMap;
import de.unipotsdam.nexplorer.shared.Messager;

/**
 * Players generated by hbm2java
 */
@Entity
@Table(name = "players", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
public class Players extends Messager implements java.io.Serializable, IsSerializable {

	private List<Players> neighbours;
	private Double latitude;
	private Double longitude;
	private Long lastPositionUpdate;
	private Double battery;
	@JsonProperty("range")
	public long range;
	@JsonProperty("neighbourCount")
	public int neighbourCount;
	@JsonProperty("nearbyItemsCount")
	public int nearbyItemsCount;
	@JsonProperty("nextItemDistance")
	public Long nextItemDistance;
	@JsonProperty("itemInCollectionRange")
	public Long itemInCollectionRange;
	/**
	 * This property will be used by the gui to determine which icon to show on the given node packetCount > 3: busy packetCount between 1 and 3: away packetCount 1 or smaller then: status
	 */
	@JsonProperty("packetCount")
	public int packetCount;
	private Date boosterSince;
	@JsonProperty("nearbyItems")
	public ItemMap nearbyItemsJSON;
	private Long hasSignalRangeBooster;
	private Byte role;
	private Long hasSignalStrengthBooster;
	private Integer remainingHighPriorityMessages;
	private Long sequenceNumber;
	@GwtTransient
	private Set<AodvDataPackets> aodvDataPacketsesForCurrentNodeId;
	@GwtTransient
	private Set<AodvNodeData> aodvNodeDatas;
	@GwtTransient
	private Set<Neighbours> neighbourses;
	@GwtTransient
	private Set<AodvDataPackets> aodvDataPacketsesForDestinationId;
	@GwtTransient
	private Set<AodvDataPackets> aodvDataPacketsesForOwnerId;
	@GwtTransient
	private Set<AodvDataPackets> aodvDataPacketsesForSourceId;
	@GwtTransient
	private Set<Neighbours> nodesForNeighbour;
	private Long baseNodeRange;
	private Long itemCollectionRange;
	private Integer version;
	private Long lastPing;
	private long pingDuration;

	@JsonProperty("neighbours")
	@Transient
	public HashMap<Long, Players> getJsonNeighbours() {
		try {
			HashMap<Long, Players> result = new HashMap<Long, Players>();
			if (getNeighbourses() == null) {
				return result;
			}

			for (Neighbours neigh : getNeighbourses()) {
				Players strippedNeighbour = new Players();
				Players fullNeighbour = neigh.getNeighbour();

				strippedNeighbour.latitude = fullNeighbour.getLatitude();
				strippedNeighbour.longitude = fullNeighbour.getLongitude();
				strippedNeighbour.lastPing = fullNeighbour.getLastPing();
				strippedNeighbour.pingDuration = fullNeighbour.getPingDuration();
				result.put(fullNeighbour.getId(), strippedNeighbour);
			}

			return result;
		} catch (NullPointerException e) {
			throw e;
		}
	}

	@JsonIgnore
	@Transient
	public List<Players> getNeighbours() {
		return neighbours;
	}

	@JsonIgnore
	public void setNeighbours(List<Players> neighbours) {
		this.neighbours = neighbours;
	}

	@JsonProperty
	@Transient
	public int getHasRangeBooster() {
		return hasSignalRangeBooster != null && hasSignalRangeBooster > 0 ? 1 : 0;
	}

	public Players() {
	}

	public Players(Byte role, String name, Long score, Double latitude, Double longitude, Long lastPositionUpdate, Double battery, Long hasSignalRangeBooster, Long hasSignalStrengthBooster, Integer remainingHighPriorityMessages, Long sequenceNumber, Set<AodvDataPackets> aodvDataPacketsesForCurrentNodeId, Set<AodvNodeData> aodvNodeDatas, Set<Neighbours> neighbourses, Set<AodvDataPackets> aodvDataPacketsesForDestinationId, Set<AodvDataPackets> aodvDataPacketsesForOwnerId, Set<AodvDataPackets> aodvDataPacketsesForSourceId, Long baseNodeRange, Long itemCollectionRange, Long difficulty) {
		this.role = role;
		this.name = name;
		this.score = score;
		this.latitude = latitude;
		this.longitude = longitude;
		this.lastPositionUpdate = lastPositionUpdate;
		this.battery = battery;
		this.hasSignalRangeBooster = hasSignalRangeBooster;
		this.hasSignalStrengthBooster = hasSignalStrengthBooster;
		this.remainingHighPriorityMessages = remainingHighPriorityMessages;
		this.sequenceNumber = sequenceNumber;
		this.aodvDataPacketsesForCurrentNodeId = aodvDataPacketsesForCurrentNodeId;
		this.aodvNodeDatas = aodvNodeDatas;
		this.neighbourses = neighbourses;
		this.aodvDataPacketsesForDestinationId = aodvDataPacketsesForDestinationId;
		this.aodvDataPacketsesForOwnerId = aodvDataPacketsesForOwnerId;
		this.aodvDataPacketsesForSourceId = aodvDataPacketsesForSourceId;
		this.baseNodeRange = baseNodeRange;
		this.itemCollectionRange = itemCollectionRange;
		this.difficulty = difficulty;
	}

	@Override
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "role")
	public Byte getRole() {
		return this.role;
	}

	public void setRole(Byte role) {
		this.role = role;
	}

	@Override
	@Column(name = "name", length = 45)
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	@Column(name = "score")
	public Long getScore() {
		return this.score;
	}

	@Override
	public void setScore(Long score) {
		this.score = score;
	}

	@JsonProperty("latitude")
	@Column(name = "latitude", precision = 18, scale = 9)
	public Double getLatitude() {
		return this.latitude;
	}

	@JsonProperty("latitude")
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	@JsonProperty("longitude")
	@Column(name = "longitude", precision = 18, scale = 9)
	public Double getLongitude() {
		return this.longitude;
	}

	@JsonProperty("longitude")
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@JsonProperty("lastPositionUpdate")
	@Column(name = "last_position_update")
	public Long getLastPositionUpdate() {
		return this.lastPositionUpdate;
	}

	@JsonProperty("lastPositionUpdate")
	public void setLastPositionUpdate(Long lastPositionUpdate) {
		this.lastPositionUpdate = lastPositionUpdate;
	}

	@JsonProperty("batterieLevel")
	@Column(name = "battery", precision = 5)
	public Double getBattery() {
		return this.battery;
	}

	@JsonProperty("batterieLevel")
	public void setBattery(Double battery) {
		this.battery = battery;
	}

	@Column(name = "has_signal_range_booster")
	public Long getHasSignalRangeBooster() {
		return this.hasSignalRangeBooster;
	}

	public void setHasSignalRangeBooster(Long hasSignalRangeBooster) {
		this.hasSignalRangeBooster = hasSignalRangeBooster;
	}

	@Column(name = "has_signal_strength_booster")
	public Long getHasSignalStrengthBooster() {
		return this.hasSignalStrengthBooster;
	}

	public void setHasSignalStrengthBooster(Long hasSignalStrengthBooster) {
		this.hasSignalStrengthBooster = hasSignalStrengthBooster;
	}

	@Column(name = "remaining_high_priority_messages")
	public Integer getRemainingHighPriorityMessages() {
		return this.remainingHighPriorityMessages;
	}

	public void setRemainingHighPriorityMessages(Integer remainingHighPriorityMessages) {
		this.remainingHighPriorityMessages = remainingHighPriorityMessages;
	}

	@Column(name = "sequence_number")
	public Long getSequenceNumber() {
		return this.sequenceNumber;
	}

	public void setSequenceNumber(Long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersByCurrentNodeId")
	public Set<AodvDataPackets> getAodvDataPacketsesForCurrentNodeId() {
		return this.aodvDataPacketsesForCurrentNodeId;
	}

	public void setAodvDataPacketsesForCurrentNodeId(Set<AodvDataPackets> aodvDataPacketsesForCurrentNodeId) {
		this.aodvDataPacketsesForCurrentNodeId = aodvDataPacketsesForCurrentNodeId;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "players")
	public Set<AodvNodeData> getAodvNodeDatas() {
		return this.aodvNodeDatas;
	}

	public void setAodvNodeDatas(Set<AodvNodeData> aodvNodeDatas) {
		this.aodvNodeDatas = aodvNodeDatas;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "node")
	public Set<Neighbours> getNeighbourses() {
		return this.neighbourses;
	}

	public void setNeighbourses(Set<Neighbours> neighbourses) {
		this.neighbourses = neighbourses;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "neighbour")
	public Set<Neighbours> getNodesForNeighbour() {
		return this.nodesForNeighbour;
	}

	public void setNodesForNeighbour(Set<Neighbours> nodesForNeighbour) {
		this.nodesForNeighbour = nodesForNeighbour;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersByDestinationId")
	public Set<AodvDataPackets> getAodvDataPacketsesForDestinationId() {
		return this.aodvDataPacketsesForDestinationId;
	}

	public void setAodvDataPacketsesForDestinationId(Set<AodvDataPackets> aodvDataPacketsesForDestinationId) {
		this.aodvDataPacketsesForDestinationId = aodvDataPacketsesForDestinationId;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersByOwnerId")
	public Set<AodvDataPackets> getAodvDataPacketsesForOwnerId() {
		return this.aodvDataPacketsesForOwnerId;
	}

	public void setAodvDataPacketsesForOwnerId(Set<AodvDataPackets> aodvDataPacketsesForOwnerId) {
		this.aodvDataPacketsesForOwnerId = aodvDataPacketsesForOwnerId;
	}

	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "playersBySourceId")
	public Set<AodvDataPackets> getAodvDataPacketsesForSourceId() {
		return this.aodvDataPacketsesForSourceId;
	}

	public void setAodvDataPacketsesForSourceId(Set<AodvDataPackets> aodvDataPacketsesForSourceId) {
		this.aodvDataPacketsesForSourceId = aodvDataPacketsesForSourceId;
	}

	@JsonIgnore
	@Column(name = "base_node_range")
	public Long getBaseNodeRange() {
		return this.baseNodeRange;
	}

	public void setBaseNodeRange(Long baseNodeRange) {
		this.baseNodeRange = baseNodeRange;
	}

	/**
	 * setting the items also as jsonable
	 * 
	 * @param items
	 */
	public void setNearbyItems(List<Items> items) {
		setNearbyItemsJSON(new ItemMap(items));
		this.nearbyItemsCount = items.size();
	}

	public void setItemInCollectionRange(Long long1) {
		this.itemInCollectionRange = long1;
	}

	@Transient
	public long getRange() {
		return range;
	}

	public void setRange(long range) {
		this.range = range;
	}

	@Transient
	public int getNeighbourCount() {
		return neighbourCount;
	}

	public void setNeighbourCount(int neighbourCount) {
		this.neighbourCount = neighbourCount;
	}

	@Transient
	public int getNearbyItemsCount() {
		return nearbyItemsCount;
	}

	public void setNearbyItemsCount(int nearbyItemsCount) {
		this.nearbyItemsCount = nearbyItemsCount;
	}

	@Transient
	public Long getNextItemDistance() {
		return nextItemDistance;
	}

	public void setNextItemDistance(Long nextItemDistance) {
		this.nextItemDistance = nextItemDistance;
	}

	@Transient
	public int getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	@Transient
	public Date getBoosterSince() {
		return boosterSince;
	}

	public void setBoosterSince(Date boosterSince) {
		this.boosterSince = boosterSince;
	}

	@Transient
	public ItemMap getNearbyItemsJSON() {
		return nearbyItemsJSON;
	}

	private void setNearbyItemsJSON(ItemMap nearbyItemsJSON) {
		this.nearbyItemsJSON = nearbyItemsJSON;
	}

	@Transient
	public Long getItemInCollectionRange() {
		return itemInCollectionRange;
	}

	@JsonIgnore
	@Column(name = "item_collection_range")
	public Long getItemCollectionRange() {
		return this.itemCollectionRange;
	}

	public void setItemCollectionRange(Long itemCollectionRange) {
		this.itemCollectionRange = itemCollectionRange;
	}

	@Override
	@Column(name = "difficulty")
	public Long getDifficulty() {
		return this.difficulty;
	}

	@Override
	public void setDifficulty(Long difficulty) {
		this.difficulty = difficulty;
	}

	@Version
	@Column(name = "OPTLOCK")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "last_ping")
	public Long getLastPing() {
		return this.lastPing;
	}

	public void setLastPing(Long lastPing) {
		this.lastPing = lastPing;
	}

	@Column(name = "ping_duration")
	public long getPingDuration() {
		return this.pingDuration;
	}

	public void setPingDuration(long pingDuration) {
		this.pingDuration = pingDuration;
	}

	@Transient
	public boolean isPingActive() {
		if (this.lastPing == null) {
			return false;
		}

		long now = new Date().getTime();
		long diff = now - lastPing;
		return diff <= getPingDuration();
	}
}
