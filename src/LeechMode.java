
/**
 * @author MUGWARA ZR 220072437
 *
 */

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LeechMode extends Pane {

  private VBox vbox;
  private Button downloadBtn;
  private Button requestFileListBtn;
  private Button connectBtn;
  private LeechClient client;

  private Label hostAddressLbl;
  private Label portNumLbl;
  private Label fileListLbl;
  private Label fileIDLbl;

  private TextField fileIDTxt;
  private TextArea fileListTxt;
  private TextField hostAddressTxt;
  private TextField portNumTxt;

  /**
   * Constructor
   * @param stage the main stage
   */
  public LeechMode(Stage stage) {
	stage.setTitle("Leech Mode");
	
	//initialize variables
    vbox = new VBox();
    client = new LeechClient();
    
    downloadBtn = new Button("Download File");
    requestFileListBtn = new Button("Request File List");
    connectBtn = new Button("Connect to Host");

    hostAddressLbl = new Label("Enter Host Address");
    portNumLbl = new Label("Enter Port Number");
    fileListLbl = new Label("List of Files");
    fileIDLbl = new Label("Enter File ID:");

    fileIDTxt = new TextField();
    fileListTxt = new TextArea();
    hostAddressTxt = new TextField();
    portNumTxt = new TextField();

    //when clicking the connect button
    connectBtn.setOnAction(e -> {
    	//call the client's connectToPort method
      String str = client.connectToPort(
        Integer.parseInt(portNumTxt.getText()),
        hostAddressTxt.getText()
      );
      //show a prompt of the result of this action
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Connection Status");
      alert.setHeaderText("Connection Status");
      alert.setContentText(str);
      alert.showAndWait();
    });

    //when clicking the download button
    downloadBtn.setOnAction(e -> {
    	//download the file
      String str = LeechClient.downloadFile(fileIDTxt.getText(), 1234);
      //show the prompt
      Alert alert = new Alert(AlertType.INFORMATION);
      alert.setTitle("Download Status");
      alert.setHeaderText("Downlad Status");
      alert.setContentText(str);
      alert.showAndWait();
    });

    //when clicking the request file list button
    requestFileListBtn.setOnAction(e -> {
    	//call the getFileList method of the client
      fileListTxt.setText(client.getFileList(1234));
    });

    //add all nodes to the vbox
    vbox
      .getChildren()
      .addAll(
        hostAddressLbl,
        hostAddressTxt,
        portNumLbl,
        portNumTxt,
        connectBtn,
        fileListLbl,
        fileListTxt,
        requestFileListBtn,
        fileIDLbl,
        fileIDTxt,
        downloadBtn
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
}
