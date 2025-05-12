package edu.emmapi.services.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import edu.emmapi.entities.Service;

public class TwilioService {
    private static final String ACCOUNT_SID = "AC9ee4ac8e139bf2eebe288428e78ad967";
    private static final String AUTH_TOKEN = "3bf285de19ceb88b10218de82da7be4e";
    private static final String TWILIO_NUMBER = "+17403325976";

    public TwilioService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public String sendConfirmationSms(Service service, double rating) {
        String clientNumber = "+21627417033";
        String messageBody = String.format("Votre réservation pour %s a été confirmée. Note attribuée: %.1f/5. Merci!",
                service.getNom(), rating);

        try {
            Message message = Message.creator(
                            new PhoneNumber(clientNumber),
                            new PhoneNumber(TWILIO_NUMBER),
                            messageBody)
                    .create();
            return "SMS envoyé (SID: " + message.getSid() + ")";
        } catch (Exception e) {
            return "Erreur lors de l'envoi du SMS: " + e.getMessage();
        }
    }
}