package com.aqualen.subscriptionchanneltelegrambot.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.util.FileCopyUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.File;
import java.io.FileInputStream;
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
    public static <T> T fileToObjectConverter(File file, TypeReference<T> object) {
        try (Reader reader = new InputStreamReader(new FileInputStream(file), UTF_8)) {
            String json = FileCopyUtils.copyToString(reader);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.readValue(json, object);
        }
    }
}
