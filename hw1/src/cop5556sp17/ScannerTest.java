package cop5556sp17;

import static cop5556sp17.Scanner.Kind.*;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556sp17.Scanner.IllegalCharException;
import cop5556sp17.Scanner.IllegalNumberException;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;

import java.util.Objects;

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
        checkTokenValidity(RPAREN, 45, RPAREN.getText(), token2);
    }

}
