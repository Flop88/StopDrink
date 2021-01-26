package ru.mvlikhachev.stopdrink.Utils

import ru.mvlikhachev.stopdrink.database.DatabaseRepository
import ru.mvlikhachev.stopdrink.screens.MainActivity

lateinit var APP_ACTIVITY: MainActivity
lateinit var REPOSITORY: DatabaseRepository
const val TYPE_DATABASE = "type_database"
const val TYPE_ROOM = "type_room"