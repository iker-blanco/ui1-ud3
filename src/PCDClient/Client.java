package PCDClient;

import java.util.Random;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import java.util.logging.Logger;

import utils.CustomLogger;

import static utils.HashingAlgorithm.computeHash;


/**
 * This class represents a simple client that sends messages to a server and waits for an ACK.
 * Each message sent to the server is accompanied by a computed hash.
 */
public class Client {

    private static final Logger logger = CustomLogger.getLogger(Client.class);
    private static final String file = "data.txt";

    private static final byte ACK = 0x05;
    private static final byte NAK = 0x06;
    private static final Random random = new Random();

    public static void main(String[] args) {
        // Ensure that the correct number of arguments are provided
        if (args.length != 2) {
            logger.severe("Invalid number of arguments provided. Expected: java Client <server_ip> <port>");
            return;
        }

        String serverIP = args[0];
        int port = Integer.parseInt(args[1]);

        logger.info("Attempting to connect to server at IP: " + serverIP + " on port: " + port);

        try (
                Socket socket = new Socket(serverIP, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            logger.info("Connected successfully to the server.");

            Path path = Paths.get(file);
            for (String line : Files.readAllLines(path)) {

                // Sending the message
                out.writeObject(line);
                logger.info("Sent message: " + line);

                // Sending the hash for the message
                byte[] hash = computeHash(line);
                // Randomly introduce an error in the hash 10% of the time
                if (random.nextInt(10) == 0) {
                    hash[0] ^= 1;  // Flip the least significant bit to introduce an error
                    logger.info("Introducing an error in the hash for testing...");
                }
                out.writeObject(hash);
                logger.info("Sent hash: " + hash);

                // Flushing ensures that the data is actually sent out
                out.flush();

                // Waiting for an ACK from the server
                byte response = in.readByte();
                while (response != ACK) {
                    // Resend the message if a NAK is received
                    logger.warning("Received NAK from server. Resending message.");
                    // Re-compute the hash for the message
                    hash = computeHash(line);
                    out.writeObject(line);
                    out.writeObject(hash);
                    out.flush();
                    response = in.readByte();
                }
                logger.info("Received ACK from server.");
            }
        } catch (IOException e) {
            logger.severe("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
