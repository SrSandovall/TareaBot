package bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            Tareabot mibot = new Tareabot();
            botsApi.registerBot(mibot);

            System.out.println("el bot se esta ejecutando");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }


    }
}