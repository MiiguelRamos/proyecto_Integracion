package aiss.peertubeminer.service;

import aiss.peertubeminer.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class PeerTubeMinerService {

    @Value("${peertube.baseUrl}")
    private String peerTubeBaseUrl;

    @Value("${videominer.baseUrl}")
    private String videoMinerBaseUrl;

    @Autowired
    RestTemplate restTemplate;

    public Channel getChannel(String channelId, int maxVideos, int maxComments) {
        // 1. Get channel info from PeerTube
        String channelUrl = peerTubeBaseUrl + "/api/v1/video-channels/" + channelId;
        JsonNode ptChannel = restTemplate.getForObject(channelUrl, JsonNode.class);

        // 2. Get videos of the channel
        String videosUrl = peerTubeBaseUrl + "/api/v1/video-channels/" + channelId
                + "/videos?count=" + maxVideos + "&start=0";
        JsonNode videoResponse = restTemplate.getForObject(videosUrl, JsonNode.class);

        List<Video> videos = new ArrayList<>();
        if (videoResponse != null && videoResponse.has("data")) {
            for (JsonNode ptVideo : videoResponse.get("data")) {
                String videoId = ptVideo.get("uuid").asText();

                List<Comment> comments = fetchComments(videoId, maxComments);
                List<Caption> captions = fetchCaptions(videoId);
                User user = mapUser(ptVideo.has("account") ? ptVideo.get("account") : null);

                Video video = new Video();
                video.setId(videoId);
                video.setName(ptVideo.get("name").asText());
                video.setDescription(textOrNull(ptVideo, "description"));
                video.setReleaseTime(ptVideo.get("publishedAt").asText());
                video.setUser(user);
                video.setComments(comments);
                video.setCaptions(captions);
                videos.add(video);
            }
        }

        // 3. Build channel
        Channel channel = new Channel();
        channel.setId(ptChannel.get("name").asText());
        channel.setName(ptChannel.get("displayName").asText());
        channel.setDescription(textOrNull(ptChannel, "description"));
        channel.setCreatedTime(ptChannel.get("createdAt").asText());
        channel.setVideos(videos);

        return channel;
    }

    private List<Comment> fetchComments(String videoId, int maxComments) {
        String url = peerTubeBaseUrl + "/api/v1/videos/" + videoId
                + "/comment-threads?count=" + maxComments + "&start=0";
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        List<Comment> comments = new ArrayList<>();
        if (response != null && response.has("data")) {
            for (JsonNode ptComment : response.get("data")) {
                Comment comment = new Comment();
                comment.setId(ptComment.get("id").asText());
                comment.setText(textOrNull(ptComment, "text"));
                comment.setCreatedOn(ptComment.get("createdAt").asText());
                comments.add(comment);
            }
        }
        return comments;
    }

    private List<Caption> fetchCaptions(String videoId) {
        String url = peerTubeBaseUrl + "/api/v1/videos/" + videoId + "/captions";
        JsonNode response = restTemplate.getForObject(url, JsonNode.class);

        List<Caption> captions = new ArrayList<>();
        if (response != null && response.has("data")) {
            for (JsonNode ptCaption : response.get("data")) {
                JsonNode lang = ptCaption.get("language");
                String langId = (lang != null && !lang.isNull() && lang.has("id")) ? lang.get("id").asText() : "unknown";
                Caption caption = new Caption();
                caption.setId(videoId + "_" + langId);
                caption.setName(ptCaption.get("captionPath").asText());
                caption.setLanguage(langId);
                captions.add(caption);
            }
        }
        return captions;
    }

    private User mapUser(JsonNode account) {
        if (account == null || account.isNull()) return null;
        User user = new User();
        user.setName(account.has("displayName") ? account.get("displayName").asText() : null);
        user.setUserLink(account.has("url") ? account.get("url").asText() : null);
        if (account.has("avatars") && account.get("avatars").isArray()
                && account.get("avatars").size() > 0) {
            JsonNode avatar = account.get("avatars").get(0);
            user.setPictureLink(avatar.has("url") ? avatar.get("url").asText() : null);
        }
        return user;
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
