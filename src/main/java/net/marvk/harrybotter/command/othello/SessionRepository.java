package net.marvk.harrybotter.command.othello;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class SessionRepository {
    private final Map<User, Session> sessionMap;

    public SessionRepository() {
        this.sessionMap = new HashMap<>();
    }

    public boolean hasSession(final User user) {
        final Session session = sessionMap.get(user);

        final boolean hasSession = session != null;

        if (!hasSession) {
            return false;
        }

        return !session.isExpired();
    }

    public Session getSession(final User user) {
        return sessionMap.get(user);
    }

    public void newSession(final User user, final Session session) {
        sessionMap.put(user, session);
    }
}
