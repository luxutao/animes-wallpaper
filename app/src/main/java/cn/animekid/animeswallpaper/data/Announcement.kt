package cn.animekid.animeswallpaper.data

data class Announcement(
        var code: Int,
        var msg: String,
        var data: AnnouncementData
)

data class AnnouncementData(
        var id: Int,
        var message: String,
        var package_name: String,
        var create_time: String,
        var modify_time: String
)