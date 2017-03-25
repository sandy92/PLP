package cop5556sp17;

import cop5556sp17.AST.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
//TODO  visit the local variables
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
//        CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getType()); // TODO the starter jar had getType instead of getTypeName. Will it create a problem ?
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
        //TODO  Implement this
        return null;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws Exception {
        //TODO  Implement this
        return null;
    }

    @Override
    public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
        //TODO Implement this
        return null;
    }

    @Override
    public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitDec(Dec declaration, Object arg) throws Exception {
        //TODO Implement this
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
        //TODO Implement this
        return null;
    }

    @Override
    public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
        //TODO Implement this
        return null;

    }

    @Override
    public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
        //TODO Implement this
        return null;
    }

    @Override
    public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
        //TODO Implement this
        return null;
    }


    @Override
    public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
        //TODO Implement this
        //For assignment 5, only needs to handle integers and booleans
        return null;

    }

    @Override
    public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitTuple(Tuple tuple, Object arg) throws Exception {
        assert false : "not yet implemented";
        return null;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
        //TODO Implement this
        return null;
    }

}
