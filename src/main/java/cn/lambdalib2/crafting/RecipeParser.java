package cn.lambdalib2.crafting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author EAirPeter
 */
public class RecipeParser {
    private Reader reader = null;
    private int read = 0;
    private int cur = 0;

    private String type = null;
    private ParsedRecipeElement output = null;
    private ParsedRecipeElement[] input = null;
    private int width = -1;
    private int height = -1;
    private float exp = 0;

    RecipeParser(String str) throws Throwable {
        reader = new StringReader(str);
        getchar();
    }

    RecipeParser(File file) throws Throwable {
        reader = new FileReader(file);
        getchar();
    }

    void close() {
        try {
            reader.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public ParsedRecipeElement getOutput() {
        return output;
    }

    public ParsedRecipeElement[] getInput() {
        return input;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getExperience() {
        return exp;
    }

    private void error(String message) {
        error(message, null);
    }

    private void error(String message, Throwable cause) {
        throw new RuntimeException("Failed at: " + cur, new RecipeParsingException(message, cause));
    }

    private int getchar() {
        try {
            ++cur;
            read = reader.read();
            if (read == ';') {
                while (read != '\n' && read != '\r' && read != -1)
                    read = reader.read();
            }
            return read;
        } catch (IOException e) {
            error("Caught unexpected IOException", e);
            return -1;
        }
    }

    public boolean parseNext() {
        type = null;
        output = null;
        input = null;
        width = -1;
        height = -1;
        parseNull();
        if (read != -1) {
            parseType();
            parseOutput();
            parseExp();
            parseChar('{');
            parseInput();
            parseChar('}');
            return true;
        }
        return false;
    }

    private void parseNull() {
        while (read != -1 && Character.isWhitespace(read))
            getchar();
    }

    private void parseType() {
        parseNull();
        StringBuilder sb = new StringBuilder();
        while (read != -1 && Character.isUnicodeIdentifierPart(read)) {
            sb.append(Character.toChars(read));
            getchar();
        }
        type = sb.toString();
        if (type == null || type.isEmpty())
            error("Empty type is not allowed");
    }

    private void parseOutput() {
        parseChar('(');
        output = parseElement();
        parseChar(')');
    }

    private void parseInput() {
        List<List<ParsedRecipeElement>> rows = new ArrayList<List<ParsedRecipeElement>>();
        for (;;) {
            try {
                rows.add(parseList());
            } catch (Throwable e) {
                break;
            }
        }
        for (List<ParsedRecipeElement> row : rows) {
            if (width == -1)
                width = row.size();
            if (width != row.size())
                error("Row size must be the same");
        }
        height = rows.size();
        if (width < 1)
            error("Width must greater than zero");
        if (height < 1)
            error("Height must greater than zero");
        input = new ParsedRecipeElement[width * height];
        int index = 0;
        for (List<ParsedRecipeElement> row : rows)
            for (ParsedRecipeElement element : row)
                input[index++] = element;
    }

    private List<ParsedRecipeElement> parseList() {
        parseChar('[');
        ArrayList<ParsedRecipeElement> list = new ArrayList<ParsedRecipeElement>();
        for (;;) {
            list.add(parseElement());
            parseNull();
            if (read == ',')
                getchar();
            else
                break;
        }
        parseChar(']');
        return list;
    }

    private ParsedRecipeElement parseElement() {
        parseNull();
        ParsedRecipeElement res = new ParsedRecipeElement();
        StringBuilder sb = new StringBuilder();
        while (read != -1 && (Character.isUnicodeIdentifierPart(read) || read == ':' || read == '@' || read == '$')) {
            sb.append(Character.toChars(read));
            getchar();
        }
        if (sb.length() < 1)
            return null;
        res.name = sb.toString();
        parseNull();
        if (read == '#') {
            getchar();
            res.data = parseInteger();
            res.dataParsed = true;
            parseNull();
        }
        if (read == '*') {
            getchar();
            res.amount = parseInteger();
            parseNull();
        }
        return res;
    }

    private void parseChar(char c) {
        if (!tryParseChar(c))
            error("Expecting " + c);
    }

    private boolean tryParseChar(char c) {
        parseNull();

        if (read == c) {
            getchar();
            return true;
        } else {
            return false;
        }
    }

    private int parseInteger() {
        parseNull();
        StringBuilder sb = new StringBuilder();
        while (read != -1 && Character.isDigit(read)) {
            sb.append(Character.toChars(read));
            getchar();
        }
        return Integer.valueOf(sb.toString());
    }

    private void parseExp() {
        if (tryParseChar('[')) {
            float val = parseFloat();
            parseNull();
            parseChar(']');
            exp = val;
        } else
            exp = 0;
    }

    private float parseFloat() {
        parseNull();
        StringBuilder sb = new StringBuilder();

        boolean hasDot = false;
        while (read != -1 && Character.isDigit(read) || (!hasDot && read == '.')) {
            sb.append(Character.toChars(read));
            if (read == '.') {
                hasDot = true;
            }
            getchar();
        }
        return Float.valueOf(sb.toString());
    }

}
