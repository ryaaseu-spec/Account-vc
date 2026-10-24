package com.example.bot;

import club.minnced.discord.jdave.interop.JDaveSessionFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.audio.AudioModuleConfig;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Discord bot that joins a voice channel by its ID and stays there.
 * If the bot is kicked / moved / disconnected, it rejoins automatically.
 *
 * Environment variables:
 *   DISCORD_TOKEN     - bot token (required)
 *   VOICE_CHANNEL_ID  - voice channel ID (required)
 *   SELF_DEAFEN       - "true" (default) or "false"
 *   SELF_MUTE         - "false" (default) or "true"
 */
public class Main extends ListenerAdapter {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private final String channelId;
    private final boolean selfDeafen;
    private final boolean selfMute;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "rejoin-scheduler");
        t.setDaemon(true);
        return t;
    });
    private final AtomicBoolean rejoinScheduled = new AtomicBoolean(false);

    public Main(String channelId, boolean selfDeafen, boolean selfMute) {
        this.channelId = channelId;
        this.selfDeafen = selfDeafen;
        this.selfMute = selfMute;
    }

    public static void main(String[] args) throws Exception {
        String token = env("DISCORD_TOKEN", null);
        String channelId = env("VOICE_CHANNEL_ID", null);

        if (token == null || token.isBlank() || channelId == null || channelId.isBlank()) {
            System.err.println("DISCORD_TOKEN و VOICE_CHANNEL_ID پێویستن! (Set DISCORD_TOKEN and VOICE_CHANNEL_ID)");
            System.exit(1);
        }

        boolean selfDeafen = Boolean.parseBoolean(env("SELF_DEAFEN", "true"));
        boolean selfMute = Boolean.parseBoolean(env("SELF_MUTE", "false"));

        Main listener = new Main(channelId.trim(), selfDeafen, selfMute);

        JDA jda = JDABuilder
                .createLight(token.trim(), EnumSet.of(GatewayIntent.GUILD_VOICE_STATES))
                .enableCache(CacheFlag.VOICE_STATE)
                // DAVE end-to-end encryption: required for ALL voice connections
                .setAudioModuleConfig(new AudioModuleConfig()
                        .withDaveSessionFactory(new JDaveSessionFactory()))
                .addEventListeners(listener)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down...");
            jda.shutdown();
        }));
    }

    @Override
    public void onReady(ReadyEvent event) {
        log.info("Logged in as {}", event.getJDA().getSelfUser().getName());
        joinChannel(event.getJDA());
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        // Only care about the bot itself
        if (event.getMember().getIdLong() != event.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        AudioChannel joined = event.getChannelJoined();
        if (joined != null && joined.getId().equals(channelId)) {
            log.info("Connected to voice channel: {}", joined.getName());
            return;
        }

        // Left, was kicked, or got moved to a different channel -> go back
        log.warn("Bot is no longer in the target channel. Rejoining in 5 seconds...");
        scheduleRejoin(event.getJDA());
    }

    private void scheduleRejoin(JDA jda) {
        if (!rejoinScheduled.compareAndSet(false, true)) {
            return;
        }
        scheduler.schedule(() -> {
            rejoinScheduled.set(false);
            joinChannel(jda);
        }, 5, TimeUnit.SECONDS);
    }

    private void joinChannel(JDA jda) {
        AudioChannel channel = jda.getChannelById(AudioChannel.class, channelId);
        if (channel == null) {
            log.error("Voice channel {} not found. Check the ID and that the bot can see the channel.", channelId);
            return;
        }

        try {
            AudioManager audioManager = channel.getGuild().getAudioManager();
            audioManager.setSelfDeafened(selfDeafen);
            audioManager.setSelfMuted(selfMute);
            audioManager.openAudioConnection(channel);
            log.info("Joining voice channel: {} (guild: {})", channel.getName(), channel.getGuild().getName());
        } catch (Exception e) {
            // Usually missing Connect / View Channel permission
            log.error("Could not join the voice channel: {}", e.getMessage());
            scheduleRejoin(jda);
        }
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
