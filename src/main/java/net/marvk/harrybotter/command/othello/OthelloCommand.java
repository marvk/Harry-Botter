package net.marvk.harrybotter.command.othello;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.User;
import net.marvk.harrybotter.command.HarryCommand;
import net.marvk.nuthello.game.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class OthelloCommand extends HarryCommand {
    public static final Pattern COMMAND_PATTERN = Pattern.compile("^(?<id>\\d+)$");
    public static final int MAX_GAME_TIME_IN_MINUTES = 15;

    private final SessionRepository sessionRepository;
    private final ScheduledExecutorService executorService;

    public OthelloCommand(final String prefix) {
        super(prefix);
        this.name = "othello";

        this.executorService = Executors.newScheduledThreadPool(10);

        this.sessionRepository = new SessionRepository();
    }

    @Override
    protected void execute(final CommandEvent event) {
        final User author = event.getAuthor();

        final String actualMessage = getActualMessage(event).trim();

        if (!sessionRepository.hasSession(author)) {
            final Session newSession = new Session(author, Session.Opponent.CPU);
            sessionRepository.newSession(author, newSession);

            startSession(newSession, event);
            return;
        }

        final Session session = sessionRepository.getSession(author);

        final Game game = session.getGame();
        if ("show".equals(actualMessage)) {
            event.reply(getBoardString(game.getBoard(), game.getBoard().getValidMoves(Disc.BLACK)));
            return;
        }

        final Optional<Point> maybePlay = getPoint(actualMessage, game.getBoard().getValidMoves(Disc.BLACK));

        if (maybePlay.isEmpty()) {
            event.reply("Please reply with a valid turn id");
            return;
        }

        if (game.getTurn() != Disc.BLACK) {
            event.reply("It wasn't your turn!");
            return;
        }

        session.blackMove(maybePlay.get());
    }

    private void startSession(final Session session, final CommandEvent event) {
        final String authorName = event.getAuthor().getName();
        final Game game = session.getGame();

        final Future<?> future = executorService.submit(() -> {
            event.reply("Starting new game for "
                    + authorName
                    + "...\n"
                    + getBoardString(game.getBoard(), game.getBoard().getValidMoves(Disc.BLACK)));

            while (game.hasNextMove()) {
                final Move move = game.nextMove();

                final List<Move> validMoves;
                if (game.getTurn() == Disc.BLACK) {
                    validMoves = game.getBoard().getValidMoves(Disc.BLACK);
                } else {
                    validMoves = Collections.emptyList();
                }

                event.reply(authorName
                        + "("
                        + game.getNumberOfDiscs(Disc.BLACK)
                        + ") vs Harry("
                        + game.getNumberOfDiscs(Disc.WHITE)
                        + "):\n"
                        + getBoardString(move.getBoard(), validMoves)
                );
            }

            final GameResult result = game.getResult().get();

            if (result.getWinner() == Disc.BLACK) {
                event.reply(authorName + " won!");
            } else {
                event.reply(authorName + " lost!");
            }

            session.setExpired(true);
        });

        executorService.schedule(() -> {
            if (!future.isDone()) {
                future.cancel(true);
                event.reply(authorName + ", your game has been cancelled (15 minute timeout)!");
            }
        }, MAX_GAME_TIME_IN_MINUTES, TimeUnit.MINUTES);
    }

    private static String getBoardString(final Board board, final List<Move> validMoves) {
        return "```\n" + BoardUtil.render(board, validMoves) + "\n```";
    }

    private static Optional<Point> getPoint(final String message, final List<Move> validMoves) {
        final Matcher matcher = COMMAND_PATTERN.matcher(message);

        if (!matcher.matches()) {
            return Optional.empty();
        }

        final int rowId = Integer.parseInt(matcher.group("id"));

        return Optional.of(validMoves.get(rowId).getPoint());
    }
}
