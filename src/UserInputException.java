/**
 * Exception to indicate invalid user input
 */
public class UserInputException extends RuntimeException {
    private final String description;

    /**
     * Constructor
     *
     * @param descriptionIn the error message
     */
    public UserInputException(String descriptionIn) {
        description = descriptionIn;
    }

    /**
     * What went wrong
     *
     * @return returns message of the error
     */
    public String getDescription() {
        return description;
    }
}
