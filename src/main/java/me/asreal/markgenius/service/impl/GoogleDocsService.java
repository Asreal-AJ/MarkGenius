package me.asreal.markgenius.service.impl;

import com.google.api.services.docs.v1.Docs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleDocsService {

    private final Docs googleDocsAPi;


}
