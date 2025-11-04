package org.example.generator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;

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
        bugThreeInRowChangeIndex(m, "i", "j");
    }

    public static void bugThreeInRowIsNotEmptyCellK(MethodDeclaration m) {
        bugThreeInRowChangeIndex(m, "i", "k");
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

    public static void bugThreeInRowReturnFirstOR(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                BinaryExpr expr = ret.getExpression().get().asBinaryExpr();
                expr.setOperator(BinaryExpr.Operator.OR);
            }
        });
    }

    public static void bugThreeInRowReturnSecondOR(MethodDeclaration m) {
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
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                Expression expr = ret.getExpression().get();
                BinaryExpr binary = expr.asBinaryExpr();
                Expression right = binary.getRight();
                UnaryExpr negated = new UnaryExpr(right.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                binary.setLeft(negated);
            }
        });
    }

    public static void bugThreeInRowReturnNotThird(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent()) {
                Expression expr = ret.getExpression().get();
                BinaryExpr binary = expr.asBinaryExpr();
                Expression right = binary.getRight();
                BinaryExpr rightBinary = right.asBinaryExpr();
                Expression third = rightBinary.getRight();
                UnaryExpr negated = new UnaryExpr(third.clone(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
                binary.setLeft(negated);
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
                                stmt.replace(new ReturnStmt("Optional.of(Player.O)"));
                            } else if (s.contains("Player.O")) {
                                stmt.replace(new ReturnStmt("Optional.of(Player.X)"));
                            }
                        })
                )
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
            if (cond.getThenExpr().toString().equals("Cell.X") &&
                    cond.getElseExpr().toString().equals("Cell.O")) {
                cond.setThenExpr(new FieldAccessExpr(new FieldAccessExpr(null, "Cell"), "O"));
                cond.setElseExpr(new FieldAccessExpr(new FieldAccessExpr(null, "Cell"), "X"));
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
            String thenPart = cond.getThenExpr().toString();
            String elsePart = cond.getElseExpr().toString();
            if (thenPart.equals("Result.X_WINS") && elsePart.equals("Result.O_WINS")) {
                cond.setThenExpr(new FieldAccessExpr(new FieldAccessExpr(null, "Result"), "O_WINS"));
                cond.setElseExpr(new FieldAccessExpr(new FieldAccessExpr(null, "Result"), "X_WINS"));
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
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.DRAW")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), "ONGOING"));
            }
        });
    }

    public static void bugPlayTurnInvertResultDrawToXWins(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.DRAW")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), "X_WINS"));
            }
        });
    }

    public static void bugPlayTurnInvertResultDrawToOWins(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.DRAW")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), "O_WINS"));
            }
        });
    }

    public static void bugPlayTurnNoTurnSwitch(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().
                removeIf(stmt -> stmt.toString().contains("turn.other")));
    }

    public static void bugPlayTurnTurnToX(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("turn") &&
                    assign.getValue().toString().equals("turn.other()")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Player"), "X"));
            }
        });
    }

    public static void bugPlayTurnTurnToY(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("turn") &&
                    assign.getValue().toString().equals("turn.other()")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Player"), "Y"));
            }
        });
    }


    public static void playTurnIdxWithXX(MethodDeclaration m) {
        bugIdxWithXX(m);
    }

    public static void playTurnbugIdxWithYY(MethodDeclaration m) {
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
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                    expr.getLeft().toString().equals("move.player()") &&
                    expr.getRight().toString().equals("turn")) {
                expr.setOperator(BinaryExpr.Operator.EQUALS);
            }
        });
    }

    public static void bugValidateMoveXMoreThan0(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.LESS &&
                    expr.getLeft().toString().equals("move.x()") &&
                    expr.getRight().toString().equals("0")) {
                expr.setOperator(BinaryExpr.Operator.GREATER);
            }
        });
    }

    public static void bugValidateMoveXLessThan2(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.LESS &&
                    expr.getLeft().toString().equals("move.x()") &&
                    expr.getRight().toString().equals("2")) {
                expr.setOperator(BinaryExpr.Operator.LESS);
            }
        });
    }

    public static void bugValidateMoveYMoreThan0(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.LESS &&
                    expr.getLeft().toString().equals("move.y()") &&
                    expr.getRight().toString().equals("0")) {
                expr.setOperator(BinaryExpr.Operator.GREATER);
            }
        });
    }

    public static void bugValidateMoveYLessThan2(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.LESS &&
                    expr.getLeft().toString().equals("move.y()") &&
                    expr.getRight().toString().equals("2")) {
                expr.setOperator(BinaryExpr.Operator.LESS);
            }
        });
    }

    public static void bugValidateMoveInvertFirstAnd(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                var binaries = condition.findAll(BinaryExpr.class);
                for (BinaryExpr expr : binaries) {
                    if (expr.getOperator() == BinaryExpr.Operator.OR) {
                        expr.setOperator(BinaryExpr.Operator.AND);
                        break;
                    }
                }
            }
        });
    }

    public static void bugValidateMoveInvertSecondAnd(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                var binaries = condition.findAll(BinaryExpr.class);
                int orCount = 0;
                for (BinaryExpr expr : binaries) {
                    if (expr.getOperator() == BinaryExpr.Operator.OR) {
                        orCount++;
                        if (orCount == 2) {
                            expr.setOperator(BinaryExpr.Operator.AND);
                            break;
                        }
                    }
                }
            }
        });
    }

    public static void bugValidateMoveInvertThirdAnd(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                var binaries = condition.findAll(BinaryExpr.class);
                int orCount = 0;
                for (BinaryExpr expr : binaries) {
                    if (expr.getOperator() == BinaryExpr.Operator.OR) {
                        orCount++;
                        if (orCount == 3) {
                            expr.setOperator(BinaryExpr.Operator.AND);
                            break;
                        }
                    }
                }
            }
        });
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

    public static void bugValidateMoveBoardNotEqualsY(MethodDeclaration m) {
        bugEqualsCellY(m);
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
        body.findAll(MethodCallExpr.class).forEach(call -> {
            if (call.getNameAsString().equals("fill") &&
                    call.getScope().isPresent() &&
                    call.getScope().get().toString().equals("Arrays") &&
                    call.getArguments().size() == 2 &&
                    call.getArgument(1).toString().equals("Cell.EMPTY")) {
                call.setArgument(1, new FieldAccessExpr(new NameExpr("Cell"), "X"));
            }
        });
    }

    public static void bugResetCellY(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(MethodCallExpr.class).forEach(call -> {
            if (call.getNameAsString().equals("fill") &&
                    call.getScope().isPresent() &&
                    call.getScope().get().toString().equals("Arrays") &&
                    call.getArguments().size() == 2 &&
                    call.getArgument(1).toString().equals("Cell.EMPTY")) {
                call.setArgument(1, new FieldAccessExpr(new NameExpr("Cell"), "Y"));
            }
        });
    }

    public static void bugResetTurnToO(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("turn") &&
                    assign.getValue().toString().equals("Player.X")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Player"), "O"));
            }
        });
    }

    public static void bugResetResultToDraw(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.ONGOING")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), "DRAW"));
            }
        });
    }

    public static void bugResetResultToXWins(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.ONGOING")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), "X_WINS"));
            }
        });
    }

    public static void bugResetResultToYWins(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(AssignExpr.class).forEach(assign -> {
            if (assign.getTarget().toString().equals("result") &&
                    assign.getValue().toString().equals("Result.ONGOING")) {
                assign.setValue(new FieldAccessExpr(new NameExpr("Result"), "Y_WINS"));
            }
        });
    }

    // ========== Bugs for isBoardFull ==========

    public static void bugIsBoardFullAlwaysTrue(MethodDeclaration m) {
        bugReturnStatementOnly(m, "true");
    }

    public static void bugIsBoardFullAlwaysFalse(MethodDeclaration m) {
        bugReturnStatementOnly(m, "false");
    }

    public static void bugIsBoardFullInvertThreeInRow(MethodDeclaration m) {
        invertMethodCall(m, "threeInRow");
    }

    public static void bugIsBoardFullInvertThreeInRowAllIndex0(MethodDeclaration m) {
        bugIsBoardFullInvertThreeInRowAllByOneIndex(m, "0");
    }

    public static void bugIsBoardFullInvertThreeInRowAllIndex1(MethodDeclaration m) {
        bugIsBoardFullInvertThreeInRowAllByOneIndex(m, "1");
    }

    public static void bugIsBoardFullInvertThreeInRowAllIndex2(MethodDeclaration m) {
        bugIsBoardFullInvertThreeInRowAllByOneIndex(m, "2");
    }

    public static void bugIsBoardFullInvertReturnAfterIf(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            if (ifStmt.getThenStmt().isReturnStmt()) {
                ReturnStmt returnStmt = ifStmt.getThenStmt().asReturnStmt();
                if (returnStmt.getExpression().isPresent() &&
                        returnStmt.getExpression().get().isBooleanLiteralExpr()) {
                    BooleanLiteralExpr boolExpr = returnStmt.getExpression().get().asBooleanLiteralExpr();
                    if (boolExpr.getValue()) {
                        returnStmt.setExpression(new BooleanLiteralExpr(false));
                    }
                }
            }
        });
    }

    // ========== Bugs for initBoard ==========

    public static void bugInitBoardEmpty(MethodDeclaration m) {
        m.setBody(new BlockStmt());
    }

    public static void bugInitBoardArraySize(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ArrayCreationExpr.class).forEach(array -> {
            if (array.getElementType().asString().equals("Cell") &&
                    array.getLevels().size() == 1 &&
                    array.getLevels().get(0).getDimension().isPresent() &&
                    array.getLevels().get(0).getDimension().get().toString().equals("9")) {
                array.getLevels().get(0).setDimension(new IntegerLiteralExpr("3"));
            }
        });
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
        body.findAll(ForStmt.class).forEach(forStmt -> {
            var compare = forStmt.getCompare().orElse(null);
            if (compare != null && compare.isBinaryExpr()) {
                BinaryExpr condition = compare.asBinaryExpr();
                if (condition.getLeft().toString().equals("i") &&
                        condition.getOperator() == BinaryExpr.Operator.LESS &&
                        condition.getRight().toString().equals("board.length")) {
                    condition.setRight(new IntegerLiteralExpr("1"));
                }
            }
        });
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
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent() && ret.getExpression().get().isBinaryExpr()) {
                BinaryExpr expr = ret.getExpression().get().asBinaryExpr();
                if (expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                        expr.getLeft().toString().equals("result") &&
                        expr.getRight().toString().equals("Result.ONGOING")) {
                    expr.setOperator(BinaryExpr.Operator.EQUALS);
                }
            }
        });
    }

    public static void bugIsTerminalResultNotEqualsXWins(MethodDeclaration m) {
        bugChangeResultEquality(m, "X_WINS");
    }

    public static void bugIsTerminalResultNotEqualsYWins(MethodDeclaration m) {
        bugChangeResultEquality(m, "Y_WINS");
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

    // ========== Bugs for isBoardFull ==========
    public static void bugIsBoardFullCNotEqualsEmpty(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                BinaryExpr binary = condition.asBinaryExpr();
                if (binary.getLeft().toString().equals("c") &&
                        binary.getOperator() == BinaryExpr.Operator.EQUALS &&
                        binary.getRight().toString().equals("Cell.EMPTY")) {
                    binary.setOperator(BinaryExpr.Operator.NOT_EQUALS);
                }
            }
        });
    }

    public static void bugIsBoardFullCEqualsX(MethodDeclaration m) {
        bugEqualsCellX(m);
    }

    public static void bugIsBoardFullCEqualsY(MethodDeclaration m) {
        bugEqualsCellY(m);
    }

    // ========== Bugs for setLines ==========

    public static void bugSetLinesRemoveFirstDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{0, 1, 2}");
    }

    public static void bugSetLinesRemoveSecondDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{3, 4, 5}");
    }

    public static void bugSetLinesRemoveThirdDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{6, 7, 8}");
    }

    public static void bugSetLinesRemoveForthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{0, 3, 6}");
    }

    public static void bugSetLinesRemoveFifthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{1, 4, 7}");
    }

    public static void bugSetLinesRemoveSixthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{2, 5, 8}");
    }

    public static void bugSetLinesRemoveSeventhDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{0, 4, 8}");
    }

    public static void bugSetLinesRemoveEighthDiagonal(MethodDeclaration m) {
        removeDiagonal(m, "{2, 4, 6}");
    }

    // ========== Helper methods ==========

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

    public static void bugThreeInRowChangeIndex(MethodDeclaration m, String oldValue, String newValue) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                    expr.getRight().toString().equals("Cell.EMPTY") &&
                    expr.getLeft().isArrayAccessExpr()) {
                ArrayAccessExpr arrayAccess = expr.getLeft().asArrayAccessExpr();
                if (arrayAccess.getIndex().toString().equals(oldValue)) {
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

    private static void bugEqualsCellY(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(IfStmt.class).forEach(ifStmt -> {
            var condition = ifStmt.getCondition();
            if (condition.isBinaryExpr()) {
                BinaryExpr binary = condition.asBinaryExpr();
                if (binary.getRight().toString().equals("Cell.EMPTY")) {
                    binary.setRight(new FieldAccessExpr(new NameExpr("Cell"), "Y"));
                }
            }
        });
    }

    private static void bugIdxWithXX(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;

        body.findAll(MethodCallExpr.class).forEach(call -> {
            if (call.getNameAsString().equals("idx")) {
                FieldAccessExpr moveX = new FieldAccessExpr(new NameExpr("move"), "x");
                call.setArgument(1, moveX);
            }
        });
    }

    private static void bugIdxWithYY(MethodDeclaration m) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;

        body.findAll(MethodCallExpr.class).forEach(call -> {
            if (call.getNameAsString().equals("idx")) {
                FieldAccessExpr moveY = new FieldAccessExpr(new NameExpr("move"), "y");
                call.setArgument(0, moveY);
            }
        });
    }

    private static void removeDiagonal(MethodDeclaration m, String diagonal) {
        BlockStmt body = m.getBody().orElse(null);
        if (body == null) return;
        body.findAll(ArrayCreationExpr.class).forEach(array -> {
            if (array.getElementType().asString().equals("int")) {
                array.getInitializer().ifPresent(init -> {
                    ArrayInitializerExpr newInit = new ArrayInitializerExpr();

                    init.getValues().forEach(value -> {
                        if (!value.toString().equals(diagonal)) {
                            newInit.getValues().add(value.clone());
                        }
                    });

                    array.setInitializer(newInit);
                });
            }
        });
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
        body.findAll(ReturnStmt.class).forEach(ret -> {
            if (ret.getExpression().isPresent() && ret.getExpression().get().isBinaryExpr()) {
                BinaryExpr expr = ret.getExpression().get().asBinaryExpr();
                if (expr.getLeft().toString().equals("result") &&
                        expr.getRight().toString().equals("Result.ONGOING")) {
                    expr.setRight(new FieldAccessExpr(new NameExpr("Result"), changeName));
                }
            }
        });
    }

    private static void bugIsBoardFullInvertThreeInRowAllByOneIndex(MethodDeclaration m, String index) {
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
        body.findAll(BinaryExpr.class).forEach(expr -> {
            if (expr.getOperator() == BinaryExpr.Operator.NOT_EQUALS &&
                    expr.getRight().toString().equals("Cell.EMPTY")) {
                expr.setRight(new NameExpr(to));
            }
        });
    }
}