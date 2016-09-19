package com.pla.textfeed2images;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FlashBriefingDAO {

	public static void main(String[] args) {
		FlashBriefingDAO dao = new FlashBriefingDAO();
		String[] phrases = dao.getNflCurrentWeek();
		for (String phrase : phrases) {
			System.out.println(phrase);
		}
		System.out.format("%d phrases\n", phrases.length);
		System.exit(0);
	}

	private ArrayList<String> getFinals(ArrayList<Game> games) {
		ArrayList<String> arrayList = new ArrayList<String>();
		String dayOfWeekHold = "";
		for (Game game : games) {
			if (game.isFinished()) {
				String dayOfWeek = game.getDayOfWeek();
				if (!dayOfWeek.equals(dayOfWeekHold)) {
					arrayList.add(String.format("On %s ", dayOfWeek));
				}
				dayOfWeekHold = dayOfWeek;
				String verb = "";
				if (game.getHomePoints() > game.getAwayPoints()) {
					verb = "beat";
				}
				if (game.getHomePoints() < game.getAwayPoints()) {
					verb = "lost to";
				}
				if (game.getHomePoints() == game.getAwayPoints()) {
					verb = "tied";
				}
				arrayList.add(String.format("The %s %s the %s %d %d.", game.getHomeTeam(), verb, game.getAwayTeam(),
						game.getHomePoints(), game.getAwayPoints()));
			}
		}
		return arrayList;
	}

<<<<<<< HEAD
  private ArrayList<String> getPassingLeaders() {
    ArrayList<String> arrayList = new ArrayList<String>();
    PlayerDAO playerDAO = new PlayerDAO();
    ArrayList<Player> players = playerDAO.getPassing();
    for (int i = 0; i < 5 && i < players.size(); i++) {
      Player player = players.get(i);
      arrayList.add(String.format("Number %d with %d yards %s of the %s.", i + 1, player.getAggregate(), player.getFullName(),
          player.getTeam()));
    }
    if (!arrayList.isEmpty()) {
      arrayList.add(0, "Here are the quarterbacks with the most passing yards.");
    }
    return arrayList;
  }

  private ArrayList<String> getRushingLeaders() {
    ArrayList<String> arrayList = new ArrayList<String>();
    PlayerDAO playerDAO = new PlayerDAO();
    ArrayList<Player> players = playerDAO.getRushing();
    for (int i = 0; i < 5 && i < players.size(); i++) {
      Player player = players.get(i);
      arrayList.add(String.format("Number %d with %d yards %s of the %s.", i + 1, player.getAggregate(), player.getFullName(),
          player.getTeam()));
    }
    if (!arrayList.isEmpty()) {
      arrayList.add(0, "Here are the players with the most rushing yards.");
    }
    return arrayList;
  }

  private ArrayList<String> getReceivingLeaders() {
    ArrayList<String> arrayList = new ArrayList<String>();
    PlayerDAO playerDAO = new PlayerDAO();
    ArrayList<Player> players = playerDAO.getReceiving();
    for (int i = 0; i < 5 && i < players.size(); i++) {
      Player player = players.get(i);
      arrayList.add(String.format("Number %d with %d yards %s of the %s.", i + 1, player.getAggregate(), player.getFullName(),
          player.getTeam()));
    }
    if (!arrayList.isEmpty()) {
      arrayList.add(0, "Here are the players with the most receiving yards.");
    }
    return arrayList;
  }

  private ArrayList<String> getUpcoming(ArrayList<Game> games) {
    ArrayList<String> arrayList = new ArrayList<String>();
    if (games.isEmpty()) {
      return arrayList;
    }
    Collections.sort(games, new Comparator<Game>() {
      public int compare(Game o1, Game o2) {
        if (o1.getDate().after(o2.getDate())) {
          return 1;
        }
        if (o2.getDate().after(o1.getDate())) {
          return -1;
        }
        return o1.getHomeTeam().compareTo(o2.getHomeTeam());
      }
    });
    String dayOfWeek = null;
    for (Game game : games) {
      if (!game.isFinished()) {
        if (dayOfWeek == null || !dayOfWeek.equals(game.getDayOfWeek())) {
          dayOfWeek = game.getDayOfWeek();
          arrayList.add(String.format("Coming up on %s, ", dayOfWeek));
        }
        arrayList.add(String.format("%s at %s.", game.getAwayTeam(), game.getHomeTeam()));
      }
    }
    return arrayList;
  }
=======
	private ArrayList<String> getUpcoming(ArrayList<Game> games) {
		ArrayList<String> arrayList = new ArrayList<String>();
		if (games.isEmpty()) {
			return arrayList;
		}
		Collections.sort(games, new Comparator<Game>() {
			public int compare(Game o1, Game o2) {
				if (o1.getDate().after(o2.getDate())) {
					return 1;
				}
				if (o2.getDate().after(o1.getDate())) {
					return -1;
				}
				return o1.getHomeTeam().compareTo(o2.getHomeTeam());
			}
		});
		String dayOfWeek = null;
		for (Game game : games) {
			if (!game.isFinished()) {
				if (dayOfWeek == null || !dayOfWeek.equals(game.getDayOfWeek())) {
					dayOfWeek = game.getDayOfWeek();
					arrayList.add(String.format("Coming up on %s, ", dayOfWeek));
				}
				arrayList.add(String.format("%s at %s.", game.getAwayTeam(), game.getHomeTeam()));
			}
		}
		return arrayList;
	}
>>>>>>> ebdcf24e0e5c4ae10d5ce4f05b6fdccdb466a05a

	private ArrayList<String> getInProgress(ArrayList<Game> games) {
		ArrayList<String> arrayList = new ArrayList<String>();
		long now = System.currentTimeMillis();
		for (Game game : games) {
			if (!game.isFinished() && game.getStartTimeMilliseconds() < now) {
				String verb = "";
				if (game.getHomePoints() > game.getAwayPoints()) {
					verb = "lead";
					arrayList.add(String.format("The %s %s the %s %d %d.", game.getHomeTeam(), verb, game.getAwayTeam(),
							game.getHomePoints(), game.getAwayPoints()));
				}
				if (game.getHomePoints() < game.getAwayPoints()) {
					verb = "are loosing to";
					arrayList.add(String.format("The %s %s the %s %d %d.", game.getHomeTeam(), verb, game.getAwayTeam(),
							game.getHomePoints(), game.getAwayPoints()));
				}
				if (game.getHomePoints() == game.getAwayPoints()) {
					verb = "are tied with";
					arrayList.add(String.format("The %s %s the %s at %d.", game.getHomeTeam(), verb, game.getAwayTeam(),
							game.getHomePoints(), game.getAwayPoints()));
				}
			}
		}
		for (int i = 0; i < arrayList.size(); i++) {
			arrayList.set(i, arrayList.get(i).replaceAll(" 0", " zero"));
		}
		if (!arrayList.isEmpty()) {
			arrayList.add(0, "Currently ");
		}
		return arrayList;
	}

	private String getWinners(ArrayList<Game> games) {
		StringBuilder sb = new StringBuilder();
		sb.append("The winning teams were the ");
		String comma = "";
		for (Game game : games) {
			if (game.isFinished()) {
				sb.append(comma);
				if (game.getAwayPoints() > game.getHomePoints()) {
					sb.append(game.getAwayTeam());
				}
				if (game.getAwayPoints() < game.getHomePoints()) {
					sb.append(game.getHomeTeam());
				}
				comma = " ";
			}
		}
		sb.append(".");
		return sb.toString();
	}

	private String getLoosers(ArrayList<Game> games) {
		StringBuilder sb = new StringBuilder();
		sb.append("The loosing teams were the ");
		String comma = "";
		for (Game game : games) {
			if (game.isFinished()) {
				sb.append(comma);
				if (game.getAwayPoints() < game.getHomePoints()) {
					sb.append(game.getAwayTeam());
				}
				if (game.getAwayPoints() > game.getHomePoints()) {
					sb.append(game.getHomeTeam());
				}
				comma = " ";
			}
		}
		sb.append(".");
		return sb.toString();
	}

	private String getTies(ArrayList<Game> games) {
		StringBuilder sb = new StringBuilder();
		String comma = "";
		int quantity = 0;
		for (Game game : games) {
			if (game.isFinished()) {
				if (game.getAwayPoints() == game.getHomePoints()) {
					sb.append(comma);
					sb.append(game.getAwayTeam());
					sb.append(" and ");
					sb.append(game.getHomeTeam());
					quantity++;
					comma = " ";
				}
			}
		}
		if (quantity > 0) {
			String sentence = String.format("There was %d game%s that ended in a tie. ", quantity, getPlural(quantity));
			sb.insert(0, sentence);
			sb.append(".");
		}
		return sb.toString();
	}

	private String getPlural(int quantity) {
		if (quantity > 1) {
			return "s";
		} else {
			return "";
		}
	}

<<<<<<< HEAD
  public String[] getNflCurrentWeek() {
    ArrayList<String> phrases = new ArrayList<String>();
    GameDAO gameDAO = new GameDAO();
    ArrayList<Game> games = gameDAO.getGamesThisWeek();
    if (games.isEmpty()) {
      return phrases.toArray(new String[0]);
    }
    int week = games.get(0).getWeek();
    phrases.add(String.format("Here is your flash briefing for NFL week number %d.", week));
    // phrases.add(getWinners(games));
    // phrases.add(getLoosers(games));
    phrases.addAll(getInProgress(games));
    String ties = getTies(games);
    if (Util.isNotBlank(ties)) {
      phrases.add(ties);
    }
    phrases.addAll(getFinals(games));
    phrases.addAll(getUpcoming(games));
    phrases.addAll(getPassingLeaders());
    phrases.addAll(getReceivingLeaders());
    phrases.addAll(getRushingLeaders());
    phrases.add("That's all for your flash briefing.");
    return phrases.toArray(new String[0]);
  }
=======
	public String[] getNflCurrentWeek() {
		ArrayList<String> phrases = new ArrayList<String>();
		GameDAO gameDAO = new GameDAO();
		ArrayList<Game> games = gameDAO.getGamesThisWeek();
		if (games.isEmpty()) {
			return phrases.toArray(new String[0]);
		}
		int week = games.get(0).getWeek();
		phrases.add(String.format("Here is your flash briefing for NFL week number %d.", week));
		// phrases.add(getWinners(games));
		// phrases.add(getLoosers(games));
		ArrayList<String> inProgress = getInProgress(games);
		phrases.addAll(inProgress);
		String ties = getTies(games);
		if (Util.isNotBlank(ties)) {
			phrases.add(ties);
		}
		if (!inProgress.isEmpty() || !ties.isEmpty()) {
			phrases.add("That's all for your flash briefing.");
			return phrases.toArray(new String[0]);
		}
		phrases.addAll(getFinals(games));
		phrases.addAll(getUpcoming(games));
		phrases.add("That's all for your flash briefing.");
		return phrases.toArray(new String[0]);
	}
>>>>>>> ebdcf24e0e5c4ae10d5ce4f05b6fdccdb466a05a
}
