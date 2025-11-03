package org.example.generator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

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
}