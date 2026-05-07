package aiss.videominer.controller;

import aiss.videominer.exception.CaptionNotFoundException;
import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;

    @Autowired
    VideoRepository videoRepository;

    @GetMapping("/captions")
    public List<Caption> findAll() {
        return captionRepository.findAll();
    }

    @GetMapping("/captions/{id}")
    public Caption findOne(@PathVariable String id) throws CaptionNotFoundException {
        Optional<Caption> caption = captionRepository.findById(id);
        if (caption.isEmpty()) {
            throw new CaptionNotFoundException();
        }
        return caption.get();
    }

    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> findAllByVideo(@PathVariable String videoId) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(videoId);
        if (video.isEmpty()) {
            throw new VideoNotFoundException();
        }
        return video.get().getCaptions();
    }
}
