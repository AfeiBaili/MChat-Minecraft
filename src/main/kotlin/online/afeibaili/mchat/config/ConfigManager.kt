package online.afeibaili.mchat.config

import net.neoforged.neoforge.common.ModConfigSpec
import java.util.*


/**
 * 配置管理器
 *
 *@author AfeiBaili
 *@version 2025/11/3 16:43
 */

object ConfigManager {
    val builder = ModConfigSpec.Builder()
    val addressValue: ModConfigSpec.ConfigValue<String> = builder
        .comment("服务器地址")
        .define("address", "null")

    val portValue: ModConfigSpec.ConfigValue<Int> = builder
        .comment("服务器端口")
        .define("port", 33393)

    val tokenValue: ModConfigSpec.ConfigValue<String> = builder
        .comment("通讯密码：服务端必须和客户端一致")
        .define("token", UUID.randomUUID().toString().take(10))

    val spec: ModConfigSpec = builder.build()

    fun loadConfig(): Config = Config().apply {
        address = addressValue.get()
        port = portValue.get()
        token = tokenValue.get()
    }
}