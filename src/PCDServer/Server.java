package PCDServer;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Logger;

import utils.CustomLogger;

import static utils.HashingAlgorithm.computeHash;

/**
 * This class represents a simple server that accepts client requests.
 * The server listens for messages from the client and responds with ACK if the received hash matches the computed one.
 */
public class Server {
    private static final Logger logger = CustomLogger.getLogger(Server.class);
    private static final byte ACK = 0x05;
    private static final byte NAK = 0x06;

    public static void main(String[] args) {
        // Check for correct number of arguments
        if (args.length != 1) {
            logger.severe("Invalid number of arguments, expected 1 argument but got " + args.length + " arguments.");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server is ready to accept requests...");

            // Continuously listen for client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Accepted a new client connection.");

                // Spawn a new thread for each client to handle its requests
                Thread clientHandler = new Thread(new ClientHandler(clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            logger.severe("Error occurred while opening the socket, message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This inner class handles client requests in a separate thread.
     */
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())
            ) {
                // Continuously listen for client messages and their corresponding hashes
                while (true) {
                    String message = (String) in.readObject();
                    byte[] receivedHash = (byte[]) in.readObject();

                    logger.info("Received message: " + message);
                    logger.info("Received hash: " + receivedHash);

                    // Compute the hash for the received message and compare it with the received hash
                    logger.info("Computing hash...");
                    byte[] hash = computeHash(message);
                    logger.info("Computed hash: " + hash);

                    Thread.sleep(2000);  // Wait for 2 seconds to simulate some processing delay

                    if (Arrays.equals(receivedHash, hash)) {
                        logger.info("Hashes match, sending ACK...");
                        out.writeByte(ACK);
                    } else {
                        logger.info("Hashes do not match, sending NAK...");
                        out.writeByte(NAK);
                    }
                    out.flush();
                }

            } catch (EOFException e) {
                // This is expected when a client finishes sending data
                logger.info("Client finished sending data. Closing connection.");
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                logger.severe("Error occurred while reading from the socket, message: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}
