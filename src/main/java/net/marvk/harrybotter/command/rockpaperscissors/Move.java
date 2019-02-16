package net.marvk.harrybotter.command.rockpaperscissors;

import java.util.Optional;

public enum Move {
    ROCK, PAPER, SCISSORS;

    private Move beatsMove;

    static {
        ROCK.beatsMove = SCISSORS;
        PAPER.beatsMove = ROCK;
        SCISSORS.beatsMove = PAPER;
    }

    public boolean beats(final Move other) {
        return this.beatsMove == other;
    }

    public static Optional<Move> valueOfIgnoresCase(final String s) {
        try {
            return Optional.of(valueOf(s.toUpperCase()));
        } catch (final IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}