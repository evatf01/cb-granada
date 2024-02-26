package com.basketballticketsproject.basketballticketsproject.scheduler;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Usuario;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import com.basketballticketsproject.basketballticketsproject.utils.SchedulerUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RecontarEntradasScheduler {

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    @Autowired
    private SchedulerUtils schedulerUtils;

    //se ejecuta todos los jueves a las 12 de la mañana
    //@Scheduled(cron = "0 0 12 ? * 4 ")
    //@Scheduled(cron = " 0 46 8 * * ?")
    public void enviarCorreo() throws MessagingException {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        String text = date.format(formatters);
        LocalDateTime parsedDate = LocalDateTime.parse(text, formatters);
        Set<Partido> partidos = partidoRepo.findPartidosDesdeFechaActual(parsedDate);
        List<Usuario> usuarios = usuarioRepo.findAll();

        if (!CollectionUtils.isEmpty(partidos) && !CollectionUtils.isEmpty(usuarios)) {
            schedulerUtils.enviarEmailEntrada(partidos, usuarios);
        }

        Set<Partido> partidosAnteriores = partidoRepo.getPartidosAnteriores(parsedDate);
        if (!CollectionUtils.isEmpty(partidosAnteriores)) {
            schedulerUtils.borrarCarpetasPartidosAnteriores(partidosAnteriores);
        }
    }

}


