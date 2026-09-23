package br.com.leperber.prazoflow.bot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.util.concurrent.CompletableFuture;

@Component
@ConditionalOnProperty(name = "discord.enabled", havingValue = "true", matchIfMissing = true)
public class DiscordNotificador {

    private static final Logger log = LoggerFactory.getLogger(DiscordNotificador.class);

    private static final Color COR_PRAZO = new Color(255, 176, 32);
    private static final Color COR_ATRASO = new Color(220, 53, 69);

    private static final Color COR_CONCLUSAO = new Color(46, 204, 113);      // verde
    private static final Color COR_REAGENDAMENTO = new Color(52, 152, 219);  // azul

    private final JDA jda;

    public DiscordNotificador(JDA jda) {
        this.jda = jda;
    }

    public CompletableFuture<Void> enviarDm(String discordUserId, MessageEmbed embed) {
        return jda.retrieveUserById(discordUserId)
                .flatMap(User::openPrivateChannel)
                .flatMap(canal -> canal.sendMessageEmbeds(embed))
                .submit()
                .thenAccept(enviada -> log.info("DM enviada para o usuario {}", discordUserId));
    }

    public static EmbedBuilder novoEmbedDePrazo() {
        return new EmbedBuilder()
                .setTitle("📋 Prazos se aproximando")
                .setColor(COR_PRAZO)
                .setFooter("PrazoFlow • gerado automaticamente");
    }

    public static EmbedBuilder novoEmbedDeAtraso() {
        return new EmbedBuilder()
                .setTitle("⚠️ Demandas em atraso")
                .setColor(COR_ATRASO)
                .setFooter("PrazoFlow • gerado automaticamente");
    }

    public static EmbedBuilder novoEmbedDeConclusao() {
        return new EmbedBuilder()
                .setTitle("✅ Demanda concluída")
                .setColor(COR_CONCLUSAO)
                .setFooter("PrazoFlow • gerado automaticamente");
    }

    public static EmbedBuilder novoEmbedDeReagendamento() {
        return new EmbedBuilder()
                .setTitle("🔄 Prazo reagendado")
                .setColor(COR_REAGENDAMENTO)
                .setFooter("PrazoFlow • gerado automaticamente");
    }
}