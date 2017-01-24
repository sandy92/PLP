package cop5556sp17;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Scanner {
    /**
     * Kind enum
     */

    public static enum Kind {
        IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"),
        KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"),
        KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"),
        SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"),
        RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"),
        EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="),
        PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"),
        ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"),
        KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"),
        OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"),
        KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"),
        KW_SCALE("scale"), EOF("eof");

        Kind(String text) {
            this.text = text;
        }

        final String text;

        String getText() {
            return text;
        }
    }

    /**
     * States inside the DFA for the Scanner
     */
    private static enum State {
        // TODO
        START, IN_INT_LIT
    }

    /*
     * Store the starting pos of every line w.r.t the input chars array
     */
    private List<Integer> lineStartPosArray;

    /**
     * Thrown by Scanner when an illegal character is encountered
     */
    @SuppressWarnings("serial")
    public static class IllegalCharException extends Exception {
        public IllegalCharException(String message) {
            super(message);
        }
    }

    /**
     * Thrown by Scanner when an int literal is not a value that can be represented by an int.
     */
    @SuppressWarnings("serial")
    public static class IllegalNumberException extends Exception {
        public IllegalNumberException(String message) {
            super(message);
        }
    }


    /**
     * Holds the line and position in the line of a token.
     */
    static class LinePos {
        public final int line;
        public final int posInLine;

        public LinePos(int line, int posInLine) {
            super();
            this.line = line;
            this.posInLine = posInLine;
        }

        @Override
        public String toString() {
            return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LinePos linePos = (LinePos) o;

            if (line != linePos.line) return false;
            return posInLine == linePos.posInLine;
        }

        @Override
        public int hashCode() {
            int result = line;
            result = 31 * result + posInLine;
            return result;
        }
    }


    public class Token {
        public final Kind kind;
        public final int pos;  //position in input array
        public final int length;

        //returns the text of this Token
        public String getText() {
            return chars.substring(pos, pos + length);
        }

        //returns a LinePos object representing the line and column of this Token
        LinePos getLinePos() {
            //TODO IMPLEMENT THIS
            if (lineStartPosArray != null && lineStartPosArray.size() != 0) {
                int line = Collections.binarySearch(lineStartPosArray, pos);
                if (line < 0) {
                    if (line == -1) {
                        line = 0;
                    } else {
                        // see definition of Collections.binarySearch
                        // We need the index(i.e., line) smaller than the insertion point to determine the line number
                        line = (-(line + 1)) - 1;
                    }
                }
                int posInLine = pos - lineStartPosArray.get(line);

                return new LinePos(line, posInLine);
            } else {
                return null;
            }
        }

        Token(Kind kind, int pos, int length) {
            this.kind = kind;
            this.pos = pos;
            this.length = length;
        }

        /**
         * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
         * Note that the validity of the input should have been checked when the Token was created.
         * So the exception should never be thrown.
         *
         * @return int value of this token, which should represent an INT_LIT
         * @throws NumberFormatException
         */
        public int intVal() throws NumberFormatException {
            return Integer.parseInt(getText());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Token token = (Token) o;

            if (pos != token.pos) return false;
            if (length != token.length) return false;
            return kind == token.kind;
        }

        @Override
        public int hashCode() {
            int result = kind != null ? kind.hashCode() : 0;
            result = 31 * result + pos;
            result = 31 * result + length;
            return result;
        }
    }


    Scanner(String chars) {
        Objects.requireNonNull(chars, "Scanner :: input string 'chars' can't be null");

        this.chars = chars;
        tokens = new ArrayList<Token>();
    }


    /**
     * Initializes Scanner object by traversing chars and adding tokens to tokens list.
     *
     * @return this scanner
     * @throws IllegalCharException
     * @throws IllegalNumberException
     */
    public Scanner scan() throws IllegalCharException, IllegalNumberException {
        //TODO IMPLEMENT THIS!!!!
        List<Integer> lineStartPosArray = new ArrayList<>();

        int pos = 0;
        int startPos = 0;
        int totalCharsLength = chars.length();
        State state = State.START;

        if (totalCharsLength > 0) {
            int lineStartPos = 0;
            int currentLineNumber = 0;

            char ch;

            while (pos < totalCharsLength) {
                ch = chars.charAt(pos);

                switch (state) {
                    case START:
                        startPos = pos;
                        switch (ch) {
                            case '\n':
                                lineStartPosArray.add(lineStartPos);
                                currentLineNumber++;
                                lineStartPos = ++pos;
                                break;
                            case ';':
                                tokens.add(new Token(Kind.SEMI, startPos, 1));
                                pos++;
                                break;
                            case ',':
                                tokens.add(new Token(Kind.COMMA, startPos, 1));
                                pos++;
                                break;
                            case '(':
                                tokens.add(new Token(Kind.LPAREN, startPos, 1));
                                pos++;
                                break;
                            case ')':
                                tokens.add(new Token(Kind.RPAREN, startPos, 1));
                                pos++;
                                break;
                            case '{':
                                tokens.add(new Token(Kind.LBRACE, startPos, 1));
                                pos++;
                                break;
                            case '}':
                                tokens.add(new Token(Kind.RBRACE, startPos, 1));
                                pos++;
                                break;
                            case '0':
                                tokens.add(new Token(Kind.INT_LIT, startPos, 1));
                                pos++;
                                break;
                            default:
                                if (Character.isWhitespace(ch)) {
                                    pos++;
                                } else if (Character.isDigit(ch)) {
                                    state = State.IN_INT_LIT;
                                    pos++;
                                } else {
                                    throw new IllegalCharException("Scanner :: Unexpected char encountered: '" + ch + "' at Line=" + currentLineNumber + ", pos=" + (pos - lineStartPos) + "");
                                }
                        }
                        break;
                    case IN_INT_LIT:
                        if (Character.isDigit(ch)) {
                            pos++;
                        } else {
                            Token token = new Token(Kind.INT_LIT, startPos, pos - startPos);
                            try {
                                Integer value = token.intVal();
                            } catch (NumberFormatException e) {
                                throw new IllegalNumberException("Scanner :: Unable to parse integer from token '" + token.getText() + "' :: " + e.getMessage());
                            }
                            tokens.add(token);
                            state = State.START;
                        }
                        break;
                    default:
                        assert false;
                }
            }

            lineStartPosArray.add(lineStartPos);
        }


        // Cases where DFA does not end in State.START after processing the current char
        switch (state) {
            case IN_INT_LIT:
                Token token = new Token(Kind.INT_LIT, startPos, pos - startPos);
                try {
                    Integer value = token.intVal();
                } catch (NumberFormatException e) {
                    throw new IllegalNumberException("Scanner :: Unable to parse integer from token '" + token.getText() + "' :: " + e.getMessage());
                }
                tokens.add(token);
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case START:
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            default:
                // Used for cases where file ends abruptly. E.g., When comment tag is opened but not closed
                throw new IllegalCharException("Scanner :: Invalid Token - encountered EOF");
        }

        this.lineStartPosArray = Collections.unmodifiableList(lineStartPosArray);
        return this;
    }


    final ArrayList<Token> tokens;
    final String chars;
    int tokenNum;

    /*
     * Return the next token in the token list and update the state so that
     * the next call will return the Token..
     */
    public Token nextToken() {
        if (tokenNum >= tokens.size())
            return null;
        return tokens.get(tokenNum++);
    }

    /*
     * Return the next token in the token list without updating the state.
     * (So the following call to next will return the same token.)
     */
    /*public Token peek() {
        if (tokenNum >= tokens.size())
            return null;
        return tokens.get(tokenNum + 1); // TODO Decide whether this implementation is correct or not, it should ideally be tokens.get(tokenNum + 1);
    }*/

    public Token peek() {
        if (tokenNum >= tokens.size())
            return null;
        return tokens.get(tokenNum); // TODO Keep only one peek function in code, after decide the correct implementation
    }


    /**
     * Returns a LinePos object containing the line and position in line of the
     * given token.
     * <p>
     * Line numbers start counting at 0
     *
     * @param t
     * @return
     */
    public LinePos getLinePos(Token t) {
        return t.getLinePos();
//        return null; // TODO remove
    }


}
