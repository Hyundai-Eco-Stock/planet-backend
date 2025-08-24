package org.phoenix.planet.service.mail;

import lombok.RequiredArgsConstructor;
import org.phoenix.planet.util.mail.MailUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final MailUtil mailUtil;

    @Override
    public void sendPasswordReset(String to, String name, String resetUrl) {

        String subject = "[Planet] 비밀번호 재설정 안내";
        String body = """
            <div style="font-family: Pretendard, Arial, sans-serif; line-height: 1.6; color: #333;">
                <h2 style="color:#111827;">안녕하세요, %s님 👋</h2>
                <p>아래 버튼을 클릭하시면 <strong>1시간 이내</strong> 비밀번호를 재설정할 수 있습니다.</p>
                <div style="margin:20px 0;">
                    <a href="%s"
                       style="background-color:#2563eb; color:white; padding:12px 24px; border-radius:8px;
                              text-decoration:none; font-weight:bold; display:inline-block;">
                        비밀번호 재설정하기
                    </a>
                </div>
                <p style="font-size:13px; color:#6b7280;">
                    만약 본인이 요청하지 않으셨다면 이 메일을 무시해 주세요.
                </p>
                <hr style="margin:20px 0; border:none; border-top:1px solid #e5e7eb;">
                <p style="font-size:12px; color:#9ca3af;">© Planet 서비스</p>
            </div>
            """.formatted(name, resetUrl);
        mailUtil.sendHtmlMail(to, subject, body);
    }
}