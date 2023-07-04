package edu.si.ossearch.utils.backup_restore.service;

import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jbirkhimer
 */
public interface BackupRestoreService {
    ByteArrayInputStream backupCollection(Long id, boolean withCrawlSchedule, boolean includeUsers) throws Exception;

    List<Map<String, String>> collectionListBackupsAvailable(String collectionDir);

    String localBackup(String collectionDir, String fileName, boolean delete) throws IOException;

    ResponseEntity<?> restoreCollectionAndCrawlSchedule(List<Map<String, String>> files, boolean restoreCollection, boolean restoreCrawlSchedule, boolean restoreUsers);

    ResponseEntity<?> bulkBackupCollection(List<Long> ids, boolean withCrawlSchedule, boolean includeUsers);

    ResponseEntity<?> bulkListAvailableBackupsCollection();

    Map<String, String> getLocalFileData(String file) throws IOException;
}
