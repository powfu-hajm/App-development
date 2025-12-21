package org.example.emotionbackend.controller;

import org.example.emotionbackend.common.ApiResponse;
import org.example.emotionbackend.common.PageData;
import org.example.emotionbackend.service.HomeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/page")
    public ApiResponse<PageData<?>> getHomePage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String type // 推文 article / 视频 video / 音乐 music
    ){
        PageData<?> result = homeService.getPagedContent(page, size, type);
        return ApiResponse.success(result);
    }
}
