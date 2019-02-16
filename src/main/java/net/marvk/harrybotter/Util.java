package net.marvk.harrybotter;

import net.dv8tion.jda.core.entities.ISnowflake;

public final class Util {
    private Util() {
        throw new AssertionError("No instances of utility class " + Util.class);
    }

    public static String getAtMention(final ISnowflake user) {
        return "<@" + user.getId() + ">";
    }
}
