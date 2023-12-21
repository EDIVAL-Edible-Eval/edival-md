package com.dicoding.edival.data.db

import java.util.Date


data class Reminder(
    var name: String? = null,
    var storage: String? = null,
    var store: String? = null,
    var exp: String? = null,
    var type: String? = null,
    var imageUrl: String? = null,
    var listed: String? = null,
    var documentId: Long
){
    constructor() : this(null, null, null, null, null, null,null, 0L)
}
