package cop5556sp17;

import cop5556sp17.AST.*;
import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.assertEquals;

public class ASTTest {

    static final boolean doPrint = true;

    static void show(Object s) {
        if (doPrint) {
            System.out.println(s);
        }
    }


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Program
    @Test
    public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "prog0 {}";
        Parser parser = new Parser(new Scanner(input).scan());
        ASTNode ast = parser.program();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        assertEquals("prog0", program.getName());
        assertEquals(Block.class, program.getB().getClass());
        List<ParamDec> params = program.getParams();
        assertEquals(0, params.size());
    }

    @Test
    public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "prog1 url abc, file def {image xyz sleep 30;}";
        Parser parser = new Parser(new Scanner(input).scan());
        ASTNode ast = parser.program();
        assertEquals(Program.class, ast.getClass());
        Program program = (Program) ast;
        assertEquals("prog0", program.getName());
        assertEquals(Block.class, program.getB().getClass());
        List<ParamDec> params = program.getParams();
        assertEquals(2, params.size());

        assertEquals(ParamDec.class, params.get(0).getClass());
        ParamDec paramDec = params.get(0);
        assertEquals(KW_URL, paramDec.getType().kind);
        assertEquals("abc", paramDec.getIdent().getText());

        assertEquals(ParamDec.class, params.get(1).getClass());
        ParamDec paramDec1 = params.get(1);
        assertEquals(KW_FILE, paramDec1.getType().kind);
        assertEquals("def", paramDec1.getIdent().getText());
    }


    // ParamDec
    @Test
    public void testParamDec0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "url abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.paramDec();
        assertEquals(ParamDec.class, ast.getClass());
        ParamDec dec = (ParamDec) ast;
        assertEquals(KW_URL, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("abc", dec.getIdent().getText());
    }

    @Test
    public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "file xyz";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.paramDec();
        assertEquals(ParamDec.class, ast.getClass());
        ParamDec dec = (ParamDec) ast;
        assertEquals(KW_FILE, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("abc", dec.getIdent().getText());
    }

    @Test
    public void testParamDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer def";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.paramDec();
        assertEquals(ParamDec.class, ast.getClass());
        ParamDec dec = (ParamDec) ast;
        assertEquals(KW_INTEGER, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("abc", dec.getIdent().getText());
    }

    @Test
    public void testParamDec3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "boolean rst";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.paramDec();
        assertEquals(ParamDec.class, ast.getClass());
        ParamDec dec = (ParamDec) ast;
        assertEquals(KW_BOOLEAN, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("abc", dec.getIdent().getText());
    }

    // Block
    @Test
    public void testBlock0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.block();
        assertEquals(Block.class, ast.getClass());
        Block block = (Block) ast;

        List<Dec> decs = block.getDecs();
        List<Statement> statements = block.getStatements();

        assertEquals(0, decs.size());
        assertEquals(0, statements.size());
    }

    @Test
    public void testBlock1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{abc <- 420 + 230; sleep 20;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.block();
        assertEquals(Block.class, ast.getClass());
        Block block = (Block) ast;

        List<Dec> decs = block.getDecs();
        List<Statement> statements = block.getStatements();

        assertEquals(0, decs.size());
        assertEquals(2, statements.size());

        assertEquals(AssignmentStatement.class, statements.get(0).getClass());
        AssignmentStatement assignmentStatement = (AssignmentStatement) statements.get(0);
        assertEquals(BinaryExpression.class, assignmentStatement.getE().getClass());
        assertEquals("abc", assignmentStatement.getVar().getText());

        assertEquals(SleepStatement.class, statements.get(1).getClass());
        SleepStatement sleepStatement = (SleepStatement) statements.get(1);
        assertEquals(IntLitExpression.class, sleepStatement.getE().getClass());
    }

    @Test
    public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{image xyz integer abc}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.block();
        assertEquals(Block.class, ast.getClass());
        Block block = (Block) ast;

        List<Dec> decs = block.getDecs();
        List<Statement> statements = block.getStatements();

        assertEquals(2, decs.size());
        assertEquals(0, statements.size());

        assertEquals(Dec.class, decs.get(0).getClass());
        assertEquals(KW_IMAGE, decs.get(0).getType().kind);
        assertEquals(IDENT, decs.get(0).getIdent().kind);
        assertEquals("xyz", decs.get(0).getIdent().getText());

        assertEquals(Dec.class, decs.get(1).getClass());
        assertEquals(KW_INTEGER, decs.get(1).getType().kind);
        assertEquals(IDENT, decs.get(1).getIdent().kind);
        assertEquals("abc", decs.get(1).getIdent().getText());
    }

    @Test
    public void testBlock3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{integer abc abc <- 420 + 230; sleep 20; image xyz abc <- 660;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.block();
        assertEquals(Block.class, ast.getClass());
        Block block = (Block) ast;

        List<Dec> decs = block.getDecs();
        List<Statement> statements = block.getStatements();

        assertEquals(2, decs.size());
        assertEquals(3, statements.size());

        assertEquals(Dec.class, decs.get(0).getClass());
        assertEquals(KW_INTEGER, decs.get(0).getType().kind);
        assertEquals(IDENT, decs.get(0).getIdent().kind);
        assertEquals("abc", decs.get(0).getIdent().getText());

        assertEquals(Dec.class, decs.get(1).getClass());
        assertEquals(KW_IMAGE, decs.get(1).getType().kind);
        assertEquals(IDENT, decs.get(1).getIdent().kind);
        assertEquals("xyz", decs.get(1).getIdent().getText());

        assertEquals(AssignmentStatement.class, statements.get(0).getClass());
        AssignmentStatement assignmentStatement = (AssignmentStatement) statements.get(0);
        assertEquals(BinaryExpression.class, assignmentStatement.getE().getClass());
        assertEquals("abc", assignmentStatement.getVar().getText());

        assertEquals(SleepStatement.class, statements.get(1).getClass());
        SleepStatement sleepStatement = (SleepStatement) statements.get(1);
        assertEquals(IntLitExpression.class, sleepStatement.getE().getClass());

        assertEquals(AssignmentStatement.class, statements.get(2).getClass());
        AssignmentStatement assignmentStatement1 = (AssignmentStatement) statements.get(2);
        assertEquals(IntLitExpression.class, assignmentStatement.getE().getClass());
        assertEquals("abc", assignmentStatement.getVar().getText());
    }

    // Dec
    @Test
    public void testDec0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.dec();
        assertEquals(Dec.class, ast.getClass());
        Dec dec = (Dec) ast;
        assertEquals(KW_INTEGER, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("abc", dec.getIdent().getText());
    }

    @Test
    public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "boolean xyz";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.dec();
        assertEquals(Dec.class, ast.getClass());
        Dec dec = (Dec) ast;
        assertEquals(KW_BOOLEAN, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("xyz", dec.getIdent().getText());
    }

    @Test
    public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "image def";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.dec();
        assertEquals(Dec.class, ast.getClass());
        Dec dec = (Dec) ast;
        assertEquals(KW_IMAGE, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("def", dec.getIdent().getText());
    }

    @Test
    public void testDec3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "frame rst";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.dec();
        assertEquals(Dec.class, ast.getClass());
        Dec dec = (Dec) ast;
        assertEquals(KW_FRAME, dec.getType().kind);
        assertEquals(IDENT, dec.getIdent().kind);
        assertEquals("rst", dec.getIdent().getText());
    }

    // Statement
    // SleepStatement
    @Test
    public void testSleepStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sleep 20 ;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(SleepStatement.class, ast.getClass());

        SleepStatement sleepStatement = (SleepStatement) ast;
        assertEquals(OP_SLEEP, sleepStatement.getFirstToken().kind);
        assertEquals(IntLitExpression.class, sleepStatement.getE().getClass());
    }

    // IdentLValue
    // AssignmentStatement
    @Test
    public void testAssignStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "xyz <- 420 + 235;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(AssignmentStatement.class, ast.getClass());
        AssignmentStatement assignmentStatement = (AssignmentStatement) ast;

        assertEquals(IdentLValue.class, assignmentStatement.getVar().getClass());
        IdentLValue identLValue = assignmentStatement.getVar();
        assertEquals(IDENT, identLValue.getFirstToken().kind);
        assertEquals("xyz", identLValue.getText());

        assertEquals(BinaryExpression.class, assignmentStatement.getE().getClass());
        BinaryExpression binaryExpression = (BinaryExpression) assignmentStatement.getE();
        assertEquals(IntLitExpression.class, binaryExpression.getE0().getClass());
        assertEquals(IntLitExpression.class, binaryExpression.getE1().getClass());
        assertEquals(PLUS, binaryExpression.getOp().kind);
    }

    // Chain
    // FilterOpChain
    @Test
    public void testFilterOpChain0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "blur (20)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(FilterOpChain.class, ast.getClass());

        FilterOpChain filterOpChain = (FilterOpChain) ast;
        assertEquals(OP_BLUR, filterOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, filterOpChain.getArg().getClass());
        Tuple args = filterOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(1, exprList.size());
        assertEquals(IntLitExpression.class, exprList.get(0).getClass());
    }

    @Test
    public void testFilterOpChain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "gray (abc)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(FilterOpChain.class, ast.getClass());

        FilterOpChain filterOpChain = (FilterOpChain) ast;
        assertEquals(OP_GRAY, filterOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, filterOpChain.getArg().getClass());
        Tuple args = filterOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(1, exprList.size());
        assertEquals(IdentExpression.class, exprList.get(0).getClass());
    }

    @Test
    public void testFilterOpChain2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "convolve";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(FilterOpChain.class, ast.getClass());

        FilterOpChain filterOpChain = (FilterOpChain) ast;
        assertEquals(OP_CONVOLVE, filterOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, filterOpChain.getArg().getClass());
        Tuple args = filterOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(0, exprList.size());
    }

    // FrameOpChain
    @Test
    public void testFrameOpChain0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "show (xyz)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(FrameOpChain.class, ast.getClass());
        FrameOpChain frameOpChain = (FrameOpChain) ast;

        assertEquals(KW_SHOW, frameOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, frameOpChain.getArg().getClass());

        Tuple args = frameOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(1, exprList.size());
        assertEquals(IdentExpression.class, exprList.get(0).getClass());
    }

    @Test
    public void testFrameOpChain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "hide";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(FrameOpChain.class, ast.getClass());
        FrameOpChain frameOpChain = (FrameOpChain) ast;

        assertEquals(KW_HIDE, frameOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, frameOpChain.getArg().getClass());

        Tuple args = frameOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(0, exprList.size());
    }

    @Test
    public void testFrameOpChain2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "move (20,30)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(FrameOpChain.class, ast.getClass());
        FrameOpChain frameOpChain = (FrameOpChain) ast;

        assertEquals(KW_MOVE, frameOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, frameOpChain.getArg().getClass());

        Tuple args = frameOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(2, exprList.size());
        assertEquals(IntLitExpression.class, exprList.get(0).getClass());
        assertEquals(IntLitExpression.class, exprList.get(1).getClass());
    }

    @Test
    public void testFrameOpChain3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "xloc (34)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(FrameOpChain.class, ast.getClass());
        FrameOpChain frameOpChain = (FrameOpChain) ast;

        assertEquals(KW_XLOC, frameOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, frameOpChain.getArg().getClass());

        Tuple args = frameOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(1, exprList.size());
        assertEquals(IntLitExpression.class, exprList.get(0).getClass());
    }

    @Test
    public void testFrameOpChain4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "yloc (983)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(FrameOpChain.class, ast.getClass());
        FrameOpChain frameOpChain = (FrameOpChain) ast;

        assertEquals(KW_YLOC, frameOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, frameOpChain.getArg().getClass());

        Tuple args = frameOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(1, exprList.size());
        assertEquals(IntLitExpression.class, exprList.get(0).getClass());
    }

    // ImageOpChain
    @Test
    public void testImageOpChain0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "width";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(ImageOpChain.class, ast.getClass());
        ImageOpChain imageOpChain = (ImageOpChain) ast;

        assertEquals(OP_WIDTH, imageOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, imageOpChain.getArg().getClass());
        Tuple args = imageOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(0, exprList.size());
    }

    @Test
    public void testImageOpChain1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "height (abc)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(ImageOpChain.class, ast.getClass());
        ImageOpChain imageOpChain = (ImageOpChain) ast;

        assertEquals(OP_HEIGHT, imageOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, imageOpChain.getArg().getClass());
        Tuple args = imageOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(1, exprList.size());

        assertEquals(IdentExpression.class, exprList.get(0).getClass());
    }

    @Test
    public void testImageOpChain2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "scale (20, xyz)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();

        assertEquals(ImageOpChain.class, ast.getClass());
        ImageOpChain imageOpChain = (ImageOpChain) ast;

        assertEquals(KW_SCALE, imageOpChain.getFirstToken().kind);
        assertEquals(Tuple.class, imageOpChain.getArg().getClass());
        Tuple args = imageOpChain.getArg();
        List<Expression> exprList = args.getExprList();
        assertEquals(2, exprList.size());

        assertEquals(IntLitExpression.class, exprList.get(0).getClass());
        assertEquals(IdentExpression.class, exprList.get(1).getClass());
    }

    // IdentChain
    // ChainElem
    // BinaryChain
    @Test
    public void testBinaryChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc -> blur (abc,23) |-> show (123,75);";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(BinaryChain.class, ast.getClass());

        BinaryChain binaryChain = (BinaryChain) ast;
        assertEquals(BinaryChain.class, binaryChain.getE0().getClass());
        assertEquals(FrameOpChain.class, binaryChain.getE1().getClass());
        assertEquals(BARARROW, binaryChain.getArrow().kind);

        BinaryChain binaryChain1 = (BinaryChain) binaryChain.getE0();
        assertEquals(IdentChain.class, binaryChain1.getE0().getClass());
        IdentChain identChain = (IdentChain) binaryChain1.getE0();
        assertEquals(IDENT, identChain.getFirstToken().kind);
        assertEquals("abc", identChain.getFirstToken().getText());
        assertEquals(FilterOpChain.class, binaryChain1.getE1().getClass());
        assertEquals(ARROW, binaryChain.getArrow().kind);
    }

    // WhileStatement
    @Test
    public void testWhileStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "while (true) { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(WhileStatement.class, ast.getClass());

        WhileStatement whileStatement = (WhileStatement) ast;
        assertEquals(BooleanLitExpression.class, whileStatement.getE().getClass());
        assertEquals(Block.class, whileStatement.getB().getClass());
    }

    // IfStatement
    @Test
    public void testIfStatement() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "if (abc > 32) { sleep 653; } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.statement();
        assertEquals(IfStatement.class, ast.getClass());

        IfStatement ifStatement = (IfStatement) ast;
        assertEquals(Block.class, ifStatement.getB().getClass());
        assertEquals(BinaryExpression.class, ifStatement.getE().getClass());
    }

    // Expression
    // IdentExpression
    @Test
    public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(IdentExpression.class, ast.getClass());
        IdentExpression identExpression = (IdentExpression) ast;
        assertEquals(identExpression.getFirstToken().kind, Scanner.Kind.IDENT);
    }

    // IntLitExpression
    @Test
    public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "123";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(IntLitExpression.class, ast.getClass());
        IntLitExpression intLitExpression = (IntLitExpression) ast;
        assertEquals(intLitExpression.getFirstToken().kind, Scanner.Kind.INT_LIT);
    }

    // BooleanLitExpression
    @Test
    public void testBooleanLitExpression0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(BooleanLitExpression.class, ast.getClass());

        BooleanLitExpression booleanLitExpression = (BooleanLitExpression) ast;
        assertEquals(KW_TRUE, booleanLitExpression.getFirstToken().kind);
    }

    @Test
    public void testBooleanLitExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(BooleanLitExpression.class, ast.getClass());

        BooleanLitExpression booleanLitExpression = (BooleanLitExpression) ast;
        assertEquals(KW_FALSE, booleanLitExpression.getFirstToken().kind);
    }

    // ConstantExpression
    @Test
    public void testConstantExpression0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenwidth";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(ConstantExpression.class, ast.getClass());

        ConstantExpression constantExpression = (ConstantExpression) ast;
        assertEquals(KW_SCREENWIDTH, constantExpression.getFirstToken().kind);
    }

    @Test
    public void testConstantExpression1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenheight";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(ConstantExpression.class, ast.getClass());

        ConstantExpression constantExpression = (ConstantExpression) ast;
        assertEquals(KW_SCREENHEIGHT, constantExpression.getFirstToken().kind);
    }


    // BinaryExpression
    @Test
    public void testBinaryExpr0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "1+abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(BinaryExpression.class, ast.getClass());
        BinaryExpression be = (BinaryExpression) ast;
        assertEquals(IntLitExpression.class, be.getE0().getClass());
        assertEquals(IdentExpression.class, be.getE1().getClass());
        assertEquals(PLUS, be.getOp().kind);
    }

    @Test
    public void testBinaryExprForLeftAssociativity() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "5-3-2";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.expression();
        assertEquals(BinaryExpression.class, ast.getClass());
        BinaryExpression be = (BinaryExpression) ast;
        assertEquals(BinaryExpression.class, be.getE0().getClass());
        assertEquals(IntLitExpression.class, be.getE1().getClass());
        assertEquals(MINUS, be.getOp().kind);

        BinaryExpression be2 = (BinaryExpression) be.getE0();
        assertEquals(IntLitExpression.class, be2.getE0().getClass());
        assertEquals(IntLitExpression.class, be2.getE1().getClass());
        assertEquals(MINUS, be2.getOp().kind);
    }

    // Tuple
    @Test
    public void testTuple0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (abc, 5, false, screenheight, 2*3+5 , true, screenwidth)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.arg();
        assertEquals(Tuple.class, ast.getClass());
        Tuple tuple = (Tuple) ast;
        List<Expression> expressionList = tuple.getExprList();
        assertEquals(7, expressionList.size());
        Expression expression;

        expression = expressionList.get(0);
        assertEquals(IdentExpression.class, expression.getClass());
        IdentExpression identExpression = (IdentExpression) ast;
        assertEquals(IDENT, identExpression.getFirstToken().kind);

        expression = expressionList.get(1);
        assertEquals(IntLitExpression.class, expression.getClass());
        IntLitExpression intLitExpression = (IntLitExpression) ast;
        assertEquals(INT_LIT, intLitExpression.getFirstToken().kind);

        expression = expressionList.get(2);
        assertEquals(BooleanLitExpression.class, expression.getClass());
        BooleanLitExpression booleanLitExpression1 = (BooleanLitExpression) ast;
        assertEquals(KW_FALSE, booleanLitExpression1.getFirstToken().kind);

        expression = expressionList.get(3);
        assertEquals(ConstantExpression.class, expression.getClass());
        ConstantExpression constantExpression1 = (ConstantExpression) ast;
        assertEquals(KW_SCREENHEIGHT, constantExpression1.getFirstToken().kind);

        expression = expressionList.get(4);
        assertEquals(BinaryExpression.class, expression.getClass());

        expression = expressionList.get(5);
        assertEquals(BooleanLitExpression.class, expression.getClass());
        BooleanLitExpression booleanLitExpression2 = (BooleanLitExpression) ast;
        assertEquals(KW_TRUE, booleanLitExpression2.getFirstToken().kind);

        expression = expressionList.get(6);
        assertEquals(ConstantExpression.class, expression.getClass());
        ConstantExpression constantExpression2 = (ConstantExpression) ast;
        assertEquals(KW_SCREENWIDTH, constantExpression2.getFirstToken().kind);
    }

    @Test
    public void testTuple1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.arg();
        assertEquals(Tuple.class, ast.getClass());
        Tuple tuple = (Tuple) ast;
        List<Expression> expressionList = tuple.getExprList();
        assertEquals(0, expressionList.size());
    }

    @Test
    public void testRandomCode() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "randProg {i <- 0;\n\nwhile(i < 10){\n\n integer abc   if( i % 2 == 0){\n\n        j <- i / 2; \n\n        show(j) -> width(2);\n\n    }\n\n    i <- i + 1;\n\n}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        ASTNode ast = parser.parse();

        assertEquals(Program.class, ast.getClass());

        Program program = (Program) ast;
        assertEquals("randProg", program.getName());
        assertEquals(0, program.getParams().size());
        assertEquals(Block.class, program.getB().getClass());

        Block block = program.getB();
        assertEquals(0, block.getDecs().size());
        assertEquals(2, block.getStatements().size());

        assertEquals(AssignmentStatement.class, block.getStatements().get(0));
        assertEquals(WhileStatement.class, block.getStatements().get(1));

        WhileStatement whileStatement = (WhileStatement) block.getStatements().get(1);

        assertEquals(Block.class, whileStatement.getB().getClass());

        Block whileBlock = whileStatement.getB();

        assertEquals(1, whileBlock.getDecs().size());
        assertEquals(2, whileBlock.getStatements().size());

        assertEquals(IfStatement.class, block.getStatements().get(0));
        assertEquals(AssignmentStatement.class, block.getStatements().get(1));

        IfStatement ifStatement = (IfStatement) whileBlock.getStatements().get(0);

        assertEquals(Block.class, whileStatement.getB().getClass());

        Block ifBlock = ifStatement.getB();

        assertEquals(0, ifBlock.getDecs().size());
        assertEquals(2, ifBlock.getStatements().size());

        assertEquals(AssignmentStatement.class, ifBlock.getStatements().get(0));
        assertEquals(BinaryChain.class, ifBlock.getStatements().get(1));

        BinaryChain binaryChain = (BinaryChain) ifBlock.getStatements().get(1);

        assertEquals(FrameOpChain.class, binaryChain.getE0().getClass());
        assertEquals(ImageOpChain.class, binaryChain.getE1().getClass());
        assertEquals(ARROW, binaryChain.getArrow().kind);
    }
}
