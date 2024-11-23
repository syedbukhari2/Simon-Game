// Programmed by Syed Bukhari (2317577)
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import swiftbot.Button;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;

public class Simon {

	static SwiftBotAPI API = new SwiftBotAPI();
	static Scanner scanner = new Scanner (System.in); // Used to scan for user input
	static Random rand = new Random();
	
	static int roundNumber = 1;
	static int score = 0;
	static int coins = 0; // Default amount
	static int buttonClicks = 0; // Number of times a button has been clicked
	
	static boolean gameOver = false; // This boolean is used to end the Game
	static int livesRemaining = 3;
	
	static ArrayList<Integer> generatedSequence = new ArrayList<Integer>(); // This ArrayList holds the randomly generated numbers between 1->4
	static ArrayList<Integer> userSequence = new ArrayList<Integer>(); // This ArrayList holds the sequence entered by the user with the buttons
	static ArrayList<String> ownedColours = new ArrayList<>();
	
	// Set default colour values in case text file is not formatted as expected.
	static String buttonA_colour = "Red", buttonB_colour = "Blue", buttonX_colour = "Green", buttonY_colour = "Yellow";
	static String chosenLevel;
	
	public static void main(String[] args) {

		setupGame();
		menu();
		
		while ((gameOver == false) && (livesRemaining >= 0)) {

			try { // Small pause before round starts to update gameOver from previous round (if applicable)
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!gameOver && livesRemaining >= 0) {
				System.out.println("\nRound " + roundNumber + "!");
				System.out.println("Score: " + score);
				System.out.println("You have " + livesRemaining + " lives remaining...");
				
				generatePattern(chosenLevel);

				for (int j = 0; j < generatedSequence.size(); ++j) { // This loop uses the ArrayList to display the light pattern
					if (generatedSequence.get(j) == 0) {
						displayLight("A");
					} else if (generatedSequence.get(j) == 1) {
						displayLight("X");
					} else if (generatedSequence.get(j) == 2) {
						displayLight("B");
					} else {
						displayLight("Y");
					}
				}

				buttonClicks = 0; // Set to 0 at the start of every round.
				userSequence.clear();

				int previousLivesRemaining = livesRemaining;
				
				try { // Try enabling all four buttons
					API.enableButton(Button.A, () -> {
						System.out.println("Button A pressed.");
						displayLight("A"); // 0 means red (middle left) under-light
						userSequence.add(0);
						++buttonClicks;
						checkPattern(); // After button has been clicked, check to see if it is correct
					});
					
					API.enableButton(Button.B, () -> {
						System.out.println("Button B pressed.");
						displayLight("B"); // 2 means blue (bottom left) under-light
						userSequence.add(2);
						++buttonClicks;
						checkPattern();
					});
					
					API.enableButton(Button.X, () -> {
						System.out.println("Button X pressed.");
						displayLight("X"); // 1 means green (middle right) under-light
						userSequence.add(1);
						++buttonClicks;
						checkPattern();
					});
					
					API.enableButton(Button.Y, () -> {
						System.out.println("Button Y pressed.");
						displayLight("Y"); // 3 means yellow (bottom right) under-light
						userSequence.add(3);
						++buttonClicks;
						checkPattern();
					});

				} catch (Exception e) { // Display the error message if an exception occurs
					e.printStackTrace();
				}

				// Loop is used to wait for the user to click the buttons enough times depending on the round
				while (buttonClicks < generatedSequence.size() && !gameOver) {
					
					try { // Small wait
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (livesReduced(previousLivesRemaining)) {
						break;
					}
				}
				
				API.disableAllButtons(); // Once the player has clicked the buttons enough times, disable them.

				correctPatternIndicator();
				
				if ((livesRemaining >= 0) && (roundNumber % 5 == 0)) { // Ask user if they want to quit after every 5 rounds
					System.out.println("\nWould you like to end the game? (Y/N)");
					String endInput = scanner.next();
					if (endInput.toLowerCase().equals("y")) { // Using endInput.toLowerCase().equals("y") ensures it still works if in lower case
						gameOver = true;
						endGame("See you again champ!");
					} else if (!(endInput.toLowerCase().equals("y")) && !(endInput.toLowerCase().equals("n"))) {
						System.out.println("Invalid input. Starting the next round...\n");
					}
				}

				if (!gameOver) {
					++roundNumber;
					if (!livesReduced(previousLivesRemaining)) {
						++score;
					}
				} else if (livesRemaining < 0) {
					break; // If game IS over, exit the While loop
				}
			}
		}
		endGame("Game over!");
	}

	private static void setupGame() {
		ownedColours.clear();
		FileReader readhandle;
		try {
			readhandle = new FileReader("./info.txt"); // The first line is the number of coins and the rest are the colours owned by the player
			BufferedReader br = new BufferedReader(readhandle);
			
			String line;
            while ((line = br.readLine()) != null) {
            	ownedColours.add(line); // Read the lines from the file and add them to ArrayList
            }
            String line1 = ownedColours.get(0);
            try {
                int intValue = Integer.parseInt(line1); // Convert first line to an integer
                coins = intValue;
                ownedColours.remove(0); // Remove the first element of ArrayList (since it is supposed to be the number of coins)
            } catch (NumberFormatException e) {
                System.err.println("Error: File is not formatted correctly! Setting default values...");
                coins = 0;
                e.printStackTrace();
            }
            br.close();
            readhandle.close();
            
            String arrayElement;
            for (int i = 0; i <= ownedColours.size() - 1; ++i) {
            	arrayElement = ownedColours.get(i);
            	
            	// Split the string into an array based on space
                String[] elementParts = arrayElement.split(" ");
                // Check if the array has at least two elements
                if (elementParts.length == 2) {
                	if (elementParts[1].equals("A")) {
                		buttonA_colour = elementParts[0];
                	} else if (elementParts[1].equals("B")) {
                		buttonB_colour = elementParts[0];
                	} else if (elementParts[1].equals("X")) {
                		buttonX_colour = elementParts[0];
                	} else if (elementParts[1].equals("Y")) {
                		buttonY_colour = elementParts[0];
                	}
                }
            }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void menu() {
		System.out.println("\n\nWelcome to the Simon Game!\n");
		System.out.println("1) Play\n2) Colour shop\n3) Button configuration\nSelect an option: ");
		String option = scanner.next();
		if (option.equals("1")) {
			chosenLevel = startGame();
		} else if (option.equals("2")) {
			shop();
		} else if (option.equals("3")) {
			configureButtons();
		} else {
			menu();
		}
	}

	private static void shop() {
		String input;
		System.out.println("\n--- Colour shop! ---");
		System.out.println("Coins: " + coins);
		System.out.println("You currently own these colours: " + ownedColours);
		System.out.println("You can purchase these additional colours (120 coins each):\n1) Brown\n2) Cyan\n3) Orange\n4) Pink\n5) Purple");
		if (coins < 120) {
			System.out.println("\nYou currently don't have enough coins to buy any of these colours. Play more games to earn coins!\nReturning to main menu...");
			menu();
		} else {
			System.out.println("Enter the light number you'd like to buy (1-5) or 0 to cancel: ");
			input = scanner.next();
			if (input.equals("1")) {
				ownedColours.add("Brown");
				coins -= 120;
				writeArrayListToFile(ownedColours);
				System.out.println("Purchased brown. Coins remaining: " + coins);
			} else if (input.equals("2")) {
				ownedColours.add("Cyan");
				coins -= 120;
				writeArrayListToFile(ownedColours);
				System.out.println("Purchased cyan. Coins remaining: " + coins);
			} else if (input.equals("3")) {
				ownedColours.add("Orange");
				coins -= 120;
				writeArrayListToFile(ownedColours);
				System.out.println("Purchased orange. Coins remaining: " + coins);
			} else if (input.equals("4")) {
				ownedColours.add("Pink");
				coins -= 120;
				writeArrayListToFile(ownedColours);
				System.out.println("Purchased pink. Coins remaining: " + coins);
			} else if (input.equals("5")) {
				ownedColours.add("Purple");
				coins -= 120;
				writeArrayListToFile(ownedColours);
				System.out.println("Purchased purple. Coins remaining: " + coins);
			} else {
				System.out.println("No purchase was made");
			}
			setupGame();
			menu();
		}
	}
	
	private static void configureButtons() {
		System.out.println("\nCurrent configuration is shown with letters A, B, X and Y."); 
		for (int i = 0; i <= ownedColours.size() - 1; ++i) {
			System.out.println(ownedColours.get(i));
		}
		System.out.println("Which button would you like to assign to a different colour?\nEnter A, B, X or Y: ");
		String button = scanner.next();
		if (button.toUpperCase().equals("A")) {
			System.out.println("Enter the new colour for Button A: ");
			String newColour = scanner.next();
			setNewButtonColour(newColour, "A");
		} else if (button.toUpperCase().equals("B")) {
			System.out.println("Enter the new colour for Button B: ");
			String newColour = scanner.next();
			setNewButtonColour(newColour, "B");
		} else if (button.toUpperCase().equals("X")) {
			System.out.println("Enter the new colour for Button X: ");
			String newColour = scanner.next();
			setNewButtonColour(newColour, "X");
		} else if (button.toUpperCase().equals("Y")) {
			System.out.println("Enter the new colour for Button Y: ");
			String newColour = scanner.next();
			setNewButtonColour(newColour, "Y");
		} else {
			System.out.println("Invalid input.");
		}
		writeArrayListToFile(ownedColours);
		setupGame();
		menu();
	}
	
	private static void setNewButtonColour(String colour, String button) {
		String arrayElement;
		for (int i = 0; i <= ownedColours.size() - 1; ++i) {
        	arrayElement = ownedColours.get(i);
        	
        	// Split the string into an array based on space
            String[] elementParts = arrayElement.split(" ");
            
            // Check if the array has at least two elements
            if (elementParts.length == 2) {
            	if (elementParts[1].equals(button)) {
            		ownedColours.set(i, elementParts[0]); // Remove the button 'A', 'B', 'X' or 'Y' for that colour
            	}
            }
            if (elementParts[0].equals(colour)) {
        		ownedColours.set(i, elementParts[0] + " " + button); // Add the letter 'A', 'B', 'X' or 'Y' to new colour
        	}
        }
        writeArrayListToFile(ownedColours);
	}
	
	private static void writeArrayListToFile(ArrayList<String> list) {
		try {
			FileWriter writehandle = new FileWriter("./info.txt");
			BufferedWriter bw = new BufferedWriter(writehandle);
			bw.write(String.valueOf(coins)); // First line is coins
			bw.newLine();
			for (int i = 0; i <= ownedColours.size() - 1; ++i) {
				bw.write(ownedColours.get(i));
				if (!(i == ownedColours.size() - 1)) {
					bw.newLine();
				}
			}
			bw.close();
			writehandle.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	private static String startGame() {
		System.out.println("\nPlease select a level:\nA) Default - A new colour is added to the pattern after completing each round.");
		System.out.println("B) Level 1 - Sequence of up to 3 colours.");
		System.out.println("C) Level 2 - Sequence of 4 to 6 colours.");
		System.out.println("D) Level 3 - Sequence of 7 to 10 colours.");
		System.out.println("E) Level 4 - Sequence of 11 to 15 colours.");
		System.out.println("\nChoose your level (A-E): ");
		String chosenLevel = scanner.next();
		chosenLevel = chosenLevel.toLowerCase();
		System.out.println("choosnLevel: " + chosenLevel);
		if (!chosenLevel.equals("a") && !chosenLevel.equals("b") && !chosenLevel.equals("c") && !chosenLevel.equals("d") && !chosenLevel.equals("e")) {
			System.out.println("Invalid choice. Choosing default option A.");
			chosenLevel = "a";
		}
		return chosenLevel;
	}
	
	private static void displayLight(String button) {
		/* Under-light information
            Middle left = Red, Assigned to button A
            Middle right = Green, Assigned to button X
            Back left = Blue, Assigned to button B
            Back right = Yellow, Assigned to button Y
		 */
		try {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (button.equals("A")) {
				int[] colourToLightUp = getRGBValues(buttonA_colour);
				API.setUnderlight(Underlight.MIDDLE_LEFT, colourToLightUp);
			} else if (button.equals("X")) {
				int[] colourToLightUp = getRGBValues(buttonX_colour);
				API.setUnderlight(Underlight.MIDDLE_RIGHT, colourToLightUp);
			} else if (button.equals("B")) {
				int[] colourToLightUp = getRGBValues(buttonB_colour);
				API.setUnderlight(Underlight.BACK_LEFT, colourToLightUp);
			} else if (button.equals("Y")) {
				int[] colourToLightUp = getRGBValues(buttonY_colour);
				API.setUnderlight(Underlight.BACK_RIGHT, colourToLightUp);
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			API.disableUnderlights();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	private static int[] getRGBValues(String colour) {
        switch (colour.toLowerCase()) {
            case "red":
                return new int[]{255, 0, 0};
            case "green":
                return new int[]{0, 0, 255};
            case "blue":
                return new int[]{0, 255, 0};
            case "yellow":
                return new int[]{255, 0, 255};
            case "brown":
                return new int[]{139, 19, 69};
            case "cyan":
                return new int[]{0, 255, 255};
            case "orange":
                return new int[]{255, 0, 165};
            case "pink":
                return new int[]{255, 193, 182};
            case "purple":
                return new int[]{128, 128, 0};
            default:
                return new int[]{255, 255, 255}; // Default to white for unknown colours
        }
    }

	private static void generatePattern(String modeChoice) { // This method generates a random pattern based on which mode the user is playing
		if (modeChoice.toLowerCase().equals("a")) {
			int randomInt = rand.nextInt(4); // Generate a random number between 0-3 (4 different possible values)
			generatedSequence.add(randomInt); // Add the number generated to the ArrayList that holds these values
		} else {
			int upperBound = 0;
			generatedSequence.clear(); // clear the ArrayList
			if (modeChoice.toLowerCase().equals("b")) {
				// Generate a random number between 1 (inclusive) and 4 (exclusive)
				upperBound = rand.nextInt(3) + 1;
			} else if (modeChoice.toLowerCase().equals("c")) {
				// Generate a random number between 4 (inclusive) and 7 (exclusive)
				upperBound = rand.nextInt(3) + 4;
			} else if (modeChoice.toLowerCase().equals("d")) {
				// Generate a random number between 7 and 10 (inclusive)
				upperBound = rand.nextInt(4) + 7;
			} else if (modeChoice.toLowerCase().equals("e")) {
				// Generate a random number between 11 and 15 (inclusive)
				upperBound = rand.nextInt(5) + 11;
			}
			System.out.println("upperBound: " + upperBound);
			for (int i = 0; i < upperBound; ++i) {
				int randomInt = rand.nextInt(4); // Generate a random number between 0-3 (4 different possible values)
				generatedSequence.add(randomInt); // Add the number generated to the ArrayList that holds these values
				System.out.println("Random number generated: " + i);
			}
		}
	}

	private static void checkPattern() {
		if (userSequence.get(buttonClicks - 1) != generatedSequence.get(buttonClicks - 1)) {
			System.out.println("Oops! That wasn't right!");
			--livesRemaining;
		} else {
			gameOver = false;
		}
	}

	private static boolean livesReduced(int previousLivesRemaining) {
		if (livesRemaining < previousLivesRemaining) {
			return true;
		} else {
			return false;
		}
	}

	private static void correctPatternIndicator() {
		try {
			API.setButtonLight(Button.A, true);
			Thread.sleep(80);
			API.setButtonLight(Button.B, true);
			Thread.sleep(70);
			API.setButtonLight(Button.Y, true);
			Thread.sleep(60);
			API.setButtonLight(Button.X, true);
			Thread.sleep(350);
			API.disableButtonLights();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void celebrate() { // Move in "V" shape with lights flashing.
		System.out.println("\nCelebration starting! Make sure the SwiftBot doesn't fall off!");

		try {
			Thread.sleep(1500);
			System.out.println("Starting celebration in");
			Thread.sleep(1000);
			for (int i = 3; i > 0; --i) {
				System.out.println(i + "... ");
				Thread.sleep(1000);
			}

			API.startMove(100, 100); // Go forwards for the duration of the celebration light pattern
			celebrationLightPattern();
			API.stopMove(); // Stop
			API.startMove(0, 100); // Turn for 800 milliseconds
			Thread.sleep(800);
			API.stopMove();
			API.startMove(100, 100); // // Go forwards again for the duration of the celebration light pattern
			celebrationLightPattern();
			API.stopMove(); // Stop
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void celebrationLightPattern() {
		for (int i = 0; i < 4; ++i) { // Generate 3 random numbers between 0-3 (4 different possible values)
			int randomInt = rand.nextInt(4);
			try {
				if (randomInt == 0) {
					int[] colourToLightUp = {255, 0, 0};
					API.setUnderlight(Underlight.MIDDLE_LEFT, colourToLightUp); // R, B, G
				} else if (randomInt == 1) {
					int[] colourToLightUp = {0, 0, 255};
					API.setUnderlight(Underlight.MIDDLE_RIGHT, colourToLightUp);
				} else if (randomInt == 2) {
					int[] colourToLightUp = {0, 255, 0};
					API.setUnderlight(Underlight.BACK_LEFT, colourToLightUp);
				} else if (randomInt == 3) {
					int[] colourToLightUp = {255, 0, 255};
					API.setUnderlight(Underlight.BACK_RIGHT, colourToLightUp);
				}
				Thread.sleep(500);
				API.disableUnderlights();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void endGame(String endMessage) {
		System.out.println("Final score: " + score + "!");
		if (score >= 5) {
			System.out.println("You've earned 50 coins!");
			coins += 50;
			writeArrayListToFile(ownedColours);
			celebrate();
		}
		System.out.println("\n" + endMessage);
		scanner.close();
		System.exit(0);
	}
}