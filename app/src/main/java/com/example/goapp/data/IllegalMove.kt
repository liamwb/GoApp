package com.example.goapp.data

/***
 * enum class for different types of illegal move. IllegalMove.NONE indicates a legal move.
 */
enum class IllegalMove {
    SELFCAPTURE,
    WRONGPLAYERTOMOVE,
    KO,
    NONE
}