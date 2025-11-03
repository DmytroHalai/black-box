package org.example.generator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;

public class BugLibrary {
    // ========== Bugs for threeInRow ==========

    public static void bugThreeInRowAlwaysTrue(MethodDeclaration m) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("true")));
    }

    public static void bugThreeInRowAlwaysFalse(MethodDeclaration m) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("false")));
    }

    public static void bugThreeInRowInvertCondition(MethodDeclaration m) {
        m.findAll(ReturnStmt.class).forEach(ret -> ret.setExpression(new UnaryExpr(ret.getExpression()
                .orElse(new BooleanLiteralExpr(true)), UnaryExpr.Operator.LOGICAL_COMPLEMENT)));
    }

    public static void bugThreeInRowCompareOnlyTwo(MethodDeclaration m) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("board[i] == board[j]")));
    }

    public static void bugThreeInRowSkipEmptyCheck(MethodDeclaration m) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("board[i] == board[j] && board[j] == board[k]")));
    }

    // ========== Bugs for getWinner ==========

    public static void bugGetWinnerAlwaysX(MethodDeclaration m) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("Optional.of(Player.X)")));
    }

    public static void bugGetWinnerAlwaysEmpty(MethodDeclaration m) {
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("Optional.empty()")));
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
        m.setBody(new BlockStmt().addStatement(new ReturnStmt("Optional.empty()")));
    }

    public static void bugGetWinnerDefaultX(MethodDeclaration m) {
        m.setBody(new BlockStmt().
                addStatement(new ReturnStmt("Optional.ofNullable(result == Result.X_WINS ? Player.X : null)")));
    }

    // ========== Bugs for playTurn ==========

    public static void bugPlayTurnSkipWinCheck(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().removeIf(stmt -> stmt.toString().contains("hasWin")));
    }

    public static void bugPlayTurnAlwaysDraw(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.addStatement(
                new ExpressionStmt(new AssignExpr(new NameExpr("result"),
                        new FieldAccessExpr(new NameExpr("Result"), "DRAW"),
                        AssignExpr.Operator.ASSIGN))));
    }

    public static void bugPlayTurnNoTurnSwitch(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().
                removeIf(stmt -> stmt.toString().contains("turn.other")));
    }

    public static void bugPlayTurnInvertTurnAssignment(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.addStatement(new ExpressionStmt(new AssignExpr(
                new NameExpr("turn"),
                new MethodCallExpr("turn.other"),
                AssignExpr.Operator.ASSIGN))));
    }

    public static void bugPlayTurnSkipAssignment(MethodDeclaration m) {
        m.getBody().ifPresent(body -> body.getStatements().
                removeIf(stmt -> stmt.toString().contains("board[i]")));
    }
}