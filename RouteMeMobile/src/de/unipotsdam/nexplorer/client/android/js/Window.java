package de.unipotsdam.nexplorer.client.android.js;

import java.util.TimerTask;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

public class Window {

	public static Object undefined = null;

	public static Location location = null;

	public static Button loginButton = null;

	public static Text beginDialog = null;

	public static Text waitingText = null;

	public static Text hint = null;

	public static Text nextItemDistance = null;

	public static Text activeItems = null;

	public static Button collectItemButton = null;

	public static Geolocation geolocation = null;

	public static MainPanelToolbar mainPanelToolbar = null;

	public static Overlay loginOverlay = null;

	public static Overlay waitingForGameOverlay = null;
	public static Overlay noPositionOverlay = null;

	public static SenchaMap senchaMap = null;

	private static Activity ui = null;

	private static RestTemplate template;
	private static String host;

	public static void createInstance(android.widget.Button collectItem, android.widget.Button login, android.widget.TextView activeItemsText, android.widget.TextView hintText, android.widget.TextView nextItemDistanceText, android.widget.TextView waitingTextText, Activity host, android.widget.TextView beginText, TextView score, TextView neighbourCount, TextView remainingPlayingTime, TextView battery, android.app.Dialog loginDialog, String hostAdress, android.app.Dialog waitingForGameDialog, android.app.Dialog noPositionDialog, GoogleMap map) {
		collectItemButton = new Button(collectItem, host);
		loginButton = new Button(login, host);

		activeItems = new Text(activeItemsText, host);
		hint = new Text(hintText, host);
		nextItemDistance = new Text(nextItemDistanceText, host);
		waitingText = new Text(waitingTextText, host);

		location = new Location(host);

		beginDialog = new Text(beginText, host);

		geolocation = new Geolocation(host, host);

		mainPanelToolbar = new MainPanelToolbar(score, neighbourCount, remainingPlayingTime, battery, host);

		loginOverlay = new Overlay(loginDialog, host);

		waitingForGameOverlay = new Overlay(waitingForGameDialog, host);
		noPositionOverlay = new Overlay(noPositionDialog, host);

		senchaMap = new SenchaMap(map);

		ui = host;

		SimpleClientHttpRequestFactory http = new SimpleClientHttpRequestFactory();
		http.setConnectTimeout(8000);
		template = new RestTemplate(true, http);
		template.getMessageConverters().add(new GsonHttpMessageConverter());
		Window.host = hostAdress;
	}

	public static void clearInterval(Interval interval) {
		if (interval != null) {
			interval.clear();
		}
	}

	public static Interval setInterval(TimerTask callback, long timeMillis) {
		Interval interval = new Interval();
		interval.set(callback, timeMillis);
		return interval;
	}

	public static <T> void ajax(final Options<T> options) {
		final AjaxTask<T> task = new AjaxTask<T>(host, template, options);

		Runnable job = new Runnable() {

			@Override
			public void run() {
				Object result = task.doInBackground();
				if (result instanceof Exception) {
					options.error((Exception) result);
				} else {
					options.success((T) result);
				}
			}
		};

		if (options.async) {
			new Thread(job).start();
		} else {
			job.run();
		}
	}

	public static <S, T> void each(java.util.Map<S, T> objects, Call<S, T> callback) {
		for (S key : objects.keySet()) {
			callback.call(key, objects.get(key));
		}
	}

	public static boolean isNaN(double result) {
		return Double.isNaN(result);
	}

	public static double parseFloat(String value) {
		return Double.parseDouble(value);
	}

	public static Integer parseInt(String value) {
		if (value == null) {
			return null;
		}
		return Integer.parseInt(value);
	}
}
