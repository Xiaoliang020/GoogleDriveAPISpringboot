package com.gdrive.sbt.drivelistfiles.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;

/**
 * @author Xiaoliang Chen
 *
 */
@Controller
public class HomepageController {
    private static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);

    private static final String USER_IDENTIFIER_KEY = "MY_USER_TEST_2";

    @Value("${google.oauth.callback.uri}")
    private String CALLBACK_URI;

    @Value("${google.secret.key.path}")
    private Resource gdSecretKeys;

    @Value("${google.credentials.folder.path}")
    private Resource credentialsFolder;


    private GoogleAuthorizationCodeFlow flow;

    @PostConstruct
    public void init() throws Exception {
        GoogleClientSecrets secrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(gdSecretKeys.getInputStream()));
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, secrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(credentialsFolder.getFile())).build();
    }

    @GetMapping(value = { "/" })
    public String showHomePage() throws Exception {
        boolean isUserAuthenticated = false;

        Credential credential = flow.loadCredential(USER_IDENTIFIER_KEY);
        if (credential != null) {
            boolean tokenValid = credential.refreshToken();
            if (tokenValid) {
                isUserAuthenticated = true;
            }
        }

        return isUserAuthenticated ? "dashboard.html" : "index.html";
    }

    @GetMapping(value = {"/googlesignin"})
    public void doGoogleSignIn(HttpServletResponse response) throws Exception {
        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
        String redirectURL = url.setRedirectUri(CALLBACK_URI).setAccessType("offline").build();
        response.sendRedirect(redirectURL);
    }

    @GetMapping(value = { "/oauth" })
    public String saveAuthorizationCode(HttpServletRequest request) throws Exception {
        String code = request.getParameter("code");
        if (code != null) {
            saveToken(code);

            return "dashboard.html";
        }

        return "index.html";
    }

    private void saveToken(String code) throws Exception {
        GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(CALLBACK_URI).execute();
        flow.createAndStoreCredential(response, USER_IDENTIFIER_KEY);
    }

    @GetMapping(value = { "/create" })
    public void createFile(HttpServletResponse response) throws Exception {
        Credential cred = flow.loadCredential(USER_IDENTIFIER_KEY);

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                .setApplicationName("googledrivespringbootexample").build();

        File file = new File();
        file.setName("sample.jpg");

        // Edit this section to upload your file
        FileContent content = new FileContent("image/jpeg", new java.io.File("C:\\Users\\Xiaoliang Chen\\Pictures\\WorldKind Logo.jpg"));
        File uploadedFile = drive.files().create(file, content).setFields("id").execute();

        String fileReference = String.format("{fileID: '%s'}", uploadedFile.getId());
        response.getWriter().write(fileReference);
    }

    @GetMapping(value = { "/createfolder/{folderName}" }, produces = "application/json")
    public @ResponseBody Message createFolder(@PathVariable(name = "folderName") String folder) throws Exception {
        Credential cred = flow.loadCredential(USER_IDENTIFIER_KEY);

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                .setApplicationName("googledrivespringbootexample").build();

        File file = new File();
        file.setName(folder);
        file.setMimeType("application/vnd.google-apps.folder");

        drive.files().create(file).execute();

        Message message = new Message();
        message.setMessage("Folder has been created successfully.");
        return message;
    }

    @GetMapping(value = { "/uploadinfolder" })
    public void uploadFileInFolder(HttpServletResponse response) throws Exception {
        Credential cred = flow.loadCredential(USER_IDENTIFIER_KEY);

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                .setApplicationName("googledrivespringbootexample").build();

        File file = new File();
        file.setName("logo.jpg");
        file.setParents(Arrays.asList("1BuqVesfpiZVCBqfJ4t34dukG4FvP0hoi"));

        FileContent content = new FileContent("image/jpeg", new java.io.File("C:\\Users\\Xiaoliang Chen\\Pictures\\WorldKind Logo.jpg"));
        File uploadedFile = drive.files().create(file, content).setFields("id").execute();

        String fileReference = String.format("{fileID: '%s'}", uploadedFile.getId());
        response.getWriter().write(fileReference);
    }

    @GetMapping(value = { "/downloadfile" })
    public @ResponseBody Message downloadFile() throws Exception {
        Credential cred = flow.loadCredential(USER_IDENTIFIER_KEY);

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred)
                .setApplicationName("googledrivespringbootexample").build();

        String fileId = "1y9kO0dIqEokN7wThEN1T62psy4aDQ8yS";
        OutputStream outputStream = new ByteArrayOutputStream();
        drive.files().get(fileId).executeMediaAndDownloadTo(outputStream);

        Message message = new Message();
        message.setMessage("File has been downloaded successfully.");
        return message;
    }


    class Message {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
