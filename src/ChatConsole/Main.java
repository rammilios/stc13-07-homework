package ChatConsole;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
  private static Integer port = 5555;
  private static BlockingQueue<SocketServerThread> socketServerThreads = new LinkedBlockingQueue<>();
  private static HashSet<String> names = new HashSet<>();

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(port);

      while (true){
        Socket socket = serverSocket.accept();
        SocketServerThread socketThread = new SocketServerThread(socket, socketServerThreads, names);
        Thread thread = new Thread(socketThread);
        thread.setDaemon(true);
        thread.start();
        socketServerThreads.add(socketThread);

        if (socketServerThreads.size() == 0)
          break;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}