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


}
