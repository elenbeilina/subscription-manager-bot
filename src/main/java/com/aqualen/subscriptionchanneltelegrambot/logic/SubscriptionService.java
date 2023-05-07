package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Subscriber;
import com.aqualen.subscriptionchanneltelegrambot.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static com.aqualen.subscriptionchanneltelegrambot.util.BotUtils.fileToObjectConverter;

@Service
public class SubscriptionService {

    private final File subscribersFile;
    private final Map<String, Subscriber> subscriberMap;

    public SubscriptionService() {
        this.subscribersFile = new File("src/main/resources/botData/subscribers-list.json");
        subscriberMap = getSubscribers(subscribersFile);
    }

    @SneakyThrows
    public void processSubscription(User user) {
        String username = user.getUsername();
        if (subscriberMap.containsKey(username)) {
            Subscriber subscriber = subscriberMap.get(username);
            subscriber.addOrRenewSubscription(user);
            subscriberMap.put(username, subscriber);
        } else {
            subscriberMap.put(username, Subscriber.buildSubscriber(user));
        }

        Files.writeString(subscribersFile.toPath(), new ObjectMapper()
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .registerModule(new JavaTimeModule()).writeValueAsString(subscriberMap),
                StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private Map<String, Subscriber> getSubscribers(File subscribersFile) {
        return fileToObjectConverter(subscribersFile, new TypeReference<>() {
        });
    }
}
