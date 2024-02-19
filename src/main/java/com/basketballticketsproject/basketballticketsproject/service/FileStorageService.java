package com.basketballticketsproject.basketballticketsproject.service;

import com.aspose.pdf.Document;
import com.basketballticketsproject.basketballticketsproject.entity.Partido;
import com.basketballticketsproject.basketballticketsproject.entity.Ticket;
import com.basketballticketsproject.basketballticketsproject.repo.PartidoRepo;
import com.basketballticketsproject.basketballticketsproject.repo.TicketRepo;
import com.basketballticketsproject.basketballticketsproject.utils.Constants;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public String storeFile(final File entradas, Partido partido) throws IOException {
        //splittear el pdf en varios
        final PDDocument document = PDDocument.load(entradas);
        final Splitter splitter = new Splitter();
        final List<PDDocument> pages = splitter.split(document);
        final Iterator<PDDocument> iterator = pages.listIterator();

        final Set<Ticket> ticketSet = new HashSet<>();

        //comprobar si no se ha creado ese partido
        final Partido byFechaPartido = partidoRepo.findByFechaPartido(partido.getFechaPartido());

        if (ObjectUtils.isEmpty(byFechaPartido)) {
            partido.setStockEntradas(NUM_ENTRADAS);
            partidoRepo.save(partido);
            // En ../Entradas están las entradas de la app (ENTRADAS_PATH). Cada partido tiene una subcarpeta con un nombre único y dentro sus entradas
            // Entradas/[fechaPartido]_Granada-[EquipoVisitante]/entrada[i].pdf
            LocalDateTime fecha = partido.getFechaPartido();
            String fechaStr = fecha.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String carpetaDestino = Constants.ENTRADAS_PATH+"/"+fechaStr+"_"+"Granada-"+partido.getEquipoVisitante();
            new File(carpetaDestino).mkdirs();
            
            //recorrer todas las paginas del pdf
            int numStock = 0;
            for (int i = 1; iterator.hasNext(); i++) {
                final Ticket ticket = new Ticket();
                ticket.setPath(carpetaDestino+"/entrada"+i+".pdf");
                ticket.setPartido(partido);
                ticketSet.add(ticket);
                ticketRepo.save(ticket);

                final PDDocument entradaDoc = iterator.next();
                //Se crea el pdf de la entrada en su carpeta
                entradaDoc.save(carpetaDestino + "/entrada"+i+".pdf"); 
                entradaDoc.close();
                //Optimizamos la entrada individual que sigue pesando igual que el documento original con todas las entradas
                Document entradaOptimizada = new Document(carpetaDestino+"/entrada"+i+".pdf");
                entradaOptimizada.optimizeResources();
                //borramos la entrada sin optimizar
                new File(carpetaDestino + "/entrada"+i+".pdf").delete();
                //guardamos la entrada ya optimizada
                entradaOptimizada.save(carpetaDestino +"/entrada"+i+".pdf");
                numStock = i;

            }
            partido.setStockEntradas(numStock);
            partido.setTickets(ticketSet);
            partidoRepo.save(partido);
        }
        document.close();
        return "done";
    }

   
    public File getFileBase(final String base1, final String base2) {
        String base64 = base1 + base2;
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

    // public  byte[] getFileByNumber(String fileName)  {
    //     final Ticket byEntrada = ticketRepo.findByEntrada(fileName);
    //     System.out.println(byEntrada.getPdfBase64());
    //     return decodeBase64ToPdf(byEntrada.getPdfBase64());
    // }

    public static byte[] decodeBase64ToPdf(String base64)  {
        return Base64.getDecoder().decode(base64);
    }

}