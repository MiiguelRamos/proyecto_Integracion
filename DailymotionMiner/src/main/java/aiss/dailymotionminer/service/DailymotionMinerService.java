package aiss.dailymotionminer.service;

import aiss.dailymotionminer.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DailymotionMinerService {

    @Value("${dailymotion.baseUrl}")
    private String dailymotionBaseUrl;

    @Value("${videominer.baseUrl}")
    private String videoMinerBaseUrl;

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannel(String channelId, int maxVideos, int maxPages) {
        // 1. Obtener información del usuario/canal desde Dailymotion
        String userUrl = dailymotionBaseUrl + "/user/" + channelId
                + "?fields=id,screenname,description,created_time";
        JsonNode dmUser = restTemplate.getForObject(userUrl, JsonNode.class);

        // 2. Obtener los vídeos (paginados con maxPages)
        List<Video> videos = new ArrayList<>();
        int page = 1;

        while (page <= maxPages && videos.size() < maxVideos) {
            int remaining = maxVideos - videos.size();
            int limit = Math.min(remaining, 100);

            String videosUrl = dailymotionBaseUrl + "/user/" + channelId
                    + "/videos?fields=id,title,description,created_time,owner"
                    + "&limit=" + limit + "&page=" + page;
            JsonNode videoResponse = restTemplate.getForObject(videosUrl, JsonNode.class);

            if (videoResponse == null || !videoResponse.has("list")) break;

            for (JsonNode dmVideo : videoResponse.get("list")) {
                if (videos.size() >= maxVideos) break;

                String videoId = dmVideo.get("id").asText();
                long createdTime = dmVideo.get("created_time").asLong();

                List<Comment> comments = fetchTags(videoId);
                List<Caption> captions = fetchSubtitles(videoId);
                User user = mapOwner(channelId);

                Video video = new Video();
                video.setId(videoId);
                video.setName(dmVideo.get("title").asText());
                video.setDescription(textOrNull(dmVideo, "description"));
                video.setReleaseTime(toISO(createdTime));
                video.setUser(user);
                video.setComments(comments);
                video.setCaptions(captions);
                videos.add(video);
            }

            boolean hasMore = videoResponse.has("has_more") && videoResponse.get("has_more").asBoolean();
            if (!hasMore) break;
            page++;
        }

        // 3. Construir el canal
        Channel channel = new Channel();
        channel.setId(dmUser.get("id").asText());
        channel.setName(dmUser.get("screenname").asText());
        channel.setDescription(textOrNull(dmUser, "description"));
        channel.setCreatedTime(toISO(dmUser.get("created_time").asLong()));
        channel.setVideos(videos);

        return channel;
    }

    // Dailymotion no tiene comentarios — se usan los tags del vídeo en su lugar
    private List<Comment> fetchTags(String videoId) {
        String url = dailymotionBaseUrl + "/video/" + videoId + "?fields=tags";
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        List<Comment> comments = new ArrayList<>();
        if (response != null && response.has("tags")) {
            int i = 0;
            for (JsonNode tag : response.get("tags")) {
                Comment comment = new Comment();
                comment.setId(videoId + "_tag_" + i);
                comment.setText(tag.asText());
                comment.setCreatedOn(null);
                comments.add(comment);
                i++;
            }
        }
        return comments;
    }

    // Las captions en Dailymotion se llaman "subtitles"
    private List<Caption> fetchSubtitles(String videoId) {
        String url = dailymotionBaseUrl + "/video/" + videoId
                + "/subtitles?fields=id,language,url";
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        List<Caption> captions = new ArrayList<>();
        if (response != null && response.has("list")) {
            int i = 0;
            for (JsonNode sub : response.get("list")) {
                Caption caption = new Caption();
                caption.setId(sub.has("id") ? sub.get("id").asText() : videoId + "_sub_" + i);
                caption.setName(sub.has("url") ? sub.get("url").asText() : null);
                caption.setLanguage(sub.has("language") ? sub.get("language").asText() : null);
                captions.add(caption);
                i++;
            }
        }
        return captions;
    }

    private User mapOwner(String ownerId) {
        User user = new User();
        user.setName(ownerId);
        user.setUserLink("https://www.dailymotion.com/" + ownerId);
        return user;
    }

    private String toISO(long unixTimestamp) {
        return Instant.ofEpochSecond(unixTimestamp)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private String textOrNull(JsonNode node, String field) {
        if (node != null && node.has(field) && !node.get(field).isNull()) {
            String val = node.get(field).asText();
            return val.isBlank() ? null : val;
        }
        return null;
    }

    public void sendToVideoMiner(Channel channel) {
        restTemplate.postForObject(
                videoMinerBaseUrl + "/videominer/channels", channel, Channel.class);
    }
}
