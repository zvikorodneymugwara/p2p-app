
/**
 * @author MUGWARA ZR 220072437
 *
 */
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;


import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//seed mode uploads/sends the files
public class SeedMode extends Pane {

  private VBox vbox;
  private SeedClient client;
  private Stage mainStage;

  private Button getFilelistBtn;
  private Button addFileBtn;
  private Button sendFileListBtn;
  private Button sendFileBtn;

  private TextField fileIDTxt;
  private TextArea listOfFilesTxt;

  private Label listOfFilesLbl;
  private Label fileIDLbl;
  File chosenFile;

  /**
   * Constructor
   * @param stage the main stage
   */
  public SeedMode(Stage stage) {
	mainStage = stage;
	
	//Initialize the variables
	stage.setTitle("Seed Mode");
    client = new SeedClient(6524);
    vbox = new VBox();

    addFileBtn = new Button("Add a File");
    sendFileListBtn = new Button("Send Files List");
    sendFileBtn = new Button("Send File");
    getFilelistBtn = new Button("Get File List");

    listOfFilesLbl = new Label("List of Available Files");
    fileIDLbl = new Label("Enter File ID:");
    listOfFilesTxt = new TextArea();
    fileIDTxt = new TextField();

    //when clicking the get file list button
    getFilelistBtn.setOnAction(e -> {
      showFilesList(null);	//call the showFileList method
    });

    //when clicking the add file button
    addFileBtn.setOnAction(e -> {
      FileChooser chooser = new FileChooser();
      File f = new File("data/seeder files");
      chooser.setInitialDirectory(f);	//set initial directory of FileChooser
      chosenFile = chooser.showOpenDialog(mainStage);	//get the file
      showFilesList(chosenFile); //add the chosen file to the file list
    });

    //when clicking the send file list button
    sendFileListBtn.setOnAction(e -> {
      try {
        //send the request
        String request = "FILELIST " + showFilesList(chosenFile);
        DatagramPacket packet = new DatagramPacket(
          request.getBytes(),
          request.getBytes().length,
          client.getIPAddress(),
          4321
        );
        client.getSocket().send(packet);
        
        //prompt the status of the request
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Status");
        alert.setHeaderText("File List Sent");
        alert.setContentText("File List Sent Sucessfully!");
        alert.showAndWait();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    });

    //when clicking the send file button 
    sendFileBtn.setOnAction(e -> {
    	//get the directory and list the files
      File dir = new File("data/seeder files");
      File[] listOfFiles = dir.listFiles();
      
      //if there's no chosenFile
      if (chosenFile == null) {
    	  //select the file to send from the list of files
        File fileToSend = listOfFiles[Integer.parseInt(fileIDTxt.getText())];
        try {
        	//send the id of the file that we are sending
          DatagramPacket id = new DatagramPacket(
            fileIDTxt.getText().getBytes(),
            fileIDTxt.getText().getBytes().length,
            client.getIPAddress(),
            4321	//4321 is the default port number of the Leecher when in SeedMode
          );
          client.getSocket().send(id);

          //send the filename first
          DatagramPacket fname = new DatagramPacket(
            fileToSend.getName().getBytes(),
            fileToSend.getName().getBytes().length,
            client.getIPAddress(),
            4321
          );
          client.getSocket().send(fname);

          //send the file itself
          DatagramPacket packet = SeedClient.sendFile(
            fileIDTxt.getText(),
            client.getIPAddress(),
            4321
          );
          client.getSocket().send(packet);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
        //if there is a chosen file to send
      } else if (
        chosenFile != null &&
        Integer.parseInt(fileIDTxt.getText()) == listOfFiles.length
      ) {
        try {
        	//send the id of the file that we are sending
          DatagramPacket id = new DatagramPacket(
            fileIDTxt.getText().getBytes(),
            fileIDTxt.getText().getBytes().length,
            client.getIPAddress(),
            4321
          );
          client.getSocket().send(id);

          //send the filename first
          DatagramPacket fname = new DatagramPacket(
            chosenFile.getName().getBytes(),
            chosenFile.getName().getBytes().length,
            client.getIPAddress(),
            4321
          );
          client.getSocket().send(fname);

          //send the file itself
          DatagramPacket packet = SeedClient.sendParticularFile(
            chosenFile,
            client.getIPAddress(),
            4321
          );
          client.getSocket().send(packet);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }

      //prompt to indicate the state of the request
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Status");
      alert.setHeaderText("File Sent");
      alert.setContentText("File Sent Sucessfully!");
      alert.showAndWait();
    });

    //add all nodes to the vbox
    vbox
      .getChildren()
      .addAll(
        listOfFilesLbl,
        getFilelistBtn,
        listOfFilesTxt,
        addFileBtn,
        sendFileListBtn,
        fileIDLbl,
        fileIDTxt,
        sendFileBtn
      );

    //set vbox padding
    vbox.setPadding(new Insets(20, 20, 20, 20));
  }

  /**
   * getter for vbox
   * @return the GUI's vbox
   */
  public VBox getVbox() {
    return this.vbox;
  }

  /**
   * method to show the file list
   * @param addedFile if theres and added file, show it in the list
   * @return the list of files
   */
  private String showFilesList(File addedFile) {
	  //get the directory and list the files
    File dir = new File("data/seeder files");
    File[] listOfFiles = dir.listFiles();
    
    String list = "";	//the file list to return
    int counter = 0;	//keeps track of the file id
    //for each file
    for (File f : listOfFiles) {
      //if the file is not a directory
      if (!f.isDirectory()) {
    	//add the file to the file list
        list +=
          ("ID: " + "(" + counter + ")" + ", File: " + f.getName() + "\n");
        counter += 1;
      }
    }
    
    //if there is an added file
    if (addedFile != null) {
      //add that file to the file list
      list +=
        (
          "ID: " +
          "(" +
          counter +
          ")" +
          ", File: " +
          addedFile.getName() +
          ", Path: " +
          addedFile.getAbsolutePath() +
          "\n"
        );
      fileIDTxt.setText("" + counter);
    }
    listOfFilesTxt.setText(list);
    return list;
  }
}
