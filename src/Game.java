import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;


public class Game {
    private static int playerWins = 0;
    private static int computerWins = 0;

    public static void main(String[] args) {
        // playGame returns false if the user chooses to play no additional rounds, stopping the while loop
        while (playGame());

        // These messages are displayed once the user chooses not to play any additional rounds
        System.out.println("\nPlayer wins: " + playerWins + "\nComputer wins: " + computerWins);
        System.out.println("You won " + roundTwoPlaces(playerWins / (playerWins + computerWins)) * 100 + "% of the games played.");
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

    private static double roundTwoPlaces(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(number));
    }

    // The computer will fire a shell at a random angle and speed; it will return false if it strikes its target
    private static boolean computerTurn(int[] enemyRange) {
        Scanner scan = new Scanner(System.in);
        String degreesSign = "\u00B0";
        int angleMin = 1;
        int angleMax = 89;
        int speedMin = 20;
        int speedMax = 100;
        int angle = generateRandom(angleMin, angleMax);
        int speed = generateRandom(speedMin, speedMax);

        System.out.println("\nEnemy fired at " + angle + degreesSign + " at " + speed + " m/s.");

        double shellDistance = getShellDistance(angle, speed);
        shellDistance = roundTwoPlaces(shellDistance);

        int enemyDistance = (enemyRange[0] + enemyRange[1]) / 2;

        double distanceFromPosition;
        System.out.println("Its shell traveled " + shellDistance + " meters.");
        if (shellDistance < enemyDistance) {
            distanceFromPosition = roundTwoPlaces(enemyDistance - shellDistance);
            System.out.print("Shell exploded " + distanceFromPosition + " meters from your position. (press any key to continue)");

            scan.nextLine();
        } else if (shellDistance > enemyDistance) {
            distanceFromPosition = roundTwoPlaces(shellDistance - enemyDistance);
            System.out.print("Shell exploded " + distanceFromPosition + " meters behind your position. (press any key to continue)");
            scan.nextLine();
        }

        if (shellDistance >= enemyRange[0] && shellDistance <= enemyRange[1]) {
            System.out.println("Your artillery was destroyed.");
            computerWins++;
            return false;
        }
        return true;
    }

    // The player will enter an angle and speed at which to fire a shell; it will return false if the player wins
    private static boolean playerTurn(int[] enemyRange) {
        Scanner scan = new Scanner(System.in);
        int enemyDistance = (enemyRange[0] + enemyRange[1]) / 2;

        System.out.println("\nEnemy artillery is at approximately " + enemyDistance + " meter(s).");
        System.out.println("\nEnemy range is between " + enemyRange[0] + " meters(s) and " + enemyRange[1] + " meter(s).");

        System.out.print("Enter an angle in degrees: ");
        double angle = Double.parseDouble(scan.nextLine());
        System.out.print("Enter a speed: ");
        double speed = Double.parseDouble(scan.nextLine());

        double shellDistance = getShellDistance(angle, speed);
        shellDistance = roundTwoPlaces(shellDistance);

        System.out.print("\nShell exploded at " + shellDistance + " meters. (press any key to continue)");
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
            else if (!computerTurn(enemyRange)) { break; }
        }

        System.out.print("Would you like to play again? ( Y / N ) ");
        String response = scan.next();
        return response.equalsIgnoreCase("y");

    }

}