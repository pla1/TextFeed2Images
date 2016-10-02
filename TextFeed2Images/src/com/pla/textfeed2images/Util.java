package com.pla.textfeed2images;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public class Util {
	private static Properties properties;

	public static void main(String[] args) throws Exception {
		if (false) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] fontNames = ge.getAvailableFontFamilyNames();
			for (int i = 0; i < fontNames.length; i++) {
				System.out.println(fontNames[i]);
			}
		}

	}

	static {
		String fileName = "/etc/com.pla.properties";
		System.out.println("Loading properties from file: " + fileName);
		try {
			InputStream input = new FileInputStream(new File(fileName));
			properties = new Properties();
			properties.load(input);
			System.out.println("Properties loaded from file: " + fileName);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Properties not loaded from file: " + fileName + " " + e.getLocalizedMessage());
		}
	}

	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	public static boolean isBlank(String s) {
		return (s == null || s.trim().length() == 0);
	}

	public static long transfer(Timestamp timestamp) {
		if (timestamp == null) {
			return 0;
		} else {
			return timestamp.getTime();
		}
	}

	public static String getDateDisplay(Timestamp timestamp) {
		if (timestamp == null) {
			return "";
		} else {
			// TODO finish format
			SimpleDateFormat sdf = new SimpleDateFormat("EEE ");
			return sdf.format(timestamp);
		}
	}

	public static void runCommand(String s) {
		try {
			Runtime.getRuntime().exec(s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isNFLWeeInProgress(ArrayList<Game> games) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		Date fromDate = calendar.getTime();
		// TODO BUMP BACK DOWN TO 2 AFTER TESTING.
		calendar.add(Calendar.DAY_OF_YEAR, 2);
		Date toDate = calendar.getTime();
		boolean answer = false;
		for (Game game : games) {
			if (game.getDate().after(fromDate) && game.getDate().before(toDate)) {
				System.out.println(game.toString() + " " + game.getDate());
				answer = true;
			}
		}
		// System.out.println("Date range: " + fromDate + " " + toDate +
		// " In progress: " + answer);
		return answer;
	}

	public Util() {
	}

	public static int getNFLYear(ArrayList<Game> games) {
		if (games.isEmpty()) {
			return 0;
		}
		return games.get(0).getYear();
	}

	public static String getNFLWinLoss(Team team, ArrayList<Game> games) {
		if (games.isEmpty()) {
			return "";
		}
		int wins = 0;
		int losses = 0;
		int ties = 0;
		for (Game game : games) {
			if (game.isFinished()) {
				if (game.getAwayPoints() == game.getHomePoints()) {
					ties++;
				}
				if (team.getName().equals(game.getAwayTeam())) {
					if (game.getAwayPoints() > game.getHomePoints()) {
						wins++;
					}
					if (game.getAwayPoints() < game.getHomePoints()) {
						losses++;
					}
				} else {
					if (game.getAwayPoints() < game.getHomePoints()) {
						wins++;
					}
					if (game.getAwayPoints() > game.getHomePoints()) {
						losses++;
					}
				}
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(wins).append("-");
		sb.append(losses);
		if (ties > 0) {
			sb.append("-");
			sb.append(ties);
		}
		return sb.toString();
	}

	public static String getNFLWeekTitle(ArrayList<Game> games) {
		int week = 0;
		int year = 0;
		String seasonType = "";
		if (games.size() > 0) {
			Game game = games.get(0);
			week = game.getWeek();
			year = game.getYear();
			seasonType = game.getType();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(year);
		sb.append(" ");
		if (!"Regular".equals(seasonType)) {
			sb.append(seasonType);
			sb.append(" ");
		}
		sb.append("NFL Week ");
		sb.append(week);
		return sb.toString();
	}

	public static String getWundergroundApiKey() {
		return properties.getProperty("WUNDERGROUND_API_KEY");
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/nfldb", "nfldb",
				properties.getProperty("nfldb.password"));
	}

	public static void close(ResultSet rs, PreparedStatement ps, Connection connection) {
		close(rs);
		close(ps);
		close(connection);
	}

	public static void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean isDevelopmentEnvironment() {
		return Util.getIpAddress().startsWith("192.168.1.");
	}

	public static String getIpAddress() {
		String ipAddress = "";
		try {
			Socket socket = new Socket("google.com", 80);
			InetAddress inetAddress = socket.getLocalAddress();
			if (inetAddress != null) {
				ipAddress = inetAddress.getHostAddress();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipAddress;
	}

	public static double getDouble(String s) {
		if (s == null) {
			return 0;
		}
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException nfe) {
			return 0;
		}

	}

	public static int getInt(String s) {
		if (s==null) {
			return 0;
		}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}

	public static int getHour() {
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public static String scrapePage(URL url) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConnection = null;
		InputStream inputStream = null;

		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
				}
			} };
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			urlConnection = url.openConnection();
			inputStream = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine = bufferedReader.readLine();
			while (inputLine != null) {
				sb.append("\n");
				sb.append(inputLine);
				inputLine = bufferedReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
		}
		return sb.toString();
	}

}
