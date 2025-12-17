package online.afeibaili.mchat.config


/**
 * 配置类
 *
 *@author AfeiBaili
 *@version 2025/11/3 14:36
 */

class Config() {
    lateinit var token: String
    lateinit var address: String
    var port: Int = 33393

    constructor(token: String, address: String, port: Int) : this() {
        this.token = token
        this.address = address
        this.port = port
    }
}