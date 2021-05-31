package ch.epfl.tchu;

/**
 * This class simplify the writing of preconditions
 *
 * @author Noah Munz (310779)
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Checks if the given parameter is valid i.e. is true when it should be
     *
     * @param shouldBeTrue value to check
     * @throws IllegalArgumentException if given parameter should be true and is not
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) throw new IllegalArgumentException();
    }
}
