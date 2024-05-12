// NumberleModel.java
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class NumberleModel extends Observable implements INumberleModel {
  private String targetNumber;
  private StringBuilder currentGuess;
  private int remainingAttempts;
  private boolean gameWon;
  private StringBuilder current;
  private boolean showErrorMassage;
  private boolean testPurpose;
  private boolean selectEquationsRandomly;

  private Set<Character> unused = new HashSet<>();
  private Set<Character> correctPosition = new HashSet<>();
  private Set<Character> incorrect = new HashSet<>();
  private Set<Character> incorrectPositions = new HashSet<>();

  private String input;


  //    private List<Observer> observers;
  public NumberleModel(boolean showErrorMassage, boolean testPurpose, boolean selectEquationsRandomly){
    this.showErrorMassage = showErrorMassage;
    this.testPurpose = testPurpose;
    this.selectEquationsRandomly = selectEquationsRandomly;
  }
  @Override
  public void initialize() {
      if (showErrorMassage) {
        loadEquations();
      } else {
        targetNumber = "1+1+1=3";
      }

      if (testPurpose) {
        System.out.println("Target Number: " + targetNumber);
      }


      currentGuess = new StringBuilder(targetNumber.length());


     for (int i = 0; i < targetNumber.length(); i++) {
        currentGuess.append(" ");
      }

      remainingAttempts = MAX_ATTEMPTS;
      gameWon = false;
      setChanged();
    notifyObservers();
    }


  @Override
  public boolean processInput(String input) {
    for (char digit = '0'; digit <= '9'; digit++) {
      unused.add(digit);
    }
    unused.add('+');
    unused.add('-');
    unused.add('*');
    unused.add('/');
    unused.add('=');
    String sanitizedInput = input.replace('×', '*').replace('÷', '/').replaceAll("\\s+", "");

    if(showErrorMassage){
      if(sanitizedInput.length() != targetNumber.length()){
        System.out.println("Invalid input format.");
        return false;
      }
      if(!validateInput(sanitizedInput)){
        System.out.println("Invalid input format.");
        return false;
      }
    }


    for(int i = 0; i<targetNumber.length(); i++){
      if(sanitizedInput.charAt(i) == targetNumber.charAt(i)){
        currentGuess.setCharAt(i,'g');
        correctPosition.add(sanitizedInput.charAt(i));
        unused.remove(sanitizedInput.charAt(i));
      } else if (targetNumber.contains(String.valueOf(sanitizedInput.charAt(i)))) {
        currentGuess.setCharAt(i,'o');
        incorrectPositions.add(sanitizedInput.charAt(i));
        unused.remove(sanitizedInput.charAt(i));
      }else {
        currentGuess.setCharAt(i,'r');
        incorrect.add(sanitizedInput.charAt(i));
        unused.remove(sanitizedInput.charAt(i));
      }
    }

    System.out.println(correctPosition);
    System.out.println(incorrect);
    System.out.println(incorrectPositions);
    System.out.println(unused);
    remainingAttempts--;


      if (targetNumber.equals(sanitizedInput)) {
        gameWon = true;
      }
      setChanged();
      notifyObservers();
      return true;
  }

  @Override
  public boolean isGameOver() {
    return remainingAttempts <= 0 || gameWon;
  }

  @Override
  public boolean isGameWon() {
    return gameWon;
  }

  @Override
  public String getTargetNumber() {
    return targetNumber;
  }

  @Override
  public StringBuilder getCurrentGuess() {
    return currentGuess;
  }



  @Override
  public int getRemainingAttempts() {
    return remainingAttempts;
  }

  @Override
  public void startNewGame() {
    initialize();
  }




  private void loadEquations() {
    try {
      // Open the file
      BufferedReader reader = new BufferedReader(new FileReader("equations.txt"));

      // Create a list to store equations
      List<String> allEquations = new ArrayList<>();

      // Read each line from the file
      String line;
      while ((line = reader.readLine()) != null) {
        // Add the line (equation) to the list
        allEquations.add(line);
      }

      // Close the reader
      reader.close();

      // If the file is not empty
      if (!allEquations.isEmpty()) {
        // Create a Random object
        Random rand = new Random();

        // Choose a random equation from the list and set it as the targetNumber
        targetNumber = allEquations.get(rand.nextInt(allEquations.size()));
      } else {
        // If the file is empty or doesn't exist, print an error message
        System.err.println("equations.txt is empty or not existing.");
      }
    } catch (IOException e) {
      // If an IOException occurs (e.g., file not found), print the stack trace and an error message
      e.printStackTrace();
      System.err.println("Failed to read equations.txt");
    }
  }



  public boolean validateInput(String input) {
    String[] parts = input.split("=");
    if (parts.length != 2) {
      return parts.length == 2 && validateExpression(parts[0]) && validateExpression(parts[1]);
    }
    try {
      double leftValue = evaluateInput(parts[0]);
      double rightValue = evaluateInput(parts[1]);
      return Math.abs(leftValue - rightValue) < 0.000001;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean validateExpression(String expression) {
    // Count of open and close parentheses
    int openParentheses = 0;
    int closeParentheses = 0;

    // Iterate through each character in the expression
    for (char c : expression.toCharArray()) {
      if (!Character.isDigit(c) && !isOperator(c) && c != '(' && c != ')') {
        // Character is neither a digit, operator, nor parentheses
        return false;
      }
      if (c == '(') {
        // Increment count of open parentheses
        openParentheses++;
      } else if (c == ')') {
        // Increment count of close parentheses
        closeParentheses++;
      }
      if (closeParentheses > openParentheses) {
        // More close parentheses than open parentheses
        return false;
      }
    }

    // Check if parentheses are balanced
    return openParentheses == closeParentheses;
  }

  // Check if character is an operator
  private boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/';
  }


  private double evaluateInput(String input) {
    // Stacks to store numbers and operators
    Stack<Double> numbers = new Stack<>();
    Stack<Character> operators = new Stack<>();

    // Remove spaces from the input string
    input = input.replaceAll("\\s+", "");

    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (Character.isDigit(c)) {
        // Read a number
        StringBuilder sb = new StringBuilder();
        while (i < input.length() && (Character.isDigit(input.charAt(i)) || input.charAt(i) == '.')) {
          sb.append(input.charAt(i++));
        }
        i--;
        numbers.push(Double.parseDouble(sb.toString()));
      } else if (c == '(') {
        // Handle parentheses
        operators.push(c);
      } else if (c == ')') {
        // Calculate the expression within parentheses
        while (operators.peek() != '(') {
          evaluate(numbers, operators);
        }
        operators.pop(); // Pop the left parenthesis
      } else if (c == '+' || c == '-' || c == '*' || c == '/') {
        // Handle operators
        while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
          evaluate(numbers, operators);
        }
        operators.push(c);
      }
    }

    // Process remaining operators
    while (!operators.isEmpty()) {
      evaluate(numbers, operators);
    }

    // Return the result
    return numbers.pop();
  }

  // Evaluate the top two numbers on the stack with the top operator and push the result back
  private void evaluate(Stack<Double> numbers, Stack<Character> operators) {
    double b = numbers.pop();
    double a = numbers.pop();
    char op = operators.pop();
    switch (op) {
      case '+':
        numbers.push(a + b);
        break;
      case '-':
        numbers.push(a - b);
        break;
      case '*':
        numbers.push(a * b);
        break;
      case '/':
        numbers.push(a / b);
        break;
    }
  }

  // Return the precedence of an operator
  private int precedence(char op) {
    if (op == '+' || op == '-') {
      return 1;
    } else if (op == '*' || op == '/') {
      return 2;
    }
    return 0;
  }
}
