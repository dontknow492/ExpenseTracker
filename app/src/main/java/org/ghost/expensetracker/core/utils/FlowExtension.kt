package org.ghost.expensetracker.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine as combineInternal

/**
 * A type-safe combine function for 6 different Flows.
 */
inline fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    crossinline transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combineInternal(
    combineInternal(flow, flow2, flow3, ::Triple),
    combineInternal(flow4, flow5, flow6, ::Triple)
) { t1, t2 ->
    transform(
        t1.first, t1.second, t1.third,
        t2.first, t2.second, t2.third
    )
}