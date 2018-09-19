package ChatConsole;

import java.io.*;
import java.net.Socket;


public class SocketReaderThread implements Runnable {
  private Socket socket;
  private ChatClient chatClient;
  private BufferedReader socketReader;

  public SocketReaderThread(Socket socket, ChatClient chatClient){
    this.socket = socket;
    this.chatClient = chatClient;
    try {
      this.socketReader = new BufferedReader(
              new InputStreamReader(socket.getInputStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void run() {
    while (!socket.isClosed()) {
      String msg = null;
      try {
        msg = socketReader.readLine();
      } catch (IOException e) {
        if ("Socket closed".equals(e.getMessage())) {
          break;
        }
        System.out.println("Connection lost");
        chatClient.close();
      }
      if (msg == null) {
        System.out.println("Server has closed connection");
        chatClient.close();
      } else {
        System.out.println(msg);
      }
    }
  }
}