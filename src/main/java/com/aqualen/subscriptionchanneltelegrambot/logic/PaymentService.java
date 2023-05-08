package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.Channel;
import com.aqualen.subscriptionchanneltelegrambot.entity.User;
import com.aqualen.subscriptionchanneltelegrambot.enums.PaymentTypes;
import com.aqualen.subscriptionchanneltelegrambot.enums.Periods;
import com.aqualen.subscriptionchanneltelegrambot.props.PaymentProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Service
public class PaymentService {

    public static final String CHOOSE_PAYMENT_MESSAGE = "Choose payment type:";

    private final Map<PaymentTypes, String> paymentTypes;

    private final PaymentProperties paymentProperties;
    private final ChannelService channelService;

    public PaymentService(PaymentProperties paymentProperties,
                          ChannelService channelService) {
        this.paymentProperties = paymentProperties;
        this.channelService = channelService;
        paymentTypes = fillPaymentTypesMap();
    }

    public SendInvoice generateInvoice(Long chatId, User user) {
        String periodName = user.getPeriods().getName();
        String channelName = channelService.getChannels().get(user.getChannelId()).getName();
        return SendInvoice.builder()
                .chatId(chatId)
                .title("Subscription payment.")
                .description(String.format("Channel: %s for the period: %s.", channelName, periodName))
                .currency("RUB")
                .startParameter("test")
                .providerToken(getToken(user))
                .payload(String.format("Channel: %s, period: %s, user: %s.", user.getChannelId(),
                        periodName, user.getUsername()))
                .price(LabeledPrice.builder().label(periodName).amount(getPrice(user)).build()
                ).build();
    }

    private Map<PaymentTypes, String> fillPaymentTypesMap() {
        return Arrays.stream(PaymentTypes.values()).collect(Collectors.toMap(
                paymentType -> paymentType, PaymentTypes::getName)
        );
    }

    @SneakyThrows
    private String getToken(User user) {
        PaymentTypes paymentType = user.getPaymentType();
        return switch (paymentType) {
            case YOOMONEY -> paymentProperties.getYoomoneyToken();
        };
    }

    private int getPrice(User user) {
        Map<String, Channel> channels = channelService.getChannels();
        Channel channel = channels.get(user.getChannelId());
        Map<Periods, Integer> subscriptionCost = channel.getSubscriptionCost();
        return subscriptionCost.get(user.getPeriods());
    }
}
