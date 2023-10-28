package utils;

public class HashingAlgorithm {

    /**
     * Computes a simple hash for a given message.
     * @param message The message to compute the hash for.
     * @return A byte array representing the computed hash.
     */
    public static byte[] computeHash(String message) {
        return new byte[]{(byte) message.hashCode()};
    }
}
