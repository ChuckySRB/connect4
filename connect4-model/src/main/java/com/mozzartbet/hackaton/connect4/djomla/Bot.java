package com.mozzartbet.hackaton.connect4.djomla;

import com.mozzartbet.hackaton.connect4.model.GameBoard;
import com.mozzartbet.hackaton.connect4.model.Player;

import java.util.concurrent.ThreadLocalRandom;

import static com.mozzartbet.hackaton.connect4.model.GameConsts.COLUMNS;

public class Bot extends Player {
    private boolean firstMoveMe = true;
    private boolean firstMoveOpponent = true;
    private boolean meFirst = false;
    private int lastOpponentMove;
    private int lastMyMove;

    private long timeOutMilis;
    private GameBoard gameBoard;

    private void firstMove() {
        if (meFirst) {
            move = 3;
        } else {
            if (lastOpponentMove >= 4) {
                move = lastOpponentMove - 1;
            } else if (lastOpponentMove <= 3) {
                move = lastOpponentMove + 1;
            } else {
                move = lastOpponentMove - 1;
            }
        }
    }

    @Override
    public void configure(long timeoutMillis) {
        // TODO Auto-generated method stub
        this.timeOutMilis = timeoutMillis;
        this.gameBoard = new GameBoard();
    }

    @Override
    public void move() {
        if (firstMoveMe) {
            if (firstMoveOpponent)
                meFirst = true;
            else {
                meFirst = false;
            }
            firstMoveMe = false;
            firstMove();
            lastMyMove = move;
            place(move, 1);
            return;
        }

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        move = rnd.nextInt(COLUMNS);

        move = defence();
        if (move >= 0) {
            place(move, 1);
            return;
        }

        int potAttack;
        potAttack = attack();
        place(potAttack, 1);

        move = defence();
        if (move >= 0) {
            place(potAttack, 0);
        } else {
            move = potAttack;
        }

        lastMyMove = move;
        place(move, 1);
    }

    private int attack() {

        int dijagonal = findDijagonalDangerAttack();
        if (dijagonal >= 0)
            return dijagonal;
        int horizontal = findHorizontalDangerAttack();
        if (horizontal >= 0) {
            return horizontal;
        }
        int vertical = findVerticalDangerAttack();
        if (vertical >= 0) {
            return vertical;
        }

        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        return rnd.nextInt(COLUMNS);

    }

    private int findVerticalDangerAttack() {
        for (int j = 0; j < 8; j++) {
            if (cntOpponentInColAttack(j) >= 2)
                return j;
        }
        return -1;
    }

    private int cntOpponentInColAttack(int col) {
        int cnt = 0;
        for (int i = 0; i < 6; i++) {
            int counter = gameBoard.getBoard()[i][col];
            if (counter == 0)
                continue;
            if (counter == 1)
                cnt++;
            else
                break;
        }
        return cnt;
    }

    private int findHorizontalDangerAttack() {
        for (int j = 0; j < 6; j++) {
            for (int i = 1; i < 6; i++) {
                if (j == 5) {
                    if (gameBoard.getBoard()[j][i - 1] == 0 && gameBoard.getBoard()[j][i + 2] == 0
                            && gameBoard.getBoard()[j][i] == gameBoard.getBoard()[j][i + 1]
                            && gameBoard.getBoard()[j][i] == 1) {
                        return i - 1;
                    }
                } else {
                    if (gameBoard.getBoard()[j][i - 1] == 0 && gameBoard.getBoard()[j][i + 2] == 0
                            && gameBoard.getBoard()[j][i] == gameBoard.getBoard()[j][i + 1]
                            && gameBoard.getBoard()[j][i] == 1 && gameBoard.getBoard()[j + 1][i - 1] != 0
                            && gameBoard.getBoard()[j + 1][i + 2] != 0) {
                        return i - 1;
                    }
                }
            }

            for (int i = 1; i < 5; i++) {
                if (j == 5) {
                    boolean two0two = gameBoard.getBoard()[j][i] == 1 && gameBoard.getBoard()[j][i + 1] == 0
                            && gameBoard.getBoard()[j][i + 2] == 1;
                    if (two0two) {
                        return i + 1;
                    }
                } else {
                    boolean two0two = gameBoard.getBoard()[j][i] == 1 && gameBoard.getBoard()[j][i + 1] == 0
                            && gameBoard.getBoard()[j][i + 2] == 1;
                    boolean fillUnder = gameBoard.getBoard()[j + 1][i - 1] != 0 && gameBoard.getBoard()[j + 1][i] != 0
                            && gameBoard.getBoard()[j + 1][i + 1] != 0 && gameBoard.getBoard()[j + 1][i + 2] != 0
                            && gameBoard.getBoard()[j + 1][i + 3] != 0;

                    if (two0two && fillUnder) {
                        return i + 1;
                    }
                }
            }

            for (int i = 0; i < 6; i++) {
                boolean threeSame = gameBoard.getBoard()[j][i] == 1 && gameBoard.getBoard()[j][i + 1] == 1
                        && gameBoard.getBoard()[j][i + 2] == 1;
                if (j == 5) {
                    if (i == 0) {
                        if (threeSame && gameBoard.getBoard()[j][i + 3] == 0) {
                            return i + 3;
                        }
                    } else if (i == 5) {
                        if (threeSame && gameBoard.getBoard()[j][i - 1] == 0) {
                            return i - 1;
                        }
                    } else {
                        if (threeSame && (gameBoard.getBoard()[j][i - 1] == 0 || gameBoard.getBoard()[j][i + 3] == 0)) {
                            if (gameBoard.getBoard()[j][i - 1] == 0)
                                return i - 1;
                            if (gameBoard.getBoard()[j][i + 3] == 0) {
                                return i + 3;
                            }
                        }
                    }
                } else {
                    boolean fillUnder = gameBoard.getBoard()[j + 1][i] != 0 && gameBoard.getBoard()[j + 1][i + 1] != 0
                            && gameBoard.getBoard()[j + 1][i + 2] != 0;
                    if (i == 0) {
                        if (threeSame && gameBoard.getBoard()[j][i + 3] == 0 && fillUnder
                                && gameBoard.getBoard()[j + 1][i + 3] != 0) {
                            return i + 3;
                        }
                    } else if (i == 5) {
                        if (threeSame && gameBoard.getBoard()[j][i - 1] == 0 && fillUnder
                                && gameBoard.getBoard()[j + 1][i - 1] != 0) {
                            return i - 1;
                        }
                    } else {
                        if (threeSame && (gameBoard.getBoard()[j][i - 1] == 0 || gameBoard.getBoard()[j][i + 3] == 0)
                                && fillUnder && gameBoard.getBoard()[j + 1][i + 3] != 0 && fillUnder
                                && gameBoard.getBoard()[j + 1][i - 1] != 0) {
                            if (gameBoard.getBoard()[j][i - 1] == 0)
                                return i - 1;
                            if (gameBoard.getBoard()[j][i + 3] == 0) {
                                return i + 3;
                            }
                        }
                    }
                }
            }

        }

        return -1;

    }

    private int findDijagonalDangerAttack() {
        int ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_1L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_2L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_3L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_4L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_5L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_1R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_2R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_3R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_4R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonalAttack(DIJAGONALS_5R);
        if (ret >= 0)
            return ret;
        return -1;
    }

    private int findDangerDijagonalAttack(int[][] dij) {
        for (int k = 5; k >= 3; k--) {
            int rowK = k;
            /*
             * boolean no = false; for (int i = dij.length - 1; i >= 2; i--,
             * rowK--) { int row = rowK, col = dij[i][1], nextRow = rowK - 1,
             * nextCol = dij[i - 1][1]; if (gameBoard.getBoard()[row][col] != 2)
             * { no = true; break; } if (gameBoard.getBoard()[row][col] !=
             * gameBoard.getBoard()[nextRow][nextCol]) { print(row + " " + col +
             * " , " + nextRow + " " + nextCol); no = true; break; } } if (!no)
             * { print("dijago danger" + dij[0][1]); return dij[0][1]; }
             */
            int cnt = 0;
            int cntOne = 0;
            int notSameCol = 0, notSameRow = 0;
            for (int i = dij.length - 1; i >= 0; i--, rowK--) {
                int row = rowK, col = dij[i][1];
                if (gameBoard.getBoard()[row][col] == 1) {
                    cnt++;
                } else if (gameBoard.getBoard()[row][col] == 0) {
                    notSameCol = col;
                    notSameRow = row;
                } else {
                    cntOne++;
                }
            }
            if (cnt == 3 && cntOne == 0) {
                boolean fillUnder = true;
                for (int q = notSameRow + 1; q < 6; q++) {
                    fillUnder = fillUnder && gameBoard.getBoard()[q][notSameCol] != 0;
                }
                if (fillUnder)
                    return notSameCol;
            }
        }
        return -1;
    }

    @Override
    public void stop() {

    }

    @Override
    public void opponentMove(int move) {
        lastOpponentMove = move;
        if (firstMoveOpponent) {
            firstMoveOpponent = false;
            meFirst = false;
        }
        place(move, 2);
    }

    @Override
    public void finished(int winner) {
        // TODO Auto-generated method stub

    }

    private int defence() {
        int dijagonal = findDijagonalDanger();
        if (dijagonal >= 0)
            return dijagonal;
        int horizontal = findHorizontalDanger();
        if (horizontal >= 0) {
            return horizontal;
        }
        int vertical = findVerticalDanger();
        if (vertical >= 0) {
            return vertical;
        }

        return -1;
    }

    private int findVerticalDanger() {
        for (int j = 0; j < 8; j++) {
            if (cntOpponentInCol(j) >= 2)
                return j;
        }
        return -1;
    }

    private int findHorizontalDanger() {
        for (int j = 0; j < 6; j++) {
            for (int i = 1; i < 6; i++) {
                if (j == 5) {
                    if (gameBoard.getBoard()[j][i - 1] == 0 && gameBoard.getBoard()[j][i + 2] == 0
                            && gameBoard.getBoard()[j][i] == gameBoard.getBoard()[j][i + 1]
                            && gameBoard.getBoard()[j][i] == 2) {
                        return i - 1;
                    }
                } else {
                    if (gameBoard.getBoard()[j][i - 1] == 0 && gameBoard.getBoard()[j][i + 2] == 0
                            && gameBoard.getBoard()[j][i] == gameBoard.getBoard()[j][i + 1]
                            && gameBoard.getBoard()[j][i] == 2 && gameBoard.getBoard()[j + 1][i - 1] != 0
                            && gameBoard.getBoard()[j + 1][i + 2] != 0) {
                        return i - 1;
                    }
                }
            }

            for (int i = 1; i < 5; i++) {
                if (j == 5) {
                    boolean two0two = gameBoard.getBoard()[j][i] == 2 && gameBoard.getBoard()[j][i + 1] == 0
                            && gameBoard.getBoard()[j][i + 2] == 2;
                    if (two0two) {
                        return i + 1;
                    }
                } else {
                    boolean two0two = gameBoard.getBoard()[j][i] == 2 && gameBoard.getBoard()[j][i + 1] == 0
                            && gameBoard.getBoard()[j][i + 2] == 2;
                    boolean fillUnder = gameBoard.getBoard()[j + 1][i - 1] != 0 && gameBoard.getBoard()[j + 1][i] != 0
                            && gameBoard.getBoard()[j + 1][i + 1] != 0 && gameBoard.getBoard()[j + 1][i + 2] != 0
                            && gameBoard.getBoard()[j + 1][i + 3] != 0;

                    if (two0two && fillUnder) {
                        return i + 1;
                    }
                }
            }

            for (int i = 0; i < 6; i++) {
                boolean threeSame = gameBoard.getBoard()[j][i] == 2 && gameBoard.getBoard()[j][i + 1] == 2
                        && gameBoard.getBoard()[j][i + 2] == 2;
                if (j == 5) {
                    if (i == 0) {
                        if (threeSame && gameBoard.getBoard()[j][i + 3] == 0) {
                            return i + 3;
                        }
                    } else if (i == 5) {
                        if (threeSame && gameBoard.getBoard()[j][i - 1] == 0) {
                            return i - 1;
                        }
                    } else {
                        if (threeSame && (gameBoard.getBoard()[j][i - 1] == 0 || gameBoard.getBoard()[j][i + 3] == 0)) {
                            if (gameBoard.getBoard()[j][i - 1] == 0)
                                return i - 1;
                            if (gameBoard.getBoard()[j][i + 3] == 0) {
                                return i + 3;
                            }
                        }
                    }
                } else {
                    boolean fillUnder = gameBoard.getBoard()[j + 1][i] != 0 && gameBoard.getBoard()[j + 1][i + 1] != 0
                            && gameBoard.getBoard()[j + 1][i + 2] != 0;
                    if (i == 0) {
                        if (threeSame && gameBoard.getBoard()[j][i + 3] == 0 && fillUnder
                                && gameBoard.getBoard()[j + 1][i + 3] != 0) {
                            return i + 3;
                        }
                    } else if (i == 5) {
                        if (threeSame && gameBoard.getBoard()[j][i - 1] == 0 && fillUnder
                                && gameBoard.getBoard()[j + 1][i - 1] != 0) {
                            return i - 1;
                        }
                    } else {
                        if (threeSame && (gameBoard.getBoard()[j][i - 1] == 0 || gameBoard.getBoard()[j][i + 3] == 0)
                                && fillUnder && gameBoard.getBoard()[j + 1][i + 3] != 0 && fillUnder
                                && gameBoard.getBoard()[j + 1][i - 1] != 0) {
                            if (gameBoard.getBoard()[j][i - 1] == 0)
                                return i - 1;
                            if (gameBoard.getBoard()[j][i + 3] == 0) {
                                return i + 3;
                            }
                        }
                    }
                }
            }

        }

        return -1;

    }

    private int[][] DIJAGONALS_1L = { { 2, 0 }, { 3, 1 }, { 4, 2 }, { 5, 3 } };
    private int[][] DIJAGONALS_2L = { { 2, 1 }, { 3, 2 }, { 4, 3 }, { 5, 4 } };
    private int[][] DIJAGONALS_3L = { { 2, 2 }, { 3, 3 }, { 4, 4 }, { 5, 5 } };
    private int[][] DIJAGONALS_4L = { { 2, 3 }, { 3, 4 }, { 4, 5 }, { 5, 6 } };
    private int[][] DIJAGONALS_5L = { { 2, 4 }, { 3, 5 }, { 4, 6 }, { 5, 7 } };

    private int[][] DIJAGONALS_1R = { { 2, 7 }, { 3, 6 }, { 4, 5 }, { 5, 6 } };
    private int[][] DIJAGONALS_2R = { { 2, 6 }, { 3, 5 }, { 4, 4 }, { 5, 3 } };
    private int[][] DIJAGONALS_3R = { { 2, 5 }, { 3, 4 }, { 4, 3 }, { 5, 2 } };
    private int[][] DIJAGONALS_4R = { { 2, 4 }, { 3, 3 }, { 4, 2 }, { 5, 1 } };
    private int[][] DIJAGONALS_5R = { { 2, 3 }, { 3, 2 }, { 4, 1 }, { 5, 0 } };

    private int findDangerDijagonal(int[][] dij) {
        for (int k = 5; k >= 3; k--) {
            int rowK = k;
            /*
             * boolean no = false; for (int i = dij.length - 1; i >= 2; i--,
             * rowK--) { int row = rowK, col = dij[i][1], nextRow = rowK - 1,
             * nextCol = dij[i - 1][1]; if (gameBoard.getBoard()[row][col] != 2)
             * { no = true; break; } if (gameBoard.getBoard()[row][col] !=
             * gameBoard.getBoard()[nextRow][nextCol]) { print(row + " " + col +
             * " , " + nextRow + " " + nextCol); no = true; break; } } if (!no)
             * { print("dijago danger" + dij[0][1]); return dij[0][1]; }
             */
            int cnt = 0;
            int cntOne = 0;
            int notSameCol = 0, notSameRow = 0;
            for (int i = dij.length - 1; i >= 0; i--, rowK--) {
                int row = rowK, col = dij[i][1];
                if (gameBoard.getBoard()[row][col] == 2) {
                    cnt++;
                } else if (gameBoard.getBoard()[row][col] == 0) {
                    if (row > notSameRow)
                        notSameRow = row;
                    notSameCol = col;
                } else {
                    cntOne++;
                }
            }
            if (cnt == 3 && cntOne == 0) {
                boolean fillUnder = true;
                for (int q = notSameRow + 1; q < 6; q++) {
                    fillUnder = fillUnder && gameBoard.getBoard()[q][notSameCol] != 0;
                }
                if (fillUnder)
                    return notSameCol;
            }
        }
        return -1;
    }

    private int findDijagonalDanger() {
        int ret;
        ret = findDangerDijagonal(DIJAGONALS_1L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_2L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_3L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_4L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_5L);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_1R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_2R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_3R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_4R);
        if (ret >= 0)
            return ret;
        ret = findDangerDijagonal(DIJAGONALS_5R);
        if (ret >= 0)
            return ret;
        return -1;
    }

    private int cntOpponentInCol(int col) {
        int cnt = 0;
        for (int i = 0; i < 6; i++) {
            int counter = gameBoard.getBoard()[i][col];
            if (counter == 0)
                continue;
            if (counter == 2)
                cnt++;
            else
                break;
        }
        return cnt;
    }

    private void print(String s) {
        System.out.println(s);
    }

    private boolean place(int move, int cnt) {
        return gameBoard.placeCounter(move, cnt);
    }
}
