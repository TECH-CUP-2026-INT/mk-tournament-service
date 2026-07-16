package co.edu.escuelaing.techcup.tournament.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RulebookRetrievalPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.RulebookStoragePort;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Component
public class GridFsRulebookStorageAdapter implements RulebookStoragePort, RulebookRetrievalPort {

    private final GridFsTemplate gridFsTemplate;

    public GridFsRulebookStorageAdapter(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public String store(String fileName, String contentType, long sizeBytes, InputStream content) {
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document("contentType", contentType).append("sizeBytes", sizeBytes));
        ObjectId fileId = gridFsTemplate.store(content, fileName, contentType, options.getMetadata());
        return fileId.toHexString();
    }

    @Override
    public RulebookFile retrieve(String fileId) {
        com.mongodb.client.gridfs.model.GridFSFile gridFsFile =
                gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(new ObjectId(fileId))));
        if (gridFsFile == null) {
            throw new IllegalStateException("No se encontró el archivo de reglamento en GridFS con id " + fileId);
        }
        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);
        try {
            return new RulebookFile(
                    resource.getFilename(),
                    resource.getContentType(),
                    resource.getInputStream()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("Error al leer el reglamento desde GridFS", e);
        }
    }
}