package cop5556sp17;

import cop5556sp17.AST.*;

import java.util.Objects;

public class TypeCheckVisitor implements ASTVisitor {

    @SuppressWarnings("serial")
    public static class TypeCheckException extends Exception {
        TypeCheckException(String message) {
            super(message);
        }
    }

    SymbolTable symtab = new SymbolTable();

    @Override
    public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
        Objects.requireNonNull(booleanLitExpression, "TypeCheckVisitor :: BooleanLitExpression can't be null");
        booleanLitExpression.setTypeName(Type.TypeName.BOOLEAN);
        return booleanLitExpression;
    }

    @Override
    public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        Objects.requireNonNull(intLitExpression, "TypeCheckVisitor :: IntLitExpression can't be null");
        intLitExpression.setTypeName(Type.TypeName.INTEGER);
        return intLitExpression;
    }

    @Override
    public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitDec(Dec declaration, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
        Objects.requireNonNull(constantExpression, "TypeCheckVisitor :: ConstantExpression can't be null");
        constantExpression.setTypeName(Type.TypeName.INTEGER);
        return constantExpression;
    }

    @Override
    public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }

    @Override
    public Object visitTuple(Tuple tuple, Object arg) throws Exception {
        // TODO Auto-generated method stub
        assert false;
        return null;
    }


}