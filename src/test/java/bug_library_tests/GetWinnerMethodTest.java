package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GetWinnerMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                class Test {
                    @Override
                    public Optional<Player> getWinner() {
                        return switch (result) {
                            case X_WINS -> Optional.of(Player.X);
                            case O_WINS -> Optional.of(Player.O);
                            default -> Optional.empty();
                        };
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
    void testBugGetWinnerAlwaysXWins() {
        //given
        BugLibrary.bugGetWinnerAlwaysX(m);

        // then
        assertHasReturn(m,"Optional.of(Player.X)");
    }

    @Test
    void testBugGetWinnerAlwaysEmpty() {
        //given
        BugLibrary.bugGetWinnerAlwaysEmpty(m);

        //then
        assertHasReturn(m, "Optional.empty()");
    }

    @Test
    void testBugGetWinnerFlipWinners() {
        //given
        BugLibrary.bugGetWinnerFlipWinners(m);

        //then
        assertTrue(checkSwitch(m, 0, "Optional.of(Player.O);"),
                "Expected first switch entry to return Optional.of(Player.O);");
        assertTrue(checkSwitch(m, 1, "Optional.of(Player.X);"),
                "Expected second switch entry to return Optional.of(Player.X);");
    }

    @Test
    void testBugGetWinnerRemoveSwitch() {
        //given
        BugLibrary.bugGetWinnerRemoveSwitch(m);

        //then
        assertHasReturn(m, "Optional.empty()");
    }

    private void assertHasReturn(MethodDeclaration m, String expectedReturnExpr) {
        // when — extract method body
        BlockStmt body = m.getBody().orElseThrow();

        // ensure the method has at least one statement
        assertFalse(body.getStatements().isEmpty(),
                "Expected method body not to be empty");

        // collect all return statements
        List<ReturnStmt> returns = body.findAll(ReturnStmt.class);

        // check if any return matches the expected expression
        boolean hasExpectedReturn = returns.stream().anyMatch(ret ->
                ret.getExpression().isPresent() &&
                        ret.getExpression().get().toString().equals(expectedReturnExpr)
        );

        // assert that expected return statement is found
        assertTrue(hasExpectedReturn,
                "Expected return statement 'return " + expectedReturnExpr + ";' in method body");
    }

    private boolean checkSwitch(MethodDeclaration m, int index, String equalifier) {
        //when
        return m.getBody().
                get().findAll(SwitchEntry.class).get(index).getStatements().
                get(0).toString().equals(equalifier);
    }
}
