package aiss.peertubeminer.service;

import aiss.peertubeminer.etl.Transformer;
import aiss.peertubeminer.model.peertube.Caption;
import aiss.peertubeminer.model.peertube.CaptionSearch;
import aiss.peertubeminer.model.peertube.Channel;
import aiss.peertubeminer.model.peertube.Comment;
import aiss.peertubeminer.model.peertube.CommentSearch;
import aiss.peertubeminer.model.peertube.Video;
import aiss.peertubeminer.model.peertube.VideoSearch;
import aiss.peertubeminer.model.videominer.VMCaption;
import aiss.peertubeminer.model.videominer.VMChannel;
import aiss.peertubeminer.model.videominer.VMComment;
import aiss.peertubeminer.model.videominer.VMVideo;
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


     // Obtiene un canal completo de PeerTube y lo devuelve en formato VideoMiner.

    public VMChannel getChannel(String channelId, int maxVideos, int maxComments) {
        // 1. Obtener información del canal desde PeerTube
        Channel ptChannel = restTemplate.getForObject(
                peerTubeBaseUrl + "/api/v1/video-channels/" + channelId,
                Channel.class);

        // 2. Obtener los vídeos del canal y mapearlos
        VideoSearch videoResponse = restTemplate.getForObject(
                peerTubeBaseUrl + "/api/v1/video-channels/" + channelId
                        + "/videos?count=" + maxVideos + "&start=0",
                VideoSearch.class);

        List<VMVideo> vmVideos = new ArrayList<>();
        if (videoResponse != null && videoResponse.getData() != null) {
            for (Video ptVideo : videoResponse.getData()) {
                List<VMComment> vmComments = fetchComments(ptVideo.getUuid(), maxComments);
                List<VMCaption> vmCaptions = fetchCaptions(ptVideo.getUuid());
                vmVideos.add(Transformer.toVMVideo(ptVideo, vmComments, vmCaptions));
            }
        }

        // 3. Construir el VMChannel final con todos los vídeos transformados
        return Transformer.toVMChannel(ptChannel, vmVideos);
    }

    private List<VMComment> fetchComments(String videoId, int maxComments) {
        CommentSearch response = restTemplate.getForObject(
                peerTubeBaseUrl + "/api/v1/videos/" + videoId
                        + "/comment-threads?count=" + maxComments + "&start=0",
                CommentSearch.class);

        List<VMComment> result = new ArrayList<>();
        if (response != null && response.getData() != null) {
            for (Comment ptComment : response.getData()) {
                result.add(Transformer.toVMComment(ptComment));
            }
        }
        return result;
    }

    private List<VMCaption> fetchCaptions(String videoId) {
        CaptionSearch response = restTemplate.getForObject(
                peerTubeBaseUrl + "/api/v1/videos/" + videoId + "/captions",
                CaptionSearch.class);

        List<VMCaption> result = new ArrayList<>();
        if (response != null && response.getData() != null) {
            for (Caption ptCaption : response.getData()) {
                result.add(Transformer.toVMCaption(ptCaption, videoId));
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
