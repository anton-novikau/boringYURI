package boringyuri.processor.common.xvisitors

import androidx.room.compiler.processing.XArrayType
import androidx.room.compiler.processing.XType

abstract class AbstractXTypeVisitor<R, P> : XTypeVisitor<R, P> {
    override fun visit(type: XType, param: P): R? = null

    override fun visitArray(type: XArrayType, param: P): R? = null
}