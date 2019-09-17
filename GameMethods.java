import java.util.Random;
import java.util.Scanner;

public class GameMethods {

	private short delay = 1000; // The default delay between batters in this is 1 second, however this can be
								// changed in the setting

	private int homeScore = 0;
	private int awayScore = 0;
	private String homeTeam = "New York Yankees"; // The names of the teams are given a default value but can be changed
													// in the settings
	private String awayTeam = "Toronto Blue Jays";
	private int[] homePlayers = new int[20]; // This array as well as the one below keep track of which players are on
												// what base. If a payer is not on a base, they are set to -1. However
												// if they are on base, they will be assigned the same number as the
												// base they are on.
	private int[] awayPlayers = new int[20];
	private int[] basesRanHomePlayers = new int[20]; // This is used for the MVP award at the end. It keeps track of
														// what a batter hits. Whoever runs the most bases off of their
														// own bat is given the award
	private int[] basesRanAwayPlayers = new int[20];
	private String[] homeNames = new String[20]; // This stores the names of the players on each team
	private String[] awayNames = new String[20];
	private Scanner user_input = new Scanner(System.in);
	private Random r = new Random();

	public static void main(String[] args) {

		GameMethods game = new GameMethods();
		game.playBall();
	}

	private void playBall() {

		// Set the default names of the players
		initializeHomeTeamNames();
		initializeAwayTeamNames();

		if (mainMenu(user_input) == 1) { // User has decided to customize their game.
			configureGame(); // Change settings
		}

		// User has decided to just start the game with default settings

		Boolean isGameOver = false;
		int innings = 0;

		System.out.print(
				"\nToday is a fine day with an exciting ball game lined up. Get your peanuts and popcorn ready because today we have \nthe "
						+ awayTeam + " playing the " + homeTeam);

		// This will continue to run until the number of innings surpasses 9 and the
		// scores are different.
		while (isGameOver == false) {
			innings++;
			System.out.print("\n\n- - - ### Inning " + innings + " ### - - -");

			// This is where the players bat
			awayScore += playInning(awayTeam, awayPlayers, basesRanAwayPlayers, awayNames, r, delay);

			// Check to see if the game is over.
			if (innings == 9 && homeScore != awayScore) {
				isGameOver = true; // The game is over

			} else {// Play another inning
				homeScore += playInning(homeTeam, homePlayers, basesRanHomePlayers, homeNames, r, delay);

				System.out.println(homeTeam + " scored " + homeScore + ", " + awayTeam + " scored " + awayScore);

			}
		}
		decideMVP(homeTeam, awayTeam, homeScore, awayScore, innings, basesRanHomePlayers, basesRanAwayPlayers,
				homeNames, awayNames);
	}

	public static int mainMenu(Scanner user_input) {

		System.out.print("Hey batter batter batter swing!!\nWelcome to a baseball simulation game.");

		int userDecision; // Keeps track of whether the user wants to jump straight into the game or
							// customize it
		// Decide whether the user wants to customize the game or not
		do {
			System.out.print("\nEnter \'1\' to customize game or \'2\' for quick play : ");
			userDecision = user_input.nextInt();

			if (userDecision != 1 && userDecision != 2) {
				System.out.print("Invalid input, please try again.");
			}
		} while (userDecision != 1 && userDecision != 2);

		return userDecision;
	}

	public static void decideMVP(String homeTeam, String awayTeam, int homeScore, int awayScore, int innings,
			int basesRanHomePlayers[], int basesRanAwayPlayers[], String homeNames[], String awayNames[]) {

		// Once the game is over, the MVP award is decided
		int mvpAward = -1;
		int numMVP = -1;

		// The award can only be given to one of the winning team's players
		if (homeScore > awayScore) {
			for (int p = 0; p < 19; p++) {
				if (mvpAward < (basesRanHomePlayers[(p)])) {
					mvpAward = basesRanHomePlayers[p];
					numMVP = p;
				}
			}
			System.out.println(homeTeam + " scored " + homeScore + ", " + awayTeam + " scored " + awayScore);
			System.out.print("... and that does it! The " + homeTeam + " win " + homeScore + "-" + awayScore + " after " + innings
					+ " innings! The MVP award of this game goes to " + homeNames[numMVP] + " who ran " + mvpAward
					+ " bases off of their own hits\nTune in next week for some more baseball action!");

		} else{
			for (int p = 0; p < 19; p++) {
				if (mvpAward < (basesRanAwayPlayers[p])) {
					mvpAward = basesRanAwayPlayers[p];
					numMVP = p;
				}
			}
			System.out.println("\n\n" + homeTeam + " scored " + homeScore + ", " + awayTeam + " scored " + awayScore);
			System.out.print("... and that does it! The " + awayTeam + " win " + awayScore + "-" + homeScore + "! The MVP award of this game goes to "
					+ awayNames[numMVP] + " who ran " + mvpAward
					+ " bases off of their own hits\nTune in next week for some more baseball action!");
		}
	}

	public static int playInning(String teamName, int[] players, int[] basesRanByPlayers, String[] playerNames, Random r,
			short delay) {

		int teamOuts = 0; // Keeps track of the outs of the team for the inning
		boolean input; // input is used to find which player is at bat. It goes from 0-19 in a constant
						// loop and searches the array 'players' to selects who is up next
		int playerNum = 0; // This is a counter that is used to search the array 'players'
		int movePlayer = 0; // movePlayer will be set to the index of the player at bat in the array
							// 'players'
		int base[] = new int[3]; // There are 3 bases. If one is occupied, it is assigned the index of the player
									// in the array 'players' of the respective team
		int pointsScored = 0;

		// Reset all positions at the start of the inning. No one is on base.
		for (int z = 0; z < 20; z++) {
			players[z] = -1;
			if (z < 3) {
				base[z] = -1;
			}
		}
		teamOuts = 0;
		System.out.println("\n\n*** " + teamName + " at bat ***");

		// This do loop will repeat until the team has 3 outs
		do {
			// 'action' determins the hit of the batter (whether they hit a single, double,
			// triple, or homerun)
			int action = r.nextInt(100);
			input = false;

			// Determine who is at bat
			do {
				if (players[playerNum] == -1) {
					movePlayer = playerNum;
					input = true;
				}

				// If the playerNum (the counter) reaches the end of the array, loop back to the
				// start and continue
				if (playerNum < 19)
					playerNum++;
				else {
					playerNum = 0;
				}
			} while (input == false);

			// The hitter has struck out (50% chance)
			if (action < 51) {
				System.out.println("\n" + playerNames[movePlayer] + " struck out");
				teamOuts++;
			}

			// The hitter has hit a single (20% chance)
			else if (action < 71) {
				System.out.println("\n" + playerNames[movePlayer] + " hit a single");
				basesRanByPlayers[movePlayer]++; // The 1 base play is recorded

				// This is what happens when the single is hit
				if (base[0] == -1) {// Will enter if base 1 is unoccupied
					base[0] = movePlayer;
					players[movePlayer] = 1;
				} else {// Means that someone is on base 1
					if (base[1] == -1) {// Will enter if base 2 is unoccupied
						base[1] = base[0];
						players[base[1]]++;
						base[0] = movePlayer;
						players[movePlayer] = 1;
					} else { // Base 2 is occupied
						if (base[2] == -1) {// Will enter if base 3 is unoccupied
							base[2] = base[1];
							players[base[2]]++;
							base[1] = base[0];
							players[base[1]]++;
							base[0] = movePlayer;
							players[movePlayer] = 1;
						} else {// Base 3 is occupied
							pointsScored += 1;
							players[base[2]] = -1;
							base[2] = base[1];
							players[base[2]]++;
							base[1] = base[0];
							players[base[1]]++;
							base[0] = movePlayer;
							players[movePlayer] = 1;
						}
					}

				}

			}

			// Batter hits a double (15% chance)
			else if (action < 86) {
				System.out.println("\n" + playerNames[movePlayer] + " hit a double");
				basesRanByPlayers[movePlayer] += 2;
				if (base[1] == -1) {// Will enter if base 2 is unoccupied
					base[1] = movePlayer;
					players[movePlayer] = 2;
					if (base[0] != -1 && base[2] == -1) {// Will enter if there is someone on base 1, but nobody on base
															// 3
						base[2] = base[0];
						players[base[0]] = 3;
						base[0] = -1;
					} else if (base[0] != -1 && base[2] != -1) {// Will enter if base 1 and 3 are occupied
						pointsScored += 1;
						players[base[2]] = -1;
						base[2] = base[0];
						players[base[0]] = 3;
						base[0] = -1;
						base[1] = movePlayer;
						players[movePlayer] = 2;

					}
				} else {// Base 2 is occupied
					if (base[0] == -1 && base[2] == -1) {// Base 1 and 3 are free. Person on base 2 is not on base 3
						base[2] = base[1];
						players[base[1]] = 3;
						base[1] = movePlayer;
						players[movePlayer] = 2;
					} else if (base[0] == -1) {// Base 1 is free but base 3 is taken. (base 3 scores) person on base 2
												// is now on base 3
						players[base[2]] = -1;
						pointsScored += 1;
						base[2] = base[1];
						players[base[1]] = 3;
						base[1] = movePlayer;
						players[movePlayer] = 2;
					} else if (base[2] == -1) {// Base 3 is free but base 1 is taken (person that was on base 2 scores)
												// and whoever was on base 1 goes to base 3
						pointsScored += 1;
						players[base[1]] = -1;
						base[2] = base[0];
						players[base[0]] = 3;
						base[1] = movePlayer;
						players[movePlayer] = 2;
					} else // Bases are loaded. People on base 2 and 3 score. Person on base 1 goes to 3
							// and hitter goes to 2
					{
						pointsScored += 2;
						players[base[2]] = -1;
						players[base[1]] = -1;
						base[2] = base[0];
						players[base[0]] = 3;
						base[1] = movePlayer;
						players[movePlayer] = 2;
						base[0] = -1;
					}
				}
			} else if (action < 96) { // Batter hit a triple (10% chance)
				System.out.println("\n" + playerNames[movePlayer] + " hit a triple");
				basesRanByPlayers[movePlayer] += 3;
				if (base[0] == -1 && base[1] == -1 && base[2] == -1) {// no one is on a base
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else if (base[0] == -1 && base[1] == -1) {// base 3 is occupied. Person on base 3 scores and hitter
															// goes to base 3
					pointsScored += 1;
					players[base[2]] = -1; // base 3 scored
					System.out.print(playerNames[base[2]] + " scored!\n");
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else if (base[0] == -1 && base[2] == -1) {// base 2 is occupied. Person on base 2 goes home and hitter
															// is on base 3
					pointsScored += 1;
					players[base[1]] = -1;
					System.out.print(playerNames[base[1]] + " scored!\n");
					base[1] = -1; // base 2 scores
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else if (base[0] == -1) {// base 2 and 3 are occupied. Players on bases 2 and 3 score, hitter is on
											// base 3
					pointsScored += 2;
					players[base[1]] = -1;
					System.out.print(playerNames[base[1]] + " scored!\n");
					base[1] = -1;
					players[base[2]] = -1;
					System.out.print(playerNames[base[2]] + " scored!\n");
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else if (base[0] != -1 && base[1] == -1 && base[2] == -1) {// base 2 and 3 free. Player on base 1
																				// scores. Hitter is on base 3.
					pointsScored += 1;
					players[base[0]] = -1;
					System.out.print(playerNames[base[0]] + " scored!\n");
					base[0] = -1;
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else if (base[0] != -1 && base[1] == -1) {// base 2 is free. Base 1 and 3 score. Hitter is on base 3
					pointsScored += 2;
					players[base[0]] = -1;
					System.out.print(playerNames[base[0]] + " scored!\n");
					base[0] = -1;
					players[base[2]] = -1;
					System.out.print(playerNames[base[2]] + " scored!\n");
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else if (base[0] != -1 && base[2] == -1) {// base 3 is free. Players on bases 1 and 2 scores. Hitter
															// is on base 3
					pointsScored += 2;
					players[base[1]] = -1;
					System.out.print(playerNames[base[1]] + " scored!\n");
					base[1] = -1;
					players[base[0]] = -1;
					System.out.print(playerNames[base[0]] + " scored!\n");
					base[0] = -1;
					base[2] = movePlayer;
					players[movePlayer] = 3;
				} else {// All bases are occupied. All players on base score and hitter is on base 3
					pointsScored += 2;
					players[base[2]] = -1;
					System.out.print(playerNames[base[2]] + " scored!\n");
					base[2] = movePlayer;
					players[base[1]] = -1;
					System.out.print(playerNames[base[1]] + " scored!\n");
					base[1] = -1;
					players[base[0]] = -1;
					System.out.print(playerNames[base[0]] + " scored!\n");
					base[0] = -1;
					players[movePlayer] = 3;
				}

			} else {// Batter hit a homerun (5% chance)
				System.out.println("\n" + playerNames[movePlayer] + " hit a homerun!");
				// Checks if each base is occupied. If so, the player on that base scores
				basesRanByPlayers[movePlayer] += 4;
				for (int x = 2; x > -1; x--) {
					if (base[x] != -1) {
						pointsScored += 1;
						players[base[x]] = -1;
						System.out.print(playerNames[base[x]] + " scored!\n");
						// record score
						base[x] = -1;
					}
				}
				pointsScored += 1;// The hitter scores
			}

			// Set delay between hitters
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} while (teamOuts < 3);
		return pointsScored;
	}

	public void configureGame() {// This is where the settings are be changed.
		int change = 0;
		do {// Keep on asking until the user enters 6 (Exit)
			System.out.print(
					"\n- - -  CUSTOMIZE  - - -\n\nEnter\n\'1\' to change the name of the home team\n\'2\' to change the name of the away team\n\'3\' to change the name of the players on the home team\n\'4\' to change the name of the players on the away team\n\'5\' to change delay between batters\n\'6\' to EXIT\n");
			change = user_input.nextInt();
			if (change > 6 || change < 0) {// Prevents invalid inputs
				System.out.print("Invalid input. Please enter a value from 1 to 6 inclusive");
			} else if (change == 1) {// Change the name of the home team
				System.out.print("\nYou have decided to change the name of the home team. It is currently: " + homeTeam
						+ ". What would you like to change it to?\nNew home team name: ");
				user_input.nextLine();
				homeTeam = user_input.nextLine();
				System.out.print("Success! The new home team name is: " + homeTeam);
			} else if (change == 2) {// Change the name of the away team
				System.out.print("\nYou have decided to change the name of the away team. It is currently: " + awayTeam
						+ ". What would you like to change it to?\nNew away team name: ");
				user_input.nextLine();
				awayTeam = user_input.nextLine();
				System.out.print("Success! The new away team name is: " + awayTeam);
			} else if (change == 3) {// Change the names of the players on the home team
				user_input.nextLine();
				System.out.print(
						"\nYou have decided to change the names of the home team players. This team has 20 players and will ask for each of their names.");
				for (int x = 0; x < 20; x++) {
					System.out.print("\nEnter player " + (x + 1) + "\'s name: ");
					homeNames[x] = user_input.nextLine();
				}
			} else if (change == 4) {// Change the names of the players on the away team
				user_input.nextLine();
				System.out.print(
						"\nYou have decided to change the names of the away team players. This team has 20 players and will ask for each of their names.");
				for (int x = 0; x < 20; x++) {
					System.out.print("\nEnter player " + (x + 1) + "\'s name: ");
					awayNames[x] = user_input.nextLine();
				}
				System.out.print(awayNames[0] + " " + awayNames[1]);
			} else if (change == 5) {// Change the delay between batters
				System.out.print(
						"You have decided to change the delay between actions. Enter a positive integer greater or equal to 0.\nNote that the delay is mesured in miliseconds meaning 0 delay will output everything instantly and 1000 delay will wait one second.\nHow much delay would you like? : ");
				delay = user_input.nextShort();
			} else {// Exit, continue with game
				System.out.print("Lets get right into the game!");
			}
		} while (change != 6);
	}

	public void initializeAwayTeamNames() { // Set deafult names for players
		awayNames[0] = "Joe Biagini";
		awayNames[1] = "Matt Dermody";
		awayNames[2] = "Conner Greene";
		awayNames[3] = "J.A. Happ";
		awayNames[4] = "Tom Koehler";
		awayNames[5] = "Aaron Loup";
		awayNames[6] = "Tim Mayza";
		awayNames[7] = "Roberto Osuna";
		awayNames[8] = "Aaron Sanchez";
		awayNames[9] = "Ryan Tepera";
		awayNames[10] = "Troy Tulowitzki";
		awayNames[11] = "Richard Urena";
		awayNames[12] = "Anthony Alford";
		awayNames[13] = "Ezequiel Carrera";
		awayNames[14] = "Teoscar Hernandez";
		awayNames[15] = "Steve Pearce";
		awayNames[16] = "Kevin Pillar";
		awayNames[17] = "Dalton Pompey";
		awayNames[18] = "Dwight Smith Jr.";
		awayNames[19] = "Kendrys Morales";
	}

	public void initializeHomeTeamNames() {// Set deafult names for players
		homeNames[0] = "Ben Heller";
		homeNames[1] = "Domingo German";
		homeNames[2] = "Luis Cessa";
		homeNames[3] = "Jonathan Holder";
		homeNames[4] = "Bryan Mitchell";
		homeNames[5] = "David Robertson";
		homeNames[6] = "Miguel Andujar";
		homeNames[7] = "Tyler Austin";
		homeNames[8] = "Starlin Castro";
		homeNames[9] = "Didi Gregorius";
		homeNames[10] = "Chase Headley";
		homeNames[11] = "Ronald Torreyes";
		homeNames[12] = "Jacoby Ellsbury";
		homeNames[13] = "Clint Frazier";
		homeNames[14] = "Brett Gardner";
		homeNames[15] = "Aaron Hicks";
		homeNames[16] = "Aaron Judge";
		homeNames[17] = "Billy McKinney";
		homeNames[18] = "Greg Bird";
		homeNames[19] = "Adam Warren";
	}
}