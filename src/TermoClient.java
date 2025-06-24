import java.io.*;
import java.net.*;
import java.util.Scanner;

public class TermoClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado ao servidor Termo.");
            String line;

            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (line.startsWith("Tentativa")) {
                    String tentativa = scanner.nextLine();
                    out.write(tentativa + "\n");
                    out.flush();
                }
            }


        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
}
