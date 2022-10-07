package boringyuri.processor.common.xvisitors

import androidx.room.compiler.processing.XArrayType
import androidx.room.compiler.processing.XType

interface XTypeVisitor<R, P> {
    fun visit(type: XType, param: P): R?

    fun visitArray(type: XArrayType, param: P): R?
}

fun <R, P> XType.accept(visitor: XTypeVisitor<R, P>, param: P): R? {
    return when (this) {
        is XArrayType -> {
            acceptXArrayType(this, visitor, param)
        }
        else -> {
            visitor.visit(this, param)
        }
    }
}

fun <R, P> acceptXArrayType(
    type: XArrayType,
    visitor: XTypeVisitor<R, P>,
    param: P
): R? {
    return visitor.visitArray(type, param)
}
