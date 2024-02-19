package com.basketballticketsproject.basketballticketsproject.scheduler;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import com.basketballticketsproject.basketballticketsproject.utils.EnviarEmail;
import jakarta.mail.MessagingException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Configuration
@EnableScheduling
public class RecontarEntradasScheduler {

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private EnviarEmail enviarEmail;

    //se ejecuta todos los jueves a las 12 de la mañana
    //@Scheduled(cron = "0 0 12 ? * 4 ")
    //@Scheduled(cron = " 0 49 11 * * ?")
    public void enviarCorreo() throws MessagingException {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String text = date.format(formatters);
        LocalDateTime parsedDate = LocalDateTime.parse(text, formatters);
        Set<Partido> partidos = partidoRepo.findPartidosDesdeFechaActual(parsedDate);
        List<Usuario> usuarios = usuarioRepo.findAll();

        if (!CollectionUtils.isEmpty(partidos) && !CollectionUtils.isEmpty(usuarios)) {
            enviarEmail.enviarEmailEntrada(partidos, usuarios);
        }

    }


}


