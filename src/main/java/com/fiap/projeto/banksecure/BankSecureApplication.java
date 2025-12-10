package com.fiap.projeto.banksecure;

import com.fiap.projeto.banksecure.console.ConsoleMenu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BankSecureApplication {
    private final static boolean runConsoleMenu = true;
    private final static boolean runConsoleRunner = false;

	public static void main(String[] args) {
        ConsoleRunner.skipConsoleRunner = !runConsoleRunner;
        ConfigurableApplicationContext context = SpringApplication.run(BankSecureApplication.class, args);

        if (runConsoleMenu) {
            ConsoleMenu consoleMenu = context.getBean(ConsoleMenu.class);
            consoleMenu.start();
        }
	}
}