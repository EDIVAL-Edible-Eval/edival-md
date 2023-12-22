package com.dicoding.edival.data.db

import java.util.Date


data class Reminder(
    var name: String? = null,
    var storage_type: String? = null,
    var store_date: String? = null,
    var exp_date: String? = null,
    var type: String? = null,
    var img_path: String? = null,
    var status: String? = null,
    var documentId: Long
){
    constructor() : this(null, null, null, null, null, null,null, 0L)
}
