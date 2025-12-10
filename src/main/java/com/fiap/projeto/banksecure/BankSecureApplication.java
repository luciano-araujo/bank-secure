package com.fiap.projeto.banksecure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BankSecureApplication {
    @Value("${run.consoleMenu:true}")
    private static boolean runConsoleMenu;
    @Value("${run.consoleRunner:false}")
    private static boolean runConsoleRunner;

	public static void main(String[] args) {
        ConsoleRunner.skipConsoleRunner = !runConsoleRunner;
        ConfigurableApplicationContext context = SpringApplication.run(BankSecureApplication.class, args);

        if (runConsoleMenu) {
            ConsoleMenu consoleMenu = context.getBean(ConsoleMenu.class);
            consoleMenu.start();
        }
	}
}