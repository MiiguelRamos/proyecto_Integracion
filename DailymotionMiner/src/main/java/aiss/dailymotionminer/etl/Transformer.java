package aiss.dailymotionminer.etl;

import aiss.dailymotionminer.model.dailymotion.Channel;
import aiss.dailymotionminer.model.dailymotion.Subtitle;
import aiss.dailymotionminer.model.dailymotion.Video;
import aiss.dailymotionminer.model.videominer.VMCaption;
import aiss.dailymotionminer.model.videominer.VMChannel;
import aiss.dailymotionminer.model.videominer.VMComment;
import aiss.dailymotionminer.model.videominer.VMUser;
import aiss.dailymotionminer.model.videominer.VMVideo;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Transformer {

    private Transformer() {
    }

    public static VMChannel toVMChannel(Channel dmChannel, List<VMVideo> vmVideos) {
        if (dmChannel == null) return null;
        VMChannel vm = new VMChannel();
        vm.setId(dmChannel.getId());
        vm.setName(dmChannel.getScreenname());
        vm.setDescription(blankToNull(dmChannel.getDescription()));
        vm.setCreatedTime(toIsoDate(dmChannel.getCreatedTime()));
        vm.setVideos(vmVideos != null ? vmVideos : new ArrayList<>());
        return vm;
    }

    public static VMVideo toVMVideo(Video dmVideo,
                                    List<VMComment> vmComments,
                                    List<VMCaption> vmCaptions) {
        if (dmVideo == null) return null;
        VMVideo vm = new VMVideo();
        vm.setId(dmVideo.getId());
        vm.setName(dmVideo.getTitle());
        vm.setDescription(blankToNull(dmVideo.getDescription()));
        vm.setReleaseTime(toIsoDate(dmVideo.getCreatedTime()));
        vm.setUser(buildOwnerUser(dmVideo.getOwner()));
        vm.setComments(vmComments != null ? vmComments : new ArrayList<>());
        vm.setCaptions(vmCaptions != null ? vmCaptions : new ArrayList<>());
        return vm;
    }


    //  Dailymotion sólo expone el id del owner (string) en cada vídeo, no un objeto User.
    //  Sintetizamos un VMUser razonable a partir de ese id.

    public static VMUser buildOwnerUser(String ownerId) {
        if (ownerId == null || ownerId.isBlank()) return null;
        VMUser vm = new VMUser();
        vm.setName(ownerId);
        vm.setUserLink("https://www.dailymotion.com/" + ownerId);
        return vm;
    }

    /*
      En Dailymotion no existen comentarios. Según , usamos las tags del vídeo como comentarios,
       como dice el enunciado del proyecto. Cada tag se convierte en un VMComment.
     */
    public static List<VMComment> tagsToVMComments(String videoId, List<String> tags) {
        List<VMComment> result = new ArrayList<>();
        if (tags == null) return result;
        int i = 0;
        for (String tag : tags) {
            VMComment vm = new VMComment();
            vm.setId(videoId + "_tag_" + i);
            vm.setText(tag);
            vm.setCreatedOn(null);
            result.add(vm);
            i++;
        }
        return result;
    }


     // Las captions en Dailymotion se llaman "subtitles".

    public static VMCaption toVMCaption(Subtitle dmSubtitle, String videoId, int fallbackIndex) {
        if (dmSubtitle == null) return null;
        VMCaption vm = new VMCaption();
        vm.setId(dmSubtitle.getId() != null ? dmSubtitle.getId() : videoId + "_sub_" + fallbackIndex);
        vm.setName(dmSubtitle.getUrl());
        vm.setLanguage(dmSubtitle.getLanguage());
        return vm;
    }

    private static String toIsoDate(Long unixTimestamp) {
        if (unixTimestamp == null) return null;
        return Instant.ofEpochSecond(unixTimestamp)
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        return value.isBlank() ? null : value;
    }
}
