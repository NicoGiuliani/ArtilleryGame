import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;


public class Game {
    private static int playerWins = 0;
    private static int computerWins = 0;
    private static double marginOfError = 0.4;
    private static int numberOfTurns = 0;

    public static void main(String[] args) {
        // playGame returns false if the user chooses to play no additional rounds, stopping the while loop
        while (playGame());

        // These messages are displayed once the user chooses not to play any additional rounds, showing the number and percentage of wins
        System.out.println("\nPlayer wins: " + playerWins + "\nComputer wins: " + computerWins);
        System.out.println("You won " + roundTwoPlaces((double) playerWins / (double) (playerWins + computerWins)) * 100 + "% of the games played.");
    }

    // Returns a random int between the min and max values passed in
    private static int generateRandom(int min, int max) {
        Random random = new Random();
        return Math.abs(random.nextInt()) % (max - min + 1) + min;
    }

    // Converts theta from degrees to radians, then returns the distance the fired shell has traveled
    private static double getShellDistance(double theta, double speed) {
        theta = (theta * Math.PI) / 180;
        return (Math.pow(speed, 2) * Math.sin(2 * theta)) / 9.8;
    }

    // Returns a double rounded to two decimal places
    private static double roundTwoPlaces(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(number));
    }

    // The computer takes a turn firing a shell at player 1; it will return false if it strikes its target
    private static boolean computerTurn(int[] enemyRange, int enemyDistance) {
        Scanner scan = new Scanner(System.in);
        int lowEnd = (int) (enemyDistance * (1.0 - marginOfError));
        int highEnd = (int) (enemyDistance * (1.0 + marginOfError));
        int distanceFromPosition;

        // The enemy will take aim on player 1's artillery with a certain margin of error
        int aimValue = generateRandom(lowEnd, highEnd);
        System.out.println("\n------------------------------------------------------------");

        if (aimValue < enemyDistance) {
            distanceFromPosition = (int) roundTwoPlaces(enemyDistance - aimValue);
            System.out.print("\nA shell exploded " + distanceFromPosition + " meters from your position. (press any key to continue)");
            scan.nextLine();
        } else if (aimValue > enemyDistance) {
            distanceFromPosition = (int) roundTwoPlaces(aimValue - enemyDistance);
            System.out.print("\nA shell exploded " + distanceFromPosition + " meters behind your position. (press any key to continue)");
            scan.nextLine();
        }



        if (aimValue >= enemyRange[0] && aimValue <= enemyRange[1]) {
            System.out.println("Your artillery was destroyed.");
            computerWins++;
            return false;
        } else {
            System.out.println("The margin of error in this shot was " + marginOfError);

            if (numberOfTurns < 3) {
                marginOfError = (marginOfError * 10 - 1) / 10;
            }
            else if (numberOfTurns < 8) {
                marginOfError = (marginOfError * 100 - 1) / 100;
            }
            System.out.println("The margin of error of the next shot will be " + marginOfError);
            numberOfTurns++;

            return true;
        }

    }

    // The player will enter an angle and speed at which to fire a shell; it will return false if the player wins
    private static boolean playerTurn(int[] enemyRange) {
        Scanner scan = new Scanner(System.in);
        int enemyDistance = (enemyRange[0] + enemyRange[1]) / 2;
        double angle = 0;
        double speed = 0;
        boolean invalidInput;

        System.out.println("\n------------------------------------------------------------");
        System.out.println("\nEnemy artillery is at approximately " + enemyDistance + " meter(s).");
        System.out.println("Enemy range is between " + enemyRange[0] + " and " + enemyRange[1] + " meter(s).");

        do {
            try {
                System.out.print("\nEnter an angle in degrees: ");
                angle = Double.parseDouble(scan.nextLine());

                if (angle > 89 || angle < 1) {
                    invalidInput = true;
                    System.out.println("Input must be an angle between 0 and 90 degrees.");
                } else {
                    invalidInput = false;
                }

            } catch (NumberFormatException nfe) {
                System.out.println("Input must be an angle between 0 and 90 degrees.");
                invalidInput = true;
            }

        } while (invalidInput);

        do {
            try {
                System.out.print("\nEnter a speed in m/s: ");
                speed = Double.parseDouble(scan.nextLine());

                if (speed > 300 || speed < 1) {
                    invalidInput = true;
                    System.out.println("Speed must be between 0 and 300 m/s.");
                } else {
                    invalidInput = false;
                }

            } catch (NumberFormatException nfe) {
                System.out.println("Input must be an speed between 0 and 300 meters per second.");
                invalidInput = true;
            }

        } while (invalidInput);

        double shellDistance = roundTwoPlaces(getShellDistance(angle, speed));
        System.out.print("\nThe shell exploded at " + shellDistance + " meters. (press any key to continue)");
        scan.nextLine();

        if (shellDistance >= enemyRange[0] && shellDistance <= enemyRange[1]) {
            System.out.println("\nEnemy was destroyed.");
            playerWins++;
            return false;
        }
        return true;
    }

    private static boolean playGame() {
        Scanner scan = new Scanner(System.in);

        // This is the distance between players 1 & 2
        int enemyDistance = generateRandom(50, 500);
        int radius = 5;
        int[] enemyRange = { enemyDistance - radius, enemyDistance + radius };

        while (true) {
            // If during either player's turn the other's artillery is destroyed, the methods return false.
            if (!playerTurn(enemyRange)) { break; }
            else if (!computerTurn(enemyRange, enemyDistance)) { break; }
        }

        marginOfError = 0.4;
        numberOfTurns = 0;
        System.out.print("Would you like to play again? ( Y / N ) ");
        String response = scan.next();
        return response.equalsIgnoreCase("y");

    }

}