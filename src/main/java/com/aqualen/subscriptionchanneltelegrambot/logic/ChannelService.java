package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Channel;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.CreateChatInviteLink;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aqualen.subscriptionchanneltelegrambot.util.BotUtils.fileToObjectConverter;

@Getter
@Service
public class ChannelService {

    public static final String CHOOSE_CHANNEL_MESSAGE = "Choose channel:";
    private final Map<String, String> channelNames;
    private final Map<String, Channel> channels;

    public ChannelService(@Value("classpath:botData/channel-info.json")
                          File channelInfoFile) {
        channels = getChannels(channelInfoFile);
        channelNames = getChannelNames(channels);
    }

    public CreateChatInviteLink getChatInviteLink(String channelId) {
        return CreateChatInviteLink.builder().chatId(channelId).build();
    }

    private Map<String, Channel> getChannels(File channelInfoFile) {
        List<Channel> channelList = fileToObjectConverter(channelInfoFile, new TypeReference<>() {
        });
        return channelList.stream().collect(Collectors.toMap(Channel::getId, channel -> channel));
    }

    private Map<String, String> getChannelNames(Map<String, Channel> channels) {
        return channels.values().stream().collect(Collectors.toMap(channel ->
                Channel.CHANNEL_PREFIX + channel.getId(), Channel::getName));
    }
}
