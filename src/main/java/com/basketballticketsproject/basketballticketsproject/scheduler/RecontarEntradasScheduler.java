package com.basketballticketsproject.basketballticketsproject.scheduler;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import com.basketballticketsproject.basketballticketsproject.service.TicketService;
import com.basketballticketsproject.basketballticketsproject.utils.EnviarEmail;
import jakarta.mail.MessagingException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.DATE_FORMATTER;

@Configuration
@EnableScheduling
public class RecontarEntradasScheduler {

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private EnviarEmail enviarEmail;

    //@Scheduled(cron = "0 0 13 ? * 4 ")
    @Scheduled(cron = " 0 23 10 * * ?")
    public void enviarCorreo() throws MessagingException {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String text = date.format(formatters);
        LocalDateTime parsedDate = LocalDateTime.parse(text, formatters);
        Set<Partido> partidos = partidoRepo.findPartidosDesdeFechaActual(parsedDate);

        if (!CollectionUtils.isEmpty(partidos)) {
            enviarEmail.enviarEmailEntrada(partidos);
        }

    }

}
