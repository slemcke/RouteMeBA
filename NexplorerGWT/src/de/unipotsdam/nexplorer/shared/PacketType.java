package de.unipotsdam.nexplorer.shared;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public enum PacketType implements Serializable {
		@JsonProperty("Mail")
		Mail ("Mail", (byte)1),
		@JsonProperty("Html")
		Html ("Html", (byte)2),
		@JsonProperty("Chat")
		Chat ("Chat", (byte)3),
		@JsonProperty("Navigation")
		Navigation ("Navigation", (byte)4),
		@JsonProperty("VoIP")
		VoIP ("VoIP", (byte)5);


		
	    private final String name;
	    private final Byte priority; 

	    
	    private PacketType(String name, Byte priority) {
	    	this.name=name;
	    	this.priority=priority;
	    }
	    
	    public boolean equalsName(String otherName){
	        return (otherName == null)? false:name.equals(otherName);
	    }

	    public String toString(){
	       return name;
	    }
	    
	    public Byte getPriority(){
		       return priority;
		    }
	    
	}

