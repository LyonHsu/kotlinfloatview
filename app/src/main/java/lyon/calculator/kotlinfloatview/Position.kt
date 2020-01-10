package lyon.calculator.kotlinfloatview

data class Position(val fx: Float, val fy: Float) {

    val x: Int
        get() = fx.toInt()

    val y: Int
        get() = fy.toInt()

    operator fun plus(p: Position) = Position(fx + p.fx, fy + p.fy)
    operator fun minus(p: Position) = Position(fx - p.fx, fy - p.fy)
}