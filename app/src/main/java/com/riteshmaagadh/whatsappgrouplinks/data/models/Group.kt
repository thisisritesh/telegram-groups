package com.riteshmaagadh.whatsappgrouplinks.data.models

data class Group(var title: String, var group_link: String, var category: String, var imageUrl: String){
    constructor() : this("","","","")
}
