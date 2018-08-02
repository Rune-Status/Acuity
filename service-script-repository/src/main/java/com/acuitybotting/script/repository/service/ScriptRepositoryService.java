package com.acuitybotting.script.repository.service;

import com.acuitybotting.db.arango.acuity.script.repository.repositories.ScriptAuthRepository;
import com.acuitybotting.db.arango.acuity.script.repository.repositories.ScriptRepository;
import com.acuitybotting.script.repository.compile.CompileService;
import com.acuitybotting.script.repository.github.GitHubService;
import com.acuitybotting.script.repository.obfuscator.ObfuscatorService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

@Getter
@Service
public class ScriptRepositoryService {

    private final ScriptRepository scriptRepository;
    private final ScriptAuthRepository scriptAuthRepository;

    private final CompileService compileService;
    private final GitHubService gitHubService;
    private final ObfuscatorService obfuscatorService;

    @Autowired
    public ScriptRepositoryService(ScriptRepository scriptRepository, ScriptAuthRepository scriptAuthRepository, CompileService compileService, GitHubService gitHubService, ObfuscatorService obfuscatorService) {
        this.scriptRepository = scriptRepository;
        this.scriptAuthRepository = scriptAuthRepository;
        this.compileService = compileService;
        this.gitHubService = gitHubService;
        this.obfuscatorService = obfuscatorService;
    }

    public File downloadAndCompile(String repositoryName) throws Exception{
        Objects.requireNonNull(repositoryName);

        File parentFile = new File(UUID.randomUUID().toString().replaceAll("\\.", "-"));
        if (!parentFile.mkdirs()) throw new RuntimeException("Failed to create temp file.");

        File downloadLocation = new File(parentFile, repositoryName + ".zip");
        gitHubService.downloadRepoAsZip(repositoryName, downloadLocation);

        File unzipLocation = new File(parentFile, repositoryName);
        compileService.unzip(downloadLocation, unzipLocation);

        File outLocation = new File(parentFile, "out");
        compileService.compile(
                new File("C:\\Users\\zgher\\Documents\\RSPeer\\cache\\rspeer.jar"),
                unzipLocation,
                outLocation
        );

        File jarLocation = new File(parentFile,repositoryName + ".jar");
        compileService.jar(outLocation, jarLocation);

        File obfuscatedJarLocation = new File(parentFile,repositoryName + "-Obbed.jar");
        obfuscatorService.obfuscate(
                new File("C:\\Users\\S3108772\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\allatori.jar"),
                new File("C:\\Users\\S3108772\\IdeaProjects\\Acuity\\service-script-repository\\src\\main\\resources\\config-placeholder.xml"),
                new File(parentFile,"active.xml"),
                jarLocation,
                obfuscatedJarLocation
        );

        return parentFile;
    }

}
