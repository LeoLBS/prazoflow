package br.com.leperber.prazoflow.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumSet;

@Configuration
@ConditionalOnProperty(name = "discord.enabled", havingValue = "true", matchIfMissing = true)
public class DiscordBotConfig {

    private static final Logger log = LoggerFactory.getLogger(DiscordBotConfig.class);

    @Bean(destroyMethod = "shutdown")
    public JDA jda(@Value("${discord.token}") String token) throws InterruptedException {
        JDA jda = JDABuilder
                .createLight(token, EnumSet.noneOf(GatewayIntent.class))
                .build()
                .awaitReady();

        log.info("Bot conectado ao Discord como {}", jda.getSelfUser().getName());
        return jda;
    }
}