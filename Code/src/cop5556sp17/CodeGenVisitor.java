package cop5556sp17;

import com.sun.deploy.security.ValidationState;
import cop5556sp17.AST.*;
import cop5556sp17.AST.Type;
import org.objectweb.asm.*;

import java.util.ArrayList;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

    /**
     * Indicates whether genPrint and genPrintTOS should generate code.
     */
    final boolean DEVEL;
    final boolean GRADE;
    ClassWriter cw;
    String className;
    String classDesc;
    String sourceFileName;
    MethodVisitor mv; // visitor of method currently under construction
    int argIndex;
    int localVariableIndex;

    /**
     * @param DEVEL          used as parameter to genPrint and genPrintTOS
     * @param GRADE          used as parameter to genPrint and genPrintTOS
     * @param sourceFileName name of source file, may be null.
     */
    public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
        super();
        this.DEVEL = DEVEL;
        this.GRADE = GRADE;
        this.sourceFileName = sourceFileName;
        this.argIndex = 0;
        this.localVariableIndex = 1;
    }

    private void visitLocalVariablesInBlock(MethodVisitor methodVisitor, Block block, Label startLabel, Label endLabel) {
        // Note: Based on hw5 use cases, it seems ok to write a common function to reuse code.
        // There is a possibility that in hw6 this function will be removed and the code is duplicated across this file
        for (Dec dec : block.getDecs()) {
            switch (dec.getTypeName()) {
                case INTEGER:
                    methodVisitor.visitLocalVariable(dec.getIdent().getText(), "I", null, startLabel, endLabel, dec.getSlot());
                    break;
                case BOOLEAN:
                    methodVisitor.visitLocalVariable(dec.getIdent().getText(), "Z", null, startLabel, endLabel, dec.getSlot());
                    break;
                case FRAME:
                    methodVisitor.visitLocalVariable(dec.getIdent().getText(), PLPRuntimeFrame.JVMDesc, null, startLabel, endLabel, dec.getSlot());
                    break;
                case IMAGE:
                    methodVisitor.visitLocalVariable(dec.getIdent().getText(), PLPRuntimeImageIO.BufferedImageDesc, null, startLabel, endLabel, dec.getSlot());
                    break;
                default:
                    throw new RuntimeException("Invalid dec: " + dec.getIdent().getText() + " at " + dec.getFirstToken().getLinePos());
            }
        }
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws Exception {
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        className = program.getName();
        classDesc = "L" + className + ";";
        String sourceFileName = (String) arg;
        cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
                new String[]{"java/lang/Runnable"});
        cw.visitSource(sourceFileName, null);

        // generate constructor code
        // get a MethodVisitor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
                null);
        mv.visitCode();
        // Create label at start of code
        Label constructorStart = new Label();
        mv.visitLabel(constructorStart);
        // this is for convenience during development--you can see that the code
        // is doing something.
        CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
        // generate code to call superclass constructor
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        // visit parameter decs to add each as field to the class
        // pass in mv so decs can add their initialization code to the
        // constructor.
        ArrayList<ParamDec> params = program.getParams();
        for (ParamDec dec : params)
            dec.visit(this, mv);
        mv.visitInsn(RETURN);
        // create label at end of code
        Label constructorEnd = new Label();
        mv.visitLabel(constructorEnd);
        // finish up by visiting local vars of constructor
        // the fourth and fifth arguments are the region of code where the local
        // variable is defined as represented by the labels we inserted.
        mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
        mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
        // indicates the max stack size for the method.
        // because we used the COMPUTE_FRAMES parameter in the classwriter
        // constructor, asm
        // will do this for us. The parameters to visitMaxs don't matter, but
        // the method must
        // be called.
        mv.visitMaxs(1, 1);
        // finish up code generation for this method.
        mv.visitEnd();
        // end of constructor

        // create main method which does the following
        // 1. instantiate an instance of the class being generated, passing the
        // String[] with command line arguments
        // 2. invoke the run method.
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
                null);
        mv.visitCode();
        Label mainStart = new Label();
        mv.visitLabel(mainStart);
        // this is for convenience during development--you can see that the code
        // is doing something.
        CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
        mv.visitTypeInsn(NEW, className);
        mv.visitInsn(DUP);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
        mv.visitInsn(RETURN);
        Label mainEnd = new Label();
        mv.visitLabel(mainEnd);
        mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
        mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // create run method
        mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
        mv.visitCode();
        Label startRun = new Label();
        mv.visitLabel(startRun);
        CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
        program.getB().visit(this, null);
        mv.visitInsn(RETURN);
        Label endRun = new Label();
        mv.visitLabel(endRun);
        mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
        visitLocalVariablesInBlock(mv, program.getB(), startRun, endRun);
        mv.visitMaxs(1, 1);
        mv.visitEnd(); // end of run method


        cw.visitEnd();//end of class

        //generate classfile and return it
        return cw.toByteArray();
    }


    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
        assignStatement.getE().visit(this, arg);
        CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
        CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());
        assignStatement.getVar().visit(this, arg);
        return null;
    }

    @Override
    public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
        binaryExpression.getE0().visit(this, null);
        binaryExpression.getE1().visit(this, null);

        Label startLabel;
        Label endLabel;

        Expression e0 = binaryExpression.getE0();
        Expression e1 = binaryExpression.getE1();

        Scanner.Token op = binaryExpression.getOp();

        switch (op.kind) {
            case EQUAL:
                switch (e0.getTypeName()) {
                    case INTEGER:
                    case BOOLEAN:
                        startLabel = new Label();
                        endLabel = new Label();
                        mv.visitJumpInsn(IF_ICMPEQ, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case NOTEQUAL:
                switch (e0.getTypeName()) {
                    case INTEGER:
                    case BOOLEAN:
                        startLabel = new Label();
                        endLabel = new Label();
                        mv.visitJumpInsn(IF_ICMPNE, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case LT:
                switch (e0.getTypeName()) {
                    case INTEGER:
                    case BOOLEAN:
                        startLabel = new Label();
                        endLabel = new Label();
                        mv.visitJumpInsn(IF_ICMPLT, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case GT:
                switch (e0.getTypeName()) {
                    case INTEGER:
                    case BOOLEAN:
                        startLabel = new Label();
                        endLabel = new Label();
                        mv.visitJumpInsn(IF_ICMPGT, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case LE:
                switch (e0.getTypeName()) {
                    case INTEGER:
                    case BOOLEAN:
                        startLabel = new Label();
                        endLabel = new Label();
                        mv.visitJumpInsn(IF_ICMPLE, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case GE:
                switch (e0.getTypeName()) {
                    case INTEGER:
                    case BOOLEAN:
                        startLabel = new Label();
                        endLabel = new Label();
                        mv.visitJumpInsn(IF_ICMPGE, startLabel);
                        mv.visitInsn(ICONST_0);
                        mv.visitJumpInsn(GOTO, endLabel);
                        mv.visitLabel(startLabel);
                        mv.visitInsn(ICONST_1);
                        mv.visitLabel(endLabel);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case PLUS:
                switch (e0.getTypeName()) {
                    case INTEGER:
                        mv.visitInsn(IADD);
                        break;
                    case IMAGE:
                        if(e1.getTypeName().isType(Type.TypeName.IMAGE)) {
                            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", PLPRuntimeImageOps.addSig, false);
                        } else {
                            throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos() + "; " + e1.getTypeName() + " at " + e1.getFirstToken().getLinePos());
                        }
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case MINUS:
                switch (e0.getTypeName()) {
                    case INTEGER:
                        mv.visitInsn(ISUB);
                        break;
                    case IMAGE:
                        if(e1.getTypeName().isType(Type.TypeName.IMAGE)) {
                            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", PLPRuntimeImageOps.subSig, false);
                        } else {
                            throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos() + "; " + e1.getTypeName() + " at " + e1.getFirstToken().getLinePos());
                        }
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case TIMES:
                switch (e0.getTypeName()) {
                    case INTEGER:
                        if(e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                            mv.visitInsn(IMUL);
                        } else if(e1.getTypeName().isType(Type.TypeName.IMAGE)) {
                            mv.visitInsn(SWAP);
                            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
                        } else {
                            throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos() + "; " + e1.getTypeName() + " at " + e1.getFirstToken().getLinePos());
                        }
                        break;
                    case IMAGE:
                        if(e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mul", PLPRuntimeImageOps.mulSig, false);
                        } else {
                            throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos() + "; " + e1.getTypeName() + " at " + e1.getFirstToken().getLinePos());
                        }
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case DIV:
                switch (e0.getTypeName()) {
                    case INTEGER:
                        mv.visitInsn(IDIV);
                        break;
                    case IMAGE:
                        if(e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
                        } else {
                            throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos() + "; " + e1.getTypeName() + " at " + e1.getFirstToken().getLinePos());
                        }
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case MOD:
                switch (e0.getTypeName()) {
                    case INTEGER:
                        mv.visitInsn(IREM);
                        break;
                    case IMAGE:
                        if(e1.getTypeName().isType(Type.TypeName.INTEGER)) {
                            mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);
                        } else {
                            throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos() + "; " + e1.getTypeName() + " at " + e1.getFirstToken().getLinePos());
                        }
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case AND:
                switch (e0.getTypeName()) {
                    case BOOLEAN:
                        mv.visitInsn(IAND);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            case OR:
                switch (e0.getTypeName()) {
                    case BOOLEAN:
                        mv.visitInsn(IOR);
                        break;
                    default:
                        throw new RuntimeException("Unexpected Type: " + e0.getTypeName() + " at " + e0.getFirstToken().getLinePos());
                }
                break;
            default:
                throw new RuntimeException("Invalid op: " + op.getText() + " at " + op.getLinePos());
        }

        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        for (Dec dec : block.getDecs()) {
            dec.visit(this, null);
        }

        for (Statement statement : block.getStatements()) {
            statement.visit(this, null);
            if (statement instanceof BinaryChain) {
                mv.visitInsn(POP);
            }
        }

        return null;
    }

    @Override
    public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
        mv.visitLdcInsn(booleanLitExpression.getValue());
        return null;
    }

    @Override
    public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
        Scanner.Token token = constantExpression.getFirstToken();
        switch (token.kind) {
            case KW_SCREENWIDTH:
                mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
                break;
            case KW_SCREENHEIGHT:
                mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
                break;
            default:
        }
        return null;
    }

    @Override
    public Object visitDec(Dec declaration, Object arg) throws Exception {
        declaration.setSlot(localVariableIndex);
        localVariableIndex++;
        return null;
    }

    @Override
    public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
        Dec dec = identExpression.getDec();
        if (dec instanceof ParamDec) {
            switch (dec.getTypeName()) {
                case INTEGER:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "I");
                    break;
                case BOOLEAN:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, className, dec.getIdent().getText(), "Z");
                    break;
                default:
                    throw new RuntimeException("Invalid ident: " + identExpression.getFirstToken().getText() + " at " + identExpression.getFirstToken().getLinePos());
            }
        } else {
            switch (dec.getTypeName()) {
                case INTEGER:
                    mv.visitVarInsn(ILOAD, dec.getSlot());
                    break;
                case BOOLEAN:
                    mv.visitVarInsn(ILOAD, dec.getSlot());
                    break;
                default:
                    throw new RuntimeException("Invalid ident: " + identExpression.getFirstToken().getText() + " at " + identExpression.getFirstToken().getLinePos());
            }
        }
        return null;
    }

    @Override
    public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
        Dec dec = identX.getDec();
        if (dec instanceof ParamDec) {
            switch (dec.getTypeName()) {
                case INTEGER:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitInsn(SWAP);
                    mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "I");
                    break;
                case BOOLEAN:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitInsn(SWAP);
                    mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), "Z");
                    break;
                case URL:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitInsn(SWAP);
                    mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), Type.TypeName.URL.getJVMTypeDesc());
                    break;
                case FILE:
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitInsn(SWAP);
                    mv.visitFieldInsn(PUTFIELD, className, dec.getIdent().getText(), Type.TypeName.FILE.getJVMTypeDesc());
                    break;
                default:
                    throw new RuntimeException("Invalid dec for assignment: " + dec.getIdent().getText() + " at " + dec.getFirstToken().getLinePos());
            }
        } else {
            switch (dec.getTypeName()) {
                case INTEGER:
                    mv.visitVarInsn(ISTORE, dec.getSlot());
                    break;
                case BOOLEAN:
                    mv.visitVarInsn(ISTORE, dec.getSlot());
                    break;
                case IMAGE:
                    mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "copyImage", PLPRuntimeImageOps.copyImageSig, false);
                    break;
                case FRAME:
                    mv.visitVarInsn(ISTORE, dec.getSlot());
                    break;
                default:
                    throw new RuntimeException("Invalid dec for assignment: " + dec.getIdent().getText() + " at " + dec.getFirstToken().getLinePos());
            }
        }
        return null;

    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
        ifStatement.getE().visit(this, null);
        Label startBlock = new Label();
        Label endBlock = new Label();

        mv.visitJumpInsn(IFEQ, endBlock);

        mv.visitLabel(startBlock);
        ifStatement.getB().visit(this, null);
        mv.visitLabel(endBlock);

        visitLocalVariablesInBlock(mv, ifStatement.getB(), startBlock, endBlock);
        return null;
    }

    @Override
    public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        mv.visitLdcInsn(intLitExpression.getFirstToken().intVal());
        return null;
    }


    @Override
    public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
        //For assignment 5, only needs to handle integers and booleans
        MethodVisitor methodVisitor;
        FieldVisitor fieldVisitor;
        if (arg instanceof MethodVisitor) {
            methodVisitor = (MethodVisitor) arg;
        } else {
            throw new RuntimeException("Illegal arg to visitParamDec");
        }

        paramDec.setSlot(-1); // Slot does not apply to paramDec, hence -1

        switch (paramDec.getTypeName()) {
            case INTEGER:
                fieldVisitor = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), "I", null, null);
                fieldVisitor.visitEnd();
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitLdcInsn(argIndex);
                methodVisitor.visitInsn(AALOAD);
                methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
                methodVisitor.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
                break;
            case BOOLEAN:
                fieldVisitor = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), "Z", null, null);
                fieldVisitor.visitEnd();
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitLdcInsn(argIndex);
                methodVisitor.visitInsn(AALOAD);
                methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
                methodVisitor.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
                break;
            case URL:
                fieldVisitor = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), Type.TypeName.URL.getJVMTypeDesc(), null, null);
                fieldVisitor.visitEnd();
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitLdcInsn(argIndex);
                methodVisitor.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
                methodVisitor.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), Type.TypeName.URL.getJVMTypeDesc());
                break;
            case FILE:
                fieldVisitor = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), Type.TypeName.FILE.getJVMTypeDesc(), null, null);
                fieldVisitor.visitEnd();
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitLdcInsn(argIndex);
                methodVisitor.visitInsn(AALOAD);
                methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
                methodVisitor.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), Type.TypeName.FILE.getJVMTypeDesc());
                break;
            default:
                throw new RuntimeException("Unexpected paramDec " + paramDec.getTypeName());
        }
        argIndex++;

        return null;

    }

    @Override
    public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
        sleepStatement.getE().visit(this, null);
        mv.visitInsn(I2L);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
        return null;
    }

    @Override
    public Object visitTuple(Tuple tuple, Object arg) throws Exception {
        for (Expression expression : tuple.getExprList()) {
            expression.visit(this, null);
        }
        return null;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        Label guard = new Label();
        Label startBlock = new Label();
        Label endBlock = new Label();

        mv.visitJumpInsn(GOTO, guard);

        mv.visitLabel(startBlock);
        whileStatement.getB().visit(this, null);
        mv.visitLabel(endBlock);

        mv.visitLabel(guard);
        whileStatement.getE().visit(this, null);

        mv.visitJumpInsn(IFNE, startBlock);

        visitLocalVariablesInBlock(mv, whileStatement.getB(), startBlock, endBlock);
        return null;
    }

}
