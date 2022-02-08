package cn.asone.endless.utils.account

import cn.asone.endless.ui.gui.accounts.GuiAccounts
import cn.asone.endless.utils.ClientUtils
import cn.asone.endless.utils.extensions.mc
import com.google.gson.JsonParser
import com.mojangorigin.authlib.Agent
import com.mojangorigin.authlib.exceptions.AuthenticationException
import com.mojangorigin.authlib.exceptions.AuthenticationUnavailableException
import com.mojangorigin.authlib.yggdrasil.YggdrasilAuthenticationService
import com.thealtening.auth.service.AlteningServiceType
import net.minecraft.util.Session
import java.net.Proxy
import java.util.*

object LoginUtils {
    @JvmStatic
    fun login(minecraftAccount: MinecraftAccount?): String {
        if (minecraftAccount == null)
            return ""
        if (GuiAccounts.auth.service != AlteningServiceType.MOJANG) {
            try {
                GuiAccounts.auth.updateService(AlteningServiceType.MOJANG)
            } catch (e: NoSuchFieldException) {
                ClientUtils.logger.error("Something went wrong while trying to switch alt service.", e)
            } catch (e: IllegalAccessException) {
                ClientUtils.logger.error("Something went wrong while trying to switch alt service.", e)
            }
        }
        if (minecraftAccount.isCracked) {
            loginCracked(minecraftAccount.username)
            return "§cYour name is now §8" + minecraftAccount.username + "§c."
        }
        val result: LoginResult = loginMojang(minecraftAccount.username, minecraftAccount.password)
        if (result === LoginResult.LOGGED) {
            //val userName: String = imc.getSession().getUsername()
            val userName = mc.session.username
            minecraftAccount.inGameName = userName
            return "§cYour name is now §f§l$userName§c."
        }
        if (result === LoginResult.WRONG_PASSWORD) return "§cWrong password."
        if (result === LoginResult.NO_CONTACT) return "§cCannot contact authentication server."
        if (result === LoginResult.INVALID_ACCOUNT_DATA) return "§cInvalid username or password."
        return if (result === LoginResult.MIGRATED) "§cAccount migrated." else ""
    }

    @JvmStatic
    fun loginMojang(username: String?, password: String?): LoginResult {
        val userAuthentication = YggdrasilAuthenticationService(
            Proxy.NO_PROXY,
            ""
        ).createUserAuthentication(Agent.MINECRAFT)

        userAuthentication.setUsername(username)
        userAuthentication.setPassword(password)

        return try {
            userAuthentication.logIn()
            mc.session = Session(
                userAuthentication.selectedProfile.name,
                userAuthentication.selectedProfile.id.toString(), userAuthentication.authenticatedToken, "mojang"
            )
            LoginResult.LOGGED
        } catch (exception: AuthenticationUnavailableException) {
            LoginResult.NO_CONTACT
        } catch (exception: AuthenticationException) {
            val message = exception.message!!
            when {
                message.contains("invalid username or password.", ignoreCase = true) -> LoginResult.INVALID_ACCOUNT_DATA
                message.contains("account migrated", ignoreCase = true) -> LoginResult.MIGRATED
                else -> LoginResult.NO_CONTACT
            }
        } catch (exception: NullPointerException) {
            LoginResult.WRONG_PASSWORD
        }
    }

    @JvmStatic
    fun loginTheAltening(token: String): String {
        lateinit var result: LoginResult
        runCatching {
            GuiAccounts.auth.updateService(AlteningServiceType.THEALTENING)
            result = loginMojang(token, "Endless")
            GuiAccounts.auth.updateService(AlteningServiceType.MOJANG)
        }.onSuccess {
            return if (result === LoginResult.LOGGED)
                "§aYour name is now §f§l${mc.session.username}§c."
            else
                "§cFailed"
        }.onFailure {
            it.printStackTrace()
            return "§cFailed to login: ${it.message}"
        }
        return "§cFailed"
    }

    @JvmStatic
    fun loginCracked(username: String?) {
        mc.session = Session(username!!, UserUtils.getUUID(username), "-", "legacy")
    }

    @JvmStatic
    fun loginSessionId(sessionId: String): LoginResult {
        val decodedSessionData = try {
            String(Base64.getDecoder().decode(sessionId.split(".")[1]), Charsets.UTF_8)
        } catch (e: Exception) {
            return LoginResult.FAILED_PARSE_TOKEN
        }

        val sessionObject = try {
            JsonParser().parse(decodedSessionData).asJsonObject
        } catch (e: java.lang.Exception) {
            return LoginResult.FAILED_PARSE_TOKEN
        }
        val uuid = sessionObject.get("spr").asString
        val accessToken = sessionObject.get("yggt").asString

        if (!UserUtils.isValidToken(accessToken)) {
            return LoginResult.INVALID_ACCOUNT_DATA
        }

        val username = UserUtils.getUsername(uuid) ?: return LoginResult.INVALID_ACCOUNT_DATA

        mc.session = Session(username, uuid, accessToken, "mojang")

        return LoginResult.LOGGED
    }

    enum class LoginResult {
        WRONG_PASSWORD, NO_CONTACT, INVALID_ACCOUNT_DATA, MIGRATED, LOGGED, FAILED_PARSE_TOKEN
    }
}