package de.unipotsdam.nexplorer.client.android;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter.LengthFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.unipotsdam.nexplorer.client.android.callbacks.UIPacketFooter;
import de.unipotsdam.nexplorer.client.android.rest.Packet;

public class PacketFooterFragment extends Fragment implements UIPacketFooter {

	// private TextView nextItemDistance;
	private boolean isSendingPacket;
	private LinearLayout packetLayout;
	private ViewGroup container;
	private View result;
	private Button packetid;
	private TextView packetText;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// no send requested
		this.isSendingPacket = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		result = inflater.inflate(R.layout.fragment_packet_footer, container,
				false);
		// map = inflater.inflate(R.layout.activity_map, container,false);
		this.container = container;
		packetid = (Button) result.findViewById(R.id.packetid);
		packetText = (TextView) result.findViewById(R.id.packetText);
		packetLayout = (LinearLayout) result.findViewById(R.id.packages);
		return result;
	}

	@Override
	public void updateFooter(HashMap<Long, Packet> packages, Long level) {
		// only update footer if level 3 is active
		if (level != 3) {
			// hide fragment and return
			container.findViewById(R.id.packetFooter).setVisibility(View.GONE);
			// map.findViewById(R.id.packetFooter).setVisibility(View.GONE);
			return;
		} else {
			container.findViewById(R.id.packetFooter).setVisibility(
					View.VISIBLE);
		}

		// get context
		Context context = getActivity();
		// update packages
		// sort by id

		// reset packetLayout
		packetLayout.removeAllViews();

		// create tree map to sort packets by id -> oldest packet has the lowest
		// key
		TreeMap<Long, Packet> packetsTree = new TreeMap<Long, Packet>(packages);
		int resId;
		if(packetsTree.size()>0){
			packetText.setText(R.string.packetText_notSelected);
		}
		for (Packet packet : packetsTree.values()) {
			Byte packetType = packet.getType();
			// show empty image if no legal type is given
			resId = R.drawable.placeholder;
			if (packetType == (byte) 1) {
				resId = R.drawable.mail;
			} else if (packetType == (byte) 2) {
				resId = R.drawable.html;
			} else if (packetType == (byte) 3) {
				resId = R.drawable.chat;
			} else if (packetType == (byte) 4) {
				resId = R.drawable.navigation;
			} else if (packetType == (byte) 5) {
				resId = R.drawable.voip;
			} else {
				// TODO is this really necessary?
				resId = R.drawable.placeholder;
			}
			// create new image view for each packet
			ImageButton newPacket = new ImageButton(context);
			// set packet id as image id
			newPacket.setImageResource(resId);
			newPacket.setId((packet.getId().intValue()));
			// packet is packet to be sent
			if (((String) packetid.getText()).equals(String.valueOf(packet
					.getId()))) {
				// transparent

				newPacket.setBackgroundColor(Color.CYAN);
				packetText.setText(R.string.packetText_selected);
			} else {
				newPacket.setBackgroundColor(Color.TRANSPARENT);

			}
			newPacket.setOnClickListener(new View.OnClickListener() {

				private Method handler;

				@Override
				public void onClick(View v) {
					// simply unselect button if it was selected before
					if (((String) packetid.getText()).equals(String.valueOf(v
							.getId()))) {
						disablePacketButton(v);
						// packetid.setEnabled(false);
						// packetid.setText(R.string.default_packet);
						// v.setBackgroundColor(Color.TRANSPARENT);
						try {
							// using reflections for calling method in
							// MapActivity
							handler = v.getContext().getClass()
									.getMethod("abortSending", View.class);
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							handler.invoke(v.getContext(), v);
							// if(result != null){
							// disablePacketButton(v);
							// }
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {

						enablePacketButton(v);
						// v.setBackgroundColor(Color.CYAN);
						// packetid.setEnabled(true);
						// packetid.setText(String.valueOf(v.getId()));
						try {
							// using reflections for calling method in
							// MapActivity
							handler = v.getContext().getClass()
									.getMethod("sendPacket", View.class);
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							handler.invoke(v.getContext(), v);
							// if(result != null){
							// disablePacketButton(v);
							// }
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			packetLayout.addView(newPacket);
		}
	}

	private void enablePacketButton(View v) {
		packetid.setEnabled(true);
		packetid.setText(String.valueOf(v.getId()));
		v.setBackgroundColor(Color.CYAN);
		packetText.setText(R.string.packetText_selected);
	}

	private void disablePacketButton(View v) {
		// call GUI
		packetid.setEnabled(false);
		packetid.setText(R.string.default_packet);
		packetText.setText(R.string.packetText_notSelected);
		v.setBackgroundColor(Color.TRANSPARENT);
	}

	@Override
	public void setIsSendingPacket(boolean isSendingPacket) {
		this.isSendingPacket = isSendingPacket;
		if (isSendingPacket) {
			packetid.setEnabled(false);
			packetid.setText(R.string.default_packet);
		}

	}
	
	@Override
	public void showToast(String info){
		Context context = getActivity();
		Toast toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
		toast.show();
	}
}
