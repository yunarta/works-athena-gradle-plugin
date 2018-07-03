package testKit

fun <T, R> List<T>.range(begin: T, end: T, closure: (List<T>) -> R): R {

    val from = indexOf(begin)
    val to = indexOf(end)

    return closure(when {
        to < from || from == -1 || to == -1 -> emptyList()
        else -> subList(from, to + 1)
    })
}