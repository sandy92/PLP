/**
 * Important to test the error cases in case the
 * AST is not being completely traversed.
 * <p>
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import cop5556sp17.AST.Dec;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SymbolTableTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testNoVariableDeclaredMoreThanOnceInTheSameScope() throws Exception {
        // Manually simulating enter and leave scope to test the scenario
        SymbolTable symbolTable = new SymbolTable();
        String input = "integer a integer a integer x integer x integer x";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        symbolTable.enterScope(); // First Scope

        Scanner.Token firstToken;
        Scanner.Token dec;
        Boolean insertionResult;

        firstToken = scanner.nextToken();
        dec = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", dec);

        insertionResult = symbolTable.insert(dec.getText(), new Dec(firstToken, dec));

        assertEquals("SymbolTable insertion failed", true, insertionResult);

        symbolTable.enterScope(); // Second Scope

        firstToken = scanner.nextToken();
        dec = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", dec);

        insertionResult = symbolTable.insert(dec.getText(), new Dec(firstToken, dec));

        assertEquals("SymbolTable insertion failed", true, insertionResult);

        firstToken = scanner.nextToken();
        dec = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", dec);

        insertionResult = symbolTable.insert(dec.getText(), new Dec(firstToken, dec));

        assertEquals("SymbolTable insertion failed", true, insertionResult);

        firstToken = scanner.nextToken();
        dec = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", dec);

        insertionResult = symbolTable.insert(dec.getText(), new Dec(firstToken, dec));

        assertEquals("SymbolTable insertion failed", false, insertionResult);

        symbolTable.leaveScope(); // Leave Second Scope

        firstToken = scanner.nextToken();
        dec = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", dec);

        insertionResult = symbolTable.insert(dec.getText(), new Dec(firstToken, dec));

        assertEquals("SymbolTable insertion failed", true, insertionResult);
    }

    @Test
    public void testLookup() throws Exception {
        // Manually simulating enter and leave scope to test the scenario
        SymbolTable symbolTable = new SymbolTable();
        String input = "integer x integer x integer x integer x";
        //create and initialize the scanner
        Scanner scanner = new Scanner(input);
        scanner.scan();

        symbolTable.enterScope(); // First Scope

        Scanner.Token firstToken;
        Scanner.Token decToken1;
        Scanner.Token decToken2;
        Scanner.Token decToken3;
        Scanner.Token decToken4;
        Boolean insertionResult;

        Dec dec1;
        Dec dec2;
        Dec dec3;
        Dec dec4;
        Dec resultDec;

        firstToken = scanner.nextToken();
        decToken1 = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", decToken1);
        dec1 = new Dec(firstToken, decToken1);

        insertionResult = symbolTable.insert(decToken1.getText(), new Dec(firstToken, decToken1));
        assertEquals("SymbolTable insertion failed", true, insertionResult);

        symbolTable.enterScope(); // Second Scope

        firstToken = scanner.nextToken();
        decToken2 = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", decToken2);
        dec2 = new Dec(firstToken, decToken2);

        insertionResult = symbolTable.insert(decToken2.getText(), new Dec(firstToken, decToken2));
        assertEquals("SymbolTable insertion failed", true, insertionResult);

        symbolTable.enterScope(); // Third Scope

        firstToken = scanner.nextToken();
        decToken3 = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", decToken3);
        dec3 = new Dec(firstToken, decToken3);

        insertionResult = symbolTable.insert(decToken3.getText(), new Dec(firstToken, decToken3));
        assertEquals("SymbolTable insertion failed", true, insertionResult);

        resultDec = symbolTable.lookup("x");
        assertEquals("Lookup failed", dec3, resultDec);

        symbolTable.leaveScope();

        resultDec = symbolTable.lookup("x");
        assertEquals("Lookup failed", dec2, resultDec);

        symbolTable.enterScope(); // Fourth Scope

        firstToken = scanner.nextToken();
        decToken4 = scanner.nextToken();
        assertNotNull("First token can't be null", firstToken);
        assertNotNull("Dec can't be null", decToken4);
        dec4 = new Dec(firstToken, decToken4);

        insertionResult = symbolTable.insert(decToken4.getText(), new Dec(firstToken, decToken4));
        assertEquals("SymbolTable insertion failed", true, insertionResult);

        resultDec = symbolTable.lookup("x");
        assertEquals("Lookup failed", dec4, resultDec);

        symbolTable.leaveScope();

        resultDec = symbolTable.lookup("x");
        assertEquals("Lookup failed", dec2, resultDec);

        symbolTable.leaveScope();

        resultDec = symbolTable.lookup("x");
        assertEquals("Lookup failed", dec1, resultDec);
    }
}
