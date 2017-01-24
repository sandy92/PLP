package cop5556sp17;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Objects;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.*;

public class ScannerTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private void checkTokenValidity(Kind expectedKind, int expectedPos, String expectedText, Token actualToken) {
        Objects.requireNonNull(expectedKind, "ScannerTest.checkTokenValidity :: expectedKind can't be null");
        Objects.requireNonNull(expectedKind, "ScannerTest.checkTokenValidity :: expectedText can't be null");

        if (expectedPos < 0) {
            throw new IllegalArgumentException("ScannerTest.checkTokenValidity :: expectedPos can't be negative");
        }

        assertEquals(expectedKind, actualToken.kind);
        assertEquals(expectedPos, actualToken.pos);
        assertEquals(expectedText.length(), actualToken.length);
        assertEquals(expectedText, actualToken.getText());
    }

    @Test
    public void testEmpty() throws IllegalCharException, IllegalNumberException {
        String input = "";
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.nextToken();

        assertEquals(EOF, token.kind);
    }

    @Test
    public void testSemiConcat() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ";;;";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(SEMI, token.kind);
        assertEquals(0, token.pos);
        String text = SEMI.getText();
        assertEquals(text.length(), token.length);
        assertEquals(text, token.getText());
        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        assertEquals(SEMI, token1.kind);
        assertEquals(1, token1.pos);
        assertEquals(text.length(), token1.length);
        assertEquals(text, token1.getText());
        Scanner.Token token2 = scanner.nextToken();
        assertEquals(SEMI, token2.kind);
        assertEquals(2, token2.pos);
        assertEquals(text.length(), token2.length);
        assertEquals(text, token2.getText());
        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(Scanner.Kind.EOF, token3.kind);
    }

    @Test
    public void testComma() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ",,,";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(COMMA, 0, COMMA.getText(), token);

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(COMMA, 1, COMMA.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(COMMA, 2, COMMA.getText(), token2);

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testNextToken() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ";(}";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(SEMI, 0, SEMI.getText(), token);

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LPAREN, 1, LPAREN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(RBRACE, 2, RBRACE.getText(), token2);

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testNextTokenWhenAtEOF() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "";

        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(EOF, token.kind);

        Scanner.Token token1 = scanner.nextToken();
        assertNull("Scanner.nextToken :: expected null when nextToken is invoked after EOF", token1);
    }


    @Test
    public void testPeekForEmptyInput() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "";

        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.peek();
        assertEquals(EOF, token.kind);
    }

    @Test
    public void testPeek() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ";(}";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.peek();
        checkTokenValidity(SEMI, 0, SEMI.getText(), token);
        scanner.nextToken();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.peek();
        checkTokenValidity(LPAREN, 1, LPAREN.getText(), token1);
        scanner.nextToken();

        Scanner.Token token2 = scanner.peek();
        checkTokenValidity(RBRACE, 2, RBRACE.getText(), token2);
        scanner.nextToken();

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.peek();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testPeekForMultipleInvocations() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ";";

        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        Scanner.Token token = scanner.peek();
        Scanner.Token token1 = scanner.peek();

        Scanner.Token token2 = scanner.nextToken();

        checkTokenValidity(SEMI, 0, SEMI.getText(), token);
        assertEquals("Scanner.peek :: subsequent peeks return different objects", token, token1);
        assertEquals("Scanner.peek :: peek does not show the next token", token, token2);

        Scanner.Token token3 = scanner.peek();
        assertEquals(EOF, token3.kind);
        assertNotEquals("Scanner.peek :: the state of the scanner is not changed after calling nextToken", token, token3);
    }

    @Test
    public void testPeekWhenAtEOF() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "";

        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the first token and check its kind, position, and contents
        scanner.nextToken();

        Scanner.Token token = scanner.peek();
        assertNull("Scanner.peek :: expected null when peek is invoked after EOF", token);
    }


    /**
     * This test illustrates how to check that the Scanner detects errors properly.
     * In this test, the input contains an int literal with a value that exceeds the range of an int.
     * The scanner should detect this and throw and IllegalNumberException.
     *
     * @throws IllegalCharException
     * @throws IllegalNumberException
     */
    @Test
    public void testIntOverflowError() throws IllegalCharException, IllegalNumberException {
        String input = "99999999999999999";
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalNumberException.class);
        scanner.scan();
    }

    @Test
    public void testZero() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "0";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(INT_LIT, 0, "0", token);

        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EOF, token1.kind);
    }

    @Test
    public void testMultipleZeros() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "00000";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        for (int i = 0; i < 5; i++) {
            Scanner.Token token = scanner.nextToken();
            checkTokenValidity(INT_LIT, i, "0", token);
        }

        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EOF, token1.kind);
    }

    @Test
    public void testDigit() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "4";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents

        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(INT_LIT, 0, "4", token);

        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EOF, token1.kind);
    }

    @Test
    public void testMultipleDigits() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "4931";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents

        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(INT_LIT, 0, "4931", token);

        Scanner.Token token1 = scanner.nextToken();
        assertEquals(EOF, token1.kind);
    }

    @Test
    public void testMultipleIntLit() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "0049310\n582";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents

        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(INT_LIT, 0, "0", token);

        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(INT_LIT, 1, "0", token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(INT_LIT, 2, "49310", token2);

        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(INT_LIT, 8, "582", token3);

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(EOF, token4.kind);
    }

    @Test
    public void testGetLinePos() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "23748 2837;\n23478 753426 283754;\n 328 457 829437;";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents

        Scanner.Token token;
        Scanner.LinePos linePos;

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(0, 0), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(0, 6), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(0, 10), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(1, 0), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(1, 6), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(1, 13), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(1, 19), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(2, 1), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(2, 5), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(2, 9), linePos);

        token = scanner.nextToken();
        linePos = scanner.getLinePos(token);
        assertEquals("Scanner.getLinePos :: Invalid Line Pos", new Scanner.LinePos(2, 15), linePos);

        token = scanner.nextToken();
        assertEquals(EOF, token.kind);
    }


    // TODO "single token" tests - should contain test cases where input results in a single token
    // TODO "multiple tokens in a single line" tests - should contain test cases where input results in multiple tokens in single line
    // TODO "multiple lines" tests
    // TODO "negative tests"


    // Single Line Inputs
    @Test
    public void testSingleLineInputForSpaces() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "{ () }";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(LBRACE, 0, LBRACE.getText(), token);

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LPAREN, 2, LPAREN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(RPAREN, 3, RPAREN.getText(), token2);

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(RBRACE, 5, RBRACE.getText(), token3);

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(EOF, token4.kind);
    }

    // Multi Line Inputs
    @Test
    public void testMultiLineInputForSpaces() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "{ () \n }";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        checkTokenValidity(LBRACE, 0, LBRACE.getText(), token);

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LPAREN, 2, LPAREN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(RPAREN, 3, RPAREN.getText(), token2);

        //check that the scanner has inserted an EOF token at the end
        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(RBRACE, 7, RBRACE.getText(), token3);

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(EOF, token4.kind);
    }

    // Comments
    @Test
    public void testSingleLineComments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "/* this is a * random comment */";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(EOF, token.kind);
    }

    @Test
    public void testMultiLineComments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "/* this is a \n multiline comment */";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(EOF, token.kind);
    }

    @Test
    public void testMultiLineCommentsWithForbiddenChars() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "/* this is a \n multiline comment \n with invalid characters such as \n ^,@,# etc. */";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();
        //get the first token and check its kind, position, and contents
        Scanner.Token token = scanner.nextToken();
        assertEquals(EOF, token.kind);
    }

    @Test
    public void testUnclosedSingleLineComments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "/* this is an unclosed single line comment";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("encountered EOF");
        scanner.scan();
    }

    @Test
    public void testUnclosedMultiLineComments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "/* this is an unclosed \n multi line comment";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("encountered EOF");
        scanner.scan();
    }

    @Test
    public void testInlineSingleLineComments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "(/* this is an inline single line comment*/)";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LPAREN, 0, LPAREN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(RPAREN, 43, RPAREN.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testInlineMultiLineComments() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "(/* this is an inline \n multi line comment*/)";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LPAREN, 0, LPAREN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(RPAREN, 44, RPAREN.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    // Operators
    @Test
    public void testOr() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "||";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OR, 0, OR.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OR, 1, OR.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testAnd() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "&&";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(AND, 0, AND.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(AND, 1, AND.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testEqual() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "====";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(EQUAL, 0, EQUAL.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(EQUAL, 2, EQUAL.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testNotEqual() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!=!=";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(NOTEQUAL, 0, NOTEQUAL.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(NOTEQUAL, 2, NOTEQUAL.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testLT() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<<";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LT, 0, LT.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(LT, 1, LT.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testGT() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ">>";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(GT, 0, GT.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(GT, 1, GT.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testLE() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<=<=";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(LE, 0, LE.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(LE, 2, LE.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testGE() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ">=>=";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(GE, 0, GE.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(GE, 2, GE.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testLTEqualGTthrowsException() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<==>";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("Unexpected char encountered: '>' at Line=0, pos=3");
        scanner.scan();
    }

    @Test
    public void testPlus() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "++";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(PLUS, 0, PLUS.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(PLUS, 1, PLUS.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testMinus() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "--";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(MINUS, 0, MINUS.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(MINUS, 1, MINUS.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testTimes() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "**";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(TIMES, 0, TIMES.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(TIMES, 1, TIMES.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testDiv() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "//";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(DIV, 0, DIV.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(DIV, 1, DIV.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testMod() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "%%";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(MOD, 0, MOD.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(MOD, 1, MOD.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testNot() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!!";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(NOT, 0, NOT.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(NOT, 1, NOT.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testNotEqualGT() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!=>";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(NOTEQUAL, 0, NOTEQUAL.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(GT, 2, GT.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testArrow() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "->->";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(ARROW, 0, ARROW.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(ARROW, 2, ARROW.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testBarArrow() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "|->|->";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(BARARROW, 0, BARARROW.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(BARARROW, 3, BARARROW.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testAssign() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<-<-";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(ASSIGN, 0, ASSIGN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(ASSIGN, 2, ASSIGN.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testAssignArrow() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<-->";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(ASSIGN, 0, ASSIGN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(ARROW, 2, ARROW.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testAssignBarArrow() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<-|->";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(ASSIGN, 0, ASSIGN.getText(), token1);

        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(BARARROW, 2, BARARROW.getText(), token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testNotEqualThrowsException() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "!==";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("Invalid Token - encountered EOF after '='");
        scanner.scan();
    }

    @Test
    public void testGEThrowsException() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = ">==";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("Invalid Token - encountered EOF after '='");
        scanner.scan();
    }

    @Test
    public void testLEThrowsException() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "<==";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("Invalid Token - encountered EOF after '='");
        scanner.scan();
    }

    // Keywords
    @Test
    public void testB() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "b";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "b", token1);

        Scanner.Token token2 = scanner.nextToken();
        assertEquals(EOF, token2.kind);
    }

    @Test
    public void testBB() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "bb";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "bb", token1);

        Scanner.Token token2 = scanner.nextToken();
        assertEquals(EOF, token2.kind);
    }

    @Test
    public void testBlur() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "blurblur";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_BLUR, 0, "blur", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_BLUR, 4, "blur", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testBoolean() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "booleanboolean";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_BOOLEAN, 0, "boolean", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_BOOLEAN, 7, "boolean", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testC() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "c";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "c", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testCC() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "cc";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "cc", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testConvolve() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "convolveconvolve";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_CONVOLVE, 0, "convolve", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_CONVOLVE, 8, "convolve", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testF() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "f";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "f", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testFF() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ff";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "ff", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testFalse() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "falsefalse";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_FALSE, 0, "false", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_FALSE, 5, "false", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testFile() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "filefile";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_FILE, 0, "file", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_FILE, 4, "file", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testFrame() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "frameframe";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_FRAME, 0, "frame", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_FRAME, 5, "frame", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testG() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "g";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "g", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testGG() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "gg";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "gg", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testGray() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "graygray";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_GRAY, 0, "gray", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_GRAY, 4, "gray", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testH() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "h";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "h", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testHH() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "hh";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "hh", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testHeight() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "heightheight";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_HEIGHT, 0, "height", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_HEIGHT, 6, "height", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testHide() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "hidehide";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_HIDE, 0, "hide", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_HIDE, 4, "hide", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    // TODO tests for 'Capitalised keywords are idents'

    @Test
    public void testI() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "i";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "i", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testII() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ii";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "ii", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIf() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ifif";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_IF, 0, "if", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_IF, 2, "if", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testImage() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "imageimage";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_IMAGE, 0, "image", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_IMAGE, 5, "image", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testInteger() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "integerinteger";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_INTEGER, 0, "integer", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_INTEGER, 7, "integer", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testM() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "m";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "m", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testMM() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "mm";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "mm", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testMove() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "movemove";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_MOVE, 0, "move", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_MOVE, 4, "move", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testS() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "s";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "s", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testSS() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ss";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "ss", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testSc() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "sc";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "sc", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testScSc() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "scsc";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "scsc", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testScale() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "scalescale";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_SCALE, 0, "scale", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_SCALE, 5, "scale", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testScreen() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "screen";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "screen", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testScreenScreen() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "screenscreen";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "screenscreen", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testScreenHeight() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "screenheightscreenheight";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_SCREENHEIGHT, 0, "screenheight", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_SCREENHEIGHT, 12, "screenheight", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testScreenwidth() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "screenwidthscreenwidth";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_SCREENWIDTH, 0, "screenwidth", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_SCREENWIDTH, 11, "screenwidth", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testShow() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "showshow";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_SHOW, 0, "show", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_SHOW, 4, "show", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testSleep() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "sleepsleep";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_SLEEP, 0, "sleep", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_SLEEP, 5, "sleep", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testT() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "t";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "t", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testTT() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "tt";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "tt", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testTrue() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "truetrue";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_TRUE, 0, "true", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_TRUE, 4, "true", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testU() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "u";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "u", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testUU() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "uu";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "uu", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testUrl() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "urlurl";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_URL, 0, "url", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_URL, 3, "url", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testW() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "w";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "w", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testWW() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ww";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "ww", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testWhile() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "whilewhile";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_WHILE, 0, "while", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_WHILE, 5, "while", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testWidth() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "widthwidth";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_WIDTH, 0, "width", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_WIDTH, 5, "width", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testX() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "x";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "x", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testXX() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "xx";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "xx", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testXloc() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "xlocxloc";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_XLOC, 0, "xloc", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_XLOC, 4, "xloc", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testY() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "y";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "y", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testYY() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "yy";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "yy", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testYloc() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "ylocyloc";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_YLOC, 0, "yloc", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(KW_YLOC, 4, "yloc", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testSingleLineStatement() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "this is a single line statement";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "this", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 5, "is", token2);

        //get the next token and check its kind, position, and contents
        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(IDENT, 8, "a", token3);

        //get the next token and check its kind, position, and contents
        Scanner.Token token4 = scanner.nextToken();
        checkTokenValidity(IDENT, 10, "single", token4);

        //get the next token and check its kind, position, and contents
        Scanner.Token token5 = scanner.nextToken();
        checkTokenValidity(IDENT, 17, "line", token5);

        //get the next token and check its kind, position, and contents
        Scanner.Token token6 = scanner.nextToken();
        checkTokenValidity(IDENT, 22, "statement", token6);

        Scanner.Token token7 = scanner.nextToken();
        assertEquals(EOF, token7.kind);
    }

    @Test
    public void testMultiLineStatement() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "this is a \n multi line statement";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "this", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 5, "is", token2);

        //get the next token and check its kind, position, and contents
        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(IDENT, 8, "a", token3);

        //get the next token and check its kind, position, and contents
        Scanner.Token token4 = scanner.nextToken();
        checkTokenValidity(IDENT, 12, "multi", token4);

        //get the next token and check its kind, position, and contents
        Scanner.Token token5 = scanner.nextToken();
        checkTokenValidity(IDENT, 18, "line", token5);

        //get the next token and check its kind, position, and contents
        Scanner.Token token6 = scanner.nextToken();
        checkTokenValidity(IDENT, 23, "statement", token6);

        Scanner.Token token7 = scanner.nextToken();
        assertEquals(EOF, token7.kind);
    }

    @Test
    public void testIdent() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "$AbcD_efG123h";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "$AbcD_efG123h", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForSingleCapitalLetter() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "A";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "A", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForDoubleCapitalLetter() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "AA";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "AA", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForSingleSmallLetter() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "a";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "a", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForDoubleSmallLetter() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "aa";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "aa", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForDollar() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "$";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "$", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForDoubleDollar() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "$$";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "$$", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForDollarUnderScore() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "$_";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "$_", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForKeywordUnderScore() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "width_";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(OP_WIDTH, 0, "width", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 5, "_", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentForUnderScoreKeyword() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "_width";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(IDENT, 0, "_width", token1);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testOverlappingKeywordsCase1() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "xloconvolve";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_XLOC, 0, "xloc", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 4, "onvolve", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testOverlappingKeywordsCase2() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "xlocconvolve";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_XLOC, 0, "xloc", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(OP_CONVOLVE, 4, "convolve", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIdentStartingWithIntLit() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "43bd";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(INT_LIT, 0, "43", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 2, "bd", token2);

        Scanner.Token token3 = scanner.nextToken();
        assertEquals(EOF, token3.kind);
    }

    @Test
    public void testIllegalCharsCase1() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "Scanner@cop5556";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("Unexpected char encountered: '@' at Line=0, pos=7");

        scanner.scan();
    }

    @Test
    public void testIllegalCharsCase2() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "Calculate \n 4^7";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        thrown.expect(IllegalCharException.class);
        thrown.expectMessage("Unexpected char encountered: '^' at Line=1, pos=2");

        scanner.scan();
    }

    @Test
    public void testCapitalisedKeywordsAsIdents() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "true and False";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_TRUE, 0, "true", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 5, "and", token2);

        //get the next token and check its kind, position, and contents
        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(IDENT, 9, "False", token3);

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(EOF, token4.kind);
    }

    @Test
    public void testKeywordsAndIdents() throws IllegalCharException, IllegalNumberException {
        //input string
        String input = "true and false";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        //get the next token and check its kind, position, and contents
        Scanner.Token token1 = scanner.nextToken();
        checkTokenValidity(KW_TRUE, 0, "true", token1);

        //get the next token and check its kind, position, and contents
        Scanner.Token token2 = scanner.nextToken();
        checkTokenValidity(IDENT, 5, "and", token2);

        //get the next token and check its kind, position, and contents
        Scanner.Token token3 = scanner.nextToken();
        checkTokenValidity(KW_FALSE, 9, "false", token3);

        Scanner.Token token4 = scanner.nextToken();
        assertEquals(EOF, token4.kind);
    }
}
