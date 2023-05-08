# Telegram bot for managing subscription to the private channels.

<a name="build-project"></a>
### Build project

```
mvn clean package
```
------------------------

### Configuration

#### In `application.propeerties` there are several configurable parameters:

- **bot.username** - username of the telegram bot.
- **bot.token** - token of the bot, that was provided by Botfather.
- **payment.yoomoney-token** - token for paying via yoomoney, that was provided by Botfather.

------------------------

### Useful links:
- [Telegram API](https://core.telegram.org/api);
- [Telegram Bot Java Library](https://github.com/rubenlagus/TelegramBots);
- [Telegram Payment API](https://core.telegram.org/bots/payments);
- [Telegram Payment API via Yookassa](https://yookassa.ru/docs/support/payments/onboarding/integration/cms-module/telegram?lang=en).