package de.unipotsdam.nexplorer.client.android;

import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unipotsdam.nexplorer.client.android.callbacks.UIFooter;
import de.unipotsdam.nexplorer.client.android.rest.Packet;
import de.unipotsdam.nexplorer.client.android.rest.RoutingRequest;

public class PacketFooterFragment extends Fragment implements UIFooter {

//	private Button collectItem;
	private TextView activeItems;
	private TextView hint;
//	private TextView nextItemDistance;
	private boolean isCollectingItem;
	private boolean isSendingPacket;
	private LinearLayout packetLayout;
	private View result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.isCollectingItem = false;
		this.isSendingPacket = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		result = inflater.inflate(R.layout.fragment_packet_footer, container, false);
		packetLayout= (LinearLayout) result.findViewById(R.id.packages);
//		collectItem = (Button) result.findViewById(R.id.collectItem);
		activeItems = (TextView) result.findViewById(R.id.activeItems);
//		hint = (TextView) result.findViewById(R.id.hint);
//		nextItemDistance = (TextView) result.findViewById(R.id.nextItemDistance);

		return result;
	}


	@Override
	public void updateFooter(Integer nextItemDistance, boolean hasRangeBooster, boolean itemInCollectionRange, String hint, HashMap<Long,Packet> packages) {
//		this.hint.setText(hint);
//		//dont show hints at lvl 3
//
//		if (nextItemDistance != null)
//			setText(this.nextItemDistance, "Entfernung zum nächsten Gegenstand " + nextItemDistance + " Meter.");
//		else
//			setText(this.nextItemDistance, "Keine Gegenstände in der Nähe.");
//
		int boosterImageElement;
		if (hasRangeBooster) {
			boosterImageElement = R.drawable.mobile_phone_cast;
		} else {
			boosterImageElement = R.drawable.mobile_phone_cast_gray;
		}
//
		setText(activeItems, "Aktive Gegenstände: ", boosterImageElement);
//
		if (!this.isCollectingItem) {
//			setText(collectItem, "Gegenstand einsammeln");
//
//			boolean isDisabled = !collectItem.isEnabled();
//			if (itemInCollectionRange && isDisabled) {
//				collectItem.setEnabled(true);
//			} else if (!itemInCollectionRange && !isDisabled) {
//				collectItem.setEnabled(false);
//			}
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
					
					setIsSendingPackage(true);
					return true;
				}
			});
			packetLayout.addView(newPacket);
		}
	}
	   
	@Override
	public void setIsCollectingItem(boolean isCollectingItem) {
		this.isCollectingItem = isCollectingItem;

		if (isCollectingItem) {
//			collectItem.setEnabled(false);
//			collectItem.setText("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
		}
	}
	
//	@Override
	public void setIsSendingPackage(boolean isSendingPackage){
		this.isSendingPacket = isSendingPackage;
		//TODO don't send more packets while sending (remove function)
		//TODO don't allow pings while sending packets
	}

	private void setText(TextView text, final String string, final Integer imageId) {
		if (imageId != null) {
			Drawable image = getResources().getDrawable(imageId);
			text.setCompoundDrawablesWithIntrinsicBounds(null, null, image, null);
		}

		text.setText(string);
		if (text.getVisibility() != View.VISIBLE) {
			text.setVisibility(View.VISIBLE);
		}
	}

	void setText(TextView text, final String string) {
		setText(text, string, null);
	}
}
