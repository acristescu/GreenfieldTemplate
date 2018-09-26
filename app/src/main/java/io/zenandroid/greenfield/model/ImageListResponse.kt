package io.zenandroid.greenfield.model

import java.util.*

/**
 * Created by alex on 24/01/2018.
 */

data class ImageListResponse (
    private val title: String? = null,
    private val link: String? = null,
    private val description: String? = null,
    private val modified: Date? = null,
    private val generator: String? = null,
    var items: List<Image>? = null
)
