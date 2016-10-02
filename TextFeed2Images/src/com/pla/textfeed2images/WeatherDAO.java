package com.pla.textfeed2images;

import java.io.ObjectInputStream.GetField;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.postgresql.core.Utils;

import com.google.gson.Gson;

public class WeatherDAO {
	private static final String WUNDERGROUND_BASE_URL = "http://api.wunderground.com/api/";
	private static final String STATE_CITY = "/q/29461.json";

	public static void main(String[] args) throws Exception {
		WeatherDAO dao = new WeatherDAO();
		double latitude = 33.04253200000001;
		double longitude = -80.1484297;
		ArrayList<String> phrases = dao.getFlashBriefing(latitude, longitude);
		for (String phrase : phrases) {
			System.out.println(phrase);
		}
		System.out.println(phrases.size() + " phrases");
	}

	public ArrayList<String> getFlashBriefing(double latitude, double longitude) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String ivrStatement = getIvrStatement(latitude, longitude);
		String[] phrases = ivrStatement.split("\\.");
		for (int i = 0; i < phrases.length; i++) {
			arrayList.add(phrases[i].trim().concat("."));
		}
		return arrayList;
	}

	public String getIvrStatement(double latitude, double longitude) {
		StringBuilder sb = new StringBuilder();
		String urlSegment = "geolookup/conditions/forecast/q/" + latitude + "," + longitude + ".json";
		String json = getJson(urlSegment);
		System.out.println(json);
		Gson gson = new Gson();
		Container container = gson.fromJson(json, Container.class);
		System.out.println(container.toString());
		sb.append("In ");
		sb.append(container.location.city);
		sb.append(" the temperature is ");
		sb.append((int) container.current_observation.temp_f);
		sb.append(" degrees.");
		int j = 0;
		if (Util.getHour() > 17) {
			j = 1;
		}
		for (int i = 0; i < 2; i++) {
			sb.append(" ");
			sb.append(container.forecast.txt_forecast.forecastday.get(j).title).append(", ");
			sb.append(container.forecast.txt_forecast.forecastday.get(j).fcttext);
			j++;
		}
		String forecast = sb.toString().toLowerCase();
		forecast = forecast.replaceAll("(\\d+)(f)", "$1 degrees");
		forecast = forecast.replaceAll("winds w ", " winds west ");
		forecast = forecast.replaceAll("winds s ", " winds south ");
		forecast = forecast.replaceAll("winds n ", " winds north ");
		forecast = forecast.replaceAll("winds e ", " winds east ");
		forecast = forecast.replaceAll("winds sse ", " winds south southeast ");
		forecast = forecast.replaceAll("winds ssw ", " winds south southwest ");
		forecast = forecast.replaceAll("winds se ", " winds southeast ");
		forecast = forecast.replaceAll("winds sw ", " winds southwest ");
		forecast = forecast.replaceAll("winds nne ", " winds north northeast ");
		forecast = forecast.replaceAll("winds nnw ", " winds north northwest ");
		forecast = forecast.replaceAll("winds ne ", " winds northeast ");
		forecast = forecast.replaceAll("winds nw ", " winds northwest ");
		forecast = forecast.replaceAll("winds wne ", " winds west northeast ");
		forecast = forecast.replaceAll("winds wnw ", " winds west northwest ");
		forecast = forecast.replaceAll("winds wsw ", " winds west southwest ");
		forecast = forecast.replaceAll("mph", "miles per hour");
		return forecast;
	}

	public String getJson(String function) {
		StringBuilder sb = new StringBuilder();
		sb.append(WUNDERGROUND_BASE_URL);
		sb.append(Util.getWundergroundApiKey());
		sb.append(function);
		sb.append(STATE_CITY);
		URL url;
		try {
			url = new URL(sb.toString());
			return Util.scrapePage(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	class Container {
		public String toString() {
			return String.format("City: %s", location.city);
		}

		public Forecast forecast;
		public CurrentObservation current_observation;
		public Location location;

		class CurrentObservation {
			public double temp_f;
		}

		class Location {
			public String city;
		}

		class Forecast {
			public TextForecast txt_forecast;

			class TextForecast {
				ArrayList<ForecastDay> forecastday;

				class ForecastDay {

					public String period;
					public String fcttext;
					public String title;

				}
			}
		}
	}
}
