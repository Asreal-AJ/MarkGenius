package me.asreal.markgenius.controller;

import lombok.AllArgsConstructor;
import me.asreal.markgenius.dto.LoginUserDto;
import me.asreal.markgenius.dto.RegisterUserDto;
import me.asreal.markgenius.dto.VerifyUserDto;
import me.asreal.markgenius.entity.UserAccount;
import me.asreal.markgenius.responses.LoginResponse;
import me.asreal.markgenius.service.AuthenticationService;
import me.asreal.markgenius.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserAccount> registerUser(@RequestBody RegisterUserDto registerUserDto) {
        var registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        var authenticatedUser = authenticationService.authenticate(loginUserDto);
        var jwtToken = jwtService.generateToken(authenticatedUser);
        var response = new LoginResponse(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reverify")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
