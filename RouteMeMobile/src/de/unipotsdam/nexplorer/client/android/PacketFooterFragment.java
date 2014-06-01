package de.unipotsdam.nexplorer.client.android;

import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.unipotsdam.nexplorer.client.android.callbacks.UIPacketFooter;
import de.unipotsdam.nexplorer.client.android.rest.Packet;

public class PacketFooterFragment extends Fragment implements UIPacketFooter{

	//	private TextView nextItemDistance;
	private boolean isSendingPacket;
	private LinearLayout packetLayout;
	private FrameLayout map;
	private View result;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.isSendingPacket = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		result = inflater.inflate(R.layout.fragment_packet_footer, container, false);
		map = (FrameLayout) result.getParent();
		packetLayout= (LinearLayout) result.findViewById(R.id.packages);
		return result;
	}
	
	
	@Override
	public void updateFooter(HashMap<Long,Packet> packages,Long level) {
		//only update footer if level 3 is active
		if(level != 3){
			//hide fragment and return
			map.findViewById(R.id.packetFooter).setVisibility(View.GONE);
			return;
		}
		
		//get context
		Context context = getActivity();
		//update packages
		//sort by id
		
		//reset packetLayout
		packetLayout.removeAllViews();
		
		//create tree map to sort packets by id -> oldest packet has the lowest key
		TreeMap<Long,Packet> packetsTree = new TreeMap<Long, Packet>(packages);
		int resId;
		
		for (Packet packet : packetsTree.values()) {
			Byte packetType = packet.getType();
			//show empty image if no legal type is given
			//TODO all images
			//TODO handle null pointer for packettype
			resId = R.drawable.placeholder;
			if(packetType == (byte)1){
				resId =  R.drawable.voip;
			} else if(packetType ==(byte)2){
				resId =  R.drawable.voip;
			}else if(packetType==(byte)3){
				resId =  R.drawable.chat;
			}else if(packetType==(byte)4){
				resId =  R.drawable.voip;
			}else if(packetType==(byte)5){
				resId =  R.drawable.voip;
			}
			//create new image view for each packet
			ImageView newPacket = new ImageView(context);
			//set packet id as image id to
			newPacket.setImageResource(resId);
			newPacket.setId((packet.getId().intValue()));
			newPacket.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					//moving down respectively up means simple tip on screen while we need left/right action to scroll through the view
					if(MotionEvent.ACTION_DOWN != event.getAction()){
						return false;
					}
					//TODO start sending packet here
					//initiate packet sending
					
					isSendingPacket(true);
					return true;
				}
			});
			packetLayout.addView(newPacket);
		}
		}

		@Override
		public void isSendingPacket(boolean isSendingPacket) {
			this.isSendingPacket = isSendingPacket;
			
		}
}
