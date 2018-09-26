package io.zenandroid.greenfield.model

import java.util.*

/**
 * Created by alex on 24/01/2018.
 */

data class Image (
    val title: String? = null,
    val link: String? = null,
    val media: MediaLink? = null,
    val dateTaken: Date? = null,
    val description: String? = null,
    val published: Date? = null,
    val author: String? = null,
    val authorId: String? = null,
    val tags: String? = null
)
