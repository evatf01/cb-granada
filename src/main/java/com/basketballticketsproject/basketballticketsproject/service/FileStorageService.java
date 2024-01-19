package com.basketballticketsproject.basketballticketsproject.service;

import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.repo.UsuarioRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.basketballticketsproject.basketballticketsproject.utils.Constants.*;

@Service
@Slf4j
public class FileStorageService {

    @Autowired
    private PartidoRepo partidoRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @Autowired
    private UsuarioRepo usuarioRepo;

    public Long storeFile(final File convFile, final String tituloPartido, final String fechaPartido) throws IOException {
        log.info("FECHA PARTIDO: " + fechaPartido);
        //splittear el pdf en varios
        final PDDocument document = PDDocument.load(convFile);

        final Splitter splitter = new Splitter();

        final List<PDDocument> pages = splitter.split(document);

        final Iterator<PDDocument> iterator = pages.listIterator();


        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        LocalDateTime fecha = LocalDateTime.parse(fechaPartido, dtf);
        final Partido partido = new Partido();
        partido.setNombrePartido(tituloPartido);
        partido.setFechaPartido(fecha);

        final Set<Ticket> ticketSet = new HashSet<>();

        //comprobar si no se ha creado ese partido
        final Partido byFechaPartido = partidoRepo.findByFechaPartido(fecha);


        if (ObjectUtils.isEmpty(byFechaPartido)) {
            //recorrer todas las paginas del pdf
            for (int i = 1; iterator.hasNext(); i++) {

                final Ticket ticket = new Ticket();
                final PDDocument pd = iterator.next();
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                pd.save(baos);

                //encodear el pdf en base64
                final String base64String = Base64.getEncoder().encodeToString(baos.toByteArray());
                ticket.setPdfBase64(base64String);
                ticket.setEntrada(String.valueOf(i));
                ticket.setPartido(partido);
                ticketSet.add(ticket);
                ticketRepo.save(ticket);

                pd.close();

            }
            partido.setTickets(ticketSet);
            partidoRepo.save(partido);

        }
        document.close();

        return partido.getId();

    }


    public File getFileBase(final String base64) {
    	final File file = new File(NOMBRE_PDF_ENTRADAS);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			final String cadenaLimpia = base64.replace(REPLACE_BASE64, StringUtils.EMPTY);
			byte[] decoder = Base64.getDecoder().decode(cadenaLimpia);
			fos.write(decoder);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			log.info("Error al decodificar pdf", e);
		}
		return file;
    }

    public  byte[] getFileByNumber(String fileName)  {
        final Ticket byEntrada = ticketRepo.findByEntrada(fileName);
        System.out.println(byEntrada.getPdfBase64());
        return decodeBase64ToPdf(byEntrada.getPdfBase64());
    }

    public static byte[] decodeBase64ToPdf(String base64)  {
        return Base64.getDecoder().decode(base64);
    }

}