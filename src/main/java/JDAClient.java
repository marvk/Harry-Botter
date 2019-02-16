import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import command.othello.OthelloCommand;
import command.rockpaperscissors.RockPaperScissorsCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.Properties;

public class JDAClient {
    private final JDABuilder jda;

    public JDAClient(final String token, final Properties config) {
        jda = new JDABuilder(AccountType.BOT);
        jda.setToken(token);

        final String prefix = config.getProperty("prefix");

        final CommandClientBuilder commandBuilder = new CommandClientBuilder();
        commandBuilder.setPrefix(prefix);
        commandBuilder.setOwnerId(config.getProperty("ownerId"));
        commandBuilder.addCommand(new RockPaperScissorsCommand(prefix));
        commandBuilder.addCommand(new OthelloCommand(prefix));
        commandBuilder.setEmojis("\u2714\ufe0f", "\u26a0", "\u274c");

        jda.addEventListener(new EventWaiter());
        jda.addEventListener(commandBuilder.build());
    }

    public void start() throws LoginException {
        jda.build();
    }
}
