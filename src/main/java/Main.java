import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by time2die on 06.11.16.
 */
@SpringBootApplication

public class Main {
    private static final Logger logger = Logger.getLogger("tRussianBot");

    public static void main(String[] args) throws TelegramApiRequestException {
        TelegramBotsApi tg = new TelegramBotsApi();
        ApiContextInitializer.init();

        tg.registerBot(new MyAmazingBot());
    }

    public static class MyAmazingBot extends TelegramLongPollingBot {
        @Override
        public void onUpdateReceived(Update update) {
            logger.log(Level.ALL, update.getMessage().getChatId().toString());
            logger.log(Level.ALL, update.getMessage().getText());
            if (updateHasCommand(update,"/status")) {
                if (update.getMessage().getChatId() == 69711013 || update.getMessage().getChatId() == -29036710) {

                    SendMessage message = new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText(GoogleApiClient.getStatus());
                    try {
                        sendMessage(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean updateHasCommand(Update update, String message){
            return  update.hasMessage() && update.getMessage().hasText() && message.equals(update.getMessage().getText().trim().toLowerCase()) ;
        }

        @Override
        public String getBotUsername() {
            return "tRussianBank";
        }

        @Override
        public String getBotToken() {
            return "";
        }
    }
}

