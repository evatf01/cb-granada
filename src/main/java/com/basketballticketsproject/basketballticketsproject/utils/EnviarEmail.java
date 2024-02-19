package com.basketballticketsproject.basketballticketsproject.utils;

import com.basketballticketsproject.basketballticketsproject.dto.PartidoResponse;
import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.EMAIL_ASUNTO;
import static com.basketballticketsproject.basketballticketsproject.utils.Constants.EMAIL_MENSAJE;



@Component
@Slf4j
public class EnviarEmail {

    @Autowired
    private  JavaMailSender mailSender;

    public  void enviarEmailEntrada(Set<Partido> partidos, List<Usuario> usuarios) throws MessagingException {
        List<PartidoResponse> partidoResponseList = new ArrayList<>();
        SimpleMailMessage email = new SimpleMailMessage();
        String listString = StringUtils.EMPTY;
        for (Partido partido: partidos) {
            String fecha = StringUtils.replace(String.valueOf(partido.getFechaPartido()), "T", " ");
            log.info("PARTIDOS: " + partido.toString());
            partidoResponseList.add(PartidoResponse.builder().equipoVisitante("Granada - "+ partido.getEquipoVisitante())
                    .fechaPartido(fecha).build());
            listString = partidoResponseList.stream().map(Object::toString)
                    .collect(Collectors.joining(""));
        }
        for (Usuario usuario : usuarios) {
            email.setTo(usuario.getEmail());
            email.setSubject(EMAIL_ASUNTO);

            email.setText(EMAIL_MENSAJE + listString);
            mailSender.send(email);
        }

    }

    private static Properties getProperties(String claveemail) {
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
        props.put("mail.smtp.user", "evatallon01@gmail.com");
        props.put("mail.smtp.clave", claveemail);    //La clave de la cuenta
        props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
        props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
        props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        return props;
    }
}


