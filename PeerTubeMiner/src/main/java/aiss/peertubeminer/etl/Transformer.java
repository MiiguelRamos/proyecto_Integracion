package aiss.peertubeminer.etl;

import aiss.peertubeminer.model.peertube.Caption;
import aiss.peertubeminer.model.peertube.Channel;
import aiss.peertubeminer.model.peertube.Comment;
import aiss.peertubeminer.model.peertube.Pictures;
import aiss.peertubeminer.model.peertube.User;
import aiss.peertubeminer.model.peertube.Video;
import aiss.peertubeminer.model.videominer.VMCaption;
import aiss.peertubeminer.model.videominer.VMChannel;
import aiss.peertubeminer.model.videominer.VMComment;
import aiss.peertubeminer.model.videominer.VMUser;
import aiss.peertubeminer.model.videominer.VMVideo;

import java.util.ArrayList;
import java.util.List;

public class Transformer {

    private Transformer() {
    }

    public static VMChannel toVMChannel(Channel ptChannel, List<VMVideo> vmVideos) {
        if (ptChannel == null) return null;
        VMChannel vm = new VMChannel();
        vm.setId(ptChannel.getName());
        vm.setName(ptChannel.getDisplayName());
        vm.setDescription(blankToNull(ptChannel.getDescription()));
        vm.setCreatedTime(ptChannel.getCreatedAt());
        vm.setVideos(vmVideos != null ? vmVideos : new ArrayList<>());
        return vm;
    }

    public static VMVideo toVMVideo(Video ptVideo,
                                    List<VMComment> vmComments,
                                    List<VMCaption> vmCaptions) {
        if (ptVideo == null) return null;
        VMVideo vm = new VMVideo();
        vm.setId(ptVideo.getUuid());
        vm.setName(ptVideo.getName());
        vm.setDescription(blankToNull(ptVideo.getDescription()));
        vm.setReleaseTime(ptVideo.getPublishedAt());
        vm.setUser(toVMUser(ptVideo.getAccount()));
        vm.setComments(vmComments != null ? vmComments : new ArrayList<>());
        vm.setCaptions(vmCaptions != null ? vmCaptions : new ArrayList<>());
        return vm;
    }

    public static VMUser toVMUser(User ptUser) {
        if (ptUser == null) return null;
        VMUser vm = new VMUser();
        vm.setName(ptUser.getDisplayName());
        vm.setUserLink(ptUser.getUrl());
        if (ptUser.getAvatars() != null && !ptUser.getAvatars().isEmpty()) {
            Pictures avatar = ptUser.getAvatars().get(0);
            vm.setPictureLink(avatar != null ? avatar.getUrl() : null);
        }
        return vm;
    }

    public static VMComment toVMComment(Comment ptComment) {
        if (ptComment == null) return null;
        VMComment vm = new VMComment();
        vm.setId(ptComment.getId() != null ? ptComment.getId().toString() : null);
        vm.setText(ptComment.getText());
        vm.setCreatedOn(ptComment.getCreatedAt());
        return vm;
    }

    public static VMCaption toVMCaption(Caption ptCaption, String videoId) {
        if (ptCaption == null) return null;
        VMCaption vm = new VMCaption();
        String langId = ptCaption.getLanguage() != null
                ? ptCaption.getLanguage().getId()
                : "unknown";
        vm.setId(videoId + "_" + langId);
        vm.setName(ptCaption.getCaptionPath());
        vm.setLanguage(langId);
        return vm;
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        return value.isBlank() ? null : value;
    }
}
