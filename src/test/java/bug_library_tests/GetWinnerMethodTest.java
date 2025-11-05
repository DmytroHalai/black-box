package bug_library_tests;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        BugLibrary.bugGetWinnerAlwaysX(m);

        String result = m.toString();
        assertTrue(result.contains("return Optional.of(Player.X);"), "Method works not correctly");
    }

    @Test
    void testBugGetWinnerAlwaysEmpty() {
        BugLibrary.bugGetWinnerAlwaysEmpty(m);

        String result = m.toString();
        assertTrue(result.contains("return Optional.empty();"), "Method works not correctly");
    }

    @Test
    void testBugGetWinnerFlipWinners() {
        BugLibrary.bugGetWinnerFlipWinners(m);

        String result = m.toString();
        System.out.println(result);
        assertTrue(result.contains("return Optional.of(Player.O);"), "Method works not correctly");
        assertTrue(result.contains("return Optional.of(Player.X);"), "Method works not correctly");
    }

    @Test
    void testBugGetWinnerRemoveSwitch() {
        BugLibrary.bugGetWinnerRemoveSwitch(m);

        String result = m.toString();
        System.out.println(result);
        assertTrue(result.contains("return Optional.empty();"), "Method works not correctly");
    }
}
