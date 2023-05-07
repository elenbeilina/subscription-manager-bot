package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Subscriber;
import com.aqualen.subscriptionchanneltelegrambot.entity.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import static com.aqualen.subscriptionchanneltelegrambot.util.BotUtils.fileToObjectConverter;

@Service
@Getter
public class SubscriptionService {

    private File subscribersFile;
    private Map<String, Subscriber> subscriberMap;

    public SubscriptionService() {
        uploadSubscribersMap();
    }

    public void removeExpiredChannel(Map<String, Subscriber.Channel> channelMap,
                                     Map.Entry<String, Subscriber.Channel> channel,
                                     Map.Entry<String, Subscriber> subscriber) {
        channelMap.remove(channel.getKey(), channel.getValue());

        if (channelMap.isEmpty()) {
            subscriberMap.remove(subscriber.getKey());
        } else {
            setActiveChannels(subscriber.getValue(), channelMap);
        }
    }

    private void setActiveChannels(Subscriber subscriber,
                                   Map<String, Subscriber.Channel> channelMap) {
        subscriber.setChannels(channelMap);
        subscriberMap.put(subscriber.getUserName(), subscriber);
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

        renewSubscribersFile();
    }

    public void uploadSubscribersMap() {
        this.subscribersFile = new File("src/main/resources/botData/subscribers-list.json");
        subscriberMap = getSubscribers(subscribersFile);
    }

    @SneakyThrows
    public void renewSubscribersFile() {
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
