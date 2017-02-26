package cop5556sp17;

import cop5556sp17.Parser.SyntaxException;
import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // program
    @Test
    public void testProgram0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "prog0 {}";
        Parser parser = new Parser(new Scanner(input).scan());
        parser.parse();
    }

    @Test
    public void testProgram1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "prog1 url abc, file def {image xyz sleep 30;}";
        Parser parser = new Parser(new Scanner(input).scan());
        parser.parse();
    }

    @Test
    public void testProgramError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "prog0 ";
        Parser parser = new Parser(new Scanner(input).scan());
        thrown.expect(Parser.SyntaxException.class);
        parser.parse();
    }

    @Test
    public void testProgramError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "prog1 url abc";
        Parser parser = new Parser(new Scanner(input).scan());
        thrown.expect(Parser.SyntaxException.class);
        parser.parse();
    }

    // paramDec
    @Test
    public void testParamDec0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "url abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.paramDec();
    }

    @Test
    public void testParamDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "file abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.paramDec();
    }

    @Test
    public void testParamDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.paramDec();
    }

    @Test
    public void testParamDec3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "boolean abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.paramDec();
    }

    @Test
    public void testParamDecError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "url ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.paramDec();
    }

    // block
    @Test
    public void testBlock0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.block();
    }

    @Test
    public void testBlock1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{sleep 20; }";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.block();
    }

    @Test
    public void testBlock2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{image xyz sleep 30;}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.block();
    }

    @Test
    public void testBlockError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.block();
    }

    @Test
    public void testBlockError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "{sleep 20;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.block();
    }

    // dec
    @Test
    public void testDec0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.dec();
    }

    @Test
    public void testDec1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "boolean abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.dec();
    }

    @Test
    public void testDec2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "image abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.dec();
    }

    @Test
    public void testDec3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "frame abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.dec();
    }

    @Test
    public void testDecError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "integer ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.dec();
    }

    // statement
    @Test
    public void testStatement0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sleep 20 ;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.statement();
    }

    @Test
    public void testStatement1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "while (abc == 32) { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.statement();
    }

    @Test
    public void testStatement2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "if (abc == 32) { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.statement();
    }

    @Test
    public void testStatement3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc -> blur (abc,23) -> show (123,75);";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.statement();
    }

    @Test
    public void testStatement4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc <- 420;";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.statement();
    }

    @Test
    public void testStatementError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "sleep 20";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.statement();
    }

    @Test
    public void testStatementError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "while () { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.statement();
    }

    @Test
    public void testStatementError2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "if () { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.statement();
    }

    @Test
    public void testStatementError3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc -> blur (abc,23) -> show (123,75)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.statement();
    }

    @Test
    public void testStatementError4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc <- 420";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.statement();
    }


    // assign
    @Test
    public void testAssign() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc <- 420";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.assign();
    }

    @Test
    public void testAssignError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc <- ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.assign();
    }

    // chain
    @Test
    public void testChain() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc -> blur abc,23 |-> show 123,75";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.chain();
    }

    @Test
    public void testChainError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.chain();
    }

    // whileStatement
    @Test
    public void testWhile() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "while (abc == 32) { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.whileStatement();
    }

    @Test
    public void testWhileError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "while () { integer def } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.whileStatement();
    }

    // ifStatement
    @Test
    public void testIf() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "if (abc > 32) { sleep 653; } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.ifStatement();
    }

    @Test
    public void testIfError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "if () { sleep 653; } ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.ifStatement();
    }

    // arrowOp
    @Test
    public void testArrowOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "->";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.arrowOp();
    }

    @Test
    public void testArrowOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "|->";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.arrowOp();
    }

    @Test
    public void testArrowOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "!";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.arrowOp();
    }

    // chainElem
    @Test
    public void testChainElem0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.chainElem();
    }

    @Test
    public void testChainElem1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "blur abc,23";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.chainElem();
    }

    @Test
    public void testChainElem2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "hide true, false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.chainElem();
    }

    @Test
    public void testChainElem3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "scale screenwidth,screenheight";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.chainElem();
    }

    @Test
    public void testChainElem4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "xloc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.chainElem();
    }

    @Test
    public void testChainElemError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "!";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.chainElem();
    }

    // filterOp
    @Test
    public void testFilterOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "blur";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.filterOp();
    }

    @Test
    public void testFilterOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "gray";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.filterOp();
    }

    @Test
    public void testFilterOp2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "convolve";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.filterOp();
    }

    @Test
    public void testFilterOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenwidth";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.filterOp();
    }

    // frameOp
    @Test
    public void testFrameOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "show";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.frameOp();
    }

    @Test
    public void testFrameOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "hide";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.frameOp();
    }

    @Test
    public void testFrameOp2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "move";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.frameOp();
    }

    @Test
    public void testFrameOp3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "xloc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.frameOp();
    }

    @Test
    public void testFrameOp4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "yloc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.frameOp();
    }

    @Test
    public void testFrameOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenwidth";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.frameOp();
    }

    // imageOp
    @Test
    public void testImageOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "width";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.imageOp();
    }

    @Test
    public void testImageOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "height";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.imageOp();
    }

    @Test
    public void testImageOp2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "scale";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.imageOp();
    }

    @Test
    public void testImageOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenwidth";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.imageOp();
    }

    // arg
    @Test
    public void testArg0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3,5) ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.arg();
    }

    @Test
    public void testArg1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.arg();
    }

    @Test
    public void testArgError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3,) ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.arg();
    }

    @Test
    public void testArgError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "  (3, 5, ) ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.arg();
    }

    // expression
    @Test
    public void testExpression() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc > def ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.expression();
    }

    @Test
    public void testExpressionError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc >  ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.expression();
    }

    @Test
    public void testExpressionError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc >= def < == ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.expression();
    }

    // term
    @Test
    public void testTerm() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc + def ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.term();
    }

    @Test
    public void testTermError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc +  ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.term();
    }

    @Test
    public void testTermError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc - def | +";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.term();
    }

    // elem
    @Test
    public void testElem() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc * def ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.elem();
    }

    @Test
    public void testElemError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc *  ";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.elem();
    }

    @Test
    public void testElemError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc / def & %";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.elem();
    }

    // factor
    @Test
    public void testFactor0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactor1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "832479";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactor2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "true";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactor3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "false";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactor4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenwidth";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactor5() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "screenheight";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactor6() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "(abc * def + xyz > rst)";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.factor();
    }

    @Test
    public void testFactorError0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "blur";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.factor();
    }

    @Test
    public void testFactorError1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "( abc";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.factor();
    }

    // relOp
    @Test
    public void testRelOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "<";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.relOp();
    }

    @Test
    public void testRelOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "<=";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.relOp();
    }

    @Test
    public void testRelOp2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = ">";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.relOp();
    }

    @Test
    public void testRelOp3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = ">=";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.relOp();
    }

    @Test
    public void testRelOp4() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "==";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.relOp();
    }

    @Test
    public void testRelOp5() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "!=";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.relOp();
    }

    @Test
    public void testRelOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "!";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.relOp();
    }

    // weakOp
    @Test
    public void testWeakOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "+";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.weakOp();
    }

    @Test
    public void testWeakOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "-";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.weakOp();
    }

    @Test
    public void testWeakOp2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "|";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.weakOp();
    }

    @Test
    public void testWeakOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "!";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.weakOp();
    }

    // strongOp
    @Test
    public void testStrongOp0() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "*";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.strongOp();
    }

    @Test
    public void testStrongOp1() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "/";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.strongOp();
    }

    @Test
    public void testStrongOp2() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "&";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.strongOp();
    }

    @Test
    public void testStrongOp3() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "%";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.strongOp();
    }

    @Test
    public void testStrongOpError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "!";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        parser.strongOp();
    }

    // Random Code
    @Test
    public void testRandomCode() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "randProg {i <- 0;\n\nwhile(i < 10){\n\n    if( i % 2 == 0){\n\n        j <- i / 2; \n\n        show(j) -> hide(2);\n\n    }\n\n    i <- i + 1;\n\n}}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        parser.parse();
    }

    @Test
    public void testRandomCodeError() throws IllegalCharException, IllegalNumberException, SyntaxException {
        String input = "randProg {i <- 0;\n\nwhile(i < 10){\n\n    if( i % 2 == 0){\n\n        j <- i / 2; \n\n        show(j) -> hide(2);\n\n    }\n\n    i <- i + 1;\n\n}";
        Scanner scanner = new Scanner(input);
        scanner.scan();
        Parser parser = new Parser(scanner);
        thrown.expect(Parser.SyntaxException.class);
        thrown.expectMessage("saw EOF");
        parser.parse();
    }

}
