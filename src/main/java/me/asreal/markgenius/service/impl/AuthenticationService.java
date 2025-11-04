package me.asreal.markgenius.service.impl;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import me.asreal.markgenius.dto.LoginUserDto;
import me.asreal.markgenius.dto.RegisterUserDto;
import me.asreal.markgenius.dto.VerifyUserDto;
import me.asreal.markgenius.entity.UserAccount;
import me.asreal.markgenius.repository.UserAccountRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@AllArgsConstructor
@Service
public class AuthenticationService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final FileUploadServiceImpl fileUploadService;

    public UserAccount signup(RegisterUserDto registerUserDto) {
        var userAccount = new  UserAccount(
                registerUserDto.getUsername(),
                registerUserDto.getEmail(),
                passwordEncoder.encode(registerUserDto.getPassword())
        );
        userAccount.setVerificationCode(generateVerificationCode());
        userAccount.setVerificationExpiration(LocalDateTime.now().plusMinutes(20));//Expires after 20 minutes
        userAccount.setEnabled(false);
        sendVerificationEmail(userAccount);//Send verification email
        return userAccountRepository.save(userAccount);
    }

    public UserAccount authenticate(LoginUserDto loginUserDto) {
        var userAccount = userAccountRepository.findByEmail(loginUserDto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));//Get account by email
        if (!userAccount.isEnabled()) {
            throw new RuntimeException("Your account is not verified. Please verify your account.");
        }
        //Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );
        return userAccount;
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        var optionalUserAccount = userAccountRepository.findByEmail(verifyUserDto.getEmail());
        //Check if there's a user to verify
        if (optionalUserAccount.isPresent()) {
            var userAccount = optionalUserAccount.get();
            //Check if verification is expired
            if (userAccount.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired.");
            }
            var verificationCode = verifyUserDto.getVerificationCode();
            if (userAccount.getVerificationCode().equals(verificationCode)) {
                userAccount.setEnabled(true);
                userAccount.setVerificationCode(null);
                userAccount.setVerificationExpiration(null);
                //Create projects folder in AWS
                fileUploadService.createFolder(userAccount.getId() + "/projects");
                //Save verified account
                userAccountRepository.save(userAccount);
            } else {
                throw new RuntimeException("Verification code is invalid.");
            }
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void resendVerificationCode(String email) {
        var optionalUserAccount = userAccountRepository.findByEmail(email);
        if (optionalUserAccount.isPresent()) {
            var userAccount = optionalUserAccount.get();
            if (userAccount.isEnabled()) {
                throw new RuntimeException("Account is already verified.");
            }
            userAccount.setVerificationCode(generateVerificationCode());
            userAccount.setVerificationExpiration(LocalDateTime.now().plusMinutes(20));
            sendVerificationEmail(userAccount);//Send verification email
            //Save account
            userAccountRepository.save(userAccount);
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void sendVerificationEmail(UserAccount userAccount) {
        String subject = "Mark Genius Account Verification";
        String verificationCode = userAccount.getVerificationCode();
        String body = "Verify your account using this code: " + verificationCode; //TODO: Update with html body
        //Send email
        try {
            emailService.sendEmail(userAccount.getEmail(), subject, body);
        } catch (MessagingException e) {
            e.printStackTrace();//For debugging purposes
        }
    }

    private String generateVerificationCode() {
        var rand = new Random();
        var verificationCode = rand.nextInt(900000) + 100000;//TODO: Replace with a more better method
        return String.valueOf(verificationCode);
    }

}
