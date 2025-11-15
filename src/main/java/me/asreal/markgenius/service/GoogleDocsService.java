package me.asreal.markgenius.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.docs.v1.model.Document;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public interface GoogleDocsService {

    Document fetchGoogleDocument(Credential credential, String documentId) throws IOException, GeneralSecurityException;

    List<Document> fetchGoogleDocuments(Credential credential) throws IOException, GeneralSecurityException;

    String fetchGoogleDocumentParsed(Credential credential, String documentId) throws GeneralSecurityException, IOException;


}
