package com.aqualen.subscriptionchanneltelegrambot.logic;

import com.aqualen.subscriptionchanneltelegrambot.entity.PaymentType;
import com.aqualen.subscriptionchanneltelegrambot.entity.User;
import com.aqualen.subscriptionchanneltelegrambot.props.PaymentProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.aqualen.subscriptionchanneltelegrambot.util.BotUtils.resourceToObjectConverter;

@Getter
@Service
public class PaymentService {

    public static final String CHOOSE_PAYMENT_MESSAGE = "Выберите тип оплаты:";
    private final List<PaymentType> paymentTypeList;
    private final Map<String, String> paymentTypes;
    private final PaymentProperties paymentProperties;
    private final ChannelService channelService;

    public PaymentService(@Value("classpath:payment-type.json")
                          Resource paymentTypeFile,
                          PaymentProperties paymentProperties,
                          ChannelService channelService) {
        paymentTypeList = getPaymentTypeList(paymentTypeFile);
        this.paymentProperties = paymentProperties;
        this.channelService = channelService;
        paymentTypes = getPaymentTypes(paymentTypeList);
    }

    public SendInvoice generateInvoice(Long chatId, User user) {
        return SendInvoice.builder()
                .chatId(chatId)
                .title("Оплата подписки.")
                .description(String.format("Канал: %s на период: %s.",
                        channelService.getChannelNames().get(user.getChannelName()),
                        user.getPeriod().getName()))
                .currency("RUB")
                .startParameter("test")
                .providerToken(paymentProperties.getYoomoneyToken())
                .payload(String.format("Канал: %s, период: %s, юзер: %s.",
                        channelService.getChannelNames().get(user.getChannelName()),
                        user.getPeriod().getName(), user.getUsername()))
                .price(LabeledPrice.builder().label(user.getPeriod().getName()).amount(100 * 100).build()
                ).build();
    }

    private List<PaymentType> getPaymentTypeList(Resource paymentTypeFile) {
        return resourceToObjectConverter(paymentTypeFile, new TypeReference<>() {
        });
    }

    private Map<String, String> getPaymentTypes(List<PaymentType> paymentTypeList) {
        return paymentTypeList.stream().collect(Collectors.toMap(paymentType ->
                PaymentType.PAYMENT + paymentType.getType(), PaymentType::getName)
        );
    }
}
