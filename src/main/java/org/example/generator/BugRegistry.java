package org.example.generator;

import java.util.*;

public class BugRegistry {
    private static final Map<String, List<BugMutation>> BUGS = new HashMap<>();

    static {
        register("threeInRow",
                BugLibrary::bugThreeInRowAlwaysTrue,
                BugLibrary::bugThreeInRowAlwaysFalse,
                BugLibrary::bugThreeInRowIsNotEmptyEquals,
                BugLibrary::bugThreeInRowIsNotEmptyCellX,
                BugLibrary::bugThreeInRowIsNotEmptyCellO,
                BugLibrary::bugThreeInRowIsNotEmptyNull,
                BugLibrary::bugThreeInRowIsNotEmptyCellJ,
                BugLibrary::bugThreeInRowIsNotEmptyCellK,
                BugLibrary::bugThreeInRowEqualIJtoNotEqual,
                BugLibrary::bugThreeInRowEqualIJtoIK,
                BugLibrary::bugThreeInRowEqualIJtoII,
                BugLibrary::bugThreeInRowEqualIJtoJJ,
                BugLibrary::bugThreeInRowEqualJKtoNotEqual,
                BugLibrary::bugThreeInRowEqualJKtoJJ,
                BugLibrary::bugThreeInRowEqualJKtoKK,
                BugLibrary::bugThreeInRowReturnSecondOR,
                BugLibrary::bugThreeInRowReturnFirstOR,
                BugLibrary::bugThreeInRowReturnNotFirst,
                BugLibrary::bugThreeInRowReturnNotSecond,
                BugLibrary::bugThreeInRowReturnNotThird
        );
        register("getWinner",
                BugLibrary::bugGetWinnerAlwaysX,
                BugLibrary::bugGetWinnerAlwaysEmpty,
                BugLibrary::bugGetWinnerFlipWinners,
                BugLibrary::bugGetWinnerRemoveSwitch
        );

        register("playTurn",
                BugLibrary::bugPlayTurnEmpty,
                BugLibrary::bugPlayTurnRemoveFirstLine,
                BugLibrary::bugPlayTurnTurnNotEqualToPlayerX,
                BugLibrary::bugPlayTurnTurnEqualToPlayerO,
                BugLibrary::bugPlayTurnIfTurnEqualsXCellInvert,
                BugLibrary::bugPlayTurnInvertHasWins,
                BugLibrary::bugPlayTurnResultInvert,
                BugLibrary::bugPlayTurnIsBoardFullInvert,
                BugLibrary::bugPlayTurnInvertResultDrawToOngoing,
                BugLibrary::bugPlayTurnInvertResultDrawToXWins,
                BugLibrary::bugPlayTurnInvertResultDrawToOWins,
                BugLibrary::bugPlayTurnNoTurnSwitch,
                BugLibrary::bugPlayTurnToX,
                BugLibrary::bugPlayTurnToO,
                BugLibrary::bugPlayTurnIdxWithXX,
                BugLibrary::bugPlayTurnIdxWithYY
        );

        register("validateMove",
                BugLibrary::bugValidateMoveEmpty,
                BugLibrary::bugValidateMoveIsTerminalInvert,
                BugLibrary::bugValidateMoveEqualsTurn,
                BugLibrary::bugValidateMoveXMoreThan0,
                BugLibrary::bugValidateMoveXLessThan2,
                BugLibrary::bugValidateMoveYMoreThan0,
                BugLibrary::bugValidateMoveYLessThan2,
                BugLibrary::bugValidateMoveInvertFirstAnd,
                BugLibrary::bugValidateMoveInvertSecondAnd,
                BugLibrary::bugValidateMoveInvertThirdAnd,
                BugLibrary::bugValidateMoveBoardEqualsEmpty,
                BugLibrary::bugValidateMoveBoardNotEqualsX,
                BugLibrary::bugValidateMoveBoardNotEqualsO,
                BugLibrary::bugValidateMoveIdxWithXX,
                BugLibrary::bugValidateMoveIdxWithYY
        );

        register("reset",
                BugLibrary::bugResetEmpty,
                BugLibrary::bugResetNoArraysFill,
                BugLibrary::bugResetCellX,
                BugLibrary::bugResetCellO,
                BugLibrary::bugResetTurnToO,
                BugLibrary::bugResetResultToDraw,
                BugLibrary::bugResetResultToXWins,
                BugLibrary::bugResetResultToOWins
        );

        register("isBoardFull",
                BugLibrary::bugIsBoardFullAlwaysTrue,
                BugLibrary::bugIsBoardFullAlwaysFalse,
                BugLibrary::bugIsBoardFullCNotEqualsEmpty,
                BugLibrary::bugIsBoardFullCEqualsX,
                BugLibrary::bugIsBoardFullCEqualsO
        );

        register("initBoard",
                BugLibrary::bugInitBoardArraySize
        );

        register("getState",
                BugLibrary::bugGetStateArraySize,
                BugLibrary::bugGetState1Iteration,
                BugLibrary::bugGetStateInvertXAndO
        );

        register("isTerminal",
                BugLibrary::bugIsTerminalAlwaysTrue,
                BugLibrary::bugIsTerminalAlwaysFalse,
                BugLibrary::bugIsTerminalResultEqualsOngoing,
                BugLibrary::bugIsTerminalResultNotEqualsXWins,
                BugLibrary::bugIsTerminalResultNotEqualsOWins,
                BugLibrary::bugIsTerminalResultNotEqualsDraw
        );

        register("turn",
                BugLibrary::bugTurnAlwaysX,
                BugLibrary::bugTurnAlwaysO
        );

        register("setLines",
                BugLibrary::bugSetLinesRemoveFirstDiagonal,
                BugLibrary::bugSetLinesRemoveSecondDiagonal,
                BugLibrary::bugSetLinesRemoveThirdDiagonal,
                BugLibrary::bugSetLinesRemoveForthDiagonal,
                BugLibrary::bugSetLinesRemoveFifthDiagonal,
                BugLibrary::bugSetLinesRemoveSixthDiagonal,
                BugLibrary::bugSetLinesRemoveSeventhDiagonal,
                BugLibrary::bugSetLinesRemoveEighthDiagonal
        );

        register("hasWin",
                BugLibrary::bugHasWinFullInvertThreeInRowAllIndex0,
                BugLibrary::bugHasWinInvertThreeInRowAllIndex1,
                BugLibrary::bugHasWinInvertThreeInRowAllIndex2,
                BugLibrary::bugHasWinInvertThreeInRow,
                BugLibrary::bugHasWinInvertReturnAfterIf
        );
    }

    private static void register(String methodName, BugMutation... bugs) {
        BUGS.put(methodName, Arrays.asList(bugs));
    }

    public static Optional<BugMutation> getBug(String methodName, int index) {
        List<BugMutation> list = BUGS.get(methodName);
        if (list == null || index >= list.size()) return Optional.empty();
        return Optional.of(list.get(index % list.size()));
    }

    public static int getBugMethodsAmount(String bugMethodName) {
        return BUGS.get(bugMethodName).size();
    }

    public static String[] getAllMethodNames() {
        return BUGS.keySet().toArray(new String[0]);
    }
}
