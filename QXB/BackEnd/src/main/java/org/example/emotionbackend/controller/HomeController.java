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
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type   // ⭐可选
    ){

        // 1. 分页参数兜底
        if (page < 1) page = 1;
        if (size <= 0 || size > 50) size = 10;

        // 2. 内容类型 Standard
        if (type == null || type.isEmpty()) {
            type = "article"; // ⭐默认推文
        }

        // 3. 传入 service 处理
        PageData<?> result = homeService.getPagedContent(page, size, type);

        return ApiResponse.success(result);
    }
}
