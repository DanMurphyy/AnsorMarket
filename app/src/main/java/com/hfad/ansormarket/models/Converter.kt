package com.hfad.ansormarket.models

import androidx.room.TypeConverter

class Converter {

    @TypeConverter
    fun fromItemType(itemType: ItemType): String {
        return itemType.name
    }

    @TypeConverter
    fun toItemType(itemType: String): ItemType {
        return ItemType.valueOf(itemType)
    }
}