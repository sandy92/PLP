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
        START, GOT_NOT, GOT_MINUS, GOT_LT, GOT_EQUAL, GOT_GT, GOT_OR, GOT_OR_MINUS, GOT_DIV, IN_COMMENT, IN_COMMENT_MIGHT_CLOSE, IN_IDENT, GOT_B, GOT_C, GOT_F, GOT_G, GOT_H, GOT_I, GOT_M, GOT_S, GOT_T, GOT_U, GOT_W, GOT_X, GOT_Y, IN_INT_LIT
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

    private boolean isIdentifierStart(char ch) {
        return ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || (ch == '$') || (ch == '_'));
    }

    private boolean isIdentifierPart(char ch) {
        return (isIdentifierStart(ch) || Character.isDigit(ch));
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
                            case '%':
                                tokens.add(new Token(Kind.MOD, startPos, 1));
                                pos++;
                                break;
                            case '&':
                                tokens.add(new Token(Kind.AND, startPos, 1));
                                pos++;
                                break;
                            case '*':
                                tokens.add(new Token(Kind.TIMES, startPos, 1));
                                pos++;
                                break;
                            case '+':
                                tokens.add(new Token(Kind.PLUS, startPos, 1));
                                pos++;
                                break;
                            case '!':
                                state = State.GOT_NOT;
                                pos++;
                                break;
                            case '-':
                                state = State.GOT_MINUS;
                                pos++;
                                break;
                            case '<':
                                state = State.GOT_LT;
                                pos++;
                                break;
                            case '=':
                                state = State.GOT_EQUAL;
                                pos++;
                                break;
                            case '>':
                                state = State.GOT_GT;
                                pos++;
                                break;
                            case '|':
                                state = State.GOT_OR;
                                pos++;
                                break;
                            case '/':
                                state = State.GOT_DIV;
                                pos++;
                                break;
                            case 'b':
                                state = State.GOT_B;
                                pos++;
                                break;
                            case 'c':
                                state = State.GOT_C;
                                pos++;
                                break;
                            case 'f':
                                state = State.GOT_F;
                                pos++;
                                break;
                            case 'g':
                                state = State.GOT_G;
                                pos++;
                                break;
                            case 'h':
                                state = State.GOT_H;
                                pos++;
                                break;
                            case 'i':
                                state = State.GOT_I;
                                pos++;
                                break;
                            case 'm':
                                state = State.GOT_M;
                                pos++;
                                break;
                            case 's':
                                state = State.GOT_S;
                                pos++;
                                break;
                            case 't':
                                state = State.GOT_T;
                                pos++;
                                break;
                            case 'u':
                                state = State.GOT_U;
                                pos++;
                                break;
                            case 'w':
                                state = State.GOT_W;
                                pos++;
                                break;
                            case 'x':
                                state = State.GOT_X;
                                pos++;
                                break;
                            case 'y':
                                state = State.GOT_Y;
                                pos++;
                                break;
                            default:
                                if (Character.isWhitespace(ch)) {
                                    pos++;
                                } else if (Character.isDigit(ch)) {
                                    state = State.IN_INT_LIT;
                                    pos++;
                                } else if (isIdentifierStart(ch)) {
                                    state = State.IN_IDENT;
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
                    case GOT_NOT:
                        if (ch == '=') {
                            pos++;
                            tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
                        } else {
                            tokens.add(new Token(Kind.NOT, startPos, 1));
                        }
                        state = State.START;
                        break;
                    case GOT_MINUS:
                        if (ch == '>') {
                            pos++;
                            tokens.add(new Token(Kind.ARROW, startPos, 2));
                        } else {
                            tokens.add(new Token(Kind.MINUS, startPos, 1));
                        }
                        state = State.START;
                        break;
                    case GOT_LT:
                        if (ch == '-') {
                            pos++;
                            tokens.add(new Token(Kind.ASSIGN, startPos, pos - startPos));
                        } else if (ch == '=') {
                            pos++;
                            tokens.add(new Token(Kind.LE, startPos, pos - startPos));
                        } else {
                            tokens.add(new Token(Kind.LT, startPos, pos - startPos));
                        }
                        state = State.START;
                        break;
                    case GOT_EQUAL:
                        if (ch == '=') {
                            pos++;
                            tokens.add(new Token(Kind.EQUAL, startPos, 2));
                            state = State.START;
                        } else {
                            throw new IllegalCharException("Scanner :: Unexpected char encountered: '" + ch + "' at Line=" + currentLineNumber + ", pos=" + (pos - lineStartPos) + "");
                        }
                        break;
                    case GOT_GT:
                        if (ch == '=') {
                            pos++;
                            tokens.add(new Token(Kind.GE, startPos, pos - startPos));
                        } else {
                            tokens.add(new Token(Kind.GT, startPos, pos - startPos));
                        }
                        state = State.START;
                        break;
                    case GOT_OR:
                        if (ch == '-') {
                            state = State.GOT_OR_MINUS;
                            pos++;
                        } else {
                            tokens.add(new Token(Kind.OR, startPos, 1));
                            state = State.START;
                        }
                        break;
                    case GOT_OR_MINUS:
                        if (ch == '>') {
                            pos++;
                            tokens.add(new Token(Kind.BARARROW, startPos, 3));
                        } else {
                            tokens.add(new Token(Kind.OR, startPos, 1));
                            tokens.add(new Token(Kind.MINUS, startPos + 1, 1));
                        }
                        state = State.START;
                        break;
                    case GOT_DIV:
                        if (ch == '*') {
                            pos++;
                            state = State.IN_COMMENT;
                        } else {
                            tokens.add(new Token(Kind.DIV, startPos, 1));
                            state = State.START;
                        }
                        break;
                    case IN_COMMENT:
                        if (ch == '*') {
                            state = State.IN_COMMENT_MIGHT_CLOSE;
                        }
                        pos++;
                        break;
                    case IN_COMMENT_MIGHT_CLOSE:
                        if (ch == '/') {
                            state = State.START;
                        } else {
                            state = State.IN_COMMENT;
                        }
                        pos++;
                        break;
                    case IN_IDENT:
                        if (isIdentifierPart(ch)) {
                            pos++;
                        } else {
                            tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
                            state = State.START;
                        }
                        break;
                    case GOT_B:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("blur")) {
                            tokens.add(new Token(Kind.OP_BLUR, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else if (((startPos + 7) <= totalCharsLength) && chars.substring(startPos, startPos + 7).equals("boolean")) {
                            tokens.add(new Token(Kind.KW_BOOLEAN, startPos, 7));
                            state = State.START;
                            pos = startPos + 7;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_C:
                        if (((startPos + 8) <= totalCharsLength) && chars.substring(startPos, startPos + 8).equals("convolve")) {
                            tokens.add(new Token(Kind.OP_CONVOLVE, startPos, 8));
                            state = State.START;
                            pos = startPos + 8;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_F:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("file")) {
                            tokens.add(new Token(Kind.KW_FILE, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("false")) {
                            tokens.add(new Token(Kind.KW_FALSE, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("frame")) {
                            tokens.add(new Token(Kind.KW_FRAME, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_G:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("gray")) {
                            tokens.add(new Token(Kind.OP_GRAY, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_H:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("hide")) {
                            tokens.add(new Token(Kind.KW_HIDE, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else if (((startPos + 6) <= totalCharsLength) && chars.substring(startPos, startPos + 6).equals("height")) {
                            tokens.add(new Token(Kind.OP_HEIGHT, startPos, 6));
                            state = State.START;
                            pos = startPos + 6;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_I:
                        if (((startPos + 2) <= totalCharsLength) && chars.substring(startPos, startPos + 2).equals("if")) {
                            tokens.add(new Token(Kind.KW_IF, startPos, 2));
                            state = State.START;
                            pos = startPos + 2;
                        } else if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("image")) {
                            tokens.add(new Token(Kind.KW_IMAGE, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else if (((startPos + 7) <= totalCharsLength) && chars.substring(startPos, startPos + 7).equals("integer")) {
                            tokens.add(new Token(Kind.KW_INTEGER, startPos, 7));
                            state = State.START;
                            pos = startPos + 7;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_M:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("move")) {
                            tokens.add(new Token(Kind.KW_MOVE, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_S:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("show")) {
                            tokens.add(new Token(Kind.KW_SHOW, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("sleep")) {
                            tokens.add(new Token(Kind.OP_SLEEP, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("scale")) {
                            tokens.add(new Token(Kind.KW_SCALE, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else if (((startPos + 11) <= totalCharsLength) && chars.substring(startPos, startPos + 11).equals("screenwidth")) {
                            tokens.add(new Token(Kind.KW_SCREENWIDTH, startPos, 11));
                            state = State.START;
                            pos = startPos + 11;
                        } else if (((startPos + 12) <= totalCharsLength) && chars.substring(startPos, startPos + 12).equals("screenheight")) {
                            tokens.add(new Token(Kind.KW_SCREENHEIGHT, startPos, 12));
                            state = State.START;
                            pos = startPos + 12;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_T:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("true")) {
                            tokens.add(new Token(Kind.KW_TRUE, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_U:
                        if (((startPos + 3) <= totalCharsLength) && chars.substring(startPos, startPos + 3).equals("url")) {
                            tokens.add(new Token(Kind.KW_URL, startPos, 3));
                            state = State.START;
                            pos = startPos + 3;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_W:
                        if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("while")) {
                            tokens.add(new Token(Kind.KW_WHILE, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else if (((startPos + 5) <= totalCharsLength) && chars.substring(startPos, startPos + 5).equals("width")) {
                            tokens.add(new Token(Kind.OP_WIDTH, startPos, 5));
                            state = State.START;
                            pos = startPos + 5;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_X:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("xloc")) {
                            tokens.add(new Token(Kind.KW_XLOC, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
                        }
                        break;
                    case GOT_Y:
                        if (((startPos + 4) <= totalCharsLength) && chars.substring(startPos, startPos + 4).equals("yloc")) {
                            tokens.add(new Token(Kind.KW_YLOC, startPos, 4));
                            state = State.START;
                            pos = startPos + 4;
                        } else {
                            if (isIdentifierPart(ch)) {
                                pos++;
                                state = State.IN_IDENT;
                            } else {
                                tokens.add(new Token(Kind.IDENT, startPos, 1));
                                state = State.START;
                            }
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
            case GOT_NOT:
                tokens.add(new Token(Kind.NOT, startPos, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case GOT_MINUS:
                tokens.add(new Token(Kind.MINUS, startPos, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case GOT_LT:
                tokens.add(new Token(Kind.LT, startPos, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case GOT_EQUAL:
                throw new IllegalCharException("Scanner :: Invalid Token - encountered EOF after '='");
            case GOT_GT:
                tokens.add(new Token(Kind.GT, startPos, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case GOT_OR:
                tokens.add(new Token(Kind.OR, startPos, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case GOT_OR_MINUS:
                tokens.add(new Token(Kind.OR, startPos, 1));
                tokens.add(new Token(Kind.MINUS, startPos + 1, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case GOT_DIV:
                tokens.add(new Token(Kind.DIV, startPos, 1));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            case START:
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            // Edge case handling of letters is same as that of idents
            case GOT_B:
            case GOT_C:
            case GOT_F:
            case GOT_G:
            case GOT_H:
            case GOT_I:
            case GOT_M:
            case GOT_S:
            case GOT_T:
            case GOT_U:
            case GOT_W:
            case GOT_X:
            case GOT_Y:
            case IN_IDENT:
                tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));
                tokens.add(new Token(Kind.EOF, pos, 0));
                break;
            // Edge case handling for Comments is same as that of unexpected EOF errors
            case IN_COMMENT:
            case IN_COMMENT_MIGHT_CLOSE:
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
