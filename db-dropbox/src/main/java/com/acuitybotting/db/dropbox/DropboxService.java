package com.acuitybotting.db.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.*;


/**
 * Created by Zachary Herridge on 7/31/2018.
 */
@Service
@Configuration
public class DropboxService {

    @Value("${dropbox.access_token}")
    private String dropboxToken;

    private DbxClientV2 client;

    public DbxClientV2 getClient(){
        if (client == null){
            DbxRequestConfig config = DbxRequestConfig.newBuilder("acuity-client").build();
            client = new DbxClientV2(config, dropboxToken);
        }
        return client;
    }

    public FileMetadata upload(String path, File file) {
        try (InputStream in = new FileInputStream(file)) {
            return getClient().files().uploadBuilder(path).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (IOException | DbxException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void download(String path, File file) {
        try {
            DbxDownloader<FileMetadata> downloader = getClient().files().download(path);
            try (FileOutputStream out = new FileOutputStream(file)){
                downloader.download(out);
            }
        } catch (DbxException | IOException e) {
            e.printStackTrace();
        }
    }
}
