package ChatConsole;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedWriter socketWriter;
    private Scanner consoleReader;

    public ChatClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            this.socketWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        consoleReader = new Scanner(System.in);

        new Thread(new SocketReaderThread(socket, this)).start();

    }

    public void startChat() {
        while (true) {
            String msg = consoleReader.next();
            if (msg == null || msg.length() == 0 || socket.isClosed()) {
                close();
                break;
            } else {
                try {
                    socketWriter.write(msg + "\n");
                    socketWriter.flush();
                } catch (IOException e) {
                    close();
                }
            }
        }
    }

    public synchronized void close() {
        if (!socket.isClosed()) {
            try {
                socket.close();
                System.exit(0);
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }
    }
}