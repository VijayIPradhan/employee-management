package com.employee.management.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleCalendarService {

    public static final String APPLICATION_NAME = "S2BC-HRMS";
    public static final String API_KEY = "AIzaSyB3PJCHL2ukV6jqRIULLZe7YzIZ6S34_U0";
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Value("${google.client.client-id}")
    private String clientId;

    @Value("${google.client.client-secret}")
    private String clientSecret;

    public String getGoogleAccessToken() throws IOException, GeneralSecurityException {
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JSON_FACTORY)
                .setClientSecrets(clientId, clientSecret)
                .build();

        credential.refreshToken();
        return credential.getAccessToken();
    }
}
