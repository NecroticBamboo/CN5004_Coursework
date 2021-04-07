public class UserInputException extends RuntimeException {
    private final String description;

    public UserInputException(String descriptionIn){
        description=descriptionIn;
    }

    public String getDescription(){
        return description;
    }
}
