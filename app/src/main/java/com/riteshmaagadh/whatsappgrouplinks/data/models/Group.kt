package com.riteshmaagadh.whatsappgrouplinks.data.models

import com.google.firebase.firestore.DocumentId

data class Group(
    @DocumentId
    var documentId: String,
    var title: String,
    var group_link: String,
    var category: String,
    var imageUrl: String,
    var active: Boolean
) {
    constructor() : this("", "", "", "", "",false)
}