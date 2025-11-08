package com.example.calculator.storage

data class SortingOption(
    val title: String,
    val icon: String,
    val sortType: SortType,
)

enum class SortType {
    DATE_ASC,
    DATE_DESC,
    NAME_ASC,
    NAME_DESC

}
