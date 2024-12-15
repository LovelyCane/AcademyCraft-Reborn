package cn.lambdalib2.crafting;

/**
 * In a recipe file, use "NAME#DATA*AMOUNT" to specify an element.
 * 
 * @author EAirPeter
 */
public class ParsedRecipeElement {

    public String name = null;
    public int data = 0;
    public int amount = 1;

    public boolean dataParsed = false;

    @Override
    public String toString() {
        return "(" + name + "," + data + "," + amount + ")";
    }

}
