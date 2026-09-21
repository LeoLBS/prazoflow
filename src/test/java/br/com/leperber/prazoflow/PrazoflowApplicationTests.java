package br.com.leperber.prazoflow;

import br.com.leperber.prazoflow.bot.DiscordNotificador;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "discord.enabled=false")
class PrazoflowApplicationTests {

	@MockitoBean
    DiscordNotificador discordNotificador;

	@Test
	void contextLoads() {
	}

}
