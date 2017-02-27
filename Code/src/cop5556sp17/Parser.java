package cop5556sp17;

import cop5556sp17.AST.*;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
    ASTNode parse() throws SyntaxException {
        Program program = program();
        matchEOF();
        return program;
    }

    Expression expression() throws SyntaxException {
        Token firstToken = t;
        Expression expression;

        Expression e0;
        Expression e1;

        try {
            e0 = term();
            while (t.kind == LT || t.kind == LE || t.kind == GT || t.kind == GE || t.kind == EQUAL || t.kind == NOTEQUAL) {
                Token op = t;
                consume();
                e1 = term();
                e0 = new BinaryExpression(firstToken, e0, op, e1);
            }
            expression = e0;
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal expression :: " + e.getMessage());
        }

        return expression;
    }

    Expression term() throws SyntaxException {
        Token firstToken = t;
        Expression expression;

        Expression e0;
        Expression e1;

        try {
            e0 = elem();
            while (t.kind == PLUS || t.kind == MINUS || t.kind == OR) {
                Token op = t;
                consume();
                e1 = elem();
                e0 = new BinaryExpression(firstToken, e0, op, e1);
            }
            expression = e0;
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal term :: " + e.getMessage());
        }

        return expression;
    }

    Expression elem() throws SyntaxException {
        Token firstToken = t;
        Expression expression;

        Expression e0;
        Expression e1;

        try {
            e0 = factor();
            while (t.kind == TIMES || t.kind == DIV || t.kind == AND || t.kind == MOD) {
                Token op = t;
                consume();
                e1 = factor();
                e0 = new BinaryExpression(firstToken, e0, op, e1);
            }

            expression = e0;
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal elem :: " + e.getMessage());
        }

        return expression;
    }

    Expression factor() throws SyntaxException {
        Token firstToken = t;
        Expression expression;
        Kind kind = t.kind;
        switch (kind) {
            case IDENT: {
                consume();
                expression = new IdentExpression(firstToken);
            }
            break;
            case INT_LIT: {
                consume();
                expression = new IntLitExpression(firstToken);
            }
            break;
            case KW_TRUE:
            case KW_FALSE: {
                consume();
                expression = new BooleanLitExpression(firstToken);
            }
            break;
            case KW_SCREENWIDTH:
            case KW_SCREENHEIGHT: {
                consume();
                expression = new ConstantExpression(firstToken);
            }
            break;
            case LPAREN: {
                try {
                    consume();
                    expression = expression();
                    match(RPAREN);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal factor :: " + e.getMessage());
                }
            }
            break;
            default:
                throw new SyntaxException("illegal factor :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return expression;
    }

    Block block() throws SyntaxException {
        Token firstToken = t;
        Block block;

        ArrayList<Dec> decs = new ArrayList<>();
        ArrayList<Statement> statements = new ArrayList<>();

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
                        Dec dec = dec();
                        decs.add(dec);
                    } else if (statementPredictSet.contains(t.kind)) {
                        Statement statement = statement();
                        statements.add(statement);
                    } else {
                        throw new SyntaxException("Hashset error, unexpected branching for " + t.kind);
                    }
                }
                match(RBRACE);
                block = new Block(firstToken, decs, statements);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal block :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal block :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return block;
    }

    Program program() throws SyntaxException {
        Token firstToken = t;
        Program program;
        ArrayList<ParamDec> paramList = new ArrayList<>();

        try {
            match(IDENT);
            ParamDec paramDec;
            if (t.kind == KW_URL || t.kind == KW_FILE || t.kind == KW_INTEGER || t.kind == KW_BOOLEAN) {
                paramDec = paramDec();
                paramList.add(paramDec);
                while (t.kind == COMMA) {
                    consume();
                    paramDec = paramDec();
                    paramList.add(paramDec);
                }
            }
            Block block = block();
            program = new Program(firstToken, paramList, block);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal program :: " + e.getMessage());
        }
        return program;
    }

    ParamDec paramDec() throws SyntaxException {
        Token firstToken = t;
        ParamDec paramDec;
        if (t.kind == KW_URL || t.kind == KW_FILE || t.kind == KW_INTEGER || t.kind == KW_BOOLEAN) {
            try {
                consume();
                Token ident = match(IDENT);
                paramDec = new ParamDec(firstToken, ident);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal paramDec :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal paramDec :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return paramDec;
    }

    Dec dec() throws SyntaxException {
        Token firstToken = t;
        Dec dec;
        if (t.kind == KW_INTEGER || t.kind == KW_BOOLEAN || t.kind == KW_IMAGE || t.kind == KW_FRAME) {
            try {
                consume();
                Token ident = match(IDENT);
                dec = new Dec(firstToken, ident);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal dec :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal dec :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return dec;
    }

    Statement statement() throws SyntaxException {
        Token firstToken = t;
        Statement statement;

        Kind kind = t.kind;
        switch (kind) {
            case OP_SLEEP:
                try {
                    consume();
                    Expression expression = expression();
                    match(SEMI);
                    statement = new SleepStatement(firstToken, expression);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case KW_WHILE:
                try {
                    statement = whileStatement();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case KW_IF:
                try {
                    statement = ifStatement();
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            case IDENT:
                try {
                    Token next = scanner.peek();
                    if (next.kind == ASSIGN) {
                        statement = assign();
                        match(SEMI);
                    } else if (next.kind == ARROW || next.kind == BARARROW) {
                        statement = chain();
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
                    statement = chain();
                    match(SEMI);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal statement :: " + e.getMessage());
                }
                break;
            default:
                throw new SyntaxException("illegal statement :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return statement;
    }

    AssignmentStatement assign() throws SyntaxException {
        Token firstToken = t;
        AssignmentStatement assignmentStatement;
        if (t.kind == IDENT) {
            try {
                Token ident = t;
                consume();
                match(ASSIGN);
                Expression expression = expression();
                assignmentStatement = new AssignmentStatement(firstToken, new IdentLValue(ident), expression);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal ifStatement :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal assign :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return assignmentStatement;
    }

    Chain chain() throws SyntaxException {
        Token firstToken = t;
        Chain chain;

        Chain e0;
        ChainElem e1;
        Token arrow;

        try {
            e0 = chainElem();
            arrow = arrowOp();
            e1 = chainElem();
            while (t.kind == ARROW || t.kind == BARARROW) {
                e0 = new BinaryChain(firstToken, e0, arrow, e1);
                arrow = t;
                consume();
                e1 = chainElem();
            }

            chain = new BinaryChain(firstToken, e0, arrow, e1);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal ifStatement :: " + e.getMessage());
        }

        return chain;
    }

    ChainElem chainElem() throws SyntaxException {
        Token firstToken = t;
        ChainElem chainElem;

        Kind kind = t.kind;

        switch (kind) {
            case IDENT:
                consume();
                chainElem = new IdentChain(firstToken);
                break;
            case OP_BLUR:
            case OP_GRAY:
            case OP_CONVOLVE:
                try {
                    consume();
                    Tuple arg = arg();
                    chainElem = new FilterOpChain(firstToken, arg);
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
                    Tuple arg = arg();
                    chainElem = new FrameOpChain(firstToken, arg);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal chainElem :: " + e.getMessage());
                }
                break;
            case OP_WIDTH:
            case OP_HEIGHT:
            case KW_SCALE:
                try {
                    consume();
                    Tuple arg = arg();
                    chainElem = new ImageOpChain(firstToken, arg);
                } catch (SyntaxException e) {
                    throw new SyntaxException("illegal chainElem :: " + e.getMessage());
                }
                break;
            default:
                throw new SyntaxException("illegal chainElem :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }

        return chainElem;
    }

    Tuple arg() throws SyntaxException {
        Token firstToken = t;
        List<Expression> expressionList = new ArrayList<>();
        Tuple tuple;
        try {
            if (t.kind == LPAREN) {
                consume();
                expressionList.add(expression());
                while (t.kind == COMMA) {
                    consume();
                    expressionList.add(expression());
                }
                match(RPAREN);
            }
            tuple = new Tuple(firstToken, expressionList);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal arg :: " + e.getMessage());
        }

        return tuple;
    }

    WhileStatement whileStatement() throws SyntaxException {
        Token firstToken = t;
        WhileStatement whileStatement;

        if (t.kind == KW_WHILE) {
            try {
                consume();
                match(LPAREN);
                Expression expression = expression();
                match(RPAREN);
                Block block = block();
                whileStatement = new WhileStatement(firstToken, expression, block);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal whileStatement :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal whileStatement :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return whileStatement;
    }

    IfStatement ifStatement() throws SyntaxException {
        Token firstToken = t;
        IfStatement ifStatement;

        if (t.kind == KW_IF) {
            try {
                consume();
                match(LPAREN);
                Expression expression = expression();
                match(RPAREN);
                Block block = block();
                ifStatement = new IfStatement(firstToken, expression, block);
            } catch (SyntaxException e) {
                throw new SyntaxException("illegal ifStatement :: " + e.getMessage());
            }
        } else {
            throw new SyntaxException("illegal ifStatement :: saw " + t.kind + " at " + scanner.getLinePos(t).toString());
        }
        return ifStatement;
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

    Token arrowOp() throws SyntaxException {
        Token arrow;
        try {
            arrow = match(ARROW, BARARROW);
        } catch (SyntaxException e) {
            throw new SyntaxException("illegal arrowOp :: " + e.getMessage());
        }

        return arrow;
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
