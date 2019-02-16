package net.marvk.harrybotter.command.othello;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import net.marvk.nuthello.game.Board;
import net.marvk.nuthello.game.Disc;
import net.marvk.nuthello.game.Move;
import net.marvk.nuthello.game.Point;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

final class BoardUtil {
    private BoardUtil() {
        throw new AssertionError("No instances of utility class " + BoardUtil.class);
    }

    static String render(final Board board, final List<Move> validMoves) {
        final AsciiTable at = new AsciiTable();
        at.getRenderer().setCWC(new CWC_LongestLine());

        at.addRule();
        for (int i = 0; i < 8; i++) {
            final int finalI = i;
            final List<String> row =
                    IntStream.range(0, 8)
                             .mapToObj(j -> board.get(j, finalI))
                             .map(BoardUtil::discMapper)
                             .collect(Collectors.toList());

            for (int j = 0; j < validMoves.size(); j++) {
                final Move validMove = validMoves.get(j);
                final Point point = validMove.getPoint();

                if (point.getY() == i) {
                    row.set(point.getX(), Integer.toString(j));
                }
            }
            at.addRow(row);
            at.addRule();
        }

        at.setTextAlignment(TextAlignment.CENTER);
        return at.render();
    }

    private static String discMapper(final Disc disc) {
        switch (disc) {
            case BLACK:
                return "▒▒▒";
            case WHITE:
                return "███";
            default:
                return "   ";
        }
    }
}
