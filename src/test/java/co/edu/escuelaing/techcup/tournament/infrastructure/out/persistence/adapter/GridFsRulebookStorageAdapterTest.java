package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RulebookRetrievalPort;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GridFsRulebookStorageAdapterTest {

    @Test
    void store_delegaAlGridFsTemplateYRetornaElHexId() {
        GridFsTemplate gridFsTemplate = mock(GridFsTemplate.class);
        ObjectId fileId = new ObjectId();
        when(gridFsTemplate.store(any(InputStream.class), anyString(), anyString(), any()))
                .thenReturn(fileId);

        GridFsRulebookStorageAdapter adapter = new GridFsRulebookStorageAdapter(gridFsTemplate);

        String result = adapter.store("reglamento.pdf", "application/pdf", 1024L,
                new ByteArrayInputStream("contenido".getBytes()));

        assertEquals(fileId.toHexString(), result);
    }

    @Test
    void retrieve_cuandoElArchivoExiste_retornaElContenido() throws IOException {
        GridFsTemplate gridFsTemplate = mock(GridFsTemplate.class);
        GridFSFile gridFsFile = mock(GridFSFile.class);
        GridFsResource resource = mock(GridFsResource.class);
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFsFile);
        when(gridFsTemplate.getResource(gridFsFile)).thenReturn(resource);
        when(resource.getFilename()).thenReturn("reglamento.pdf");
        when(resource.getContentType()).thenReturn("application/pdf");
        when(resource.getInputStream()).thenReturn(new ByteArrayInputStream("contenido".getBytes()));

        GridFsRulebookStorageAdapter adapter = new GridFsRulebookStorageAdapter(gridFsTemplate);

        RulebookRetrievalPort.RulebookFile result = adapter.retrieve(new ObjectId().toHexString());

        assertEquals("reglamento.pdf", result.fileName());
        assertEquals("application/pdf", result.contentType());
    }

    @Test
    void retrieve_cuandoElArchivoNoExisteEnGridFs_lanzaIllegalStateException() {
        GridFsTemplate gridFsTemplate = mock(GridFsTemplate.class);
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(null);
        String fileId = new ObjectId().toHexString();

        GridFsRulebookStorageAdapter adapter = new GridFsRulebookStorageAdapter(gridFsTemplate);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> adapter.retrieve(fileId));
        assertTrue(exception.getMessage().contains(fileId));
    }

    @Test
    void retrieve_cuandoFallaLaLecturaDelStream_lanzaUncheckedIOException() throws IOException {
        GridFsTemplate gridFsTemplate = mock(GridFsTemplate.class);
        GridFSFile gridFsFile = mock(GridFSFile.class);
        GridFsResource resource = mock(GridFsResource.class);
        when(gridFsTemplate.findOne(any(Query.class))).thenReturn(gridFsFile);
        when(gridFsTemplate.getResource(gridFsFile)).thenReturn(resource);
        when(resource.getInputStream()).thenThrow(new IOException("fallo de lectura"));
        String fileId = new ObjectId().toHexString();

        GridFsRulebookStorageAdapter adapter = new GridFsRulebookStorageAdapter(gridFsTemplate);

        assertThrows(UncheckedIOException.class, () -> adapter.retrieve(fileId));
    }
}
