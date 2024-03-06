package com.employee.management.controller;

import com.employee.management.service.GoogleCalendarService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.employee.management.service.GoogleCalendarService.*;

@RestController
public class CalendarController {

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @GetMapping("/events")
    public List<String> getEvents(@RequestParam String month) throws IOException, GeneralSecurityException {
        String accessToken = googleCalendarService.getGoogleAccessToken();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(GoogleNetHttpTransport.newTrustedTransport())
                .setJsonFactory(JSON_FACTORY)
                .build().setAccessToken(accessToken);

        Calendar service = new Calendar.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(request -> request.set("key", API_KEY))
                .build();

        String calendarId = "courses@seabed2crest.com";
        String timeMin = month + "-01T00:00:00Z";
        String timeMax = month + "-31T23:59:59Z";

        Events events = service.events().list(calendarId)
                .setTimeMin(DateTime.parseRfc3339(timeMin))
                .setTimeMax(DateTime.parseRfc3339(timeMax))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        List<String> eventSummaries = new ArrayList<>();
        for (Event event : events.getItems()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(event.getStart().getDateTime().getValue());
            eventSummaries.add(date + ": " + event.getSummary());
        }

        return eventSummaries;
    }
}
