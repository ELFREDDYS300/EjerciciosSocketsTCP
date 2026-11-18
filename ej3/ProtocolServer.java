import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Ejercicio 3: servidor multihilo que entiende el protocolo
// (CONNECT, JOIN, MSG, QUIT) usando ProtocolParser y Command.
// Todavia NO hay salas ni broadcast: eso llega en el ejercicio 4.
public class ProtocolServer {
    private static final int PUERTO = 8080;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PUERTO);
        System.out.println("Servidor con protocolo listo en puerto " + PUERTO + "...");

        while (true) {
            Socket socket = server.accept();
            pool.execute(new ProtocolHandler(socket));
        }
    }

    // Handler de este ejercicio: interpreta comandos pero solo responde al propio cliente
    static class ProtocolHandler implements Runnable {
        private final Socket socket;
        private String username;
        private String sala = "#general";

        ProtocolHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                out.println("200 OK - Bienvenido al JSMS. Usa CONNECT <user>");

                String linea;
                while ((linea = in.readLine()) != null) {
                    Command cmd = ProtocolParser.parse(linea);

                    switch (cmd.getTipo()) {
                        case "CONNECT":
                            username = cmd.getPayload();
                            out.println("200 OK CONNECTED as " + username);
                            break;
                        case "JOIN":
                            sala = cmd.getPayload();
                            out.println("JOINED " + sala);
                            break;
                        case "MSG":
                            out.println("MSG " + username + ": " + cmd.getPayload());
                            break;
                        case "QUIT":
                            out.println("BYE");
                            return; // cierra la sesion
                        default:
                            out.println("401 ERROR: " + cmd.getPayload()); // el socket sigue abierto
                            break;
                    }
                }
            } catch (IOException e) {
                System.err.println("Error con el cliente: " + e.getMessage());
            }
            // try-with-resources cierra los streams (y con ellos el socket)
        }
    }
}
