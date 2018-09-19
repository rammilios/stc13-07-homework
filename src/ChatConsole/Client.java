package ChatConsole;;

public class Client {
    public static void main(String[] args) {
        ChatClient client = new ChatClient("localhost", 5555);
        client.startChat();
    }
}