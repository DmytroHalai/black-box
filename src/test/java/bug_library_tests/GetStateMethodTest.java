package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GetStateMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                   public BoardView getState() {
                       char[] nine = new char[board.length];
                       for (int i = 0; i < board.length; i++) {
                           nine[i] = switch (board[i]) {
                               case X -> 'X';
                               case O -> 'O';
                               default -> ' ';
                           };
                       }
                       return BoardState.fromChars(nine);
                   }
                }
                """;
        m = parseMethod(code);

    }

    @AfterEach
    void teardown() {
        m = null;
    }

    @Test
    void testBugGetStateArraySize() {
        //given
        BugLibrary.bugGetStateArraySize(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ArrayCreationLevel.class).
                get(0).getDimension().
                get().toString().equals("1");
        assertTrue(cond, "Expected array size to be 1 instead of board.length");
    }

    @Test
    void testBugGetState1Iteration() {
        //given
        BugLibrary.bugGetState1Iteration(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean cond = body.findAll(ForStmt.class).
                get(0).getCompare().
                get().asBinaryExpr().getRight().toString().equals("1");
        assertTrue(cond, "Expected for loop to iterate only once");
    }

    @Test
    void testBugGetStateInvertXAndO() {
        //given
        BugLibrary.bugGetStateInvertXAndO(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean oChangedToX = isChanged(body, "X", 0);
        boolean xChangedToO = isChanged(body, "O", 1);
        assertTrue(oChangedToX, "Expected 'O' to be changed to 'X'");
        assertTrue(xChangedToO, "Expected 'X' to be changed to 'O'");
    }

    private boolean isChanged(BlockStmt body, String to, int switchEntryIndex) {
        return body.findAll(AssignExpr.class).
                get(0).getValue().asSwitchExpr().
                getEntry(switchEntryIndex).
                getStatements().
                get(0).asExpressionStmt().
                getExpression().asCharLiteralExpr().
                getValue().toString().equals(to);
    }
}
