package com.copico.controller;

import com.copico.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/send-test-email")
    public ResponseEntity<?> sendTestEmail(@RequestParam String toEmail) {
        try {
            emailService.sendVerifyCode(toEmail, "123456");
            return ResponseEntity.ok("测试邮件发送成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("发送失败: " + e.getMessage());
        }
    }
}