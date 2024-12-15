package cn.lambdalib2.crafting;

/**
 * @author EAirPeter
 */
public class RecipeParsingException extends Exception {

    public RecipeParsingException() {
        super();
    }

    public RecipeParsingException(String message) {
        super(message);

    }

    public RecipeParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
