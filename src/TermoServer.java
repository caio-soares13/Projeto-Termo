import java.io.*;
import java.net.*;
import java.util.*;

public class TermoServer {
    private static final int PORT = 12345;
    private static final String[] WORDS = {"carta", "trigo", "folha", "navio", "pleno"};

    public static void main(String[] args) {
        System.out.println("Servidor Termo iniciado...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress());

                handleClient(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            String secretWord = WORDS[new Random().nextInt(WORDS.length)];
            int maxAttempts = 6;
            System.out.println("Palavra secreta: " + secretWord);

            out.write("Bem-vindo ao Termo! Tente adivinhar a palavra de 5 letras.\n");
            out.flush();

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                out.write("Tentativa " + attempt + ": \n");
                out.flush();


                String guess = in.readLine();
                if (guess == null) break;

                if (guess.length() != 5) {
                    out.write("A palavra deve ter 5 letras.\n");
                    out.flush();
                    attempt--; // não conta tentativa inválida
                    continue;
                }

                if (guess.equals(secretWord)) {
                    out.write("Parabéns! Você acertou a palavra: " + secretWord + "\n");
                    out.flush();
                    break;
                }

                out.write(verificaPalavra(guess, secretWord) + "\n");
                out.flush();
            }

            out.write("Fim do jogo. A palavra era: " + secretWord + "\n");
            out.flush();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String verificaPalavra(String guess, String secret) {
        char[] result = new char[5];
        boolean[] used = new boolean[5];

        // Inicializa com '_'
        Arrays.fill(result, '_');

        // Primeiro passa: letras na posição correta
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                result[i] = Character.toUpperCase(guess.charAt(i)); // Maiúscula para correto e na posição certa
                used[i] = true;
            }
        }

        // Segundo passa: letras certas na posição errada
        for (int i = 0; i < 5; i++) {
            if (result[i] != '_') continue; // Já acertou nessa posição

            char c = guess.charAt(i);
            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (!used[j] && secret.charAt(j) == c) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }

            if (found) {
                result[i] = Character.toLowerCase(c); // Minúscula para letra correta na posição errada
            }
            // Se não encontrou, permanece '_'
        }

        // Retorna a string com os símbolos
        // Exemplo: A__e_ (A maiúsculo = certo na posição, letras minúsculas = na palavra mas em outra posição, _ = não está)
        return new String(result);
    }

}
