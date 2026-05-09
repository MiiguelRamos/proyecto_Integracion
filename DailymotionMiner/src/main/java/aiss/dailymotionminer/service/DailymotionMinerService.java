package aiss.dailymotionminer.service;

import aiss.dailymotionminer.etl.Transformer;
import aiss.dailymotionminer.model.dailymotion.Channel;
import aiss.dailymotionminer.model.dailymotion.Subtitle;
import aiss.dailymotionminer.model.dailymotion.SubtitleSearch;
import aiss.dailymotionminer.model.dailymotion.Tags;
import aiss.dailymotionminer.model.dailymotion.Video;
import aiss.dailymotionminer.model.dailymotion.VideoSearch;
import aiss.dailymotionminer.model.videominer.VMCaption;
import aiss.dailymotionminer.model.videominer.VMChannel;
import aiss.dailymotionminer.model.videominer.VMComment;
import aiss.dailymotionminer.model.videominer.VMVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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


     // Obtiene un canal completo de Dailymotion y lo devuelve ya en formato VideoMiner.

    public VMChannel getChannel(String channelId, int maxVideos, int maxPages) {
        // 1. Obtener información del usuario/canal desde Dailymotion
        Channel dmChannel = restTemplate.getForObject(
                dailymotionBaseUrl + "/user/" + channelId
                        + "?fields=id,screenname,description,created_time",
                Channel.class);

        // 2. Obtener los vídeos (paginados con maxPages) y mapearlos
        List<VMVideo> vmVideos = new ArrayList<>();
        int page = 1;

        while (page <= maxPages && vmVideos.size() < maxVideos) {
            int remaining = maxVideos - vmVideos.size();
            int limit = Math.min(remaining, 100);

            VideoSearch videoResponse = restTemplate.getForObject(
                    dailymotionBaseUrl + "/user/" + channelId
                            + "/videos?fields=id,title,description,created_time,owner"
                            + "&limit=" + limit + "&page=" + page,
                    VideoSearch.class);

            if (videoResponse == null || videoResponse.getList() == null) break;

            for (Video dmVideo : videoResponse.getList()) {
                if (vmVideos.size() >= maxVideos) break;

                List<VMComment> vmComments = fetchTagsAsComments(dmVideo.getId());
                List<VMCaption> vmCaptions = fetchSubtitles(dmVideo.getId());
                vmVideos.add(Transformer.toVMVideo(dmVideo, vmComments, vmCaptions));
            }

            if (videoResponse.getHasMore() == null || !videoResponse.getHasMore()) break;
            page++;
        }

        // 3. Construir el VMChannel final con todos los vídeos transformados
        return Transformer.toVMChannel(dmChannel, vmVideos);
    }

    private List<VMComment> fetchTagsAsComments(String videoId) {
        Tags response = restTemplate.getForObject(
                dailymotionBaseUrl + "/video/" + videoId + "?fields=tags",
                Tags.class);
        return Transformer.tagsToVMComments(
                videoId,
                response != null ? response.getTags() : null);
    }

    private List<VMCaption> fetchSubtitles(String videoId) {
        SubtitleSearch response = restTemplate.getForObject(
                dailymotionBaseUrl + "/video/" + videoId
                        + "/subtitles?fields=id,language,url",
                SubtitleSearch.class);

        List<VMCaption> result = new ArrayList<>();
        if (response != null && response.getList() != null) {
            int i = 0;
            for (Subtitle sub : response.getList()) {
                result.add(Transformer.toVMCaption(sub, videoId, i));
                i++;
            }
        }
        return result;
    }


     // Envía un VMChannel ya transformado a VideoMiner mediante POST.

    public void sendToVideoMiner(VMChannel channel) {
        restTemplate.postForObject(
                videoMinerBaseUrl + "/videominer/channels",
                channel,
                VMChannel.class);
    }
}
