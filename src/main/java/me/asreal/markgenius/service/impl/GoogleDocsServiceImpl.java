package me.asreal.markgenius.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import me.asreal.markgenius.service.GoogleDocsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleDocsServiceImpl implements GoogleDocsService {

    @Override
    public Document fetchGoogleDocument(Credential credential, String documentId) throws IOException, GeneralSecurityException {
        //Create a google docs service
        var service = new Docs.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential).setApplicationName("MarkGenius").build();
        //Retrieve document by id using the docs service
        return service.documents().get(documentId).execute();
    }

    @Override
    public String fetchGoogleDocumentParsed(Credential credential, String documentId) throws GeneralSecurityException, IOException {
        //Retrieve document
        var document = fetchGoogleDocument(credential, documentId);
        return parseGoogleDocument(document);
    }

    @Override
    public List<Document> fetchGoogleDocuments(Credential credential) throws IOException, GeneralSecurityException {
        var drive = new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                credential).setApplicationName("MarkGenius").build();
        //Set properties and retrieve files from docs
        var result = drive.files().list()
                .setQ("mimeType='application/vnd.google-apps.document'")
                .setFields("files(id, name)")
                .execute();
        return fetchGoogleDocuments(credential, result.getFiles());
    }

    private List<Document> fetchGoogleDocuments(Credential credential, List<File> fetchedGoogleDocuments) throws IOException, GeneralSecurityException {
        var documents = new ArrayList<Document>();
        //Stream list
        for (File fetchedGoogleDocument : fetchedGoogleDocuments) {
            if (fetchedGoogleDocument.getId() != null)
                documents.add(fetchGoogleDocument(credential, fetchedGoogleDocument.getId()));
        }
        return documents;
    }

    private String parseGoogleDocument(Document document) {
        var contentBuilder = new StringBuilder();//Get string builder
        //Modify document to new format
        document.getBody()
                .getContent()
                .forEach(el -> {
                    if (el.getParagraph() != null) {
                        el.getParagraph().getElements().forEach(pe -> {
                            if (pe.getTextRun() != null) {
                                contentBuilder.append(pe.getTextRun().getContent());
                            }
                        });
                    }
                });
        return contentBuilder.toString().trim();
    }
}
