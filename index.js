const { Client } = require('discord.js-selfbot-v13');
const fs = require('fs');

// خوێندنەوەی زانیارییەکان لە فایلی config.json
const configData = fs.readFileSync('./config.json', 'utf8');
const config = JSON.parse(configData);

const client = new Client({
    checkUpdate: false
});

client.on('ready', async () => {
    console.log(`سەرکەوتووانە چووە ژوورەوە بە ناوی: ${client.user.tag}`);
    
    try {
        const channel = await client.channels.fetch(config.channelId);
        
        if (channel && channel.isVoice()) {
            await channel.join();
            console.log("سەرکەوتووانە چووە ناو ڤۆیسەکەوە و لەوێ دەمێنێتەوە!");
        } else {
            console.log("کەناڵەکە نەدۆزرایەوە یان ڤۆیس نییە، دڵنیابە لە ID کەی!");
        }
    } catch (error) {
        console.error("هەڵەیەک ڕوویدا لە چوونە ناو ڤۆیس:", error);
    }
});

client.login(config.token);
