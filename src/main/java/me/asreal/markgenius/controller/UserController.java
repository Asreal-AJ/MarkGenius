package me.asreal.markgenius.controller;

import lombok.RequiredArgsConstructor;
import me.asreal.markgenius.entity.UserAccount;
import me.asreal.markgenius.service.impl.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserAccount> authenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var queriedUser = (UserAccount) authentication.getPrincipal();
        return ResponseEntity.ok(queriedUser);
    }

    @GetMapping("/")
    public ResponseEntity<List<UserAccount>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUserAccounts());
    }

}
