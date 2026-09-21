package br.com.leperber.prazoflow.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(name = "discord.enabled", havingValue = "true", matchIfMissing = true)
public class DiscordNotificador {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotificador.class);

    private final JDA jda;

    public DiscordNotificador(JDA jda) {
        this.jda = jda;
    }

    public CompletableFuture<Void> enviarDm(String discordUserId, String mensagem) {
        return jda.retrieveUserById(discordUserId)
                .flatMap(User::openPrivateChannel)
                .flatMap(canal -> canal.sendMessage(mensagem))
                .submit()
                .thenAccept(enviada -> log.info("DM enviada para o usuario {}", discordUserId));
    }
}