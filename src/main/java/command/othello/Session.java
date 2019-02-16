package command.othello;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.User;
import net.marvk.nuthello.game.Game;
import net.marvk.nuthello.game.Point;
import net.marvk.nuthello.myplayer.MyPlayer;
import net.marvk.nuthello.player.PlayerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

@Slf4j
public class Session {
    private final Game game;

    private final PointSupplier blackSupplier = new PointSupplier();
    private boolean expired;

    public Session(final User user, final Opponent opponent) {
        this.game = new Game(8, PlayerFactory.human(blackSupplier), PlayerFactory.computer(MyPlayer.class));
    }

    public boolean isExpired() {
        return expired;
    }

    public boolean blackMove(final Point point) {
        return blackSupplier.setPoint(point);
    }

    public Game getGame() {
        return game;
    }

    public PointSupplier getBlackSupplier() {
        return blackSupplier;
    }

    public void setExpired(final boolean expired) {
        this.expired = expired;
    }

    private static class PointSupplier implements Supplier<Point> {
        private CountDownLatch latch;
        private Point point;

        public boolean setPoint(final Point point) {
            this.point = point;

            if (latch == null) {
                return false;
            }

            latch.countDown();
            latch = null;

            return true;
        }

        @Override
        public synchronized Point get() {
            latch = new CountDownLatch(1);

            try {
                latch.await();
            } catch (final InterruptedException e) {
                log.error("Move interrupted", e);
            }

            latch = null;

            return point;
        }
    }

    public enum Opponent {
        CPU, RANDOM
    }
}
