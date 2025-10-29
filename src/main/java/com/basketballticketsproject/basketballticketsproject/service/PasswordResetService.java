package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.dto.PasswordResetConfirmDTO;
import com.basketballticketsproject.basketballticketsproject.entity.PasswordResetToken;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PasswordResetTokenRepository;
import com.basketballticketsproject.basketballticketsproject.utils.Constants;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.ENLACE_RESET_PASSWORD;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final JavaMailSender mailSender;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    private final long tokenExpirationMinutes;

    private final String frontendResetBaseUrl;

    public PasswordResetService(PasswordResetTokenRepository tokenRepository,
                                JavaMailSender mailSender,
                                UsuarioService usuarioService,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.password-reset.token-expiration-minutes:60}") long tokenExpirationMinutes,
                                @Value("${app.frontend.reset-url:"+ENLACE_RESET_PASSWORD+"}") String frontendResetBaseUrl) {
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSender;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.tokenExpirationMinutes = tokenExpirationMinutes;
        this.frontendResetBaseUrl = frontendResetBaseUrl;
    }

    @Transactional
    public boolean createAndSendToken(String email) {
        Optional<Usuario> userOpt = usuarioService.getUsuarioByEmail(email);
        if (userOpt.isEmpty()) {
            return false;
        }
        Usuario usuario = userOpt.get();

        tokenRepository.findFirstByUsuarioAndUsedFalseOrderByCreatedAtDesc(usuario)
                .ifPresent(prev -> {
                    prev.setUsed(true);
                    tokenRepository.save(prev);
                });

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(tokenExpirationMinutes);

        PasswordResetToken prt = PasswordResetToken.builder()
                .token(token)
                .expiresAt(expiresAt)
                .createdAt(LocalDateTime.now())
                .used(false)
                .usuario(usuario)
                .build();

        tokenRepository.save(prt);

        String resetLink = frontendResetBaseUrl + token;
        sendResetEmail(usuario.getEmail(), resetLink);

        return true;
    }

    private void sendResetEmail(String toEmail, String resetLink) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        boolean isSpanish = lang.equalsIgnoreCase("es");

        String subject = isSpanish
                ? "Solicitud de restablecimiento de contraseña - Magenta Basket"
                : "Password Reset Request - Magenta Basket";

        String message = isSpanish
                ? """
            <html>
            <body style="margin:0; padding:0; background-color:#f4f4f4;">
              <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color:#f4f4f4;">
                <tr>
                  <td align="center" style="padding:40px 0;">
                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="600" style="background-color:#ffffff; border:1px solid #eee;">
                      <tr>
                        <td align="center" bgcolor="#e20074" style="padding:20px;">
                          <img src="cid:logoImage" alt="T-Systems" width="160" style="display:block;">
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="padding:30px; font-family:Arial,Helvetica,sans-serif; color:#333333;">
                          <h2 style="font-size:32px; color:#e20074; margin:0 0 30px 0;">Magenta Basket</h2>
                          <h2 style="font-size:22px; margin:0 0 10px 0;">¿Has olvidado tu contraseña?</h2>
                          <p style="font-size:15px; color:#555555; line-height:22px; margin:10px 0 30px 0;">
                            Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.<br>
                            Pulsa el siguiente botón para continuar:
                          </p>
                          <a href="%s" style="background-color:#e20074; color:#ffffff; text-decoration:none; padding:12px 24px; border-radius:30px; display:inline-block; font-weight:bold;">
                            Restablecer contraseña
                          </a>
                          <p style="font-size:13px; color:#888888; margin:50px 0 10px 0;">
                            Si no solicitaste este cambio, puedes ignorar este mensaje.<br>
                            El enlace expirará en 60 minutos por motivos de seguridad.
                          </p>
                        </td>
                      </tr>
                      <tr>
                        <td align="center" bgcolor="#f9f9f9" style="padding:15px; font-size:12px; color:#aaaaaa;">
                          © T-Systems Iberia — Todos los derechos reservados
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """
                : """
            <html>
            <body style="margin:0; padding:0; background-color:#f4f4f4;">
              <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="100%%" style="background-color:#f4f4f4;">
                <tr>
                  <td align="center" style="padding:40px 0;">
                    <table role="presentation" cellspacing="0" cellpadding="0" border="0" width="600" style="background-color:#ffffff; border:1px solid #eee;">
                      <tr>
                        <td align="center" bgcolor="#e20074" style="padding:20px;">
                          <img src="cid:logoImage" alt="T-Systems" width="160" style="display:block;">
                        </td>
                      </tr>
                      <tr>
                        <td align="center" style="padding:30px; font-family:Arial,Helvetica,sans-serif; color:#333333;">
                          <h2 style="font-size:32px; color:#e20074; margin:0 0 30px 0;">Magenta Basket</h2>
                          <h2 style="font-size:22px; margin:0 0 10px 0;">Forgot your password?</h2>
                          <p style="font-size:15px; color:#555555; line-height:22px; margin:10px 0 30px 0;">
                            We received a request to reset your account password.<br>
                            Click the button below to continue:
                          </p>
                          <a href="%s" style="background-color:#e20074; color:#ffffff; text-decoration:none; padding:12px 24px; border-radius:30px; display:inline-block; font-weight:bold;">
                            Reset Password
                          </a>
                          <p style="font-size:13px; color:#888888; margin:50px 0 10px 0;">
                            If you didn’t request this change, you can ignore this message.<br>
                            The link will expire in 60 minutes for security reasons.
                          </p>
                        </td>
                      </tr>
                      <tr>
                        <td align="center" bgcolor="#f9f9f9" style="padding:15px; font-size:12px; color:#aaaaaa;">
                          © T-Systems Iberia — All rights reserved
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """;

        String htmlContent = String.format(message, resetLink);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            ClassPathResource logo = new ClassPathResource("static/img/T-SYSTEMS-LOGO.png");
            helper.addInline("logoImage", logo);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de recuperación", e);
        }
    }

    @Transactional
    public boolean confirmReset(PasswordResetConfirmDTO dto) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(dto.getToken());
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken token = tokenOpt.get();

        if (token.isUsed()) return false;
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        Usuario usuario = token.getUsuario();
        if (usuario == null) return false;

        usuarioService.updatePassword(usuario.getUser_id(), dto.getNewPassword());

        token.setUsed(true);
        tokenRepository.save(token);

        return true;
    }

    @Transactional
    public void removeExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now().minusDays(30));
    }

    @Transactional(readOnly = true)
    public boolean verifyToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed() || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }


}
