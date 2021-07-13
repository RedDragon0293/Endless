package cn.endless.event

import cn.endless.features.module.Module

class UpdateEvent : Event("UpdateEvent")
class TickEvent : Event("TickEvent")
class KeyEvent(var int: Int) : Event("KeyEvent")
class StartClientEvent(var int: Int) : Event("StartClientEvent")
class SendMessageEvent(var message: String,val addToChat:Boolean) : Event("SendMessageEvent", true)
class EnableModuleEvent(val module: Module) : Event("EnableModuleEvent", true)
class DisableModuleEvent(val module: Module) : Event("DisableModuleEvent", true)