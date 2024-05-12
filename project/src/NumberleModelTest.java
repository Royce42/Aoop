import static org.junit.Assert.*;
import org.junit.Test;

public class NumberleModelTest {

  @Test
  // Scenario: Test processing of a correct input
  public void testCorrectInputProcessing() {
    INumberleModel model = new NumberleModel(false,true,false);
    model.initialize();
    // Given a correct input
    String input = "1+1+1=3";

    // When processing the input
    boolean result = model.processInput(input);

    // Then the input should be processed successfully
    assertTrue(result);
    // And the game should be won
    assertTrue(model.isGameWon());
  }

  @Test
  // Scenario: Test processing of incorrect positions
  public void testIncorrectPositionsProcessing() {
    INumberleModel model = new NumberleModel(false,true,true);
    model.initialize();
    // Given an input with incorrect positions
    String input = "4+1+1=3";

    // When processing the input
    boolean result = model.processInput(input);

    // Then the input should be processed successfully
    assertTrue(result);
    // And the current guess should contain incorrect positions
    assertTrue(model.getCurrentGuess().toString().contains("r"));
  }


  @Test
  // Scenario: Test processing of correct positions
  public void testCorrectPositionsProcessing() {
    INumberleModel model = new NumberleModel(false,true,true);
    model.initialize();
    // Given an input with correct positions
    String input = "g+1+g=g";

    // When processing the input
    boolean result = model.processInput(input);

    // Then the input should be processed successfully
    assertTrue(result);
    // And the current guess should contain correct positions
    assertTrue(model.getCurrentGuess().toString().contains("g"));
  }

}
