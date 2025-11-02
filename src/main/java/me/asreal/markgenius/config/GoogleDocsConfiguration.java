package me.asreal.markgenius.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
public class GoogleDocsConfiguration {

    private static final String APPLICATION_NAME = "MarkGenius";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    //Auth tokens for app
    private String tokenDirectoryPath = "tokens";
    private String credentialFilePath = "/credentials.json";

    private Credential getCredential(final NetHttpTransport httpTransport) throws IOException {
        //Load client secrets
        var scopes = List.of(DocsScopes.DOCUMENTS_READONLY);
        var in = GoogleDocsConfiguration.class.getResourceAsStream(credentialFilePath);
        if (in != null) {
            var clientSecrets = GoogleClientSecrets
                    .load(JSON_FACTORY, new InputStreamReader(in));
            //Build flow and trigger user auth request
            var flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, scopes
            )
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(tokenDirectoryPath)))
                    .setAccessType("offline")
                    .build();
            var receiver = new LocalServerReceiver.Builder().setPort(8080).build();
            //Return
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } else {
            throw new FileNotFoundException("Could not find any credentials file.");
        }
    }

    @Bean
    public Docs googleDocsApi() throws IOException, GeneralSecurityException {
        final var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new Docs.Builder(
                httpTransport,
                JSON_FACTORY,
                getCredential(httpTransport)
        ).setApplicationName(APPLICATION_NAME).build();
    }

}
