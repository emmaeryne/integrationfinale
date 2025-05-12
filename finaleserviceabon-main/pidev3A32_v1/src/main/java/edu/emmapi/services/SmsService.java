package edu.emmapi.services;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class SmsService {
    // ✅ REMPLACE AVEC TES PROPRES IDENTIFIANTS TWILIO
    public static final String ACCOUNT_SID = "ACf0dbef4a3f2b361c55dd2194424a7513";
    public static final String AUTH_TOKEN = "bbe1c2b686cc1d5bcb798a852dc0fcbc";
    public static final String TWILIO_PHONE_NUMBER = "+18056784109";

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
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
