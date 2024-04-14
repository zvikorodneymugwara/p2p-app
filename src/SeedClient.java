
/**
 * @author MUGWARA ZR 220072437
 *
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//class helps send the files
public class SeedClient {

  private DatagramSocket socket = null;
  private InetAddress ipAddress;
  private static final int DEFAULT_PORT = 1234;
  
  /**
   * get the socket of the seeder
   * @return the socket of the seeder
   */
  public DatagramSocket getSocket() {
    return socket;
  }
  
  /**
   * get the ip of the seeder
   * @return the ip of the seeder
   */
  public InetAddress getIPAddress() {
    return ipAddress;
  }

  /**
   * Constructor
   * @param port the port number for the seeder
   */
  public SeedClient(int port) {
    try {
      socket = new DatagramSocket(port);
      ipAddress = InetAddress.getByName("localhost");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * main method for the seeder to run the server
   * @param args
   */
  public static void main(String[] args) {
    boolean running = true;
    //create hte seeder on the default port
    SeedClient seeder = new SeedClient(DEFAULT_PORT);
    System.out.println("Seeder Server Started...");
    //while the seeder is running
    while (running) {
      try {
    	//recieve the initial request
        byte[] data = new byte[2048];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        seeder.getSocket().receive(packet);
        int senderPortNumber = packet.getPort();	//get the leecher's port number
        
        //get the data from the request
        String message = new String(packet.getData());
        String request = message.trim();
        System.out.println(request);

        if (request.equals("FILELIST")) {
          seeder.getSocket().send(sendFileList(seeder.getIPAddress(), senderPortNumber));
        } else if (Integer.parseInt(request) > -1) {
          //send the filename
          String fname = getFileName(Integer.parseInt(request));
          seeder
            .getSocket()
            .send(
              new DatagramPacket(
                fname.getBytes(),
                fname.getBytes().length,
                seeder.getIPAddress(),
                senderPortNumber
              )
            );

          //send the actual file
          System.out.println("Sending file ID: " + request);
          DatagramPacket packetToSend = sendFile(
            request,
            seeder.getIPAddress(),
            senderPortNumber
          );
          seeder.getSocket().send(packetToSend);

          // Send an empty packet to signal the end of the file
          seeder
            .getSocket()
            .send(
              new DatagramPacket(new byte[0], 0, seeder.getIPAddress(), senderPortNumber)
            );
        } else {
          System.err.println("Unknown Request!");
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * method to get the name of a file from a directory
   * @param id the id of the file
   * @return the file name
   */
  public static String getFileName(int id) {
    File dir = new File("data/seeder files");
    File f = dir.listFiles()[id];
    return f.getName();
  }

  /**
   * method to create the DatagramPacket of the requested file
   * @param fileId
   * @param ip
   * @param senderPortNumber
   * @return
   */
  public static DatagramPacket sendFile(String fileId, InetAddress ip, int senderPortNumber) {
    File directory = new File("data/seeder files");
    DatagramPacket pkt = null;
    //get the file from the file list
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      File fileToSend = files[Integer.parseInt(fileId)];
      if (fileToSend.exists()) {
    	  //use the below method to send this file
        pkt = sendParticularFile(fileToSend, ip, senderPortNumber);
      }
    }

    return pkt;
  }

  /**
   * Method to create a DatagramPacket of a specified file passed into the method
   * @param file
   * @param ip
   * @param portNum
   * @return
   */
  public static DatagramPacket sendParticularFile(File file, InetAddress ip, int portNum) {
    try (DatagramSocket socket = new DatagramSocket()) {
      FileInputStream fis = new FileInputStream(file);
      byte[] data = new byte[2048];
      int bytesRead = 0;
      DatagramPacket packet = new DatagramPacket(data, bytesRead, ip, portNum);

      while ((bytesRead = fis.read(data)) > 0) {
        packet = new DatagramPacket(data, bytesRead, ip, portNum);
      }

      System.out.println("Packet Sent");
      fis.close();
      return packet;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Method to create the DatagramPacket of the requested file list
   * @param returnAddress
   * @param senderPortNumber
   * @return
   */
  public static DatagramPacket sendFileList(InetAddress returnAddress,int senderPortNumber) {
    String filesList = "";
    File directory = new File("data/seeder files");
    //make a directory and get the name of each file in the directory
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      System.out.println(files.length);
      int counter = 0;
      for (File f : files) {
        filesList += ("ID: " + counter + ", File Name:" + f.getName() + "\n");
        counter += 1;
      }
    } else {
      filesList = "Directory doesn't exist!";
    }
    System.out.println("File List Sent!");
    return new DatagramPacket(
      filesList.getBytes(),
      filesList.getBytes().length,
      returnAddress,
      senderPortNumber
    );
  }
}
