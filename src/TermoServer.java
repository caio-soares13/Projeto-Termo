import java.io.*;
import java.net.*;
import java.text.Normalizer;
import java.util.*;

public class TermoServer {
    private static final int PORT = 12345;
    private static final String[] WORDS = {"carta", "trigo", "folha", "navio", "pleno", "limpo", "vidro", "carro", "banho", "verde",
            "fruta", "leite", "massa", "salto", "tempo", "linha", "terra", "piano", "neve", "noite",
            "lente", "lindo", "tarde", "ponto", "banco", "cinto", "gente", "vento", "chuva", "festa",
            "livro", "beijo", "fundo", "preto", "claro", "nuvem", "peixe", "cobra", "fácil", "rádio",
            "roupa", "sonho", "sinal", "pista", "caixa", "pente", "doido", "porta", "moeda", "forno"};

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
            String normalizedSecret = normalize(secretWord);
            int maxAttempts = 6;

            out.write("Bem-vindo ao Termo! Tente adivinhar a palavra de 5 letras.\n");
            out.flush();

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                out.write("Tentativa " + attempt + ": \n");
                out.flush();

                String guess = in.readLine();
                if (guess == null) break;

                guess = guess.trim().toLowerCase();
                String normalizedGuess = normalize(guess);

                if (guess.length() != 5) {
                    out.write("A palavra deve ter 5 letras.\n");
                    out.flush();
                    attempt--; // tentativa inválida não conta
                    continue;
                }

                if (normalizedGuess.equals(normalizedSecret)) {
                    out.write("Parabéns! Você acertou a palavra: " + secretWord + "\n");
                    out.flush();
                    break;
                }

                out.write(verificaPalavra(normalizedGuess, normalizedSecret, guess) + "\n");
                out.flush();
            }

            out.write("Fim do jogo. A palavra era: " + secretWord + "\n");
            out.flush();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String verificaPalavra(String guess, String secret, String originalGuess) {
        char[] result = new char[5];
        boolean[] used = new boolean[5];

        Arrays.fill(result, '_');

        // Letras corretas e na posição certa
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == secret.charAt(i)) {
                result[i] = Character.toUpperCase(originalGuess.charAt(i));
                used[i] = true;
            }
        }

        // Letras corretas na posição errada
        for (int i = 0; i < 5; i++) {
            if (result[i] != '_') continue;

            char c = guess.charAt(i);
            for (int j = 0; j < 5; j++) {
                if (!used[j] && secret.charAt(j) == c) {
                    result[i] = Character.toLowerCase(originalGuess.charAt(i));
                    used[j] = true;
                    break;
                }
            }
        }

        return new String(result);
    }

    // Função para remover acentos de palavras
    private static String normalize(String word) {
        return Normalizer.normalize(word, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase();
    }
}
