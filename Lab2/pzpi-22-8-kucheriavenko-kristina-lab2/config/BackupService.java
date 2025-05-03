package com.BiologicalMaterialsSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class BackupService {

    @Value("${backup.path}")
    private String backupPath;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${pg_dump.path}")
    private String pg_dump_path;

    @Scheduled(cron = "0 0 4 * * *")
    public void performBackup() {
        if (backupPath == null) {
            System.err.println("Path is null");
            return;
        }

        File backupDir = new File(backupPath);
        if (!backupDir.exists()) {
            boolean created = backupDir.mkdirs();
            if (!created) {
                System.err.println("Failed to create backup directory");
                return;
            }
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "backup_" + timestamp + ".sql";
        File backupFile = new File(backupDir, fileName);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    pg_dump_path,
                    "-U", "postgres",
                    "-F", "c",
                    "-f", backupFile.getAbsolutePath(),
                    "system-for-managing-medical-biological-materials"
            );

            pb.environment().put("PGPASSWORD", password);

            Process process = pb.start();
            int exitCode = process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.err.println(line);
            }

            if (exitCode == 0) {
                System.out.println("Backup successfully created");
            } else {
                System.err.println("Error creating backup. Code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
