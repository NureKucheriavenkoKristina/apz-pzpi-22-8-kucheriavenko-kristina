package com.BiologicalMaterialsSystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "backup.db")
public class BackupProperties {
    private String name;
    private String user;
    private String password;
    private String dir;

    // Геттери та сеттери
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDir() { return dir; }
    public void setDir(String dir) { this.dir = dir; }
}
