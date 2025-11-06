package org.example.generator;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

import java.util.List;

public class BugLibrary {

    // ========== Bugs for threeInRow ==========

    public static void bugThreeInRowAlwaysTrue(MethodDeclaration m) {
        bugReturnStatementOnly(m, "true");
    }

    public static void bugThreeInRowAlwaysFalse(MethodDeclaration m) {
        bugReturnStatementOnly(m, "false");
    }

    public static void bugThreeInRowIsNotEmptyEquals(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                    expr.getRight().toString().equals("Cell.EMPTY")) {
                expr.setOperator(BinaryExpr.Operator.EQUALS);
            }
        });
    }

    public static void bugThreeInRowIsNotEmptyCellX(MethodDeclaration m) {
        bugCellStatusChange(m, "Cell.X");
    }

    public static void bugThreeInRowIsNotEmptyCellO(MethodDeclaration m) {
        bugCellStatusChange(m, "Cell.O");
    }

    public static void bugThreeInRowIsNotEmptyNull(MethodDeclaration m) {
        bugCellStatusChange(m, "null");
    }

    public static void bugThreeInRowIsNotEmptyCellJ(MethodDeclaration m) {
        bugThreeInRowChangeIndex(m, "j");
    }

    public static void bugThreeInRowIsNotEmptyCellK(MethodDeclaration m) {
        bugThreeInRowChangeIndex(m, "k");
    }


    public static void bugThreeInRowEqualIJtoNotEqual(MethodDeclaration m) {
        bugThreeInRowFlipEquality(m, "i", "j");
    }

    public static void bugThreeInRowEqualIJtoIK(MethodDeclaration m) {
        bugThreeInRowChangeEqualIndex(m, "i", "j", "k", true);
    }

    public static void bugThreeInRowEqualIJtoII(MethodDeclaration m) {
        bugThreeInRowChangeEqualIndex(m, "i", "j", "i", true);
    }

    public static void bugThreeInRowEqualIJtoJJ(MethodDeclaration m) {
        bugThreeInRowChangeEqualIndex(m, "i", "j", "j", false);
    }

    public static void bugThreeInRowEqualJKtoNotEqual(MethodDeclaration m) {
        bugThreeInRowFlipEquality(m, "j", "k");
    }

    public static void bugThreeInRowEqualJKtoJJ(MethodDeclaration m) {
        bugThreeInRowChangeEqualIndex(m, "j", "k", "j", true);
    }

    public static void bugThreeInRowEqualJKtoKK(MethodDeclaration m) {
        bugThreeInRowChangeEqualIndex(m, "j", "k", "k", false);
    }

    public static void bugThreeInRowReturnSecondOR(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                var expr = ret.getExpression().get();
                var binaries = expr.findAll(BinaryExpr.class);
                BinaryExpr second = binaries.getFirst().asBinaryExpr();
                second.setOperator(BinaryExpr.Operator.OR);
            }
        });
    }

    public static void bugThreeInRowReturnFirstOR(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                var expr = ret.getExpression().get();
                var binaries = expr.findAll(BinaryExpr.class);
                BinaryExpr second = binaries.get(1);
                second.setOperator(BinaryExpr.Operator.OR);
            }
        });
    }

    public static void bugThreeInRowReturnNotFirst(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                Expression expr = ret.getExpression().get();
                BinaryExpr binary = expr.asBinaryExpr();
                Expression left = binary.getLeft();
                UnaryExpr negated = new UnaryExpr(left.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                binary.setLeft(negated);
            }
        });
    }

    public static void bugThreeInRowReturnNotSecond(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> ret.getExpression().ifPresent(expr -> {
            if (expr.isBinaryExpr()) {
                BinaryExpr outer = expr.asBinaryExpr();
                Expression left = outer.getLeft();
                if (left.isBinaryExpr()) {
                    BinaryExpr inner = left.asBinaryExpr();
                    Expression rightInner = inner.getRight();
                    inner.setRight(new UnaryExpr(rightInner.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT));
                }
            }
        }));
    }

    public static void bugThreeInRowReturnNotThird(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                Expression expr = ret.getExpression().get();
                BinaryExpr binary = expr.asBinaryExpr();
                Expression right = binary.getRight();
                UnaryExpr negated = new UnaryExpr(right.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                binary.setRight(negated);
            }
        });
    }

    // ========== Bugs for getWinner ==========

    public static void bugGetWinnerAlwaysX(MethodDeclaration m) {
        bugReturnStatementOnly(m, "Optional.of(Player.X)");
    }

    public static void bugGetWinnerAlwaysEmpty(MethodDeclaration m) {
        bugReturnStatementOnly(m, "Optional.empty()");
    }

    public static void bugGetWinnerFlipWinners(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.findAll(SwitchEntry.class)
                .forEach(entry -> entry.getStatements().forEach(stmt -> {
                    String s = stmt.toString();
                    if (s.contains("Player.X")) {
                        stmt.replace(new ExpressionStmt(new NameExpr("Optional.of(Player.O)")));
                    } else if (s.contains("Player.O")) {
                        stmt.replace(new ExpressionStmt(new NameExpr("Optional.of(Player.X)")));
                    }
                }))
        );
    }

    public static void bugGetWinnerRemoveSwitch(MethodDeclaration m) {
        bugReturnStatementOnly(m, "Optional.empty()");
    }

    // ========== Bugs for playTurn ==========

    public static void bugPlayTurnEmpty(MethodDeclaration m) {
        m.setBody(new BlockStmt());
    }

    public static void bugPlayTurnRemoveFirstLine(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().removeFirst());
    }

    public static void bugPlayTurnTurnNotEqualToPlayerX(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.EQUALS &&
                    expr.getLeft().toString().equals("turn") &&
                    expr.getRight().toString().equals("Player.X")) {
                expr.setOperator(BinaryExpr.Operator.NOT_EQUALS);
            }
        });
    }

    public static void bugPlayTurnTurnEqualToPlayerO(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.EQUALS &&
                    expr.getLeft().toString().equals("turn") &&
                    expr.getRight().toString().equals("Player.X")) {
                expr.setRight(new NameExpr("Player.O"));
            }
        });
    }

    public static void bugPlayTurnIfTurnEqualsXCellInvert(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;

        body.findAll(ConditionalExpr.class).forEach(cond -> {
            if (cond.getThenExpr().isFieldAccessExpr() && cond.getElseExpr().isFieldAccessExpr()) {
                FieldAccessExpr thenExpr = cond.getThenExpr().asFieldAccessExpr();
                FieldAccessExpr elseExpr = cond.getElseExpr().asFieldAccessExpr();
                if (thenExpr.toString().equals("Cell.X") && elseExpr.toString().equals("Cell.O")) {
                    cond.setThenExpr(new FieldAccessExpr(new NameExpr("Cell"), "O"));
                    cond.setElseExpr(new FieldAccessExpr(new NameExpr("Cell"), "X"));
                }
            }
        });
    }

    public static void bugPlayTurnInvertHasWins(MethodDeclaration m) {
        invertMethodCall(m, "hasWin");
    }

    public static void bugPlayTurnResultInvert(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ConditionalExpr.class).forEach(cond -> {
            if (cond.getThenExpr().isFieldAccessExpr() && cond.getElseExpr().isFieldAccessExpr()) {
                FieldAccessExpr thenExpr = cond.getThenExpr().asFieldAccessExpr();
                FieldAccessExpr elseExpr = cond.getElseExpr().asFieldAccessExpr();
                if (thenExpr.toString().equals("Result.X_WINS")
                        && elseExpr.toString().equals("Result.O_WINS")) {
                    cond.setThenExpr(new FieldAccessExpr(new NameExpr("Result"), "O_WINS"));
                    cond.setElseExpr(new FieldAccessExpr(new NameExpr("Result"), "X_WINS"));
                }
            }
        });
    }

    public static void bugPlayTurnIsBoardFullInvert(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            if (ifStmt.getElseStmt().isPresent()) {
                var elseStmt = ifStmt.getElseStmt().get();
                if (elseStmt.isIfStmt()) {
                    IfStmt elseIf = elseStmt.asIfStmt();
                    var condition = elseIf.getCondition();
                    if (condition.isMethodCallExpr()) {
                        MethodCallExpr call = condition.asMethodCallExpr();
                        if (call.getNameAsString().equals("isBoardFull")) {
                            elseIf.setCondition(new UnaryExpr(call.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT));
                        }
                    }
                }
            }
        });
    }

    public static void bugPlayTurnInvertResultDrawToOngoing(MethodDeclaration m) {
        bugPlayTurnChangeResultState(m, "ONGOING");
    }

    public static void bugPlayTurnInvertResultDrawToXWins(MethodDeclaration m) {
        bugPlayTurnChangeResultState(m, "X_WINS");
    }

    public static void bugPlayTurnInvertResultDrawToOWins(MethodDeclaration m) {
        bugPlayTurnChangeResultState(m, "O_WINS");
    }

    public static void bugPlayTurnNoTurnSwitch(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().
                removeIf(stmt -> stmt.toString().contains("turn.other")));
    }

    public static void bugPlayTurnToX(MethodDeclaration m) {
        bugPlayTurnChangeTurnToPlayer(m, "X");
    }

    public static void bugPlayTurnToO(MethodDeclaration m) {
        bugPlayTurnChangeTurnToPlayer(m, "O");
    }

    public static void bugPlayTurnIdxWithXX(MethodDeclaration m) {
        bugIdxWithXX(m);
    }

    public static void bugPlayTurnIdxWithYY(MethodDeclaration m) {
        bugIdxWithYY(m);
    }

    // ========== Bugs for validateMove ==========

    public static void bugValidateMoveEmpty(MethodDeclaration m) {
        m.setBody(new BlockStmt());
    }

    public static void bugValidateMoveIsTerminalInvert(MethodDeclaration m) {
        invertMethodCall(m, "isTerminal");
    }

    public static void bugValidateMoveEqualsTurn(MethodDeclaration m) {
        bugValidateMoveChangeComparisonOperator(m, "move.player()", "turn", BinaryExpr.Operator.EQUALS);
    }

    public static void bugValidateMoveXMoreThan0(MethodDeclaration m) {
        bugValidateMoveChangeComparisonOperator(m, "move.x()", "0", BinaryExpr.Operator.GREATER);
    }

    public static void bugValidateMoveXLessThan2(MethodDeclaration m) {
        bugValidateMoveChangeComparisonOperator(m, "move.x()", "2", BinaryExpr.Operator.LESS);
    }


    public static void bugValidateMoveYMoreThan0(MethodDeclaration m) {
        bugValidateMoveChangeComparisonOperator(m, "move.y()", "0", BinaryExpr.Operator.GREATER);
    }

    public static void bugValidateMoveYLessThan2(MethodDeclaration m) {
        bugValidateMoveChangeComparisonOperator(m, "move.y()", "2", BinaryExpr.Operator.LESS);
    }

    public static void bugValidateMoveInvertFirstAnd(MethodDeclaration m) {
        bugValidateMoveInvertLogicalOperator(m, (short) 0);
    }

    public static void bugValidateMoveInvertSecondAnd(MethodDeclaration m) {
        bugValidateMoveInvertLogicalOperator(m, (short) 1);
    }

    public static void bugValidateMoveInvertThirdAnd(MethodDeclaration m) {
        bugValidateMoveInvertLogicalOperator(m, (short) 2);
    }

    public static void bugValidateMoveBoardEqualsEmpty(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                BinaryExpr binary = condition.asBinaryExpr();
                if (binary.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                        binary.getRight().toString().equals("Cell.EMPTY") &&
                        binary.getLeft().toString().startsWith("board[idx(")) {
                    binary.setOperator(BinaryExpr.Operator.EQUALS);
                }
            }
        });
    }

    public static void bugValidateMoveBoardNotEqualsX(MethodDeclaration m) {
        bugEqualsCellX(m);
    }

    public static void bugValidateMoveBoardNotEqualsO(MethodDeclaration m) {
        bugEqualsCellO(m);
    }

    public static void bugValidateMoveIdxWithXX(MethodDeclaration m) {
        bugIdxWithXX(m);
    }

    public static void bugValidateMoveIdxWithYY(MethodDeclaration m) {
        bugIdxWithYY(m);
    }

    // ========== Bugs for reset ==========

    public static void bugResetEmpty(MethodDeclaration m) {
        m.setBody(new BlockStmt());
    }

    public static void bugResetNoArraysFill(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().
                removeIf(stmt -> stmt.toString().contains("Arrays.fill")));
    }

    public static void bugResetCellX(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        cellChangeTo(body, "X");
    }

    public static void bugResetCellO(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        cellChangeTo(body, "O");
    }

    public static void bugResetTurnToO(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        changeResultAssignmentTO(body,"Player","O", 0);
    }

    public static void bugResetResultToDraw(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        changeResultAssignmentTO(body,"Result", "DRAW", 1);
    }

    public static void bugResetResultToXWins(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        changeResultAssignmentTO(body, "Result","X_WINS", 1);
    }

    public static void bugResetResultToOWins(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        changeResultAssignmentTO(body, "Result","O_WINS", 1);
    }

    // ========== Bugs for isBoardFull ==========

    public static void bugIsBoardFullAlwaysTrue(MethodDeclaration m) {
        bugReturnStatementOnly(m, "true");
    }

    public static void bugIsBoardFullAlwaysFalse(MethodDeclaration m) {
        bugReturnStatementOnly(m, "false");
    }

    public static void bugIsBoardFullCNotEqualsEmpty(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(IfStmt.class).get().getCondition().asBinaryExpr().setOperator(BinaryExpr.Operator.NOT_EQUALS);
    }

    public static void bugIsBoardFullCEqualsX(MethodDeclaration m) {
        bugEqualsCellX(m);
    }

    public static void bugIsBoardFullCEqualsO(MethodDeclaration m) {
        bugEqualsCellO(m);
    }


    // ========== Bugs for initBoard ==========

    public static void bugInitBoardArraySize(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(ArrayCreationExpr.class).get().getLevels().get(0).setDimension(new IntegerLiteralExpr("3"));
    }


    // ========== Bugs for getState ==========

    public static void bugGetStateArraySize(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ArrayCreationExpr.class).forEach(array -> {
            if (array.getElementType().asString().equals("char")) {
                array.getLevels().get(0).setDimension(new IntegerLiteralExpr("1"));
            }
        });
    }

    public static void bugGetState1Iteration(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(ForStmt.class).
                get().getCompare().
                get().asBinaryExpr().setRight(new IntegerLiteralExpr("1"));
    }

    public static void bugGetStateInvertXAndO(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(SwitchExpr.class).forEach(sw -> {
            for (SwitchEntry entry : sw.getEntries()) {
                String label = entry.getLabels().getFirst().toString();
                ExpressionStmt stmt = entry.getStatements().get(0).asExpressionStmt();
                if (label.equals("X")) {
                    stmt.setExpression(new CharLiteralExpr("O"));
                } else if (label.equals("O")) {
                    stmt.setExpression(new CharLiteralExpr("X"));
                }
            }
        });
    }

    // ========== Bugs for isTerminal ==========

    public static void bugIsTerminalAlwaysTrue(MethodDeclaration m) {
        bugReturnStatementOnly(m, "true");
    }

    public static void bugIsTerminalAlwaysFalse(MethodDeclaration m) {
        bugReturnStatementOnly(m, "false");
    }

    public static void bugIsTerminalResultEqualsOngoing(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(ReturnStmt.class).get().getExpression().get().asBinaryExpr().setOperator(BinaryExpr.Operator.EQUALS);
    }

    public static void bugIsTerminalResultNotEqualsXWins(MethodDeclaration m) {
        bugChangeResultEquality(m, "X_WINS");
    }

    public static void bugIsTerminalResultNotEqualsOWins(MethodDeclaration m) {
        bugChangeResultEquality(m, "O_WINS");
    }

    public static void bugIsTerminalResultNotEqualsDraw(MethodDeclaration m) {
        bugChangeResultEquality(m, "DRAW");
    }

    // ========== Bugs for turn ==========
    public static void bugTurnAlwaysX(MethodDeclaration m) {
        bugReturnStatementOnly(m, "Player.X");
    }

    public static void bugTurnAlwaysO(MethodDeclaration m) {
        bugReturnStatementOnly(m, "Player.O");
    }

    // ========== Bugs for setLines ==========

    public static void bugSetLinesRemoveFirstDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 0);
    }

    public static void bugSetLinesRemoveSecondDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 1);
    }

    public static void bugSetLinesRemoveThirdDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 2);
    }

    public static void bugSetLinesRemoveForthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 3);
    }

    public static void bugSetLinesRemoveFifthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 4);
    }

    public static void bugSetLinesRemoveSixthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 5);
    }

    public static void bugSetLinesRemoveSeventhDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 6);
    }

    public static void bugSetLinesRemoveEighthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, 7);
    }

    // ========== Bugs for hasWin ==========

    public static void bugHasWinFullInvertThreeInRowAllIndex0(MethodDeclaration m) {
        bugHasWinInvertThreeInRowAllByOneIndex(m, "0");
    }

    public static void bugHasWinInvertThreeInRowAllIndex1(MethodDeclaration m) {
        bugHasWinInvertThreeInRowAllByOneIndex(m, "1");
    }

    public static void bugHasWinInvertThreeInRowAllIndex2(MethodDeclaration m) {
        bugHasWinInvertThreeInRowAllByOneIndex(m, "2");
    }

    public static void bugHasWinInvertThreeInRow(MethodDeclaration m) {
        invertMethodCall(m, "threeInRow");
    }

    public static void bugHasWinInvertReturnAfterIf(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(ReturnStmt.class).get().setExpression(new BooleanLiteralExpr(false));
    }

    // ========== Helper methods ==========

    private static void bugPlayTurnChangeResultState(MethodDeclaration m, String resultState) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.DRAW")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), resultState));
            }
        });
    }

    private static void bugPlayTurnChangeTurnToPlayer(MethodDeclaration m, String changedPlayer) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("turn") &&
                    assign.getValue().toString().equals("turn.other()")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Player"), changedPlayer));
            }
        });
    }

    private static void bugValidateMoveChangeComparisonOperator(MethodDeclaration m, String methodName, String compareValue, BinaryExpr.Operator newOperator) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getLeft().toString().equals(methodName) &&
                    expr.getRight().toString().equals(compareValue)) {
                expr.setOperator(newOperator);
            }
        });
    }

    private static void bugValidateMoveInvertLogicalOperator(MethodDeclaration m, short number) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;

        body.findAll(IfStmt.class).forEach(ifStmt -> {
            Expression condition = ifStmt.getCondition();
            List<BinaryExpr> ors = condition.findAll(BinaryExpr.class).stream()
                    .filter(expr -> expr.getOperator() == BinaryExpr.Operator.OR)
                    .toList();
            if (number >= 0 && number < ors.size()) {
                ors.get(ors.size() - number - 1).setOperator(BinaryExpr.Operator.AND);
            }
        });
    }

    private static void bugThreeInRowChangeEqualIndex(MethodDeclaration m, String valueLeft, String valueRight, String newValue, boolean isRight) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.EQUALS &&
                    expr.getLeft().isArrayAccessExpr() &&
                    expr.getRight().isArrayAccessExpr()) {
                ArrayAccessExpr leftArrayAccess = expr.getLeft().asArrayAccessExpr();
                ArrayAccessExpr rightArrayAccess = expr.getRight().asArrayAccessExpr();
                if (leftArrayAccess.getIndex().toString().equals(valueLeft) &&
                        rightArrayAccess.getIndex().toString().equals(valueRight)) {
                    if (isRight) rightArrayAccess.setIndex(new NameExpr(newValue));
                    else leftArrayAccess.setIndex(new NameExpr(newValue));
                }
            }
        });
    }

    private static void bugThreeInRowChangeIndex(MethodDeclaration m, String newValue) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                    expr.getRight().toString().equals("Cell.EMPTY") &&
                    expr.getLeft().isArrayAccessExpr()) {
                ArrayAccessExpr arrayAccess = expr.getLeft().asArrayAccessExpr();
                if (arrayAccess.getIndex().toString().equals("i")) {
                    arrayAccess.setIndex(new NameExpr(newValue));
                }
            }
        });
    }

    private static void bugReturnStatementOnly(MethodDeclaration m, String booleanMeaning) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt(booleanMeaning)));
    }

    private static void bugEqualsCellX(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                BinaryExpr binary = condition.asBinaryExpr();
                if (binary.getRight().toString().equals("Cell.EMPTY")) {
                    binary.setRight(new FieldAccessExpr(new NameExpr("Cell"), "X"));
                }
            }
        });
    }

    private static void bugEqualsCellO(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                BinaryExpr binary = condition.asBinaryExpr();
                if (binary.getRight().toString().equals("Cell.EMPTY")) {
                    binary.setRight(new FieldAccessExpr(new NameExpr("Cell"), "O"));
                }
            }
        });
    }

    private static void bugIdxWithXX(MethodDeclaration m) {
        bugIdx(m, "x()", 1);
    }

    private static void bugIdxWithYY(MethodDeclaration m) {
        bugIdx(m, "y()", 0);
    }

    private static void bugIdx(MethodDeclaration m, String name, int i) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;

        body.findAll(MethodCallExpr.class).forEach(call -> {
            if (call.getNameAsString().equals("idx")) {
                FieldAccessExpr moveY = new FieldAccessExpr(new NameExpr("move"), name);
                call.setArgument(i, moveY);
            }
        });
    }

    private static void removeDiagonal(MethodDeclaration m, int index) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ArrayInitializerExpr.class).
                get(0).getValues().
                get(index).ifArrayInitializerExpr(Node::remove);
    }

    private static void bugThreeInRowFlipEquality(MethodDeclaration m, String valueLeft, String valueRight) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.EQUALS &&
                    expr.getLeft().isArrayAccessExpr() &&
                    expr.getRight().isArrayAccessExpr()) {
                ArrayAccessExpr leftArrayAccess = expr.getLeft().asArrayAccessExpr();
                ArrayAccessExpr rightArrayAccess = expr.getRight().asArrayAccessExpr();
                if (leftArrayAccess.getIndex().toString().equals(valueLeft) &&
                        rightArrayAccess.getIndex().toString().equals(valueRight)) {
                    expr.setOperator(BinaryExpr.Operator.NOT_EQUALS);
                }
            }
        });
    }

    private static void bugChangeResultEquality(MethodDeclaration m, String changeName) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(BinaryExpr.class).
                get().setRight(new FieldAccessExpr(new NameExpr("Result"), changeName));
    }

    private static void bugHasWinInvertThreeInRowAllByOneIndex(MethodDeclaration m, String index) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(MethodCallExpr.class).forEach(call -> {
            if (call.getNameAsString().equals("threeInRow") && call.getArguments().size() == 3) {
                ArrayAccessExpr line0 = new ArrayAccessExpr(new NameExpr("line"), new IntegerLiteralExpr(index));
                call.setArgument(0, line0.clone());
                call.setArgument(1, line0.clone());
                call.setArgument(2, line0.clone());
            }
        });
    }

    private static void invertMethodCall(MethodDeclaration m, String methodName) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isMethodCallExpr()) {
                MethodCallExpr call = condition.asMethodCallExpr();
                if (call.getNameAsString().equals(methodName)) {
                    ifStmt.setCondition(new UnaryExpr(call.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT));
                }
            }
        });
    }

    private static void bugCellStatusChange(MethodDeclaration m, String to) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findFirst(BinaryExpr.class).
                get().setRight(new NameExpr(to));
    }

    private static void changeResultAssignmentTO(BlockStmt body, String variable, String value, int index) {
        body.findAll(AssignExpr.class).
                get(index).setValue(new FieldAccessExpr(new NameExpr(variable), value));
    }

    private static void cellChangeTo(BlockStmt body, String changeName) {
        body.findFirst(MethodCallExpr.class).
                get().setArgument(1, new FieldAccessExpr(new NameExpr("Cell"), changeName));
    }
}