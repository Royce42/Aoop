import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;

    public class NumberleView implements Observer {
      private final INumberleModel model;
      private final NumberleController controller;
      private final JFrame frame = new JFrame("Numberle");
      private JTextField inputTextField = new JTextField(7);
      private final JLabel attemptsLabel = new JLabel("Attempts remaining: ");
      private JLabel[][] cellLabels;
      private JButton[][] keypadButtons;
      private JButton delButton;
      private JButton Button1;
      private JButton Button2;
      private JButton Button3;
      private JButton Button4;
      private JButton Button5;
      private JButton enterButton;
      private String input;
      private JButton newGame;
      private int row = 0;
      private static final int BUTTON_FONT_SIZE = 22;
      private static final int LABEL_FONT_SIZE = 18;
      private static final int BUTTON_HEIGHT = 50;


      public NumberleView(INumberleModel model, NumberleController controller) {
        // Assign the controller and model passed as parameters to class variables
        this.controller = controller;
        this.model = model;

        // Initialize arrays for cell labels and keypad buttons
        this.cellLabels = new JLabel[6][7];
        this.keypadButtons = new JButton[1][10];

        // Start a new game using the controller
        this.controller.startNewGame();

        // Initialize input string
        this.input = "";

        // Register this view as an observer of the model
        ((NumberleModel) this.model).addObserver(this);

        // Initialize the frame
        initializeFrame();

        // Set this view as the view for the controller
        this.controller.setView(this);

        // Update the view with the initial state of the model
        update((NumberleModel) this.model, null);
      }


      public void initializeFrame() {
        frame.setBackground(new Color(242, 239, 228)); // Set the window background color
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Set the window to full-screen

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Set the window close operation to exit the program
        frame.setLayout(new BorderLayout()); // Use border layout manager

        Font buttonFont = new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE); // Set button font

        // Create top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setPreferredSize(new Dimension(1000, 450));

        // Create target number panel
        JPanel targetNumberPanel = new JPanel(new GridLayout(6, 7, 5, 5));
        targetNumberPanel.setPreferredSize(new Dimension(500, 400));

        // Populate target number panel with cell labels
        for (int row = 0; row < cellLabels.length; row++) {
          for (int col = 0; col < cellLabels[row].length; col++) {
            JPanel cell = new JPanel();
            JLabel label = new JLabel();
            cell.setPreferredSize(new Dimension(100, 100));
            cell.setBackground(new Color(222, 223, 233));
            cell.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            cell.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
            label.setFont(new Font("Arial", Font.BOLD, 40));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            cell.add(label);
            cellLabels[row][col] = label;
            targetNumberPanel.add(cell);
          }
        }

        // Create center panel
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setPreferredSize(new Dimension(800, 50));

        // Create keypad buttons
        keypadButtons = new JButton[1][10];
        JPanel keypadPanel = new JPanel(new GridLayout(1, 10, 10, 10));
        keypadPanel.setPreferredSize(new Dimension(800, 50));
        String[] keys = {
          "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
        };

        // Populate keypad panel with buttons
        for (int i = 0; i < keys.length; i++) {
          JButton button = new JButton(keys[i]);
          String key = keys[i];
          button.setEnabled(true);
          button.setBackground(new Color(242, 229, 208));
          button.setOpaque(true);
          button.setFont(buttonFont);
          button.setPreferredSize(new Dimension(40, 40));
          button.addActionListener(e -> {
            if (controller.getRemainingAttempts() <= 0) {
              JOptionPane.showMessageDialog(null, "Sorry! You've used up all your attempts! Please start a new game.");
              return;
            }
            if (controller.isGameOver()) {
              JOptionPane.showMessageDialog(null, "You've already won! Please start a new game.");
              return;
            }
            if (input.length() == 7) {
              JOptionPane.showMessageDialog(null, "Please enter a complete equation.");
              return;
            }
            int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
            cellLabels[row][input.length()].setText(key);
            input += key;
          });
          keypadButtons[i / 10][i % 10] = button;
          keypadPanel.add(button);
        }

        // Create bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(800, 125));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setPreferredSize(new Dimension(800, 125));
        Font labelFont = new Font("Arial", Font.BOLD, LABEL_FONT_SIZE);

        // Create attempts panel
        JPanel attemptsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        attemptsLabel.setText("Remaining Attempts: " + controller.getRemainingAttempts());
        attemptsLabel.setPreferredSize(new Dimension(200, 25));
        attemptsLabel.setFont(labelFont);
        attemptsPanel.add(attemptsLabel);

        // Create buttons for functions
        delButton = new JButton("Delete");
        delButton.addActionListener(e -> DelFunction());
        Button1 = new JButton("+");
        Button1.addActionListener(e -> Function1());
        Button2 = new JButton("-");
        Button2.addActionListener(e -> Function2());
        Button3 = new JButton("×");
        Button3.addActionListener(e -> Function3());
        Button4 = new JButton("÷");
        Button4.addActionListener(e -> Function4());
        Button5 = new JButton("=");
        Button5.addActionListener(e -> Function5());
        enterButton = new JButton("Enter");
        enterButton.addActionListener(e -> EnterFunction());

        // Set fonts and dimensions for buttons
        delButton.setFont(buttonFont);
        enterButton.setFont(buttonFont);
        Button1.setFont(buttonFont);
        Button1.setPreferredSize(new Dimension(40, 40));
        Button1.setBackground(new Color(167, 173, 192));
        Button2.setFont(buttonFont);
        Button2.setPreferredSize(new Dimension(40, 40));
        Button2.setBackground(new Color(167, 173, 192));
        Button3.setFont(buttonFont);
        Button3.setPreferredSize(new Dimension(40, 40));
        Button3.setBackground(new Color(167, 173, 192));
        Button4.setFont(buttonFont);
        Button4.setPreferredSize(new Dimension(40, 40));
        Button4.setBackground(new Color(167, 173, 192));
        Button5.setFont(buttonFont);
        Button5.setPreferredSize(new Dimension(40, 40));
        Button5.setBackground(new Color(167, 173, 192));
        delButton.setPreferredSize(new Dimension(60, 50));
        delButton.setBackground(new Color(167, 173, 192));
        enterButton.setPreferredSize(new Dimension(60, 50));
        enterButton.setBackground(new Color(167, 173, 192));

        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        // Add buttons to buttons panel
        buttonsPanel.add(delButton);
        buttonsPanel.add(Button1);
        buttonsPanel.add(Button2);
        buttonsPanel.add(Button3);
        buttonsPanel.add(Button4);
        buttonsPanel.add(Button5);
        buttonsPanel.add(enterButton);

        // Create new game button
        newGame = new JButton("New Game");
        newGame.setFont(buttonFont);
        newGame.setPreferredSize(new Dimension(160, 50));
        newGame.setBackground(new Color(105, 117, 142));
        newGame.setVisible(true);
        newGame.setEnabled(false);
        newGame.addActionListener(e -> resetGame());

        // Add components to bottom panel
        bottom.add(attemptsPanel, BorderLayout.CENTER);
        bottom.add(buttonsPanel, BorderLayout.NORTH);
        attemptsPanel.add(newGame, BorderLayout.EAST);

        // Add panels to frame
        topPanel.add(targetNumberPanel, BorderLayout.CENTER);
        centerPanel.add(keypadPanel);
        bottomPanel.add(bottom);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
      }


      @Override
      public void update(java.util.Observable o, Object arg) {
        attemptsLabel.setText("Attempts remaining: " + controller.getRemainingAttempts());
      }

      private void EnterFunction() {
        // Check if the input length is less than 7 characters
        if (input.length() < 7) {
          showMessage("Please input the full equation");
          return;
        }

        // Process the input and check if it's valid
        if (!controller.processInput(input)) {
          showMessage("Invalid input, please try again");
          return;
        }

        // Enable and show the new game button
        enableNewGameButton();

        // Update cell labels based on the current guess
        updateCellLabels();

        // Update key button colors
        updateKeyButtonsColor(input, controller.getCurrentGuess().toString());

        // Check if the game is over
        if (controller.getRemainingAttempts() <= 0 || controller.isGameWon()) {
          // Show appropriate message based on game result
          if (controller.isGameWon()) {
            showMessage("Congratulations! You Win!");
          } else {
            showMessage("Sorry! No attempts remaining!");
          }
          // Disable keypad after the game ends
          disableKeypad();
          return;
        }

        // Clear input and move to the next row
        clearInputAndMoveToNextRow();
      }

      /**
       * Displays a message using a JOptionPane dialog.
       *
       * @param message The message to be displayed
       */
      private void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
      }

      /**
       * Enables the visibility and functionality of the new game button.
       */
      private void enableNewGameButton() {
        newGame.setVisible(true);
        newGame.setEnabled(true);
      }

      /**
       * Updates the cell labels based on the current guess.
       */
      private void updateCellLabels() {
        // Get the current guess as a string
        String currentGuess = controller.getCurrentGuess().toString();

        // Iterate through the characters of the current guess
        for (int i = 0; i < currentGuess.length(); i++) {
          char status = currentGuess.charAt(i); // Get the status character
          JLabel label = cellLabels[row][i]; // Get the corresponding label
          JPanel cellPanel = (JPanel) label.getParent(); // Get the parent panel

          // Update the background color based on the status character
          switch (status) {
            case 'g':
              cellPanel.setBackground(new Color(2, 252, 26)); // Green
              break;
            case 'o':
              cellPanel.setBackground(new Color(247, 154, 111)); // Orange
              break;
            case 'r':
              cellPanel.setBackground(new Color(164, 174, 196)); // Gray
              break;
            default:
              break;
          }
        }
      }

      /**
       * Clears the input string and moves to the next row by resetting cell labels.
       */
      private void clearInputAndMoveToNextRow() {
        input = ""; // Clear the input string
        row++; // Move to the next row

        // Reset cell labels by setting background color to white and text to empty
        for (int i = 0; i < 7; i++) {
          cellLabels[row][i].setBackground(Color.WHITE);
          cellLabels[row][i].setText("");
        }
      }

      /**
       * Function to handle the addition button.
       * It adds the "+" symbol to the input string and updates the corresponding cell label.
       */
      private void Function1() {
        // Calculate the current row
        int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        // Set the label text to the addition symbol
        cellLabels[row][input.length()].setText("+");
        // Add the addition symbol to the input string
        input += "+";
      }

      /**
       * Function to handle the subtraction button.
       * It adds the "-" symbol to the input string and updates the corresponding cell label.
       */
      private void Function2() {
        // Calculate the current row
        int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        // Set the label text to the subtraction symbol
        cellLabels[row][input.length()].setText("-");
        // Add the subtraction symbol to the input string
        input += "-";
      }

      /**
       * Function to handle the multiplication button.
       * It adds the "×" symbol to the input string and updates the corresponding cell label.
       */
      private void Function3() {
        // Calculate the current row
        int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        // Set the label text to the multiplication symbol
        cellLabels[row][input.length()].setText("×");
        // Add the multiplication symbol to the input string
        input += "×";
      }

      /**
       * Function to handle the division button.
       * It adds the "÷" symbol to the input string and updates the corresponding cell label.
       */
      private void Function4() {
        // Calculate the current row
        int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        // Set the label text to the division symbol
        cellLabels[row][input.length()].setText("÷");
        // Add the division symbol to the input string
        input += "÷";
      }

      /**
       * Function to handle the equality button.
       * It adds the "=" symbol to the input string and updates the corresponding cell label.
       */
      private void Function5() {
        // Calculate the current row
        int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        // Set the label text to the equality symbol
        cellLabels[row][input.length()].setText("=");
        // Add the equality symbol to the input string
        input += "=";
      }

      private void DelFunction() {
        // Check if the game is over or won
        if (controller.getRemainingAttempts() <= 0 || controller.isGameWon()) {
          showMessage("Game over. Please start a new game.");
          disableKeypad(); // Disable keypad when the game is over
          return;
        }

        // Check if the input is empty
        if (input.isEmpty()) {
          showMessage("No input! Please input something.");
          return;
        }

        // Remove the last character from the input
        input = input.substring(0, input.length() - 1);

        // Update the corresponding cell label
        int row = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        cellLabels[row][input.length()].setText("");

        // If the game is won, notify the player
        if (controller.isGameWon()) {
          showMessage("You have won! Please start a new game.");
        }
      }


      private void updateKeyButtonsColor(String input, String currentGuess) {
        // Ensure that currentGuess and input have the same length
        if (currentGuess.length() < input.length()) {
          throw new IllegalArgumentException("Current guess string length does not match the input length.");
        }

        // Update key buttons color based on the current guess
        for (int i = 0; i < input.length(); i++) {
          char keyChar = input.charAt(i);
          char status = currentGuess.charAt(i);
          updateButtonColor(keyChar, status);
        }
      }

      private void updateButtonColor(char keyChar, char status) {
        for (JButton[] buttonRow : keypadButtons) {
          for (JButton button : buttonRow) {
            if (button != null && button.getText().equals(String.valueOf(keyChar))) {
              Color color = getColorForStatus(status);
              button.setBackground(color);
            }
          }
        }

        // Also update the color for the additional buttons
        JButton[] additionalButtons = {Button1, Button2, Button3, Button4, Button5};
        for (JButton button : additionalButtons) {
          if (button != null && button.getText().equals(String.valueOf(keyChar))) {
            Color color = getColorForStatus(status);
            button.setBackground(color);
          }
        }
      }

      /**
       * Get color based on status.
       *
       * @param status The status represented by a character.
       * @return The color corresponding to the status.
       */

      private Color getColorForStatus(char status) {
        switch (status) {
          case 'g':
            return new Color(2, 252, 26);
          case 'o':
            return new Color(247, 154, 111);
          case 'r':
            return new Color(164, 174, 196);
          default:
            return UIManager.getColor("Button.background");
        }
      }


      private void resetGame() {
        // Reset the game state using the model's method
        controller.startNewGame();

        // Reset the text and color of the interface
        for (JLabel[] labelRow : cellLabels) {
          for (JLabel label : labelRow) {
            label.setText("");
            label.setBackground(Color.WHITE);
            label.getParent().setBackground(Color.WHITE);
            label.getParent().repaint();
          }
        }

        // Reset the color of keypad buttons
        for (JButton[] buttonRow : keypadButtons) {
          for (JButton button : buttonRow) {
            if (button != null) {
              button.setBackground(new Color(242, 229, 208));
            }
          }
        }

        // Reset the color of additional buttons
        JButton[] additionalButtons = {Button1, Button2, Button3, Button4, Button5};
        for (JButton button : additionalButtons) {
          button.setBackground(new Color(167, 173, 192));
        }

        // Hide the newGame button until the next round ends
        newGame.setVisible(true);
        newGame.setEnabled(false);

        // Reset input string and current row index
        input = "";
        row = 0;

        // Update other components of the interface, such as attempt count
        update((NumberleModel) model, null);

        // Ensure all buttons are enabled
        enableKeypad();

        // Force interface refresh
        frame.repaint();
      }


      /**
       * Enable or disable all keypad buttons.
       *
       * @param enabled If true, enable all keypad buttons; otherwise, disable them.
       */
      private void setKeypadButtonsEnabled(boolean enabled) {
        for (JButton[] buttonRow : keypadButtons) {
          for (JButton button : buttonRow) {
            if (button != null) {
              button.setEnabled(enabled);
            }
          }
        }
        // Enable or disable additional buttons
        JButton[] additionalButtons = {delButton, Button1, Button2, Button3, Button4, Button5, enterButton};
        for (JButton button : additionalButtons) {
          if (button != null) {
            button.setEnabled(enabled);
          }
        }
      }

      /**
       * Enable all keypad buttons.
       */
      private void enableKeypad() {
        setKeypadButtonsEnabled(true);
      }

      /**
       * Disable all keypad buttons.
       */
      private void disableKeypad() {
        setKeypadButtonsEnabled(false);
      }
    }
