package co.edu.escuelaing.techcup.tournament.repository.adapter;

import co.edu.escuelaing.techcup.tournament.service.ports.CourtImageStoragePort;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class GridFsCourtImageStorageAdapter implements CourtImageStoragePort {

    private final GridFsTemplate gridFsTemplate;

    public GridFsCourtImageStorageAdapter(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public String store(String fileName, String contentType, long sizeBytes, InputStream content) {
        GridFSUploadOptions options = new GridFSUploadOptions()
                .metadata(new Document("contentType", contentType).append("sizeBytes", sizeBytes));
        ObjectId fileId = gridFsTemplate.store(content, fileName, contentType, options.getMetadata());
        return fileId.toHexString();
    }
}
