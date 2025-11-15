package me.asreal.markgenius.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Configuration
public class GoogleOAuthConfiguration {

    @Value("${docs.client.id}")
    private String googleClientId;
    @Value("${docs.client.secret}")
    private String googleClientSecret;
    @Getter
    @Value("${docs.redirect_uri}")
    private String googleRedirectUri;
    @Value("#{'${docs.client.scope}'.split(',')")
    private List<String> googleAccessScopes;

    @Bean
    public GoogleAuthorizationCodeFlow getGoogleAuthorizationCodeFlow() throws GeneralSecurityException, IOException {
        final var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var clientSecrets = new GoogleClientSecrets()
                .setInstalled(new GoogleClientSecrets.Details()
                        .setClientId(googleClientId)
                        .setClientSecret(googleClientSecret)
                        .setRedirectUris(List.of(googleRedirectUri))
                );
        return new GoogleAuthorizationCodeFlow.Builder(
                httpTransport,
                GsonFactory.getDefaultInstance(),
                clientSecrets,
                googleAccessScopes
        ).setAccessType("offline").build();
    }

}
