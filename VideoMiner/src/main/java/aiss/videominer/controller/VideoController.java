package aiss.videominer.controller;

import aiss.videominer.exception.VideoNotFoundException;
import aiss.videominer.model.Video;
import aiss.videominer.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/videominer")
public class VideoController {

    @Autowired
    VideoRepository videoRepository;

    @GetMapping("/videos")
    public List<Video> findAll() {
        return videoRepository.findAll();
    }

    @GetMapping("/videos/{id}")
    public Video findOne(@PathVariable String id) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(id);
        if (video.isEmpty()) {
            throw new VideoNotFoundException();
        }
        return video.get();
    }
}
