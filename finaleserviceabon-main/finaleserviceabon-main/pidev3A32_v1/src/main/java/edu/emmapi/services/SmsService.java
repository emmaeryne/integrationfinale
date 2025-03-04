package edu.emmapi.services;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SmsService {
    // ✅ REMPLACE AVEC TES PROPRES IDENTIFIANTS TWILIO
    //public static final String ACCOUNT_SID = "AC0295f668cf414f7d9f3d97a4b7c08131";
    public static final String AUTH_TOKEN = "3826e38e706a07d51324db794e6c0dc8";
    public static final String TWILIO_PHONE_NUMBER = "+15513822584";

    static {
        //Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }
    public static void envoyerSms(String numeroDestinataire, String messageTexte) {
        try {
            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(numeroDestinataire),
                    new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
                    messageTexte
            ).create();

            System.out.println("✅ SMS envoyé avec succès ! SID: " + message.getSid());
        } catch (Exception e) {
            System.out.println("❌ Erreur d'envoi du SMS : " + e.getMessage());
        }
    }}
