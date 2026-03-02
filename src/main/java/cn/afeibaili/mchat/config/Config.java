package cn.afeibaili.mchat.config;

/**
 * 配置类
 *
 * @author AfeiBaili
 * @version 2026/3/3 00:09
 */

public class Config {
    String token;
    String address;
    Integer port;
    String name;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Config(String token, String address, Integer port, String name) {
        this.token = token;
        this.address = address;
        this.port = port;
        this.name = name;
    }
}