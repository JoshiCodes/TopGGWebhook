# TopGGWebhook

Simple Webhook Listener API for the Top.GG [Webhooks](https://docs.top.gg/resources/webhooks/).

# Installation
<a alt="Version:"><img src="https://img.shields.io/github/v/release/JoshiCodes/TopGGWebhook"></a><br>
Put the following Dependency in your `pom.xml`:<br>
Replace the `VERSION` to the Current Version.
```xml

<dependency>
  <groupId>de.joshizockt</groupId>
  <artifactId>topggwebhook</artifactId>
  <version>VERSION</version>
</dependency>

```

# Usage
To create a Webhook Listener, create a new Class extending `WebhookListener`.
In the Constructor, type the Port of the HTTP Server. And if you want, also add a custom Path to receive the POST Request. Default for this is `/`.
Then, in your Bot Main method, create a new Instance of your Listener and run the Method `.start()`.
In your Listener Constructor, you can set the Token of your Bot `setSecret(String secret);`.
If you want advanced Security, enable the IP Whitelist using `setWhitelist(true);` and add all IPs of [this Page](https://docs.top.gg/resources/webhooks/#security) using `addWhitelisted(String address);`
To handle a Bot Webhook, override the Method `handleBotHook`. This Method will be called if someone Votes for your Bot.
To handle a Server Webhook, override the Method `handleServerHook`. This Method will be called if someone Votes for your Server.

For the last step, add your Address with your Path to your Bot or Server Webhook Page. You can edit your Webhook for your Bot at `https://top.gg/bot/<bot id>/webhooks`.
Replace `yourAddressHere` with your IP Address from your Server. The `yourPathHere` is the Path, you set at the Listener Constructor.
You can leave the `YourSecretTokenHere` empty or set the Same Token in your Listener using `setSecret()`.
![image](https://user-images.githubusercontent.com/55353244/132097412-8c92421a-5587-4ade-96dc-2e78b1579101.png)
