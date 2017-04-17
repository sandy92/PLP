package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.Program;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.URL;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static org.junit.Assert.assertEquals;

public class CodeGenVisitorTest {

    static final boolean doPrint = true;
    boolean devel = false;
    boolean grade = true;

    public ExpectedException thrown = ExpectedException.none();

    static void show(Object s) {
        if (doPrint) {
            System.out.println(s);
        }
    }

    private void assertProgramValidity(String inputCode, String[] args, String expectedOutput) throws Exception {
        Scanner scanner = new Scanner(inputCode);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);

        CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
        byte[] bytecode = (byte[]) program.visit(cv, null);

        String programName = program.getName();

        String classFileName = "bin/" + programName + ".class";
        OutputStream output = new FileOutputStream(classFileName);
        output.write(bytecode);
        output.close();

        Runnable instance = CodeGenUtils.getInstance(programName, bytecode, args);
        instance.run();

        String actualOutput = PLPRuntimeLog.getString();

        assertEquals("Invalid Output\n -------------------- \n" + inputCode + "\n --------------------", expectedOutput, actualOutput);
    }

    @Before
    public void initLog() throws Exception {
        if (devel || grade) PLPRuntimeLog.initLog();
    }

    @After
    public void printLog() throws Exception {
        System.out.println(PLPRuntimeLog.getString());
    }

    @Test
    public void emptyProg() throws Exception {
        //scan, parse, and type check the program
        String progname = "emptyProg";
        String input = progname + "  {}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
        show(program);

        //generate code
        CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
        byte[] bytecode = (byte[]) program.visit(cv, null);

        //output the generated bytecode
        CodeGenUtils.dumpBytecode(bytecode);

        //write byte code to file 
        String name = ((Program) program).getName();
        String classFileName = "bin/" + name + ".class";
        OutputStream output = new FileOutputStream(classFileName);
        output.write(bytecode);
        output.close();
        System.out.println("wrote classfile to " + classFileName);

        // directly execute bytecode
        String[] args = new String[0]; //create command line argument array to initialize params, none in this case
        Runnable instance = CodeGenUtils.getInstance(name, bytecode, args);
        instance.run();
    }

    @Test
    public void testEmptyProgWithAssertions() throws Exception {
        String inputCode = "emptyProg {}";
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "");
    }

    @Test
    public void testParamDecProg0() throws Exception {
        String[] input = new String[]{
                "paramDecProg0 integer b, boolean c {",
                "c <- c;",
                "b <- b;",
                "b <- 2;",
                "c <- true;",
                "c <- false;",
                "b <- 3;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{"1", "false"};

        assertProgramValidity(inputCode, args, "false12truefalse3");
    }

    @Test
    public void testdecProg0() throws Exception {
        String[] input = new String[]{
                "decProg0 integer a {",
                "integer b boolean c boolean d",
                "a <- 3;",
                "b <- 4;",
                "c <- false;",
                "b <- 5;",
                "d <- true;",
                "a <- 6;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{"1"};

        assertProgramValidity(inputCode, args, "34false5true6");
    }

    @Test
    public void testdecProg1() throws Exception {
        String[] input = new String[]{
                "decProg1 {",
                "integer b boolean c",
                "b <- 5 % 3;",
                "b <- 15 % 10;",
                "b <- 4 % 2;",
                "b <- 8 % 7 % 5;",
                "c <- true | true;",
                "c <- true | false;",
                "c <- false | true;",
                "c <- false | false;",
                "c <- true & true;",
                "c <- true & false;",
                "c <- false & true;",
                "c <- false & false;",
                "c <- false & false | true;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "2501truetruetruefalsetruefalsefalsefalsetrue");
    }

    @Test
    public void testIfStatementProg0() throws Exception {
        String[] input = new String[]{
                "ifStatementProg0 integer a {",
                "boolean b",
                "b <- true;",
                "if(b) { a <- 4; }",
                "b <- false;",
                "if(b) { a <- 5; }",
                "b <- true;",
                "if(b) { a <- 6; }",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{"0"};

        assertProgramValidity(inputCode, args, "true4falsetrue6");
    }

    @Test
    public void testIfStatementProg1() throws Exception {
        String[] input = new String[]{
                "ifStatementProg1 integer a {",
                "if(2 != 3) { a <- 1; }",
                "if(2 == 3) { a <- 2; }",
                "if(5 != 5) { a <- 3; }",
                "if(5 == 5) { a <- 4; }",
                "if(false == false) { a <- 5; }",
                "if(false != false) { a <- 6; }",
                "if(true == false) { a <- 7; }",
                "if(true != false) { a <- 8; }",
                "if(true != true) { a <- 9; }",
                "if(true == true) { a <- 10; }",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{"0"};

        assertProgramValidity(inputCode, args, "145810");
    }

    @Test
    public void testIfStatementProg2() throws Exception {
        String[] input = new String[]{
                "ifStatementProg2 {",
                "integer a",
                "if(5 == 5) { a <- 4; }",
                "if(false < false) { a <- 5; }",
                "if(false <= false) { a <- 6; }",
                "if(true <= false) { a <- 7; }",
                "if(true >= false) { a <- 8; }",
                "if(true >= true) { a <- 9; }",
                "if(true > false) { a <- 10; }",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "468910");
    }

    @Test
    public void testWhileStatementProg0() throws Exception {
        String[] input = new String[]{
                "whileStatementProg0 {",
                "integer a boolean b",
                "a <- 1;",
                "while(a <= 3) {a <- a + 1;}",
                "while(a > 1) {a <- a - 1;}",
                "while(a < 4) {a <- a * 2;}",
                "while(a >= 2) {a <- a / 2;}",
                "b <- false;",
                "while(b) { b <- true; }",
                "b <- true;",
                "while(b) { b <- false; }",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "12343212421falsetruefalse");
    }

    @Test
    public void testWhileStatementProg1() throws Exception {
        String[] input = new String[]{
                "whileStatementProg1 {",
                "integer a boolean b integer c boolean d",
                "a <- 1;",
                "b <- true;",
                "while(a < 3){ if(b){ c <- 7; while(c <= 8){ boolean a a <-false; c <- c +1; } } a <- a+1; if(2+3 == 1+4) {d <- false;} }",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "1true7false8false92false7false8false93false");
    }

    @Test
    public void testSleepProg() throws Exception {
        String fileName = "sleepProg";
        String[] input = new String[]{
                fileName + " {",
                "sleep 2880;",
                "}"
        };
        String inputCode = String.join("\n", input);
        Scanner scanner = new Scanner(inputCode);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);

        CodeGenVisitor cv = new CodeGenVisitor(devel, grade, null);
        byte[] bytecode = (byte[]) program.visit(cv, null);

        String programName = program.getName();
        String[] args = new String[0];
        Runnable instance = CodeGenUtils.getInstance(programName, bytecode, args);
        long start = System.currentTimeMillis();
        instance.run();
        long end = System.currentTimeMillis();
        assertEquals("Expected program runtime atleast 2880ms, got " + (end - start) + "ms", true, (end - start) >= 2880);
    }

    @Test
    public void testdecProgFromCompilerClass() throws Exception {
        String[] input = new String[]{
                "decProgFromCompilerClass integer a {",
                "integer b boolean c boolean d",
                "a <- 3;",
                "b <- 4;",
                "c <- false;",
                "b <- 5;",
                "d <- true;",
                "a <- 6;",
                "}"
        };
        String inputCode = String.join("\n", input);

        File currentDirectory = new File("./bin");
        File sourceCode = File.createTempFile("decProgFromCompilerClass", null, currentDirectory);
        sourceCode.deleteOnExit();

        FileWriter fileWriter = new FileWriter(sourceCode);
        fileWriter.write(inputCode);
        fileWriter.close();
        String[] args = new String[]{sourceCode.getAbsolutePath()};
        Compiler.main(args);
    }

    @Test
    public void testCopyImageFromURLProg() throws Exception {
        String progName = "CopyImageFromURLProg";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a integer i",
                "i <- 9;",
                "u -> a;",
                "a <- a;",
                "i <- 7;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "9copyimage7");
    }

    @Test
    public void testFrameFromURLProg() throws Exception {
        String progName = "FrameFromURLProg";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a frame fr integer i",
                "i <- 9;",
                "u -> a -> fr;",
                "i <- 7;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "9copyimage7");
    }

    @Test
    public void testCopyImageFromFileProg() throws Exception {
        String progName = "CopyImageFromFileProg";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);

        File currentDirectory = new File(".");
        File tempFile = File.createTempFile(progName, "jpg", currentDirectory);
        tempFile.deleteOnExit();

        ImageIO.write(image, "jpg", tempFile);

        String[] input = new String[]{
                progName + " file f {",
                "image a integer i",
                "i <- 9;",
                "f -> a;",
                "a <- a;",
                "i <- 8;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{tempFile.getAbsolutePath()};

        assertProgramValidity(inputCode, args, "9copyimage8");
    }

    @Test
    public void testFrameFromFileProg() throws Exception {
        String progName = "FrameFromFileProg";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);

        File currentDirectory = new File(".");
        File tempFile = File.createTempFile(progName, "jpg", currentDirectory);
        tempFile.deleteOnExit();

        ImageIO.write(image, "jpg", tempFile);

        String[] input = new String[]{
                progName + " file f {",
                "image a frame fr integer i",
                "i <- 9;",
                "f -> a -> fr;",
                "i <- 8;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{tempFile.getAbsolutePath()};

        assertProgramValidity(inputCode, args, "9copyimage8");
    }

    @Test
    public void testImageOps0() throws Exception {
        String progName = "ImageOps0";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b",
                "u -> a;",
                "b <- a + a;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "add");
    }

    @Test
    public void testImageOps1() throws Exception {
        String progName = "ImageOps1";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b",
                "u -> a;",
                "b <- a - a;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "sub");
    }

    @Test
    public void testImageOps2() throws Exception {
        String progName = "ImageOps2";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a;",
                "b <- a * 2;",
                "c <- 3 * a;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "mul");
    }

    @Test
    public void testImageOps3() throws Exception {
        String progName = "ImageOps0";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b",
                "u -> a;",
                "b <- a / 2;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "div");
    }

    @Test
    public void testImageOps4() throws Exception {
        String progName = "ImageOps0";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b",
                "u -> a;",
                "b <- a % 3;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "mod");
    }

    @Test
    public void testConstantExpressionProg() throws Exception {
        String progName = "ConstantExpressionProg";

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();

        screenSize.setSize(250, 200);

        String[] input = new String[]{
                progName + " {",
                "integer a integer b",
                "a <- screenwidth;",
                "b <- screenheight;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "getScreenwidth250getScreenheight200");

        screenSize.setSize(width, height); // resetting to previous value
    }


    @Test
    public void testIdentChainProgForIntegers() throws Exception {
        String progName = "IdentChainProgForIntegers";

        String[] input = new String[]{
                progName + " {",
                "integer a integer b",
                "b <- 7;",
                "a <- 12650;",
                "a -> b;",
                "b <- b;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[0];

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testIdentChainProgForWritingToFile() throws Exception {
        // Cases for frame and image are already handled in the assignment test cases above
        String progName = "IdentChainProgForWritingToFile";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        File currentDirectory = new File(".");
        File tempFile = File.createTempFile(progName, "jpg", currentDirectory);
        tempFile.deleteOnExit();

        String[] input = new String[]{
                progName + " url u, file f {",
                "image i",
                "u -> i -> f;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl, tempFile.getAbsolutePath()};

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testFilterOpChainProg0() throws Exception {
        String progName = "FilterOpChainProg0";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a -> blur -> b ;",
                "u -> c |-> blur;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testFilterOpChainProg1() throws Exception {
        String progName = "FilterOpChainProg1";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a -> gray -> b ;",
                "u -> c |-> gray;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testFilterOpChainProg2() throws Exception {
        String progName = "FilterOpChainProg2";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a -> convolve -> b ;",
                "u -> c |-> convolve;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testFilterOpChainErrorProg0() throws Exception {
        String progName = "FilterOpChainErrorProg0";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a |-> blur -> b ;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
        thrown.expect(RuntimeException.class);
    }

    @Test
    public void testFilterOpChainErrorProg1() throws Exception {
        String progName = "FilterOpChainErrorProg1";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a |-> gray -> b ;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
        thrown.expect(RuntimeException.class);
    }

    @Test
    public void testFilterOpChainErrorProg2() throws Exception {
        String progName = "FilterOpChainErrorProg2";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a image b image c",
                "u -> a |-> convolve -> b ;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
        thrown.expect(RuntimeException.class);
    }

    @Test
    public void testFrameOpChainProg0() throws Exception {
        String progName = "FrameOpChainProg0";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a frame fr",
                "u -> a -> fr -> show ;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testFrameOpChainProg1() throws Exception {
        String progName = "FrameOpChainProg1";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a frame fr",
                "u -> a -> fr -> hide ;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "71265012650");
    }

    @Test
    public void testFrameOpChainProg2() throws Exception {
        String progName = "FrameOpChainProg2";
        String imageUrl = "https://i.ytimg.com/vi/ySTQk6updjQ/hqdefault.jpg?custom=true&w=336&h=188&stc=true&jpg444=true&jpgq=90&sp=68&sigh=zL6XTS6LOp88YKMB_oJyLBKgJgA";

        String[] input = new String[]{
                progName + " url u {",
                "image a frame fr integer i",
                "i <- 9;",
                "u -> a -> fr -> move (36, 87);",
                "fr -> xloc -> i",
                "i <- i",
                "fr -> yloc -> i",
                "i <- i",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{imageUrl};

        assertProgramValidity(inputCode, args, "9movexloc36yloc87");
    }

    @Test
    public void testImageOpChainProg() throws Exception {
        String progName = "ImageOpChainProg";
        BufferedImage image = new BufferedImage(45, 65, TYPE_INT_RGB);

        File currentDirectory = new File(".");
        File tempFile = File.createTempFile(progName, "jpg", currentDirectory);
        tempFile.deleteOnExit();

        String[] input = new String[]{
                progName + " file f {",
                "image a image b integer w integer h",
                "w <- 8;",
                "u -> a -> width -> w ;",
                "w <- w;",
                "h <- 8;",
                "a -> height -> h ;",
                "h <- h;",
                "a -> scale (2) -> b;",
                "b -> width -> w ;",
                "w <- w;",
                "b -> height -> h ;",
                "h <- h;",
                "}"
        };
        String inputCode = String.join("\n", input);
        String[] args = new String[]{tempFile.getAbsolutePath()};

        assertProgramValidity(inputCode, args, "8width458height6590130");
    }

    // TODO remove
    // String imageUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
    // String value = new String(Files.readAllBytes(Paths.get(args[0])));
    // String[] args = new String[] { imageUrl, progName+"-file.png"};
    // TODO does % and other ops apply to images
    // TODO all image ops
    // TODO read from file (extend this from read from URL)
    // TODO read from URL and other
}
