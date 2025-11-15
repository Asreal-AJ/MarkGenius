package me.asreal.markgenius.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import me.asreal.markgenius.config.GoogleOAuthConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;

@RequestMapping("/google")
@Controller
@RequiredArgsConstructor
public class GoogleAuthenticationController {

    private final GoogleOAuthConfiguration googleAuthConfiguration;

    @GetMapping("/auth/login")
    public ResponseEntity<RedirectView> login() throws IOException, GeneralSecurityException {
        var flow = googleAuthConfiguration.getGoogleAuthorizationCodeFlow();
        //configure url to send user
        var url = new GoogleAuthorizationCodeRequestUrl(
                googleAuthConfiguration.getGoogleRedirectUri(),
                googleAuthConfiguration.getGoogleRedirectUri(),
                flow.getScopes()
        ).setAccessType("offline").build();
        return ResponseEntity.ok(new RedirectView(url));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<RedirectView> callback(
            @RequestParam("code") String code,
            HttpSession httpSession) throws IOException, GeneralSecurityException {
        var flow = googleAuthConfiguration.getGoogleAuthorizationCodeFlow();
        var response = flow.newTokenRequest(code)
                .setRedirectUri(googleAuthConfiguration.getGoogleRedirectUri())
                .execute();
        var credential = flow.createAndStoreCredential(response, "user");
        //Store credentials to retrieve later
        httpSession.setAttribute("userCredentials", credential);
        return ResponseEntity.ok(new RedirectView("/docs"));
    }

    @GetMapping("/fetchDocuments")
    public void docs() {
        //TODO: Implement a call back to the service method
    }

}
