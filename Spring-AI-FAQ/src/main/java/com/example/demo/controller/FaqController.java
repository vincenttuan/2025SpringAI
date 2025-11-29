package com.example.demo.controller;

import com.example.demo.service.FaqService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/faq")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String q,@RequestParam(required = false) String conversationId) {
        return faqService.ask(conversationId, q);
    }
}
