package com.aqualen.subscriptionchanneltelegrambot.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
public class BotUtils {

    public static List<List<InlineKeyboardButton>> generateButtons(Map<?, String> buttons) {
        List<List<InlineKeyboardButton>> rowsOfButtons = new ArrayList<>();
        for (Map.Entry<?, String> button : buttons.entrySet()) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            rowInline.add(
                    InlineKeyboardButton.builder()
                            .text(button.getValue())
                            .callbackData(button.getKey().toString())
                            .pay(true).build()
            );

            rowsOfButtons.add(rowInline);
        }
        return rowsOfButtons;
    }

    @SneakyThrows
    public static <T> T resourceToObjectConverter(Resource resource, TypeReference<T> object) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            String json = FileCopyUtils.copyToString(reader);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, object);
        }
    }
}
