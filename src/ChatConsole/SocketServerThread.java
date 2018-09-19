package ChatConsole;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

public class SocketServerThread implements Runnable {
    private BlockingQueue<SocketServerThread> sockets;
    private HashSet<String> names;
    private Socket socket;
    private String name;
    private BufferedReader reader;
    private BufferedWriter writer;

    public SocketServerThread(Socket socket, BlockingQueue<SocketServerThread> sockets, HashSet<String> names) {
        this.socket = socket;
        this.sockets = sockets;
        this.names = names;
        try {
            this.reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                writer.write("Input name\n");
                writer.flush();
                name = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (name == null) {
                return;
            }
            synchronized (names) {
                if (!names.contains(name)) {
                    names.add(name);
                    break;
                }
            }
        }

        try {
            writer.write("Name accepted, you can start chat(print 'quit' to exit):\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!socket.isClosed()) {
            String msg = null;
            try {
                msg = reader.readLine();
            } catch (IOException e) {
                //e.printStackTrace();
            }

            if (msg == null) {
                close();
            } else if ("quit".equals(msg)) {
                close();
            } else {
                msg = name + ": " + msg;
                System.out.println("Server debug -> " + msg);
                for (SocketServerThread socketServerThread : sockets) {
                    if (socketServerThread != this)
                        socketServerThread.send(msg);
                }
            }
        }
    }

    public synchronized void send(String msg) {
        try {
            writer.write(msg);
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            close();
        }
    }

    public synchronized void close() {
        sockets.remove(this);
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}