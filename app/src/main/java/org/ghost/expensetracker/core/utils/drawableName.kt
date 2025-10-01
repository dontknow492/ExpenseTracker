package org.ghost.expensetracker.core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources

@SuppressLint("DiscouragedApi")
fun getDrawableResourceId(iconName: String, context: Context): Int? {
    val resourceId = context.resources.getIdentifier(
        iconName, // The name of the resource
        "drawable",     // The type of resource
        context.packageName // Your app's package name
    )

    if (resourceId == 0) {
        return null
    }
    return resourceId
}

fun getResourceEntryName(resourceId: Int, context: Context): String? {
    return try {
        context.resources.getResourceEntryName(resourceId)
    } catch (e: Resources.NotFoundException) {
        null
    }
}