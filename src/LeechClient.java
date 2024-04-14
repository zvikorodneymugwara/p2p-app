
/**
 * @author MUGWARA ZR 220072437
 *
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.StringTokenizer;

//class downloads the files
public class LeechClient{

  private static DatagramSocket ds;
  private InetAddress ip;
  
  //default port to send the data over when in leech mode
  private static final int DEFAULT_PORT = 4321;
  
  //client socket will run on default port
  public LeechClient() {
    try {
      ds = new DatagramSocket(DEFAULT_PORT);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  //second constructor for when the client will run on a specified port
  public LeechClient(int portNum) {
    try {
      ds = new DatagramSocket(portNum);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  /**
   * main method to run the client server
   * @param args
   */
  public static void main(String[] args) {
    boolean running = true;
    //create a nuew leech client
    LeechClient leecher = new LeechClient();
    System.out.println("Leecher Server Started...");
    //run the server
    while (running) {
      try {
    	  //recieve the initial command
        byte[] data = new byte[2048];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        leecher.getSocket().receive(packet);
        int senderPortNum = packet.getPort();	//take the seeders port number
        
        //get the data in the message
        //tokenizer used when the Leecher is recieveing the command and the File List together
        String message = new String(packet.getData());
        StringTokenizer responseToken = new StringTokenizer(message.trim());
        String request = responseToken.nextToken();
        
        String str = message.replace(request, "");	//remove the request and keep the file list
        System.out.println(request);
        
        //request can be FILELIST or the integer ID number of the requested file
        if (request.equals("FILELIST")) {
          System.out.println(
            "Response from Leecher -> The Recieved File List:\n" +
            str.substring(1)
          );
        //download the file of the corresponding file id
        } else if (Integer.parseInt(request) > -1) {
          downloadFile(request, senderPortNum);
        } else {
          System.err.println("Unknown Request!");
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * method to connect the client to a port.
   * @param portNum the port number
   * @param hostName the host name
   * @return The returned string will be used in the GUI prompt.
   */
  public String connectToPort(int portNum, String hostName) {
    try {
      ds = new DatagramSocket(portNum);
      this.ip = InetAddress.getByName(hostName);
      return "Connected to " + hostName + " on port " + portNum;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "Unable to Connect to port " + portNum;
  }

  /**
   * method to download the file.
   * @param fileId id of requested file
   * @param senderPortNum the port number of the sender
   * @return returns a string to show the status of the request in the GUI
   */
  public static String downloadFile(String fileId, int senderPortNum) {
    System.out.println("Sending Request...");
    String status = "File Not Recieved";

    try {
      //requesting the file
      InetAddress ip = InetAddress.getByName("localhost");
      String request = fileId;
      DatagramPacket packet = new DatagramPacket(
        request.getBytes(),
        request.getBytes().length,
        ip,
        senderPortNum
      );
      ds.send(packet);
      System.out.println("Request Sent!");

      //Receiving the filename first. Filename will be used in the FileOutputStream.
      byte[] fnameData = new byte[2048];
      DatagramPacket recievedNamePacket = new DatagramPacket(
        fnameData,
        fnameData.length
      );
      ds.receive(recievedNamePacket);
      String fname = new String(recievedNamePacket.getData()).trim();
      
      //Receiving the file
      byte[] data = new byte[2048];
      DatagramPacket recievedPacket = new DatagramPacket(data, data.length);

      FileOutputStream fos = new FileOutputStream(
        "data/leecher files/" + fname
      );

      while (true) {
        ds.receive(recievedPacket);
        System.out.println("Recieving File");

        data = recievedPacket.getData();

        // Check for the end of the file (assuming the last packet is empty)
        if (recievedPacket.getLength() == 0) {
          System.out.println("File received successfully.");
          status = "File received successfully";
          break;
        }
        // Write the received data to the file
        fos.write(data, 0, recievedPacket.getLength());
      }
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return status;
  }

  /**
   * gets the list of files available in the directory
   * @param senderPortNum the port number of the sender
   * @return the lsit of files. this will be used in the GUI
   */
  public String getFileList(int senderPortNum) {
    System.out.println("Sending Request...");
    String flist = "Unable to get File List";
    try (DatagramSocket socket = new DatagramSocket();) {
      InetAddress ip = InetAddress.getByName("localhost");

      //send the request
      String request = "FILELIST";
      DatagramPacket packet = new DatagramPacket(
        request.getBytes(),
        request.getBytes().length,
        ip,
        senderPortNum
      );
      socket.send(packet);

      //Receive the file list
      byte[] data = new byte[2048];
      DatagramPacket recievedPacket = new DatagramPacket(data, data.length);
      socket.receive(recievedPacket);
      System.out.println("File List Recieved");
      return new String(recievedPacket.getData());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return flist;
  }

  /**
   * get the client's socket
   * @return the client's socket
   */
  public DatagramSocket getSocket() {
    return ds;
  }

  /**
   * get the IP of the client
   * @return the IP of the client
   */
  public InetAddress getIp() {
    return ip;
  }
}
