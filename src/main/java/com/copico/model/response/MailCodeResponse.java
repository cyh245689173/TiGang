package com.copico.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MailCodeResponse {
    private String captchaId;
}
