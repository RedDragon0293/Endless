package cn.endless.event

abstract class AbstractEvent {

}

abstract class AbstractCancellableEvent {
    var isCancelled = false
}