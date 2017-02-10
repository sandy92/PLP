package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

import java.util.HashSet;

import static cop5556sp17.Scanner.Kind.*;

public class Parser {

    /**
     * Exception to be thrown if a syntax error is detected in the input.
     * You will want to provide a useful error message.
     */
    @SuppressWarnings("serial")
    public static class SyntaxException extends Exception {
        public SyntaxException(String message) {
            super(message);
        }
    }

    /**
     * Useful during development to ensure unimplemented routines are
     * not accidentally called during development.  Delete it when
     * the Parser is finished.
     */
    @SuppressWarnings("serial")
    public static class UnimplementedFeatureException extends RuntimeException {
        public UnimplementedFeatureException() {
            super();
        }
    }

    Scanner scanner;
    Token t;

    Parser(Scanner scanner) {
        this.scanner = scanner;
        t = scanner.nextToken();
    }

    /**
     * parse the input using tokens from the scanner.
     * Check for EOF (i.e. no trailing junk) when finished
     *
     * @throws SyntaxException
     */
    void parse() throws SyntaxException {
        program();
        matchEOF();
        return;
    }

    void expression() throws SyntaxException {
        try {
            term();
            while (t.kind == LT || t.kind == LE || t.kind == GT || t.kind == GE || t.kind == EQUAL || t.kind == NOTEQUAL) {
                consume();
                term();
            }
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal expression :: " + e.getMessage());
        }
    }

    void term() throws SyntaxException {
        try {
            elem();
            while (t.kind == PLUS || t.kind == MINUS || t.kind == OR) {
                consume();
                elem();
            }
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal term :: " + e.getMessage());
        }
    }

    void elem() throws SyntaxException {
        try {
            factor();
            while (t.kind == TIMES || t.kind == DIV || t.kind == AND || t.kind == MOD) {
                consume();
                factor();
            }
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal elem :: " + e.getMessage());
        }
    }

    void factor() throws SyntaxException {
        Kind kind = t.kind;
        switch (kind) {
            case IDENT: {
                consume();
            }
            break;
            case INT_LIT: {
                consume();
            }
            break;
            case KW_TRUE:
            case KW_FALSE: {
                consume();
            }
            break;
            case KW_SCREENWIDTH:
            case KW_SCREENHEIGHT: {
                consume();
            }
            break;
            case LPAREN: {
                try {
                    consume();
                    expression();
                    match(RPAREN);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal factor :: " + e.getMessage());
                }
            }
            break;
            default:
                throw new SyntaxException("illegal factor :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void block() throws SyntaxException {
        if (t.kind == LBRACE) {
            HashSet<Kind> decPredictSet = new HashSet<>();
            decPredictSet.add(KW_INTEGER);
            decPredictSet.add(KW_BOOLEAN);
            decPredictSet.add(KW_IMAGE);
            decPredictSet.add(KW_FRAME);


            HashSet<Kind> statementPredictSet = new HashSet<>();
            statementPredictSet.add(OP_SLEEP);
            statementPredictSet.add(KW_WHILE);
            statementPredictSet.add(KW_IF);
            statementPredictSet.add(IDENT);
            statementPredictSet.add(OP_BLUR);
            statementPredictSet.add(OP_GRAY);
            statementPredictSet.add(OP_CONVOLVE);
            statementPredictSet.add(KW_SHOW);
            statementPredictSet.add(KW_HIDE);
            statementPredictSet.add(KW_MOVE);
            statementPredictSet.add(KW_XLOC);
            statementPredictSet.add(KW_YLOC);
            statementPredictSet.add(OP_WIDTH);
            statementPredictSet.add(OP_HEIGHT);
            statementPredictSet.add(KW_SCALE);

            try {
                consume();
                while (decPredictSet.contains(t.kind) || statementPredictSet.contains(t.kind)) {
                    if (decPredictSet.contains(t.kind)) {
                        dec();
                    } else if (statementPredictSet.contains(t.kind)) {
                        statement();
                    } else {
                        throw new SyntaxException("Hashset error, unexpected branching for " + t.kind);
                    }
                }
                match(RBRACE);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal block :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal block :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void program() throws SyntaxException {
        try {
            match(IDENT);
            if (t.kind == KW_URL || t.kind == KW_FILE || t.kind == KW_INTEGER || t.kind == KW_BOOLEAN) {
                paramDec();
                while (t.kind == COMMA) {
                    consume();
                    paramDec();
                }
            }
            block();
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal program :: " + e.getMessage());
        }
    }

    void paramDec() throws SyntaxException {
        if (t.kind == KW_URL || t.kind == KW_FILE || t.kind == KW_INTEGER || t.kind == KW_BOOLEAN) {
            try {
                consume();
                match(IDENT);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal paramDec :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal paramDec :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void dec() throws SyntaxException {
        if (t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {
            try {
                consume();
                match(IDENT);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal dec :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal dec :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void statement() throws SyntaxException {
        Kind kind = t.kind;
        switch (kind) {
            case OP_SLEEP:
                try {
                    consume();
                    expression();
                    match(SEMI);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case KW_WHILE:
                try {
                    whileStatement();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case KW_IF:
                try {
                    ifStatement();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case IDENT:
                try {
                    Token next = scanner.peek();
                    if (next.kind == ASSIGN) {
                        assign();
                        match(SEMI);
                    } else if (next.kind == ARROW || next.kind == BARARROW) {
                        chain();
                        match(SEMI);
                    } else {
                        throw new SyntaxException("illegal statement :: saw " + t.kind + " after IDENT at " + scanner.getLinePos(t).toString());
                    }
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case OP_BLUR:
            case OP_GRAY:
            case OP_CONVOLVE:
            case KW_SHOW:
            case KW_HIDE:
            case KW_MOVE:
            case KW_XLOC:
            case KW_YLOC:
            case OP_WIDTH:
            case OP_HEIGHT:
            case KW_SCALE:
                try {
                    chain();
                    match(SEMI);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            default:
                throw new SyntaxException("illegal statement :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void assign() throws SyntaxException {
        if (t.kind == IDENT) {
            try {
                consume();
                match(ASSIGN);
                expression();
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal ifStatement :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal assign :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void chain() throws SyntaxException {
        try {
            chainElem();
            arrowOp();
            chainElem();
            while (t.kind == ARROW || t.kind == BARARROW) {
                consume();
                chainElem();
            }
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal ifStatement :: " + e.getMessage());
        }
    }

    void chainElem() throws SyntaxException {
        Kind kind = t.kind;

        switch (kind) {
            case IDENT:
                consume();
                break;
            case OP_BLUR:
            case OP_GRAY:
            case OP_CONVOLVE:
                try {
                    consume();
                    arg();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal chainElem :: " + e.getMessage());
                }
                break;
            case KW_SHOW:
            case KW_HIDE:
            case KW_MOVE:
            case KW_XLOC:
            case KW_YLOC:
                try {
                    consume();
                    arg();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal chainElem :: " + e.getMessage());
                }
                break;
            case OP_WIDTH:
            case OP_HEIGHT:
            case KW_SCALE:
                try {
                    consume();
                    arg();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal chainElem :: " + e.getMessage());
                }
                break;
            default:
                throw new SyntaxException("illegal chainElem :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void arg() throws SyntaxException {
        try {
            if (t.kind == LPAREN) {
                consume();
                expression();
                while (t.kind == COMMA) {
                    consume();
                    expression();
                }
                match(RPAREN);
            }
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal arg :: " + e.getMessage());
        }
    }

    void whileStatement() throws SyntaxException {
        if (t.kind == KW_WHILE) {
            try {
                consume();
                match(LPAREN);
                expression();
                match(RPAREN);
                block();
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal whileStatement :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal whileStatement :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void ifStatement() throws SyntaxException {
        if (t.kind == KW_IF) {
            try {
                consume();
                match(LPAREN);
                expression();
                match(RPAREN);
                block();
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal ifStatement :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal ifStatement :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
    }

    void filterOp() throws SyntaxException {
        try {
            match(OP_BLUR, OP_GRAY, OP_CONVOLVE);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal filterOp :: " + e.getMessage());
        }
    }

    void frameOp() throws SyntaxException {
        try {
            match(KW_SHOW, KW_HIDE, KW_MOVE, KW_XLOC, KW_YLOC);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal frameOp :: " + e.getMessage());
        }
    }

    void imageOp() throws SyntaxException {
        try {
            match(OP_WIDTH, OP_HEIGHT, KW_SCALE);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal imageOp :: " + e.getMessage());
        }
    }

    void arrowOp() throws SyntaxException {
        try {
            match(ARROW, BARARROW);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal arrowOp :: " + e.getMessage());
        }
    }

    void relOp() throws SyntaxException {
        try {
            match(LT, LE, GT, GE, EQUAL, NOTEQUAL);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal relOp :: " + e.getMessage());
        }
    }

    void weakOp() throws SyntaxException {
        try {
            match(PLUS, MINUS, OR);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal weakOp :: " + e.getMessage());
        }
    }

    void strongOp() throws SyntaxException {
        try {
            match(TIMES, DIV, AND, MOD);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal strongOp :: " + e.getMessage());
        }
    }


    /**
     * Checks whether the current token is the EOF token. If not, a
     * SyntaxException is thrown.
     *
     * @return
     * @throws SyntaxException
     */
    private Token matchEOF() throws SyntaxException {
        if (t.isKind(EOF)) {
            return t;
        }
        throw new SyntaxException("expected EOF , saw " + t.kind + " at " + scanner.getLinePos(t).toString());
    }

    /**
     * Checks if the current token has the given kind. If so, the current token
     * is consumed and returned. If not, a SyntaxException is thrown.
     * <p>
     * Precondition: kind != EOF
     *
     * @param kind
     * @return
     * @throws SyntaxException
     */
    private Token match(Kind kind) throws SyntaxException {
        if (t.isKind(kind)) {
            return consume();
        }
        throw new SyntaxException("saw " + t.kind + ", expected " + kind + " at " + scanner.getLinePos(t).toString());
    }

    /**
     * Checks if the current token has one of the given kinds. If so, the
     * current token is consumed and returned. If not, a SyntaxException is
     * thrown.
     * <p>
     * * Precondition: for all given kinds, kind != EOF
     *
     * @param kinds list of kinds, matches any one
     * @return
     * @throws SyntaxException
     */
    private Token match(Kind... kinds) throws SyntaxException {
        StringBuilder allKinds = new StringBuilder("");
        for (Kind kind : kinds) {
            allKinds.append(kind + ",");
            if (t.isKind(kind)) {
                return consume();
            }
        }
        throw new SyntaxException("saw " + t.kind + ", expected one of the following: " + allKinds.toString() + " at " + scanner.getLinePos(t).toString());
    }

    /**
     * Gets the next token and returns the consumed token.
     * <p>
     * Precondition: t.kind != EOF
     *
     * @return
     */
    private Token consume() throws SyntaxException {
        Token tmp = t;
        t = scanner.nextToken();
        return tmp;
    }

}
