package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Channel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aqualen.subscriptionchanneltelegrambot.util.BotUtils.resourceToObjectConverter;

@Getter
@Service
public class ChannelService {

    public static final String CHOOSE_CHANNEL_MESSAGE = "Выберите канал:";
    private final Map<String, String> channelNames;
    private final Map<String, Channel> channels;

    public ChannelService(@Value("classpath:channel-info.json")
                          Resource channelInfoFile) {
        channels = getChannels(channelInfoFile);
        channelNames = getChannelNames(channels);
    }

    private Map<String, Channel> getChannels(Resource channelInfoFile) {
        List<Channel> channelList = resourceToObjectConverter(channelInfoFile, new TypeReference<>() {
        });
        return channelList.stream().collect(Collectors.toMap(Channel::getId, channel -> channel));
    }

    private Map<String, String> getChannelNames(Map<String, Channel> channels) {
        return channels.values().stream().collect(Collectors.toMap(channel ->
                Channel.CHANNEL_PREFIX + channel.getId(), Channel::getName));
    }
}
