package com.basketballticketsproject.basketballticketsproject.scheduler;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import com.basketballticketsproject.basketballticketsproject.service.SorteoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Configuration
@EnableScheduling
public class RecontarEntradasScheduler {

    @Autowired
    private PartidoRepo partidoRepo;


    @Autowired
    private SorteoService sorteoService;

    @Autowired
    private UsuarioRepo usuarioRepo;

    //@Scheduled(cron = "0 0 12 ? * 4 ")
    public void enviarCorreo() {
        List<Partido> fechasSortAsc = partidoRepo.getFechasSortAsc();
        /*
        if (!CollectionUtils.isEmpty(fechasSortAsc)) {
            Partido partido = fechasSortAsc.get(0);
            System.out.println(partido.getFechaPartido());
            Set<Usuario> usuariosSorteo = sorteoService.getUsuariosSorteo(partido.getFechaPartido());
            usuariosSorteo.forEach(usuario -> {

            });
        }

         */
    }

}
