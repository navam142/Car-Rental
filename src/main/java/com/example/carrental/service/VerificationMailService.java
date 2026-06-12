package com.example.carrental.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerificationMailService {

    @Value("${application.mail.from}")
    private String fromAddress;

    @Value("${application.mail.verification-url}")
    private String verificationUrl;

    @Value("${resend.api.key}")
    private String apiKey;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.resend.com")
            .build();


    public void sendVerificationEmail(String to, String token) {

        String verifyLink = verificationUrl + "?token=" + token;

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>Verify your email</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f4f6f9;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">
                
                          <!-- Header -->
                          <tr>
                            <td style="background:linear-gradient(135deg,#1a1a2e 0%%,#16213e 60%%,#0f3460 100%%);padding:40px 48px;text-align:center;">
                              <p style="margin:0;font-size:32px;letter-spacing:-0.5px;">🚗</p>
                              <h1 style="margin:12px 0 0;color:#ffffff;font-size:26px;font-weight:700;letter-spacing:-0.3px;">CarRental</h1>
                              <p style="margin:4px 0 0;color:#94a3b8;font-size:14px;">Your journey starts here</p>
                            </td>
                          </tr>
                
                          <!-- Body -->
                          <tr>
                            <td style="padding:48px 48px 32px;">
                              <h2 style="margin:0 0 16px;color:#1a1a2e;font-size:22px;font-weight:700;">Verify your email address</h2>
                              <p style="margin:0 0 12px;color:#475569;font-size:16px;line-height:1.6;">
                                Thanks for signing up! We just need to confirm that this email address belongs to you before you can start browsing and booking cars.
                              </p>
                              <p style="margin:0 0 32px;color:#475569;font-size:16px;line-height:1.6;">
                                Click the button below to verify your account. This link is valid for <strong>24 hours</strong>.
                              </p>
                
                              <!-- CTA Button -->
                              <table cellpadding="0" cellspacing="0" style="margin:0 auto 32px;">
                                <tr>
                                  <td style="background:linear-gradient(135deg,#0f3460,#1a73e8);border-radius:8px;">
                                    <a href="%s"
                                       style="display:inline-block;padding:16px 40px;color:#ffffff;font-size:16px;font-weight:600;text-decoration:none;letter-spacing:0.3px;border-radius:8px;">
                                      ✅ Verify My Email
                                    </a>
                                  </td>
                                </tr>
                              </table>
                
                              <!-- Fallback link -->
                              <p style="margin:0 0 8px;color:#94a3b8;font-size:13px;">Button not working? Copy and paste this link into your browser:</p>
                              <p style="margin:0;word-break:break-all;">
                                <a href="%s" style="color:#1a73e8;font-size:13px;text-decoration:underline;">%s</a>
                              </p>
                            </td>
                          </tr>
                
                          <!-- Divider -->
                          <tr>
                            <td style="padding:0 48px;">
                              <hr style="border:none;border-top:1px solid #e2e8f0;margin:0;" />
                            </td>
                          </tr>
                
                          <!-- Security notice -->
                          <tr>
                            <td style="padding:24px 48px 40px;">
                              <table cellpadding="0" cellspacing="0" style="background-color:#f8fafc;border-radius:8px;border-left:4px solid #f59e0b;width:100%%;">
                                <tr>
                                  <td style="padding:16px 20px;">
                                    <p style="margin:0;color:#78716c;font-size:13px;line-height:1.6;">
                                      <strong style="color:#92400e;">⚠️ Didn't create an account?</strong><br/>
                                      If you didn't sign up for CarRental, you can safely ignore this email. No account will be activated without verification.
                                    </p>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>
                
                          <!-- Footer -->
                          <tr>
                            <td style="background-color:#f8fafc;padding:24px 48px;text-align:center;border-top:1px solid #e2e8f0;">
                              <p style="margin:0 0 4px;color:#94a3b8;font-size:12px;">© 2025 CarRental. All rights reserved.</p>
                              <p style="margin:0;color:#cbd5e1;font-size:12px;">This is an automated message — please do not reply to this email.</p>
                            </td>
                          </tr>
                
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(verifyLink, verifyLink, verifyLink);

        Map<String, Object> request = Map.of(
                "from", fromAddress,
                "to", List.of(to),
                "subject", "Verify your Car Rental account",
                "html", html
        );

        restClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}