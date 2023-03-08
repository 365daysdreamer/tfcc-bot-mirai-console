package org.tfcc.bot.command

import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.toMessageChain
import org.tfcc.bot.CommandHandler
import org.tfcc.bot.storage.PermData
import org.tfcc.bot.storage.TFCCConfig

@ConsoleExperimentalApi
object AddAdmin : CommandHandler {
    override val name = "增加管理员"

    override fun showTips(groupCode: Long, senderId: Long) = "增加管理员 对方QQ号"

    override fun checkAuth(groupCode: Long, senderId: Long) = TFCCConfig.isSuperAdmin(senderId)

    override fun execute(msg: GroupMessageEvent, content: String): Pair<MessageChain?, MessageChain?> {
        val qqNumbers = content.split(" ").map {
            runCatching { it.toLong() }.getOrNull() ?: return Pair(null, null)
        }
        if (qqNumbers.isEmpty()) return Pair(null, null)
        val (succeed, failed) = qqNumbers.partition { PermData.addAdmin(it) }
        val result =
            if (succeed.isNotEmpty()) succeed.joinToString(prefix = "已增加管理员：")
            else failed.joinToString(postfix = "已经是管理员了")
        return Pair(PlainText(result).toMessageChain(), null)
    }
}