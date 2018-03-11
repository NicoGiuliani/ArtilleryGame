import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static int playerWins = 0;
    private static int computerWins = 0;
    private static int numberOfTurns = 0;
    private static double marginOfError = 0.35;

    public static void main(String[] args) {
        System.out.println();
        System.out.println("    :::     ::::::::: ::::::::::: ::::::::::: :::        :::        :::::::::: :::::::::  :::   ::: ");
        System.out.println("  :+: :+:   :+:    :+:    :+:         :+:     :+:        :+:        :+:        :+:    :+: :+:   :+: ");
        System.out.println(" +:+   +:+  +:+    +:+    +:+         +:+     +:+        +:+        +:+        +:+    +:+  +:+ +:+  ");
        System.out.println("+#++:++#++: +#++:++#:     +#+         +#+     +#+        +#+        +#++:++#   +#++:++#:    +#++:   ");
        System.out.println("+#+     +#+ +#+    +#+    +#+         +#+     +#+        +#+        +#+        +#+    +#+    +#+    ");
        System.out.println("#+#     #+# #+#    #+#    #+#         #+#     #+#        #+#        #+#        #+#    #+#    #+#    ");
        System.out.println("###     ### ###    ###    ###     ########### ########## ########## ########## ###    ###    ###    ");

        // playGame() returns true if the user wants to play another round, and false if not.
        while (playGame());

        // Once the user chooses not to play another round, these messages will show the number and percentage of wins.
        System.out.println("\n============================================================");
        System.out.println("\nPlayer wins: " + playerWins + "\nComputer wins: " + computerWins);
        double percentageOfWins = (double) playerWins / (playerWins + computerWins) * 100;
        System.out.println("You won " + percentageOfWins + "% of the games played.\n");
        System.out.println(determineMessage(percentageOfWins));
    }

    // Returns a random int between the min and max arguments.
    private static int generateRandom(int min, int max) {
        Random random = new Random();
        return Math.abs(random.nextInt()) % (max - min + 1) + min;
    }

    // Converts theta from degrees to radians, then returns the distance the fired shell will travel.
    private static double getShellDistance(double theta, double speed) {
        theta = (theta * Math.PI) / 180;
        return (Math.pow(speed, 2) * Math.sin(2 * theta)) / 9.8;
    }

    // Returns the argument rounded to two decimal places.
    private static double roundTwoPlaces(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(number));
    }

    // This method will take distance as an argument, then return a corresponding angle and speed.
    private static double[] angleAndTrajectory(int distance) {
        double thetaDegrees = generateRandom(5, 85);
        double thetaRadians = (thetaDegrees * Math.PI) / 180;
        double speed = Math.sqrt( (9.8 * distance) / (Math.sin(2 * thetaRadians) ) );
        return new double[]{thetaDegrees, speed};
    }

    // The player will enter an angle and a speed at which to fire a shell; it will return true if it hits its target.
    private static boolean playerTurn(Scanner scan, int[] enemyRange, int enemyDistance) {
        double angle = 0;
        double speed = 0;
        boolean invalidInput;

        System.out.println("\n============================================================");
        System.out.println("\nEnemy artillery is at approximately " + enemyDistance + " meter(s).");
        System.out.println("Enemy range is between " + enemyRange[0] + " and " + enemyRange[1] + " meter(s).");

        // These do whiles ensure the user enters valid input: an angle between 0 - 90°, and a speed between 0 - 300 m/s.
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
                invalidInput = true;
                System.out.println("Input must be an angle between 0 and 90 degrees.");
            }
        } while (invalidInput);

        do {
            try {
                System.out.print("\nEnter a speed in m/s: ");
                speed = Double.parseDouble(scan.nextLine());

                if (speed > 300 || speed < 0) {
                    invalidInput = true;
                    System.out.println("Speed must be between 0 and 300 m/s.");
                } else {
                    invalidInput = false;
                }
            } catch (NumberFormatException nfe) {
                invalidInput = true;
                System.out.println("Speed must be between 0 and 300 m/s.");
            }
        } while (invalidInput);

        // ShellDistance will be set to the calculated distance rounded to two places.
        double shellDistance = roundTwoPlaces(getShellDistance(angle, speed));
        System.out.print("\nThe shell exploded at " + shellDistance + " meters. (press any key to continue)");
        scan.nextLine();

        // If the shell lands within range, the artillery will be destroyed.
        if (shellDistance >= enemyRange[0] && shellDistance <= enemyRange[1]) {
            System.out.println("\nEnemy was destroyed.");
            playerWins++;
            return true;
        }
        return false;
    }

    // The computer takes a turn firing at player 1. It will return true if it strikes its target, false if it misses.
    private static boolean computerTurn(Scanner scan, int[] enemyRange, int enemyDistance) {
        int distanceFromPosition;

        // These will set lowEnd to x% below the enemyDistance, and highEnd to x% above the enemyDistance.
        int lowEnd = (int) (enemyDistance * (1.0 - marginOfError));
        int highEnd = (int) (enemyDistance * (1.0 + marginOfError));

        // The enemy will fire somewhere between lowEnd and highEnd.
        int aimValue = generateRandom(lowEnd, highEnd);

        // Since the shell distance is determined first, the following method determines an angle and speed to match it.
        double[] shotValues = angleAndTrajectory(aimValue);

        System.out.println("\n============================================================");
        System.out.printf("\nEnemy artillery fired %s degrees at %s m/s.\n", (int) shotValues[0], roundTwoPlaces(shotValues[1]));
        System.out.println("The shell travelled " + aimValue + " meters.");

        // Depending on whether the shell landed short, overshot its target, or made a direct hit, one of these messages will be shown.
        if (aimValue < enemyDistance) {
            distanceFromPosition = (int) roundTwoPlaces(enemyDistance - aimValue);
            System.out.print("\nA shell exploded " + distanceFromPosition + " meters from your position. (press any key to continue)");
            scan.nextLine();
        } else if (aimValue > enemyDistance) {
            distanceFromPosition = (int) roundTwoPlaces(aimValue - enemyDistance);
            System.out.print("\nA shell exploded " + distanceFromPosition + " meters behind your position. (press any key to continue)");
            scan.nextLine();
        } else {
            System.out.print("\nA shell landed directly on your position. (press any key to continue)");
            scan.nextLine();
        }

        // If the shell lands close enough, the artillery will be destroyed.
        if (aimValue >= enemyRange[0] && aimValue <= enemyRange[1]) {
            System.out.println("Your artillery was destroyed.");
            computerWins++;
            return true;
        } else {
//            System.out.println("The margin of error in this shot was plus or minus " + (int) (marginOfError * 100) + "%.");

            // For the first three turns, the computer's margin of error will drop by 10% each turn.
            if (numberOfTurns < 3) {
                marginOfError = (marginOfError * 10 - 1) / 10;
            }
            // For the next five turns, the computer's margin of error will drop by 1% each turn.
            else if (numberOfTurns < 8) {
                marginOfError = (marginOfError * 100 - 1) / 100;
            }
//            System.out.println("The margin of error of the next shot will be plus or minus "  + (int) (marginOfError * 100) + "%.");
            numberOfTurns++;
            return false;
        }

    }

    private static boolean playGame() {
        Scanner scan = new Scanner(System.in);

        // This positions the enemy artillery at a random distance between 50 and 500 meters.
        int enemyDistance = generateRandom(50, 500);

        // If a shell hits within this radius of either player, that player's artillery will be destroyed.
        int radius = 5;
        int[] enemyRange = { enemyDistance - radius, enemyDistance + radius };

        while (true) {
            // If during either player's turn the other's artillery is destroyed, its method returns false.
            if (playerTurn(scan, enemyRange, enemyDistance)) { break; }
            else if (computerTurn(scan, enemyRange, enemyDistance)) { break; }
        }

        // After the game has ended, the marginOfError is reset to 0.35 and numberOfTurns to 0.
        marginOfError = 0.35;
        numberOfTurns = 0;
        System.out.print("Would you like to play again? ( Y / N ) ");
        String response = scan.next();
        return response.equalsIgnoreCase("y");
    }

    private static String determineMessage(double percentage) {
        String message = "";
        if (percentage >= 90) {
            message = "Superb marksmanship. Finely done.";
        } else if (percentage >= 75) {
            message = "Not extraordinary, but not quite mediocre either. Well done.";
        } else {
            message = "Well what the hell happened? Were those all meant to be warning shots?";
        }
        return message;
    }

}