import javax.swing.*;

public class GUIApp {
  public static void main(String[] args) {

    javax.swing.SwingUtilities.invokeLater(
      new Runnable() {
        public void run() {
          createAndShowGUI();
        }
      }
    );
  }

  public static void createAndShowGUI() {
    INumberleModel model = new NumberleModel(true,true,true);
//        System.out.println(model.getCurrentGuess()+"_");
    NumberleController controller = new NumberleController(model);
    NumberleView view = new NumberleView(model, controller);
  }
}
