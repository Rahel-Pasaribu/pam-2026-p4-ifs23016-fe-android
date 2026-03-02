package org.delcom.pam_p4_ifs23016.helper

class ConstHelper {
    // Route Names
    enum class RouteNames(val path: String) {
        Home(path = "home"),
        Profile(path = "profile"),
        Plants(path = "plants"),
        PlantsAdd(path = "plants/add"),

        PlantsDetail(path = "plants/{plantId}"),
        PlantsEdit(path = "plants/{plantId}/edit"),

        books(path = "books"),
        booksAdd(path = "books/add"),

        booksDetail(path = "books/{bookId}"),
        booksEdit(path = "books/{bookId}/edit"),

    }
}