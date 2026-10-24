import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;

public class Main {
    public static void main(String[] args) throws Exception {
        String token = System.getenv("DISCORD_TOKEN");
        String channelId = System.getenv("VOICE_CHANNEL_ID");

        if (token == null || channelId == null) {
            System.out.println("DISCORD_TOKEN و VOICE_CHANNEL_ID دابنێ!");
            return;
        }

        JDA jda = JDABuilder
                .createLight(token, EnumSet.of(GatewayIntent.GUILD_VOICE_STATES))
                .enableCache(CacheFlag.VOICE_STATE)
                .build()
                .awaitReady();

        VoiceChannel channel = jda.getVoiceChannelById(channelId);
        if (channel == null) {
            System.out.println("چەناڵەکە نەدۆزرایەوە، ئایدییەکە و ڕێگەپێدانی بۆتەکە بپشکنە.");
            jda.shutdown();
            return;
        }

        channel.getGuild().getAudioManager().openAudioConnection(channel);
        System.out.println("بۆتەکە چووە ناو: " + channel.getName());
    }
}
