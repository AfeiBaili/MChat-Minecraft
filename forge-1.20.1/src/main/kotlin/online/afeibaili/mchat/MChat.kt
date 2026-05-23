package online.afeibaili.mchat

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.commands.Commands
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import online.afeibaili.mchat.MChat.Companion.mchatSystem
import online.afeibaili.mchat.config.Config
import online.afeibaili.mchat.listener.MessageListener
import online.afeibaili.mchat.socket.message.MessageManager
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.registerConfig
import java.io.Closeable
import java.io.InputStream
import java.net.URL
import java.util.*

@Mod(MChat.ID)
class MChat {
    companion object {
        const val ID = "mchat"
        val mchatSystem: MChatSystem = MChatSystem()
    }

    init {
        FORGE_BUS.register(this)
        FORGE_BUS.register(Listener())
        registerConfig(ModConfig.Type.COMMON, MChatModConfig.spec)
    }

    @SubscribeEvent
    fun onCommonSetup(event: ServerStartingEvent) {
        val manager: MessageManager<ChatFormatting, MinecraftServer> =
            object : MessageManager<ChatFormatting, MinecraftServer>() {
                override var formatting: ChatFormatting = ChatFormatting.GRAY
                override fun sendToMC(message: String, formatting: ChatFormatting) {
                    event.server.playerList.players.forEach { player ->
                        player.sendSystemMessage(Component.literal(message).withStyle(formatting))
                    }
                }

                override fun sendImageToMC(message: String, formatting: ChatFormatting) {
                    event.server.playerList.players.forEach { player ->
                        player.sendSystemMessage(
                            Component.literal("[点击查看图片消息]").withStyle { style ->
                                style.withColor(ChatFormatting.BLUE)
                                    .withClickEvent(ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/image $message"))
                            }
                        )
                    }
                }
            }


        mchatSystem.buildManager(manager)
        mchatSystem.connect(ConfigLoader.config)
    }

    @SubscribeEvent
    fun onCommonStop(event: ServerStoppingEvent) {
        MChatSystem.close()
    }

    @SubscribeEvent
    fun registerClientCommand(event: RegisterClientCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("image")
                .then(
                    Commands.argument("url", StringArgumentType.greedyString()).executes { ctx ->
                        val image = ScreenImage(StringArgumentType.getString(ctx, "url"))
                        Minecraft.getInstance().setScreen(image)
                        return@executes 1
                    })
                .then(
                    Commands.literal("release").executes { ctx ->
                        ScreenImage.close()
                        Minecraft.getInstance().player?.sendSystemMessage(
                            Component.literal("已释放图片资源")
                                .withStyle(ChatFormatting.BLUE)
                        )
                        return@executes 1
                    }
                )
        )
    }
}

class Listener : MessageListener<ServerChatEvent, PlayerEvent.PlayerLoggedInEvent, PlayerEvent.PlayerLoggedOutEvent> {
    @SubscribeEvent
    override fun onMessage(event: ServerChatEvent) {
        mchatSystem.getMessageManager()
            .sendToGroup("[${ConfigLoader.config.name}] " + event.player.name.string + ": " + event.message.string)
    }

    @SubscribeEvent
    override fun onPlayerIn(event: PlayerEvent.PlayerLoggedInEvent) {
        mchatSystem.getMessageManager()
            .sendLoginInOut("${event.entity.name.string}加入了${ConfigLoader.config.name}服务器")
    }

    @SubscribeEvent
    override fun onPlayerOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        mchatSystem.getMessageManager()
            .sendLoginInOut("${event.entity.name.string}退出了${ConfigLoader.config.name}服务器")
    }
}

object ConfigLoader {
    val config = Config(
        MChatModConfig.token.get(),
        MChatModConfig.address.get(),
        MChatModConfig.port.get(),
        MChatModConfig.name.get()
    )
}

object MChatModConfig {
    val builder = ForgeConfigSpec.Builder()

    val address: ForgeConfigSpec.ConfigValue<String> =
        builder.comment("mchat server url address")
            .define("address", "mc.afeibaili.cn")

    val port: ForgeConfigSpec.ConfigValue<Int> =
        builder.comment("mchat server port")
            .define("port", 33393)

    val token: ForgeConfigSpec.ConfigValue<String> =
        builder.comment("mchat server token")
            .define("token", "c5c5d6ce-")

    val name: ForgeConfigSpec.ConfigValue<String> =
        builder.comment("mchat server name")
            .define("name", "未命名")

    val spec: ForgeConfigSpec = builder.build()
}

class ScreenImage(url: String) : Screen(Component.literal("图片查看")), Closeable {
    val image: Image = getImageByUrl(url)

    override fun render(
        graphics: GuiGraphics,
        mouseX: Int,
        mouseY: Int,
        partialTick: Float,
    ) {

        runCatching {
            image.writeGraphics(graphics)
        }.onFailure { exception ->
            exception.printStackTrace()
            Minecraft.getInstance().player?.sendSystemMessage(
                Component.literal("图片损坏（按ESC关闭消息）：${exception.message}")
                    .withStyle(ChatFormatting.RED)
            )
        }

        super.render(graphics, mouseX, mouseY, partialTick)
    }

    override fun close() {
        image.close()
    }

    companion object {
        val map = HashMap<String, Image>()

        fun getImageByUrl(url: String): Image {
            val stream: InputStream = URL(url).openConnection().apply {
                connectTimeout = 5000
                readTimeout = 5000
            }.getInputStream()
            val nativeImage: NativeImage = NativeImage.read(stream)
            val texture = DynamicTexture(nativeImage)
            val uuid: String = UUID.randomUUID().toString()
            val location: ResourceLocation =
                Minecraft.getInstance().textureManager.register(uuid, texture)
            return Image(location, texture, nativeImage.width, nativeImage.height).apply {
                map[uuid] = this
            }
        }

        fun close() {
            map.forEach { _, image -> image.close() }
            map.clear()
        }
    }
}

class Image(val location: ResourceLocation, val texture: DynamicTexture, val width: Int, val height: Int) : Closeable {
    fun writeGraphics(graphics: GuiGraphics) {
        val mc = Minecraft.getInstance()

        val screenWidth = mc.window.guiScaledWidth
        val screenHeight = mc.window.guiScaledHeight

        val imageWidth = width.toFloat()
        val imageHeight = height.toFloat()

        val scale = minOf(
            screenWidth / imageWidth,
            screenHeight / imageHeight
        )

        val drawWidth = (imageWidth * scale).toInt()
        val drawHeight = (imageHeight * scale).toInt()

        val drawX = (screenWidth - drawWidth) / 2
        val drawY = (screenHeight - drawHeight) / 2

        graphics.blit(
            location,
            drawX,
            drawY,
            drawWidth,
            drawHeight,
            0f, 0f,
            width,
            height,
            width,
            height
        )
    }

    override fun close() {
        texture.close()
        Minecraft.getInstance().textureManager.release(location)
    }
}