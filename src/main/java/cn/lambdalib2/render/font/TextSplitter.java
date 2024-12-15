/**
* Copyright (c) Lambda Innovation, 2013-2016
* This file is part of LambdaLib modding library.
* https://github.com/LambdaInnovation/LambdaLib
* Licensed under MIT, see project root for more information.
*/
package cn.lambdalib2.render.font;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper to 'fragmenting' string to draw in multi-line in a sane way.
 * Currently isn't perfect, but is viable.
 * @author WeAthFolD
 */
public class TextSplitter {

    private static final String puncts = ",.<>?;:'\"[]{}【】：；“‘《》，。？";

    public interface IFontSizeProvider {
        /**
         * Get the width of given character when drawed.
         */
        float getCharWidth(int chr);

        /**
         * Get the text width that will be drawn.
         */
        float getTextWidth(String str);
    }

    public static List<String> split(String str, IFontSizeProvider font, float local_x, float limit) {
        TextSplitter frag = new TextSplitter(str);
        List<String> ret = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        while (frag.hasNext()) {
            Pair<TokenType, String> next = frag.next();
            TokenType type = next.getLeft();
            String content = next.getRight();
            float len = font.getTextWidth(content);
            if (local_x + len > limit) {
                if (!type.canSplit) { // Draws as whole in next line
                    if (builder.length() > 0) {
                        ret.add(builder.toString());
                    }
                    builder.setLength(0);
                    builder.append(content);
                    local_x = len;
                } else { // Seperate to this line and next line
                    while (!content.isEmpty()) {
                        double acc = 0.0;
                        int i = 0;
                        for (; i < content.length() && local_x + acc <= limit; ++i) {
                            acc += font.getCharWidth(content.charAt(i));
                        }

                        if (i < content.length() && isPunct(content.charAt(i))) {
                            ++i;
                        }

                        builder.append(content.substring(0, i));
                        String add = builder.toString();
                        ret.add(add);

                        builder.setLength(0);
                        local_x = 0;

                        content = content.substring(i);
                    }
                }
            } else {
                builder.append(content);
                local_x += len;
            }
        }

        if (builder.length() > 0) {
            ret.add(builder.toString());
        }

        return ret;
    }

    /**
     * Converts a string with line separator to a list of string, based on the display property of the given font.
     */
    public static List<String> split(String str, IFontSizeProvider font, float limit) {
        return split(str, font, 0, limit);
    }

    public static boolean isPunct(char ch) {
        for (int i = 0; i < puncts.length(); ++i) {
            if (puncts.charAt(i) == ch) return true;
        }
        return false;
    }

    public enum TokenType { NULL(true), WORD(false), SPACE(true), CJKV(true), PUNCT(true);
        public final boolean canSplit;
        TokenType(boolean _canSplit) {
            canSplit = _canSplit;
        }
    }

    final String str;
    int index = 0;

    public TextSplitter(String _str) {
        str = _str;
    }

    public Pair<TokenType, String> next() {
        if(index == str.length()) {
            return Pair.of(TokenType.NULL, null);
        } else {
            TokenType init = getType(str.charAt(index));
            int lindex = index;
            for(++index; index < str.length(); ++index) {
                TokenType type = getType(str.charAt(index));
                if(init != TokenType.CJKV && init != type && type != TokenType.PUNCT)
                    break;
            }
            return Pair.of(init, str.substring(lindex, index));
        }
    }

    public boolean hasNext() {
        return index < str.length();
    }

    private TokenType getType(char ch) {
        if (isPunct(ch)) {
            return TokenType.PUNCT;
        } else if (Character.isWhitespace(ch)) {
            return TokenType.SPACE;
        } else if (Character.isIdeographic(ch)) {
            return TokenType.CJKV;
        } else {
            return TokenType.WORD;
        }
    }

}
