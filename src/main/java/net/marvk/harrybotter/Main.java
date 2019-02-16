package net.marvk.harrybotter;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class Main extends ListenerAdapter {
    private static final String PROPERTIES_FILE_NAME = "config.properties";
    private static final String TOKEN_FILE_NAME = "discord.token";

    public static void main(final String[] args) throws LoginException, IOException, URISyntaxException {
        final Optional<Path> maybeTokenPath = getPath(TOKEN_FILE_NAME);

        if (maybeTokenPath.isEmpty()) {
            log.error("Failed to load token. Please create a file containing only your bot token named \"discord.token\" in the folder containing this application.");
            return;
        }

        final Optional<Path> maybePropertiesPath = getPath(PROPERTIES_FILE_NAME);

        if (maybePropertiesPath.isEmpty()) {
            log.error("Failed to load config. A new config file has been created, please add the relevant values.");
            createDefaultProperties();
            return;
        }

        final String token = Files.lines(maybeTokenPath.get()).collect(Collectors.joining());

        final Properties properties = new Properties();
        properties.load(new FileInputStream(maybePropertiesPath.get().toFile()));

        new JDAClient(token, properties).start();
    }

    private static void createDefaultProperties() throws IOException {
        final Properties properties = new Properties();
        properties.put("prefix", "");
        properties.put("ownerId", "");

        properties.store(new FileOutputStream(Paths.get(PROPERTIES_FILE_NAME).toFile()), "");
    }

    private static Optional<Path> getPath(final String name) throws URISyntaxException {
        final URL systemResource = ClassLoader.getSystemResource(name);

        if (systemResource == null) {
            final Path path = Paths.get(name);

            if (Files.exists(path)) {
                return Optional.of(path);
            }

            return Optional.empty();
        }

        return Optional.of(Paths.get(systemResource.toURI()));
    }
}
