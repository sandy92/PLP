package cop5556sp17;

import cop5556sp17.AST.*;

import java.util.List;
import java.util.Objects;

public class TypeCheckVisitor implements ASTVisitor {

    SymbolTable symtab = new SymbolTable();

    @Override
    public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
        Objects.requireNonNull(binaryChain, "TypeCheckVisitor :: BinaryChain can't be null");
        Chain e0 = binaryChain.getE0();
        ChainElem e1 = binaryChain.getE1();
        Scanner.Token arrowOp = binaryChain.getArrow();

        e0.visit(this, null);
        e1.visit(this, null);

        if (e0.getTypeName().isType(Type.TypeName.URL) && arrowOp.isKind(Scanner.Kind.ARROW) && e1.getTypeName().isType(Type.TypeName.IMAGE)) {
            binaryChain.setTypeName(Type.TypeName.IMAGE);
        } else if (e0.getTypeName().isType(Type.TypeName.FILE) && arrowOp.isKind(Scanner.Kind.ARROW) && e1.getTypeName().isType(Type.TypeName.IMAGE)) {
            binaryChain.setTypeName(Type.TypeName.IMAGE);
        } else if (e0.getTypeName().isType(Type.TypeName.FRAME) && arrowOp.isKind(Scanner.Kind.ARROW)) {
            if (e1 instanceof FrameOpChain) {
                Scanner.Token token = e1.getFirstToken();

                switch (token.kind) {
                    case KW_XLOC:
                    case KW_YLOC:
                        binaryChain.setTypeName(Type.TypeName.INTEGER);
                        break;
                    case KW_SHOW:
                    case KW_HIDE:
                    case KW_MOVE:
                        binaryChain.setTypeName(Type.TypeName.FRAME);
                        break;
                    default:
                        throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
                }
            } else {
                throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
            }
        } else if (e0.getTypeName().isType(Type.TypeName.IMAGE)) {
            if (e1 instanceof ImageOpChain && arrowOp.isKind(Scanner.Kind.ARROW)) {
                Scanner.Token token = e1.getFirstToken();

                switch (token.kind) {
                    case OP_WIDTH:
                    case OP_HEIGHT:
                        binaryChain.setTypeName(Type.TypeName.INTEGER);
                        break;
                    case KW_SCALE:
                        binaryChain.setTypeName(Type.TypeName.IMAGE);
                        break;
                    default:
                        throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
                }
            } else if (e1.getTypeName().isType(Type.TypeName.FRAME) && arrowOp.isKind(Scanner.Kind.ARROW)) {
                binaryChain.setTypeName(Type.TypeName.FRAME);
            } else if (e1.getTypeName().isType(Type.TypeName.FILE) && arrowOp.isKind(Scanner.Kind.ARROW)) {
                binaryChain.setTypeName(Type.TypeName.NONE);
            } else if (e1 instanceof FilterOpChain && (arrowOp.isKind(Scanner.Kind.ARROW) || arrowOp.isKind(Scanner.Kind.BARARROW))) {
                Scanner.Token token = e1.getFirstToken();

                switch (token.kind) {
                    case OP_GRAY:
                    case OP_BLUR:
                    case OP_CONVOLVE:
                        binaryChain.setTypeName(Type.TypeName.IMAGE);
                        break;
                    default:
                        throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
                }
            } else if (e1 instanceof IdentChain && e1.getTypeName().isType(Type.TypeName.IMAGE) && arrowOp.isKind(Scanner.Kind.ARROW)) {
                binaryChain.setTypeName(Type.TypeName.IMAGE);
            } else {
                throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
            }
        } else if (e0.getTypeName().isType(Type.TypeName.INTEGER)) {
            if (e1 instanceof IdentChain && e1.getTypeName().isType(Type.TypeName.INTEGER) && arrowOp.isKind(Scanner.Kind.ARROW)) {
                binaryChain.setTypeName(Type.TypeName.INTEGER);
            } else {
                throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
            }
        } else {
            throw new TypeCheckException("Invalid Binary Chain combination :: '" + e0.getTypeName() + "' " + arrowOp.getText() + " '" + e1.getTypeName() + "'");
        }

        return binaryChain;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
        Objects.requireNonNull(binaryExpression, "TypeCheckVisitor :: BinaryExpression can't be null");
        Expression e0 = binaryExpression.getE0();
        Expression e1 = binaryExpression.getE1();
        Scanner.Token op = binaryExpression.getOp();

        e0.visit(this, null);
        e1.visit(this, null);

        if (op.isKind(Scanner.Kind.PLUS) || op.isKind(Scanner.Kind.MINUS)) {
            if (e0.getTypeName().isType(Type.TypeName.INTEGER) && e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                binaryExpression.setTypeName(Type.TypeName.INTEGER);
            } else if (e0.getTypeName().isType(Type.TypeName.IMAGE) && e1.getTypeName().isType(Type.TypeName.IMAGE)) {
                binaryExpression.setTypeName(Type.TypeName.IMAGE);
            } else {
                throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
            }
        } else if (op.isKind(Scanner.Kind.TIMES)) {
            if (e0.getTypeName().isType(Type.TypeName.INTEGER) && e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                binaryExpression.setTypeName(Type.TypeName.INTEGER);
            } else if (e0.getTypeName().isType(Type.TypeName.INTEGER) && e1.getTypeName().isType(Type.TypeName.IMAGE)) {
                binaryExpression.setTypeName(Type.TypeName.IMAGE);
            } else if (e0.getTypeName().isType(Type.TypeName.IMAGE) && e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                binaryExpression.setTypeName(Type.TypeName.IMAGE);
            } else {
                throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
            }
        } else if (op.isKind(Scanner.Kind.DIV)) {
            if (e0.getTypeName().isType(Type.TypeName.INTEGER) && e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                binaryExpression.setTypeName(Type.TypeName.INTEGER);
            } else {
                throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
            }
        } else if (op.isKind(Scanner.Kind.LT) || op.isKind(Scanner.Kind.GT) || op.isKind(Scanner.Kind.LE) || op.isKind(Scanner.Kind.GE)) {
            if (e0.getTypeName().isType(Type.TypeName.INTEGER) && e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                binaryExpression.setTypeName(Type.TypeName.BOOLEAN);
            } else if (e0.getTypeName().isType(Type.TypeName.BOOLEAN) && e1.getTypeName().isType(Type.TypeName.BOOLEAN)) {
                binaryExpression.setTypeName(Type.TypeName.BOOLEAN);
            } else {
                throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
            }
        } else if ((op.isKind(Scanner.Kind.EQUAL) || op.isKind(Scanner.Kind.NOTEQUAL)) && e0.getTypeName().isType(e1.getTypeName())) {
            binaryExpression.setTypeName(Type.TypeName.BOOLEAN);
        } else if (op.isKind(Scanner.Kind.AND) || op.isKind(Scanner.Kind.OR)) {
            if (e0.getTypeName().isType(Type.TypeName.BOOLEAN) && e1.getTypeName().isType(Type.TypeName.BOOLEAN)) {
                binaryExpression.setTypeName(Type.TypeName.BOOLEAN);
            } else {
                throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
            }
        } else if (op.isKind(Scanner.Kind.MOD)) {
            if (e0.getTypeName().isType(Type.TypeName.INTEGER) && e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                binaryExpression.setTypeName(Type.TypeName.INTEGER);
            } else {
                throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
            }
        } else {
            throw new TypeCheckException("Invalid Binary Expression combination :: '" + e0.getTypeName() + "' '" + op.getText() + "' '" + e1.getTypeName() + "'");
        }

        return binaryExpression;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        Objects.requireNonNull(block, "TypeCheckVisitor :: Block can't be null");
        symtab.enterScope();

        List<Dec> decs = block.getDecs();
        for (Dec dec : decs) {
            dec.visit(this, null);
        }

        List<Statement> statements = block.getStatements();
        for (Statement statement : statements) {
            statement.visit(this, null);
        }

        symtab.leaveScope();
        return block;
    }

    @Override
    public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
        Objects.requireNonNull(booleanLitExpression, "TypeCheckVisitor :: BooleanLitExpression can't be null");
        booleanLitExpression.setTypeName(Type.TypeName.BOOLEAN);
        return booleanLitExpression;
    }

    @Override
    public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
        Objects.requireNonNull(filterOpChain, "TypeCheckVisitor :: FilterOpChain can't be null");
        Tuple tuple = filterOpChain.getArg();

        if (tuple.getExprList().size() != 0) {
            throw new TypeCheckException("Unexpected args present after '" + filterOpChain.getFirstToken().getText() + "' at " + tuple.getFirstToken().getLinePos());
        }

        tuple.visit(this, null);
        filterOpChain.setTypeName(Type.TypeName.IMAGE);
        return filterOpChain;
    }

    @Override
    public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
        Objects.requireNonNull(frameOpChain, "TypeCheckVisitor :: FrameOpChain can't be null");
        Tuple tuple = frameOpChain.getArg();
        Scanner.Token frameOp = frameOpChain.getFirstToken();

        frameOpChain.setKind(frameOp.kind);

        if (frameOp.isKind(Scanner.Kind.KW_SHOW) || frameOp.isKind(Scanner.Kind.KW_HIDE)) {
            if (tuple.getExprList().size() != 0) {
                throw new TypeCheckException("Unexpected args present after '" + frameOp.getText() + "' at " + tuple.getFirstToken().getLinePos());
            }
            frameOpChain.setTypeName(Type.TypeName.NONE);
        } else if (frameOp.isKind(Scanner.Kind.KW_XLOC) || frameOp.isKind(Scanner.Kind.KW_YLOC)) {
            if (tuple.getExprList().size() != 0) {
                throw new TypeCheckException("Unexpected args present after '" + frameOp.getText() + "' at " + tuple.getFirstToken().getLinePos());
            }
            frameOpChain.setTypeName(Type.TypeName.INTEGER);
        } else if (frameOp.isKind(Scanner.Kind.KW_MOVE)) {
            if (tuple.getExprList().size() != 2) {
                throw new TypeCheckException("Expected exactly '2' args, found '" + tuple.getExprList().size() + "' args after '" + frameOp.getText() + "' at " + tuple.getFirstToken().getLinePos());
            }
            frameOpChain.setTypeName(Type.TypeName.NONE);
        } else {
            throw new TypeCheckException("Parser Bug :: Invalid frameOp '" + frameOp.getText() + "' at '" + frameOp.getLinePos() + "'");
        }

        tuple.visit(this, null);
        return frameOpChain;
    }

    @Override
    public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
        Objects.requireNonNull(identChain, "TypeCheckVisitor :: IdentChain can't be null");
        Scanner.Token ident = identChain.getFirstToken();
        Dec dec = symtab.lookup(ident.getText());

        if (dec == null) {
            throw new TypeCheckException("Ident '" + ident.getText() + "' at " + ident.getLinePos() + " is either not declared or not visible in the current scope'");
        }

        identChain.setTypeName(dec.getTypeName());
        return identChain;
    }

    @Override
    public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
        Objects.requireNonNull(identExpression, "TypeCheckVisitor :: IdentExpression can't be null");
        Scanner.Token ident = identExpression.getFirstToken();
        Dec dec = symtab.lookup(ident.getText());

        if (dec == null) {
            throw new TypeCheckException("Ident '" + ident.getText() + "' at " + ident.getLinePos() + " is either not declared or not visible in the current scope'");
        }

        identExpression.setTypeName(dec.getTypeName());
        identExpression.setDec(dec);
        return identExpression;
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
        Objects.requireNonNull(ifStatement, "TypeCheckVisitor :: IfStatement can't be null");
        Expression expression = ifStatement.getE();
        expression.visit(this, null);
        if (!expression.getTypeName().isType(Type.TypeName.BOOLEAN)) {
            throw new TypeCheckException("Expected expression of type 'BOOLEAN' but found '" + expression.getTypeName() + "' at " + expression.getFirstToken().getLinePos());
        }

        Block block = ifStatement.getB();
        block.visit(this, null);
        return ifStatement;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        Objects.requireNonNull(intLitExpression, "TypeCheckVisitor :: IntLitExpression can't be null");
        intLitExpression.setTypeName(Type.TypeName.INTEGER);
        return intLitExpression;
    }

    @Override
    public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
        Objects.requireNonNull(sleepStatement, "TypeCheckVisitor :: SleepStatement can't be null");
        Expression expression = sleepStatement.getE();
        expression.visit(this, null);
        if (!expression.getTypeName().isType(Type.TypeName.INTEGER)) {
            throw new TypeCheckException("Expected expression of type 'INTEGER' but found '" + expression.getTypeName() + "' at " + expression.getFirstToken().getLinePos());
        }
        return sleepStatement;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        Objects.requireNonNull(whileStatement, "TypeCheckVisitor :: WhileStatement can't be null");
        Expression expression = whileStatement.getE();
        expression.visit(this, null);
        if (!expression.getTypeName().isType(Type.TypeName.BOOLEAN)) {
            throw new TypeCheckException("Expected expression of type 'BOOLEAN' but found '" + expression.getTypeName() + "' at " + expression.getFirstToken().getLinePos());
        }

        Block block = whileStatement.getB();
        block.visit(this, null);
        return whileStatement;
    }

    @Override
    public Object visitDec(Dec declaration, Object arg) throws Exception {
        Objects.requireNonNull(declaration, "TypeCheckVisitor :: Dec can't be null");
        Boolean insertionResult = symtab.insert(declaration.getIdent().getText(), declaration);

        if (insertionResult == false) {
            throw new TypeCheckException("Unable to declare param '" + declaration.getIdent().getText() + "' of type '" + declaration.getTypeName() + "'. Check if this variable is already declared in the same scope.");
        }

        declaration.setTypeName(Type.getTypeName(declaration.getType()));
        return declaration;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        Objects.requireNonNull(program, "TypeCheckVisitor :: Program can't be null");
        List<ParamDec> paramDecs = program.getParams();
        for (ParamDec paramDec : paramDecs) {
            paramDec.visit(this, null);
        }
        Block block = program.getB();
        block.visit(this, null);
        return program;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
        Objects.requireNonNull(assignStatement, "TypeCheckVisitor :: AssignmentStatement can't be null");
        IdentLValue identLValue = assignStatement.getVar();
        identLValue.visit(this, null);

        Expression expression = assignStatement.getE();
        expression.visit(this, null);

        if (!identLValue.getDec().getTypeName().isType(expression.getTypeName())) {
            throw new TypeCheckException("Invalid Assignment : '" + identLValue.getDec().getTypeName() + "' <- " + expression.getTypeName() + " at " + identLValue.getDec().getFirstToken().getLinePos());
        }

        return assignStatement;
    }

    @Override
    public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
        Objects.requireNonNull(identX, "TypeCheckVisitor :: IdentLValue can't be null");
        Dec dec = symtab.lookup(identX.getText());

        if (dec == null) {
            throw new TypeCheckException("Ident '" + identX.getText() + "' at " + identX.getFirstToken().getLinePos() + " is either not declared or not visible in the current scope'");
        }

        identX.setDec(dec);
        return identX;
    }

    @Override
    public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
        Objects.requireNonNull(paramDec, "TypeCheckVisitor :: ParamDec can't be null");
        Boolean insertionResult = symtab.insert(paramDec.getIdent().getText(), paramDec);
        if (insertionResult == false) {
            throw new TypeCheckException("Unable to declare param '" + paramDec.getIdent().getText() + "' of type '" + paramDec.getTypeName() + "'. Check if this variable is already declared in the same scope.");
        }

        paramDec.setTypeName(Type.getTypeName(paramDec.getType()));
        return paramDec;
    }

    @Override
    public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
        Objects.requireNonNull(constantExpression, "TypeCheckVisitor :: ConstantExpression can't be null");
        constantExpression.setTypeName(Type.TypeName.INTEGER);
        return constantExpression;
    }

    @Override
    public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
        Objects.requireNonNull(imageOpChain, "TypeCheckVisitor :: ImageOpChain can't be null");
        Tuple tuple = imageOpChain.getArg();
        Scanner.Token imageOp = imageOpChain.getFirstToken();

        imageOpChain.setKind(imageOp.kind);

        if (imageOp.isKind(Scanner.Kind.OP_WIDTH) || imageOp.isKind(Scanner.Kind.OP_HEIGHT)) {
            if (tuple.getExprList().size() != 0) {
                throw new TypeCheckException("Unexpected args present after '" + imageOp.getText() + "' at " + tuple.getFirstToken().getLinePos());
            }
            imageOpChain.setTypeName(Type.TypeName.INTEGER);
        } else if (imageOp.isKind(Scanner.Kind.KW_SCALE)) {
            if (tuple.getExprList().size() != 1) {
                throw new TypeCheckException("Expected exactly '1' arg, found '" + tuple.getExprList().size() + "' args after '" + imageOp.getText() + "' at " + tuple.getFirstToken().getLinePos());
            }
            imageOpChain.setTypeName(Type.TypeName.IMAGE);
        } else {
            throw new TypeCheckException("Parser Bug :: Invalid imageOp '" + imageOp.getText() + "' at '" + imageOp.getLinePos() + "'");
        }

        tuple.visit(this, null);
        return imageOpChain;
    }

    @Override
    public Object visitTuple(Tuple tuple, Object arg) throws Exception {
        Objects.requireNonNull(tuple, "TypeCheckVisitor :: Tuple can't be null");
        List<Expression> expressionList = tuple.getExprList();

        for (Expression expression : expressionList) {
            expression.visit(this, null);

            if (!expression.getTypeName().isType(Type.TypeName.INTEGER)) {
                throw new TypeCheckException("Expected Expressions of type 'Integer' as args, found '" + expression.getTypeName() + "' at " + expression.getFirstToken().getLinePos());
            }
        }

        return tuple;
    }

    @SuppressWarnings("serial")
    public static class TypeCheckException extends Exception {
        TypeCheckException(String message) {
            super(message);
        }
    }


}
