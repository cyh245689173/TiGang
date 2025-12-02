package com.copico.service.email;

import com.copico.common.exception.BizException;
import com.copico.config.EmailConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;
    private final RedisTemplate<String, String> redisTemplate;
    private static final SecureRandom random = new SecureRandom();

    /**
     * 读取 spring.mail.username
     */
    @Value("${spring.mail.username}")
    private String from;

    private static final String EMAIL_COOLDOWN_KEY_PREFIX = "email:cooldown:";
    private static final long SEND_COOLDOWN_SECONDS = 60;

    public EmailService(JavaMailSender mailSender, EmailConfig emailConfig, RedisTemplate<String, String> redisTemplate) {
        this.mailSender = mailSender;
        this.emailConfig = emailConfig;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送邮箱验证码
     *
     * @param email 收件人邮箱
     * @param code  验证码
     */
    public void sendVerifyCode(String email, String code) {
        if (!EmailValidator.isTrustedEmail(email)) {
            throw new BizException("请使用常规邮箱");
        }

        long remainingSeconds = checkAndUpdateEmailCooldown(email);

        if (remainingSeconds > 0) {
            throw new BizException("验证码已发送");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject(new String(emailConfig.getSubject().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        String contentTemplate = new String(emailConfig.getContentTemplate().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        message.setText(String.format(contentTemplate, code));
        mailSender.send(message);
    }

    /**
     * 检查并更新邮箱状态
     *
     * @return 剩余等待时间（秒），0 表示可以立即发送
     */
    private long checkAndUpdateEmailCooldown(String email) {
        String key = EMAIL_COOLDOWN_KEY_PREFIX + email;

        // 尝试设置新锁
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                key,
                "locked",
                SEND_COOLDOWN_SECONDS,
                TimeUnit.SECONDS
        );

        // 如果成功获取锁，表示可以发送
        if (Boolean.TRUE.equals(lockAcquired)) {
            return 0;
        }

        // 获取剩余等待时间
        Long remaining = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return Math.max(remaining, 0);
    }

    /**
     * 生成验证码
     */
    public String generateComplexCode(int length) {
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}
