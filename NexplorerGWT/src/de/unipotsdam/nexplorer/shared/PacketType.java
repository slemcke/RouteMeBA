package de.unipotsdam.nexplorer.shared;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public enum PacketType implements Serializable {
		@JsonProperty("MAIL")
		MAIL ("MAIL", (byte)1),
		@JsonProperty("VOIP")
		VOIP ("VOIP", (byte)2),
		@JsonProperty("CHAT")
		CHAT ("CHAT", (byte)3),
		@JsonProperty("CHAT8")
		CHAT8 ("CHAT8", (byte)4),
		@JsonProperty("CHAT1")
		CHAT1 ("CHAT1", (byte)5),
		@JsonProperty("CHAT2")
		CHAT2 ("CHAT2", (byte)6),
		@JsonProperty("CHAT3")
		CHAT3 ("CHAT3", (byte)7),
		@JsonProperty("CHAT4")
		CHAT4 ("CHAT4", (byte)8),
		@JsonProperty("CHAT5")
		CHAT5 ("CHAT5", (byte)9),
		@JsonProperty("CHAT6")
		CHAT6 ("CHAT6", (byte)10),
		@JsonProperty("CHAT7")
		CHAT7 ("CHAT7", (byte)11);
		
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

