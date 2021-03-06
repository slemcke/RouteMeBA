package de.unipotsdam.nexplorer.server.persistence.hibernate.dto;

// Generated 19.09.2012 13:58:49 by Hibernate Tools 3.2.1.GA

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.codehaus.jackson.annotate.JsonProperty;

import de.unipotsdam.nexplorer.shared.ItemType;
import de.unipotsdam.nexplorer.shared.Location;

/**
 * Items generated by hbm2java
 */
@Entity
@Table(name = "items")
public class Items implements java.io.Serializable {

	private Long id;
	private ItemType itemType;
	private Double latitude;
	private Double longitude;
	private Date createdAt;
	private Long created;
	private Integer version;

	public Items() {
		this.createdAt = new Date();
	}

	public Items(long id, ItemType itemType, Location location, Date createdAd) {
		this.id = id;
		this.itemType = itemType;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.createdAt = createdAd;
	}

	@JsonProperty("id")
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	public Long getId() {
		return this.id;
	}

	@JsonProperty("id")
	public void setId(Long id) {
		this.id = id;
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

	@Column(name = "created")
	public Long getCreated() {
		return this.created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	@Transient
	public String getName() {
		if (this.itemType.equals(ItemType.BATTERY)) {
			return "Battery";
		} else {
			return "Booster";
		}
	}

	public void setLocation(Location location) {
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
	}

	@JsonProperty("itemType")
	@Column(name = "type")
	public ItemType getItemType() {
		return itemType;
	}

	@JsonProperty("itemType")
	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	@JsonProperty("createdAt")
	@Transient
	public Date getCreatedAt() {
		return createdAt;
	}

	@JsonProperty("createdAt")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
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
