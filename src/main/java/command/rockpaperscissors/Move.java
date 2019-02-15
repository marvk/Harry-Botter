package command.rockpaperscissors;

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

    public static Move valueOfIgnoresCase(final String s) {
        return valueOf(s.toUpperCase());
    }
}