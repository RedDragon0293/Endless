package cn.asone.endless.ui.hud

class Side(var horizontal: Horizontal, var vertical: Vertical) {
    companion object {
        /**
         * Default element side
         */
        fun default() = Side(Horizontal.LEFT, Vertical.UP)
    }

    /**
     * Horizontal side
     */
    enum class Horizontal(val sideName: String) {
        LEFT("Left"),
        MIDDLE("Middle"),
        RIGHT("Right");

        companion object {
            @JvmStatic
            fun getByName(name: String) = values().find { it.sideName == name }
        }
    }

    /**
     * Vertical side
     */
    enum class Vertical(val sideName: String) {
        UP("Up"),
        MIDDLE("Middle"),
        DOWN("Down");

        companion object {
            @JvmStatic
            fun getByName(name: String) = values().find { it.sideName == name }
        }
    }
}