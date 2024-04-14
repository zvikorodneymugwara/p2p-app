
/**
 * @author MUGWARA ZR 220072437
 *
 */
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
	  SeedMode sm;
	  LeechMode lm;
	  Scene scene;
	  boolean run = true;
	  while(run) {		  
		  System.out.println("Enter S for Seed Mode, L for Leech Mode, X to Exit");
		  try (Scanner scan = new Scanner(System.in)) {
			String userInput = scan.nextLine();
			  switch(userInput) {
			  case "S","s":{
				  sm = new SeedMode(primaryStage);
				    scene = new Scene(sm.getVbox(), 300, 400);
				    primaryStage.setScene(scene);
				    run = false;
				  break;			  
			  }
			  case "L","l":{
				  lm = new LeechMode(primaryStage);
				    scene = new Scene(lm.getVbox(), 300, 400);
				    primaryStage.setScene(scene);
				    run = false;
				  break;			  
			  }
			  case "X","x":{
				  break;
			  }
			  default:{
				  System.err.println("Invalid Input");
				  break;
			  }
			  }
		}
	  }
    primaryStage.show();
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    launch(args);
  }
}