# Discord Voice Bot (JDA)

بۆتێکی دیسکۆرد بە جاڤا کە بە ئایدیی چەناڵ دەچێتە ناو ڤۆیس چەناڵێک و تێیدا دەمێنێتەوە. ئەگەر دەربکرێت یان بگوازرێتەوە، خۆی دەگەڕێتەوە.

## پێویستیەکان

- **Java 25** (JDAVE پێویستی پێیەتی)
- **Maven**
- بۆتێکی دیسکۆرد لە [Developer Portal](https://discord.com/developers/applications)

> لە ١ی مارتی ٢٠٢٦ەوە دیسکۆرد پڕۆتۆکۆڵی DAVE (E2EE) بۆ هەموو ڤۆیسێک پێویست کردووە. بۆیە ئەم پڕۆژەیە JDA 6.x و JDAVE بەکار دەهێنێت. JDA 5.x ناتوانێت ڤۆیس بکاتەوە.

## ڕێکخستنی بۆتەکە

1. لە Developer Portal ← Bot، تۆکن وەربگرە (**Reset Token**).
2. بۆتەکە بە لینکی OAuth2 بهێنە ناو سێرڤەرەکەت (Scopes: `bot`).
3. لە چەناڵەکە دڵنیابە بۆتەکە ڕێگەپێدانی **View Channel** و **Connect**ی هەیە.
4. ئایدیی چەناڵ: Discord ← Settings ← Advanced ← **Developer Mode** چالاک بکە، دواتر کلیلی ڕاست لەسەر چەناڵ ← **Copy Channel ID**.

## ڕاکردن

```bash
mvn package
```

**Linux / macOS:**
```bash
export DISCORD_TOKEN="تۆکنەکەت"
export VOICE_CHANNEL_ID="ئایدی-چەناڵ"
java --enable-native-access=ALL-UNNAMED -jar target/bot.jar
```

**Windows (PowerShell):**
```powershell
$env:DISCORD_TOKEN="تۆکنەکەت"
$env:VOICE_CHANNEL_ID="ئایدی-چەناڵ"
java --enable-native-access=ALL-UNNAMED -jar target\bot.jar
```

### ڕێکخستنە ئارەزوومەندەکان

| گۆڕاو | بنەڕەت | ماناکەی |
|---|---|---|
| `SELF_DEAFEN` | `true` | بۆتەکە خۆی کەڕ دەکات |
| `SELF_MUTE` | `false` | بۆتەکە خۆی بێدەنگ دەکات |

## Docker

```bash
docker build -t discord-voice-bot .
docker run -d --restart unless-stopped \
  -e DISCORD_TOKEN="تۆکنەکەت" \
  -e VOICE_CHANNEL_ID="ئایدی-چەناڵ" \
  discord-voice-bot
```

## گێتهەب

```bash
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/ناوی-تۆ/discord-voice-bot.git
git push -u origin main
```

### ⚠️ ئاگاداری ئاسایش

- **هەرگیز تۆکن پوش مەکە.** فایلی `.env` لە `.gitignore`دایە. تەنها `.env.example` پوش دەکرێت.
- ئەگەر تۆکنەکەت بڵاو بووەوە، یەکسەر **Reset Token** بکە لە Developer Portal.
- ئەم پڕۆژەیە GitHub Actions بۆ **build** بەکار دەهێنێت. بۆ ئەوەی بۆتەکە ٢٤ کاتژمێر کار بکات، پێویستت بە سێرڤەر (VPS)، Docker، یان هۆستینگی بۆتی دیسکۆرد هەیە. GitHub Actions بۆ ئەو مەبەستە گونجاو نییە.

## چارەسەری کێشەکان

| کێشە | چارەسەر |
|---|---|
| `Voice channel ... not found` | ئایدییەکە هەڵەیە، یان بۆتەکە لەو سێرڤەردا نییە / View Channel ی نییە |
| بۆتەکە دێتە ژوورەوە و دەردەچێتەوە | ڕێگەپێدانی **Connect** بپشکنە، و دڵنیابە Java 25 بەکاردەهێنیت |
| ئاگاداری `restricted method` | `--enable-native-access=ALL-UNNAMED` بەکاربهێنە (لە jar ی دروستکراوەکەدا ئەمە خۆی هەیە) |
