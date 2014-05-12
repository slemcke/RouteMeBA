package de.unipotsdam.nexplorer.client.android;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.unipotsdam.nexplorer.client.android.callbacks.UIFooter;

public class PackageFooterFragment extends Fragment implements UIFooter {

	private Button collectItem;
	private TextView activeItems;
	private TextView hint;
	private TextView nextItemDistance;
	private boolean isCollectingItem;
	private LinearLayout packagesLayout;
	private View result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.isCollectingItem = false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		result = inflater.inflate(R.layout.fragment_package_footer, container, false);
		packagesLayout= (LinearLayout) result.findViewById(R.id.packages);
//		collectItem = (Button) result.findViewById(R.id.collectItem);
//		activeItems = (TextView) result.findViewById(R.id.activeItems);
//		hint = (TextView) result.findViewById(R.id.hint);
//		nextItemDistance = (TextView) result.findViewById(R.id.nextItemDistance);

		return result;
	}


	@Override
	public void updateFooter(Integer nextItemDistance, boolean hasRangeBooster, boolean itemInCollectionRange, String hint, HashMap<Long,Byte> packages) {
//		this.hint.setText(hint);
//		//dont show hints at lvl 3
//
//		if (nextItemDistance != null)
//			setText(this.nextItemDistance, "Entfernung zum nächsten Gegenstand " + nextItemDistance + " Meter.");
//		else
//			setText(this.nextItemDistance, "Keine Gegenstände in der Nähe.");
//
//		int boosterImageElement;
//		if (hasRangeBooster) {
//			boosterImageElement = R.drawable.mobile_phone_cast;
//		} else {
//			boosterImageElement = R.drawable.mobile_phone_cast_gray;
//		}
//
//		setText(activeItems, "Aktive Gegenstände: ", boosterImageElement);
//
//		if (!this.isCollectingItem) {
//			setText(collectItem, "Gegenstand einsammeln");
//
//			boolean isDisabled = !collectItem.isEnabled();
//			if (itemInCollectionRange && isDisabled) {
//				collectItem.setEnabled(true);
//			} else if (!itemInCollectionRange && !isDisabled) {
//				collectItem.setEnabled(false);
//			}
//		}

		
		//get context
		Context context = getActivity();
		//update packages
		//sort by id
		
		for (Long packetID : packages.keySet()) {
			Byte packetType = packages.get(packetID);
			//show empty image if no legal type is given
			int resId = R.drawable.placeholder;
			if(packetType == 0){
				resId =  R.drawable.voip;
			} else if(packetType ==1){
				resId =  R.drawable.chat;
			}else if(packetType==2){
				resId =  R.drawable.voip;
			}else if(packetType==3){
				resId =  R.drawable.voip;
			}
			ImageView newPacket = new ImageView(context);
			newPacket.setImageResource(resId);
			packagesLayout.addView(newPacket);
		}
	}

	@Override
	public void setIsCollectingItem(boolean isCollectingItem) {
		this.isCollectingItem = isCollectingItem;

		if (isCollectingItem) {
			collectItem.setEnabled(false);
			collectItem.setText("Gegenstand wird eingesammelt...<img src='media/images/ajax-loader.gif' />");
		}
	}
	
//	@Override
//	public void setIsSendingPackage(boolean isSendingPackage){
//		
//	}

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
