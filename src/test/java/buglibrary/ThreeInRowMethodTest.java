package buglibrary;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.example.generator.BugLibrary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreeInRowMethodTest {
    MethodDeclaration m;

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = new JavaParser().parse(code).getResult().orElseThrow();
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    @BeforeEach
    void setup() {
        String code = """
                    class Test {
                        public boolean threeInRow(int i, int j, int k) {
                            boolean isNotEmpty = board[i] != Cell.EMPTY;
                            boolean equalIJ = board[i] == board[j];
                            boolean equalJK = board[j] == board[k];
                            return isNotEmpty && equalIJ && equalJK;
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
    void testBugThreeInRowAlwaysTrue() {
        // given
        BugLibrary.bugThreeInRowAlwaysTrue(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all return statements
        List<ReturnStmt> returns = body.findAll(ReturnStmt.class);
        assertFalse(returns.isEmpty(), "Expected at least one return statement");

        // 2. check if there's return true exists
        boolean hasReturnTrue = returns.getFirst().getExpression().get().toString().equals("true");
        assertTrue(hasReturnTrue);
    }


    @Test
    void testBugThreeInRowAlwaysFalse() {
        //given
        BugLibrary.bugThreeInRowAlwaysFalse(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then

        // 1. find all return statements
        List<ReturnStmt> returns = body.findAll(ReturnStmt.class);
        assertFalse(returns.isEmpty(), "Expected at least one return statement");

        // 2. check if there's return true exists
        boolean hasReturnTrue = returns.getFirst().getExpression().get().toString().equals("false");
        assertTrue(hasReturnTrue);
    }

    @Test
    void testBugThreeInRowIsNotEmptyEquals() {
        //given
        BugLibrary.bugThreeInRowIsNotEmptyEquals(m);

        //when
        BlockStmt body = m.getBody().orElseThrow();

        //then
        boolean hasBuggedCheck = body.findAll(VariableDeclarationExpr.class).stream()
                .anyMatch(v -> v.toString().contains("board[i] == Cell.EMPTY"));

        assertTrue(hasBuggedCheck, "Expected buggy version with '==' instead of '!='");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellX() {
        // given
        BugLibrary.bugThreeInRowIsNotEmptyCellX(m);

        //then
        changeCellMean(m, "i", "Cell.X");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellO() {
        //given
        BugLibrary.bugThreeInRowIsNotEmptyCellO(m);

        //then
        changeCellMean(m, "i", "Cell.O");
    }

    @Test
    void testBugThreeInRowIsNotEmptyNull() {
        //given
        BugLibrary.bugThreeInRowIsNotEmptyNull(m);

        //then
        changeCellMean(m, "i", "null");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellJ() {
        //given
        BugLibrary.bugThreeInRowIsNotEmptyCellJ(m);

        //then
        changeCellMean(m, "j", "Cell.EMPTY");
    }

    @Test
    void testBugThreeInRowIsNotEmptyCellK() {
        //given
        BugLibrary.bugThreeInRowIsNotEmptyCellK(m);

        //then
        changeCellMean(m, "k", "Cell.EMPTY");
    }

    @Test
    void testBugThreeInRowEqualIJtoNotEqual() {
        // given
        BugLibrary.bugThreeInRowEqualIJtoNotEqual(m);

        //then
        changeBooleanEqualityIndex(m, "equalIJ", "i", "j", false);
    }

    @Test
    void testBugThreeInRowEqualJKtoNotEqual() {
        //given
        BugLibrary.bugThreeInRowEqualJKtoNotEqual(m);

        //then
        changeBooleanEqualityIndex(m, "equalJK", "j", "k", false);
    }

    @Test
    void testBugThreeInRowEqualIJtoIK() {
        //given
        BugLibrary.bugThreeInRowEqualIJtoIK(m);

        //then
        changeBooleanEqualityIndex(m, "equalIJ", "i", "k", true);
    }

    @Test
    void testBugThreeInRowEqualIJtoII() {
        //given
        BugLibrary.bugThreeInRowEqualIJtoII(m);

        //then
        changeBooleanEqualityIndex(m, "equalIJ", "i", "i", true);
    }

    @Test
    void testBugThreeInRowEqualIJtoJJ() {
        //given
        BugLibrary.bugThreeInRowEqualIJtoJJ(m);

        //then
        changeBooleanEqualityIndex(m, "equalIJ", "j", "j", true);
    }

    @Test
    void testBugThreeInRowEqualJKtoJJ() {
        //given
        BugLibrary.bugThreeInRowEqualJKtoJJ(m);

        //then
        changeBooleanEqualityIndex(m, "equalJK", "j", "j", true);
    }

    @Test
    void testBugThreeInRowEqualJKtoKK() {
        //given
        BugLibrary.bugThreeInRowEqualJKtoKK(m);

        //then
        changeBooleanEqualityIndex(m, "equalJK", "k", "k", true);
    }

    @Test
    void testBugThreeInRowReturnFirstOR() {
        // given
        BugLibrary.bugThreeInRowReturnFirstOR(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasReturnWithOr = body.findAll(ReturnStmt.class).stream()
                .map(ReturnStmt::getExpression)
                .flatMap(Optional::stream)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(outer -> {
                    BinaryExpr binaryExpr = outer.getLeft().asBinaryExpr();
                    boolean isOrOperator = binaryExpr.getOperator() == BinaryExpr.Operator.OR;
                    boolean leftIsIsNotEmpty = binaryExpr.getLeft().isNameExpr() &&
                            binaryExpr.getLeft().asNameExpr().getNameAsString().equals("isNotEmpty");
                    return isOrOperator && leftIsIsNotEmpty;
                });

        assertTrue(hasReturnWithOr,
                "Expected return statement 'isNotEmpty || equalIJ && equalJK' not found in AST");
    }


    @Test
    void testBugThreeInRowReturnSecondOR() {
        // given
        BugLibrary.bugThreeInRowReturnSecondOR(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasReturnWithOr = body.findAll(ReturnStmt.class).stream()
                .map(ReturnStmt::getExpression)
                .flatMap(Optional::stream)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(outer -> {
                    boolean isOrOperator = outer.getOperator() == BinaryExpr.Operator.OR;
                    boolean leftIsIsNotEmpty = outer.getLeft().asBinaryExpr().getRight().toString().equals("equalIJ");
                    return isOrOperator && leftIsIsNotEmpty;
                });

        assertTrue(hasReturnWithOr,
                "Expected return statement 'isNotEmpty && equalIJ || equalJK' not found in AST");
    }

    @Test
    void testBugThreeInRowReturnNotFirst() {
        //given
        BugLibrary.bugThreeInRowReturnNotFirst(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasReturnWithOr = body.findAll(ReturnStmt.class).stream()
                .map(ReturnStmt::getExpression)
                .flatMap(Optional::stream)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(outer -> outer.getLeft().asUnaryExpr().getOperator().equals( UnaryExpr.Operator.LOGICAL_COMPLEMENT));

        assertTrue(hasReturnWithOr,
                "Expected return statement '!isNotEmpty && equalIJ && equalJK' not found in AST");
    }

    @Test
    void testBugThreeInRowReturnNotSecond() {
        //given
        BugLibrary.bugThreeInRowReturnNotSecond(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasReturnWithOr = body.findAll(ReturnStmt.class).stream()
                .map(ReturnStmt::getExpression)
                .flatMap(Optional::stream)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(outer -> outer.getLeft().asBinaryExpr().getRight().
                        asUnaryExpr().getOperator().equals( UnaryExpr.Operator.LOGICAL_COMPLEMENT));

        assertTrue(hasReturnWithOr,
                "Expected return statement 'isNotEmpty && !equalIJ && equalJK' not found in AST");
    }

    @Test
    void testBugThreeInRowReturnNotThird() {
        //given
        BugLibrary.bugThreeInRowReturnNotThird(m);

        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasReturnWithOr = body.findAll(ReturnStmt.class).stream()
                .map(ReturnStmt::getExpression)
                .flatMap(Optional::stream)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(outer -> outer.getRight().asUnaryExpr().getOperator().equals( UnaryExpr.Operator.LOGICAL_COMPLEMENT));

        assertTrue(hasReturnWithOr,
                "Expected return statement 'isNotEmpty && equalIJ && !equalJK' not found in AST");
    }

    private void changeCellMean(MethodDeclaration m, String index, String s) {
        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasBuggedCheck = body.findAll(VariableDeclarationExpr.class).stream()
                .filter(v -> v.getVariables().stream()
                        .anyMatch(var -> var.getNameAsString().equals("isNotEmpty")))
                .flatMap(v -> v.getVariables().stream())
                .map(var -> var.getInitializer().orElse(null))
                .filter(Objects::nonNull)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(bin -> {
                    boolean operatorIsNotEquals = bin.getOperator() == BinaryExpr.Operator.NOT_EQUALS;

                    boolean leftIsBoardI = bin.getLeft().isArrayAccessExpr() &&
                            bin.getLeft().asArrayAccessExpr().getName().asNameExpr().getNameAsString().equals("board") &&
                            bin.getLeft().asArrayAccessExpr().getIndex().asNameExpr().getNameAsString().equals(index);

                    boolean rightIsCellX = bin.getRight().toString().equals(s);

                    return operatorIsNotEquals && leftIsBoardI && rightIsCellX;
                });

        assertTrue(hasBuggedCheck, "Expected AST node for 'boolean isNotEmpty = board[" + index + "] != " + s + ";' not found");
    }

    private void changeBooleanEqualityIndex(MethodDeclaration m, String variableName, String firstIndex, String secondIndex, boolean isEqual) {
        // when
        BlockStmt body = m.getBody().orElseThrow();

        // then
        boolean hasBuggedCheck = body.findAll(VariableDeclarationExpr.class).stream()
                .filter(v -> v.getVariables().stream()
                        .anyMatch(var -> var.getNameAsString().equals(variableName)))
                .flatMap(v -> v.getVariables().stream())
                .map(var -> var.getInitializer().orElse(null))
                .filter(Objects::nonNull)
                .filter(Expression::isBinaryExpr)
                .map(Expression::asBinaryExpr)
                .anyMatch(bin -> {
                    boolean operatorIsNotEquals = bin.getOperator() == (isEqual ? BinaryExpr.Operator.EQUALS : BinaryExpr.Operator.NOT_EQUALS);

                    boolean leftIsBoardI = bin.getLeft().asArrayAccessExpr().getName().asNameExpr().getNameAsString().equals("board")
                            && bin.getLeft().asArrayAccessExpr().getIndex().asNameExpr().getNameAsString().equals(firstIndex);

                    boolean rightIsBoardJ = bin.getRight().asArrayAccessExpr().getName().asNameExpr().getNameAsString().equals("board")
                            && bin.getRight().asArrayAccessExpr().getIndex().asNameExpr().getNameAsString().equals(secondIndex);

                    return operatorIsNotEquals && leftIsBoardI && rightIsBoardJ;
                });

        assertTrue(hasBuggedCheck,
                "Expected AST node for 'boolean " + variableName + " = board[" + firstIndex + "] "
                        + (isEqual ? "==" : "!=") + " board[" + secondIndex + "];' not found");
    }
}
