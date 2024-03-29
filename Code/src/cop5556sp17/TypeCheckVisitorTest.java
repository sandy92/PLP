/**
 * Important to test the error cases in case the
 * AST is not being completely traversed.
 * <p>
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import cop5556sp17.AST.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TypeCheckVisitorTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Program
    @Test
    public void testRandProg() throws Exception {
        String input = "randProg {integer i \n i <- 0;\n\nwhile(i < 10){\n\n integer abc   if( i == 0){\n\n  integer j frame x     j <- i / 2; \n\n      x ->  show -> xloc;\n\n    }\n\n    i <- i + 1;\n\n}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
    }

    // ParamDec
    @Test
    public void testParamDec() throws Exception {
        String input = "p url a, file b, integer c, boolean d {boolean y \ny <- true;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testParamDecWithSameNameAsProgramName() throws Exception {
        String input = "p file p {boolean y \ny <- true;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testParamDecError() throws Exception {
        String input = "p file p, url p {boolean y \ny <- true;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // Dec
    @Test
    public void testDec() throws Exception {
        String input = "p {integer a boolean b image c frame d \n b <- true;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testDecWithSameNameAsProgramName() throws Exception {
        String input = "p {boolean p \n p <- true;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testDecError() throws Exception {
        String input = "p {integer p boolean p \n p <- true;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // Statement
    // SleepStatement
    @Test
    public void testSleepStatement() throws Exception {
        String input = "sleep 20 ;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Statement statement = parser.statement();
        assertEquals(SleepStatement.class, statement.getClass());

        SleepStatement sleepStatement = (SleepStatement) statement;
        Expression expression = sleepStatement.getE();

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        sleepStatement.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.INTEGER, expression.getTypeName());
    }

    @Test
    public void testSleepStatementError() throws Exception {
        String input = "sleep true ;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Statement statement = parser.statement();
        assertEquals(SleepStatement.class, statement.getClass());

        SleepStatement sleepStatement = (SleepStatement) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        sleepStatement.visit(typeCheckVisitor, null);
    }

    // AssignmentStatement
    @Test
    public void testAssignmentBoolLit0() throws Exception {
        String input = "p {\nboolean y \ny <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }

    @Test
    public void testAssignmentIntLit() throws Exception {
        String input = "p {\ninteger y \ny <- 1;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }

    @Test
    public void testAssignmentImage() throws Exception {
        String input = "p {\nimage x image y \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }

    @Test
    public void testAssignmentFrame() throws Exception {
        String input = "p {\nframe x frame y \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        program.visit(v, null);
    }

    @Test
    public void testAssignmentBoolLitError0() throws Exception {
        String input = "p {\nboolean y \ny <- 3;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }

    @Test
    public void testAssignmentIntLitError() throws Exception {
        String input = "p {\ninteger y \ny <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }

    @Test
    public void testAssignmentError0() throws Exception {
        String input = "p {\nimage y \ny <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }

    @Test
    public void testAssignmentError1() throws Exception {
        String input = "p {\nframe y \ny <- false;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode program = parser.parse();
        TypeCheckVisitor v = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);
        program.visit(v, null);
    }

    // Chain
    // ChainElem
    // IdentChain
    @Test
    public void testIdentChain() throws Exception {
        String input = "p {\nimage x image y \nx -> y;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        List<Dec> decs = block.getDecs();
        assertEquals(1, statements.size());
        assertEquals(2, decs.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        ChainElem e1 = binaryChain.getE1();
        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(IdentChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, e0.getTypeName());
        assertEquals(Type.TypeName.IMAGE, e1.getTypeName());
    }

    @Test
    public void testIdentChainWhenNotDeclared() throws Exception {
        String input = "p {\ninteger y \ny -> x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(IdentChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testIdentChainWhenDeclaredButNotVisible() throws Exception {
        String input = "p {\ninteger y \nif(true) {integer x} \ny -> x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(IdentChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // FilterOpChain
    @Test
    public void testFilterOpChain() throws Exception {
        String input = "p {blur -> gray;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(FilterOpChain.class, e0.getClass());
        assertEquals(FilterOpChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, e0.getTypeName());
        assertEquals(Type.TypeName.IMAGE, e1.getTypeName());
    }

    @Test
    public void testFilterOpChainError() throws Exception {
        String input = "p {blur -> gray (12);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(FilterOpChain.class, e0.getClass());
        assertEquals(FilterOpChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // FrameOpChain
    @Test
    public void testFrameOpChain0() throws Exception {
        String input = "p {frame a \n a -> show -> yloc;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(BinaryChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        BinaryChain binaryChain0 = (BinaryChain) e0;
        FrameOpChain frameOpChain1 = (FrameOpChain) e1;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.FRAME, binaryChain0.getTypeName());
        assertEquals(Type.TypeName.INTEGER, frameOpChain1.getTypeName());
        assertEquals(Scanner.Kind.KW_YLOC, frameOpChain1.getKind());

        e0 = binaryChain0.getE0();
        e1 = binaryChain0.getE1();

        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        frameOpChain1 = (FrameOpChain) e1;

        assertEquals(Type.TypeName.NONE, frameOpChain1.getTypeName());
        assertEquals(Scanner.Kind.KW_SHOW, frameOpChain1.getKind());
    }

    @Test
    public void testFrameOpChain1() throws Exception {
        String input = "p {frame a \n a -> hide -> xloc;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(BinaryChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        BinaryChain binaryChain0 = (BinaryChain) e0;
        FrameOpChain frameOpChain1 = (FrameOpChain) e1;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.FRAME, binaryChain0.getTypeName());
        assertEquals(Type.TypeName.INTEGER, frameOpChain1.getTypeName());
        assertEquals(Scanner.Kind.KW_XLOC, frameOpChain1.getKind());

        e0 = binaryChain0.getE0();
        e1 = binaryChain0.getE1();

        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        frameOpChain1 = (FrameOpChain) e1;

        assertEquals(Type.TypeName.NONE, frameOpChain1.getTypeName());
        assertEquals(Scanner.Kind.KW_HIDE, frameOpChain1.getKind());
    }

    @Test
    public void testFrameOpChain2() throws Exception {
        String input = "p {frame a \n a -> move (23,53) -> show -> xloc;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(BinaryChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        FrameOpChain frameOpChain2 = (FrameOpChain) e1;

        BinaryChain binaryChain1 = (BinaryChain) e0;
        Chain e2 = binaryChain1.getE0();
        Chain e3 = binaryChain1.getE1();
        assertEquals(BinaryChain.class, e2.getClass());
        assertEquals(FrameOpChain.class, e3.getClass());

        binaryChain1 = (BinaryChain) e2;

        FrameOpChain frameOpChain1 = (FrameOpChain) e3;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.NONE, frameOpChain1.getTypeName());
        assertEquals(Type.TypeName.INTEGER, frameOpChain2.getTypeName());

        assertEquals(Scanner.Kind.KW_SHOW, frameOpChain1.getKind());
        assertEquals(Scanner.Kind.KW_XLOC, frameOpChain2.getKind());

        e2 = binaryChain1.getE0();
        e3 = binaryChain1.getE1();

        assertEquals(IdentChain.class, e2.getClass());
        assertEquals(FrameOpChain.class, e3.getClass());

        frameOpChain2 = (FrameOpChain) e3;

        assertEquals(Scanner.Kind.KW_MOVE, frameOpChain2.getKind());


    }

    @Test
    public void testFrameOpChainError0() throws Exception {
        String input = "p {frame a \n a -> show (23);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testFrameOpChainError1() throws Exception {
        String input = "p {frame a \n a -> hide (23);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(IdentChain.class, e0.getClass());
        assertEquals(FrameOpChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testFrameOpChainError2() throws Exception {
        String input = "p {frame a \n a -> move (23);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e2 = binaryChain.getE0();
        Chain e3 = binaryChain.getE1();
        assertEquals(IdentChain.class, e2.getClass());
        assertEquals(FrameOpChain.class, e3.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);

    }

    // ImageOpChain
    @Test
    public void testImageOpChain0() throws Exception {
        String input = "p {scale (122) -> width;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(ImageOpChain.class, e0.getClass());
        assertEquals(ImageOpChain.class, e1.getClass());

        ImageOpChain imageOpChain0 = (ImageOpChain) e0;
        ImageOpChain imageOpChain1 = (ImageOpChain) e1;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, imageOpChain0.getTypeName());
        assertEquals(Type.TypeName.INTEGER, imageOpChain1.getTypeName());

        assertEquals(Scanner.Kind.KW_SCALE, imageOpChain0.getKind());
        assertEquals(Scanner.Kind.OP_WIDTH, imageOpChain1.getKind());
    }

    @Test
    public void testImageOpChain1() throws Exception {
        String input = "p {scale (23) -> height;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(ImageOpChain.class, e0.getClass());
        assertEquals(ImageOpChain.class, e1.getClass());

        ImageOpChain imageOpChain0 = (ImageOpChain) e0;
        ImageOpChain imageOpChain1 = (ImageOpChain) e1;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, imageOpChain0.getTypeName());
        assertEquals(Type.TypeName.INTEGER, imageOpChain1.getTypeName());

        assertEquals(Scanner.Kind.KW_SCALE, imageOpChain0.getKind());
        assertEquals(Scanner.Kind.OP_HEIGHT, imageOpChain1.getKind());
    }

    @Test
    public void testImageOpChainError0() throws Exception {
        String input = "p {width -> scale;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(ImageOpChain.class, e0.getClass());
        assertEquals(ImageOpChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testImageOpChainError1() throws Exception {
        String input = "p {scale (23) -> height (23);}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;
        Chain e0 = binaryChain.getE0();
        Chain e1 = binaryChain.getE1();
        assertEquals(ImageOpChain.class, e0.getClass());
        assertEquals(ImageOpChain.class, e1.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // BinaryChain
    @Test
    public void testBinaryChain0() throws Exception {
        String input = "p url a {image b \n a -> b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryChain.getTypeName());
    }

    @Test
    public void testBinaryChain1() throws Exception {
        String input = "p file a {image b \n a -> b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryChain.getTypeName());
    }

    @Test
    public void testBinaryChain2() throws Exception {
        String input = "p {image a frame b \n a -> b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.FRAME, binaryChain.getTypeName());
    }

    @Test
    public void testBinaryChain3() throws Exception {
        String input = "p file b {image a \n a -> b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.NONE, binaryChain.getTypeName());
    }

    @Test
    public void testBinaryChain5() throws Exception {
        List<String> inputList = new ArrayList<>();
        inputList.add("p {frame a \n a -> xloc;}");
        inputList.add("p {frame a \n a -> yloc;}");
        inputList.add("p {image a \n a -> width;}");
        inputList.add("p {image a \n a -> height;}");

        for (String input : inputList) {
            Scanner scanner = new Scanner(input);
            scanner.scan();
            Parser parser = new Parser(scanner);
            Program program = (Program) parser.parse();
            Block block = program.getB();
            List<Statement> statements = block.getStatements();
            assertEquals(1, statements.size());
            Statement statement = statements.get(0);
            assertEquals(BinaryChain.class, statement.getClass());
            BinaryChain binaryChain = (BinaryChain) statement;

            TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
            program.visit(typeCheckVisitor, null);

            assertEquals(Type.TypeName.INTEGER, binaryChain.getTypeName());
        }
    }

    @Test
    public void testBinaryChain6() throws Exception {
        List<String> inputList = new ArrayList<>();
        inputList.add("p {frame a \n a -> show;}");
        inputList.add("p {frame a \n a -> hide;}");
        inputList.add("p {frame a \n a -> move (23,34);}");

        for (String input : inputList) {
            Scanner scanner = new Scanner(input);
            scanner.scan();
            Parser parser = new Parser(scanner);
            Program program = (Program) parser.parse();
            Block block = program.getB();
            List<Statement> statements = block.getStatements();
            assertEquals(1, statements.size());
            Statement statement = statements.get(0);
            assertEquals(BinaryChain.class, statement.getClass());
            BinaryChain binaryChain = (BinaryChain) statement;

            TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
            program.visit(typeCheckVisitor, null);

            assertEquals(Type.TypeName.FRAME, binaryChain.getTypeName());
        }
    }

    @Test
    public void testBinaryChain7() throws Exception {
        List<String> inputList = new ArrayList<>();
        inputList.add("p {image a \n a -> gray;}");
        inputList.add("p {image a \n a |-> gray;}");
        inputList.add("p {image a \n a -> blur;}");
        inputList.add("p {image a \n a |-> blur;}");
        inputList.add("p {image a \n a -> convolve;}");
        inputList.add("p {image a \n a |-> convolve;}");
        inputList.add("p {image a \n a -> scale (10);}");

        for (String input : inputList) {
            Scanner scanner = new Scanner(input);
            scanner.scan();
            Parser parser = new Parser(scanner);
            Program program = (Program) parser.parse();
            Block block = program.getB();
            List<Statement> statements = block.getStatements();
            assertEquals(1, statements.size());
            Statement statement = statements.get(0);
            assertEquals(BinaryChain.class, statement.getClass());
            BinaryChain binaryChain = (BinaryChain) statement;

            TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
            program.visit(typeCheckVisitor, null);

            assertEquals(Type.TypeName.IMAGE, binaryChain.getTypeName());
        }
    }

    @Test
    public void testBinaryChain8() throws Exception {
        String input = "p {image a image b \n a -> b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryChain.getTypeName());
    }

    @Test
    public void testBinaryChain9() throws Exception {
        String input = "p {integer a integer b \n a -> b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Program program = (Program) parser.parse();
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(BinaryChain.class, statement.getClass());
        BinaryChain binaryChain = (BinaryChain) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.INTEGER, binaryChain.getTypeName());
    }

    @Test
    public void testBinaryChainError() throws Exception {
        List<String> inputList = new ArrayList<>();
        inputList.add("p {image a frame b \n a |-> b;}");
        inputList.add("p {frame a \n a |-> xloc;}");
        inputList.add("p {frame a \n a |-> yloc;}");
        inputList.add("p {image a \n a |-> width;}");
        inputList.add("p {image a \n a |-> height;}");
        inputList.add("p {frame a \n a |-> show;}");
        inputList.add("p {frame a \n a |-> hide;}");
        inputList.add("p {frame a \n a |-> move (23,34);}");
        inputList.add("p {image a \n a |-> scale (10);}");
        inputList.add("p {image a integer b \n a -> b;}");
        inputList.add("p {image a integer b \n b -> a;}");
        inputList.add("p {image a boolean b \n a -> b;}");

        for (String input : inputList) {
            Scanner scanner = new Scanner(input);
            scanner.scan();
            Parser parser = new Parser(scanner);
            Program program = (Program) parser.parse();
            Block block = program.getB();
            List<Statement> statements = block.getStatements();
            assertEquals(1, statements.size());
            Statement statement = statements.get(0);
            assertEquals(BinaryChain.class, statement.getClass());

            TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
            thrown.expect(TypeCheckVisitor.TypeCheckException.class);

            program.visit(typeCheckVisitor, null);
        }
    }

    // WhileStatement
    @Test
    public void testWhileStatement0() throws Exception {
        String input = "p {boolean y \ny <- true; while(y) {y <- false;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(WhileStatement.class, statement.getClass());
        WhileStatement whileStatement = (WhileStatement) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        Expression expression = whileStatement.getE();
        assertEquals(Type.TypeName.BOOLEAN, expression.getTypeName());
    }

    @Test
    public void testWhileStatement1() throws Exception {
        String input = "p {boolean y \ny <- true; while(false) {y <- false;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(WhileStatement.class, statement.getClass());
        WhileStatement whileStatement = (WhileStatement) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        Expression expression = whileStatement.getE();
        assertEquals(Type.TypeName.BOOLEAN, expression.getTypeName());
    }

    @Test
    public void testWhileStatementError0() throws Exception {
        String input = "p {integer y \ny <- 1; while(y) {y <- 0;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(WhileStatement.class, statement.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testWhileStatementError1() throws Exception {
        String input = "p {integer y \ny <- 1; while(0) {y <- 0;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(WhileStatement.class, statement.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // IfStatement
    @Test
    public void testIfStatement0() throws Exception {
        String input = "p {boolean y \ny <- true; if(y) {y <- false;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(IfStatement.class, statement.getClass());
        IfStatement ifStatement = (IfStatement) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        Expression expression = ifStatement.getE();
        assertEquals(Type.TypeName.BOOLEAN, expression.getTypeName());
    }

    @Test
    public void testIfStatement1() throws Exception {
        String input = "p {boolean y \ny <- true; if(false) {y <- false;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(IfStatement.class, statement.getClass());
        IfStatement ifStatement = (IfStatement) statement;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        Expression expression = ifStatement.getE();
        assertEquals(Type.TypeName.BOOLEAN, expression.getTypeName());
    }

    @Test
    public void testIfStatementError0() throws Exception {
        String input = "p {integer y \ny <- 1; if(y) {y <- 0;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(IfStatement.class, statement.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testIfStatementError1() throws Exception {
        String input = "p {integer y \ny <- 1; if(0) {y <- 0;} }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(IfStatement.class, statement.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // Expression
    // IdentExpression
    @Test
    public void testIdentExpression() throws Exception {
        String input = "p {\ninteger x integer y \nx <- 1; \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        List<Dec> decs = block.getDecs();
        assertEquals(2, statements.size());
        assertEquals(2, decs.size());
        Statement statement = statements.get(1);
        assertEquals(AssignmentStatement.class, statement.getClass());
        AssignmentStatement assignmentStatement2 = (AssignmentStatement) statement;
        Expression expression = assignmentStatement2.getE();
        assertEquals(IdentExpression.class, expression.getClass());
        IdentExpression identExpression = (IdentExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
        assertEquals(Type.TypeName.INTEGER, identExpression.getTypeName());
        assertEquals(decs.get(0), identExpression.getDec());
    }

    @Test
    public void testIdentExpressionWhenNotDeclared() throws Exception {
        String input = "p {\ninteger y \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(AssignmentStatement.class, statement.getClass());
        AssignmentStatement assignmentStatement = (AssignmentStatement) statement;
        Expression expression = assignmentStatement.getE();
        assertEquals(IdentExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testIdentExpressionWhenDeclaredButNotVisible() throws Exception {
        String input = "p {\ninteger y \nif(true) {integer x} \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(AssignmentStatement.class, statement.getClass());
        AssignmentStatement assignmentStatement = (AssignmentStatement) statement;
        Expression expression = assignmentStatement.getE();
        assertEquals(IdentExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // IdentLValue
    @Test
    public void testIdentLValue() throws Exception {
        String input = "p {\ninteger x integer y \nx <- 1; \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        List<Dec> decs = block.getDecs();
        assertEquals(2, statements.size());
        assertEquals(2, decs.size());
        Statement statement = statements.get(1);
        assertEquals(AssignmentStatement.class, statement.getClass());
        AssignmentStatement assignmentStatement2 = (AssignmentStatement) statement;
        IdentLValue identLValue = assignmentStatement2.getVar();

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);
        assertEquals(decs.get(1), identLValue.getDec());
    }

    @Test
    public void testIdentLValueWhenNotDeclared() throws Exception {
        String input = "p {\ninteger x \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        Statement statement = statements.get(0);
        assertEquals(AssignmentStatement.class, statement.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testIdentLValueWhenDeclaredButNotVisible() throws Exception {
        String input = "p {\ninteger x \nif(true) {integer y} \ny <- x;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(2, statements.size());
        Statement statement = statements.get(1);
        assertEquals(AssignmentStatement.class, statement.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    // IntLitExpression
    @Test
    public void testIntLitExpression() throws Exception {
        String input = "123";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(IntLitExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        expression.visit(typeCheckVisitor, null);

        IntLitExpression intLitExpression = (IntLitExpression) expression;
        assertEquals(Type.TypeName.INTEGER, intLitExpression.getTypeName());
    }

    // BooleanLitExpression
    @Test
    public void testBooleanLitExpression0() throws Exception {
        String input = "true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BooleanLitExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        expression.visit(typeCheckVisitor, null);

        BooleanLitExpression booleanLitExpression = (BooleanLitExpression) expression;
        assertEquals(Type.TypeName.BOOLEAN, booleanLitExpression.getTypeName());
    }

    @Test
    public void testBooleanLitExpression1() throws Exception {
        String input = "false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BooleanLitExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        expression.visit(typeCheckVisitor, null);

        BooleanLitExpression booleanLitExpression = (BooleanLitExpression) expression;
        assertEquals(Type.TypeName.BOOLEAN, booleanLitExpression.getTypeName());
    }

    // ConstantExpression
    @Test
    public void testConstantExpression0() throws Exception {
        String input = "screenwidth";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(ConstantExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        expression.visit(typeCheckVisitor, null);

        ConstantExpression constantExpression = (ConstantExpression) expression;
        assertEquals(Type.TypeName.INTEGER, constantExpression.getTypeName());
    }

    @Test
    public void testConstantExpression1() throws Exception {
        String input = "screenheight";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(ConstantExpression.class, expression.getClass());

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        expression.visit(typeCheckVisitor, null);

        ConstantExpression constantExpression = (ConstantExpression) expression;
        assertEquals(Type.TypeName.INTEGER, constantExpression.getTypeName());
    }

    // BinaryExpression
    @Test
    public void testBinaryExpr0() throws Exception {
        String input = "1+2-3";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.INTEGER, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr1() throws Exception {
        String input = "p { image a image b image c \n a <- a + b - c;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr2() throws Exception {
        String input = "3*4/6";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.INTEGER, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr3() throws Exception {
        String input = "p { image a image b \n a <- a * 2;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr4() throws Exception {
        String input = "p { image a image b \n a <- 3 * b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr5() throws Exception {
        List<String> inputList = new ArrayList<>();
        inputList.add("1 < 2");
        inputList.add("2 <= 3");
        inputList.add("3 > 4");
        inputList.add("4 >= 5");

        for (String input : inputList) {
            Scanner scanner = new Scanner(input);
            scanner.scan();
            Parser parser = new Parser(scanner);
            Expression expression = parser.expression();
            assertEquals(BinaryExpression.class, expression.getClass());
            BinaryExpression binaryExpression = (BinaryExpression) expression;

            TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
            binaryExpression.visit(typeCheckVisitor, null);

            assertEquals(Type.TypeName.BOOLEAN, binaryExpression.getTypeName());
        }
    }

    @Test
    public void testBinaryExpr6() throws Exception {
        String input = "false < true <= false > true >= false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.BOOLEAN, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr7() throws Exception {
        String input = "1 == 2";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.BOOLEAN, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr8() throws Exception {
        String input = "true != false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.BOOLEAN, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr9() throws Exception {
        String input = "4 % 3";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.INTEGER, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr10() throws Exception {
        String input = "false | true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.BOOLEAN, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr11() throws Exception {
        String input = "false & true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        binaryExpression.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.BOOLEAN, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr12() throws Exception {
        String input = "p { image a image b \n a <- b / 3;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExpr13() throws Exception {
        String input = "p { image a image b \n a <- b % 3;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        program.visit(typeCheckVisitor, null);

        assertEquals(Type.TypeName.IMAGE, binaryExpression.getTypeName());
    }

    @Test
    public void testBinaryExprError0() throws Exception {
        String input = "2 * true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError1() throws Exception {
        String input = "p { image a image b \n a <- a / b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError2() throws Exception {
        String input = "p { image a image b \n a <- a * b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError3() throws Exception {
        String input = "p { image a image b \n a <- 2 / b;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        Block block = program.getB();
        List<Statement> statements = block.getStatements();
        assertEquals(1, statements.size());
        AssignmentStatement statement = (AssignmentStatement) statements.get(0);
        Expression expression = statement.getE();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        program.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError5() throws Exception {
        String input = "false < 4";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError6() throws Exception {
        String input = "true == 1";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError7() throws Exception {
        String input = "0 != false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError8() throws Exception {
        String input = "4 % true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError9() throws Exception {
        String input = "false % 3";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError10() throws Exception {
        String input = "4 & true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    @Test
    public void testBinaryExprError11() throws Exception {
        String input = "false | 1";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Expression expression = parser.expression();
        assertEquals(BinaryExpression.class, expression.getClass());
        BinaryExpression binaryExpression = (BinaryExpression) expression;

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        binaryExpression.visit(typeCheckVisitor, null);
    }

    // Tuple
    @Test
    public void testTupleForNonIntegerExpressions() throws Exception {
        String input = "  (5, false, screenheight, 2*3+5 , 6>4, screenwidth)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Tuple tuple = parser.arg();

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        thrown.expect(TypeCheckVisitor.TypeCheckException.class);

        tuple.visit(typeCheckVisitor, null);
    }

    @Test
    public void testTupleForIntegerExpressions() throws Exception {
        String input = "  (5, screenheight / 2, 2*3+5 , screenwidth - 10)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Tuple tuple = parser.arg();

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        tuple.visit(typeCheckVisitor, null);
    }

    @Test
    public void testTupleForEmptyListOfExpressions() throws Exception {
        String input = "  ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        Tuple tuple = parser.arg();

        TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor();
        tuple.visit(typeCheckVisitor, null);
    }

}
