/**
 * Important to test the error cases in case the
 * AST is not being completely traversed.
 * <p>
 * Only need to test syntactically correct programs, or
 * program fragments.
 */

package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class TypeCheckVisitorTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // TODO test case where program name is also declared as a variable
    // TODO Your Visitor class will both decorate the tree and check conditions along the way. If a condition is violated, throw a TypeCheckException.

    // Program
    // TODO Test complex programs i.e., random codes from previous test cases

    // ParamDec
    // TODO Test paramDec with multiple params (each one with different types). Assertion by using lookup into the symbol table
    // TODO Negative - Test paramDec with multiple params of the same name. Should throw a TypeCheckException
    // TODO Test a valid case with paramDec containing same name as the program name

    // Block
    // TODO Nothing to test here

    // Dec
    // TODO Test Dec with multiple params (each one with different types). Assertion by using lookup into the symbol table
    // TODO Test Dec with multiple params of the same name. Should throw a TypeCheckException
    // TODO Test a valid case with Dec containing same name as the program name

    // Statement
    // SleepStatement
    // TODO positive and negative test case

    // AssignmentStatement
    // TODO positive and negative test case for each type
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

    // Chain
    // ChainElem
    // IdentChain
    // TODO Negative - test case where ident has not been declared
    // TODO Negative - test case where ident has been declared but not visible in this scope
    // TODO - test case where ident is declared and visible. Write assertions for identChain.type and indent.type

    // FilterOpChain
    // TODO Negative - test case where tuple size is not zero
    // TODO - test case where tuple size is zero. Write assertions for FilterOpChain.type

    // FrameOpChain
    // TODO positive and negative test cases for each of the if else branch

    // ImageOpChain
    // TODO positive and negative test cases for each of the if else branch

    // BinaryChain
    // TODO positive and negative test cases for each of the legal combination

    // WhileStatement
    // TODO positive and negative test cases. The expression in each of these test cases can be literals or idents/expression

    // IfStatement
    // TODO positive and negative test cases. The expression in each of these test cases can be literals or idents/expression

    // Expression
    // IdentExpression
    // TODO Negative - test case where ident has not been declared
    // TODO Negative - test case where ident has been declared but not visible in this scope
    // TODO - test case where ident is declared and visible. Write assertions for identExpression.type and identExpression.dec

    // IdentLValue
    // TODO Negative - test case where ident has not been declared
    // TODO Negative - test case where ident has been declared but not visible in this scope
    // TODO - test case where ident is declared and visible. Write assertions for identLValue.dec

    // IntLitExpression
    // TODO write assertions to check the type. Can we write negative test cases here ?

    // BooleanLitExpression
    // TODO write assertions to check the type. Can we write negative test cases here ?

    // ConstantExpression
    // TODO write assertions to check the type. Can we write negative test cases here ?

    // BinaryExpression
    // TODO positive and negative test cases for each of the legal combination

    // Tuple
    // TODO Condition succeeds for empty tuple
    // TODO Condition succeeds for non-empty tuple
    // TODO Negative - Condition fails for list of expressions which have different types

}
