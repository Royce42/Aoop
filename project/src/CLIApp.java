import java.util.Scanner;

public class CLIApp {
  private NumberleModel numberleModel;
  private Scanner scanner;
  private Scanner guess;

  public CLIApp() {
    numberleModel = new NumberleModel(false, true, true);
    scanner = new Scanner(System.in);
  }

  public void start() {
    System.out.println("Welcome to the Numberle Game!");

    numberleModel.initialize();

    // 列出可用的数字和符号
    System.out.println("Available digits and signs:");
    System.out.println("Digits: 0,1,2,3,4,5,6,7,8,9");
    System.out.println("Signs: +, -, *, /");

    while (!numberleModel.isGameOver()) {
      System.out.print("Enter your guess: ");
      String input = scanner.nextLine();
      System.out.println("Remaining attempts: " + numberleModel.getRemainingAttempts());
      System.out.println("Current guess: " + input);




      boolean isValidInput = numberleModel.processInput(input);
      if (!isValidInput) {
        System.out.println("Invalid input format. Please try again.");
      }

      if (numberleModel.isGameWon()) {
        System.out.println("Congratulations! You guessed the correct number.");
        break;
      }

      if (numberleModel.getRemainingAttempts() == 0) {
        System.out.println("Game over! You ran out of attempts.");
        break;
      }
    }

    scanner.close();
  }


  public static void main(String[] args) {
    CLIApp numberleCLI = new CLIApp();
    numberleCLI.start();
  }
}
