package net.marvk.harrybotter.command.rockpaperscissors;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.marvk.harrybotter.Util;
import net.marvk.harrybotter.command.HarryCommand;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
public class RockPaperScissorsCommand extends HarryCommand {
    private static final Move[] MOVES = Move.values();

    public RockPaperScissorsCommand(final String prefix) {
        super(prefix);
        this.name = "rps";
        this.help = "Rock Paper Scissors";
    }

    @Override
    protected void execute(final CommandEvent commandEvent) {
        final String message = getActualMessage(commandEvent);
        final String authorAtMention = Util.getAtMention(commandEvent.getAuthor());

        log.debug("Received " + message + " from " + commandEvent.getAuthor().getName());

        final Optional<Move> maybePlayerMove = Move.valueOfIgnoresCase(message);

        if (maybePlayerMove.isEmpty()) {
            final String legalMovesList = Arrays.stream(MOVES).map(Move::toString).collect(Collectors.joining(", "));
            commandEvent.reply(authorAtMention + ", illegal move! Try one of these: " + legalMovesList);
            return;
        }

        final Move cpuMove = randomMove();

        commandEvent.reply(outcome(maybePlayerMove.get(), cpuMove, authorAtMention));
    }

    private static Move randomMove() {
        return MOVES[ThreadLocalRandom.current().nextInt(MOVES.length)];
    }

    private static String outcome(final Move playerMove, final Move cpuMove, final String username) {
        final String prefix = "I picked " + cpuMove + ", " + username + " ";

        if (playerMove.beats(cpuMove)) {
            return prefix + "wins!";
        }

        if (cpuMove.beats(playerMove)) {
            return prefix + "looses!";
        }

        return prefix + "draws!";
    }
}
