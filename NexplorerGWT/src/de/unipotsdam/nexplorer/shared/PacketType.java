package de.unipotsdam.nexplorer.shared;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public enum PacketType implements Serializable {
		@JsonProperty("MAIL")
		MAIL ("Mail"),
		@JsonProperty("VOIP")
		VOIP ("VoIP"),
		@JsonProperty("CHAT")
		CHAT ("Chat");
		
	    private final String name;       

	    private PacketType(String s) {
	        name = s;
	    }
	    
	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }
	}

