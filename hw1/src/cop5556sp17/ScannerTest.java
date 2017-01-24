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
}
