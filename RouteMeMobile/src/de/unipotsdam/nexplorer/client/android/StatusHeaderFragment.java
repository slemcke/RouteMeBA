package de.unipotsdam.nexplorer.client.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.unipotsdam.nexplorer.client.android.callbacks.UIHeader;

public class StatusHeaderFragment extends Fragment implements UIHeader {

	private TextView score;
	private TextView neighbourCount;
	private TextView remainingPlayingTime;
	private TextView battery;
	private ImageView level;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.fragment_status_header,
				container, false);
		level = (ImageView) result.findViewById(R.id.level);
		//set level
		score = (TextView) result.findViewById(R.id.points);
		neighbourCount = (TextView) result.findViewById(R.id.neighbours);
		remainingPlayingTime = (TextView) result.findViewById(R.id.time);
		battery = (TextView) result.findViewById(R.id.battery);

		return result;
	}

	@Override
	public void updateHeader(Integer score, Integer neighbourCount,
			Long remainingPlayingTime, Double battery, Long level) {
		//set level icon (maybe onCreate method)
		if (level == 1) {
			this.level.setImageResource(R.drawable.lvlone);
		} else if (level == 2) {
			this.level.setImageResource(R.drawable.lvltwo);
		} else if (level == 3) {
			this.level.setImageResource(R.drawable.lvlthree);
		}
		this.score.setText(score + "");
		this.neighbourCount.setText(neighbourCount + "");
		this.remainingPlayingTime.setText(convertMS(remainingPlayingTime));
		this.battery.setText((battery + "%").replace(".", ","));
	}

	/**
	 * 
	 * @param ms
	 * @returns {String}
	 */
	private String convertMS(Long seconds) {
		if (seconds == null) {
			return null;
		}

		double s = seconds;
		double ms = s % 1000;
		s = (s - ms) / 1000;
		double secs = s % 60;
		s = (s - secs) / 60;
		double mins = s % 60;

		return addZ(mins);
	}

	private String addZ(double n) {
		return (n < 10 ? "0" : "") + n;
	}
}
