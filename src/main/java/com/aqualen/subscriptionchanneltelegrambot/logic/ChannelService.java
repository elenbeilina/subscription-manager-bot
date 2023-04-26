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

    private final Map<String, String> channelNames;
    private final List<Channel> channelList;
    public static final String CHOOSE_CHANNEL_MESSAGE = "Выберите канал:";

    public ChannelService(@Value("classpath:channel-info.json")
                          Resource channelInfoFile) {
        channelList = getChannels(channelInfoFile);
        channelNames = getChannelNames(channelList);
    }

    private List<Channel> getChannels(Resource channelInfoFile) {
        return resourceToObjectConverter(channelInfoFile, new TypeReference<>() {
        });
    }

    private Map<String, String> getChannelNames(List<Channel> channelList) {
        return channelList.stream().collect(Collectors.toMap(channel ->
                Channel.CHANNEL + channel.getName(), Channel::getDisplayName));
    }
}
