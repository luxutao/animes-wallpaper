package cn.animekid.animeswallpaper.data

data class ImageDataBean(
    var code: Int,
    var `data`: List<Data>,
    var msg: String
) {
    data class Data(
        var image_album_id: Int,
        var image_date: String,
        var image_date_gmt: String,
        var image_extension: String,
        var image_height: Int,
        var image_id: Int,
        var image_likes: Int,
        var image_medium: String,
        var image_name: String,
        var image_size: Int,
        var image_source: String,
        var image_thumb: String,
        var image_width: Int
    )
}

data class ResponseDataBean(
    var code: Int,
    var data: String,
    var msg: String
)

data class UserAuthBean(
    var code: Int,
    var data: Data,
    var msg: String
) {
    data class Data(
        var userid: Int,
        var token: String,
        var name: String,
        var create_time: String,
        var email: String,
        var sex: String,
        var avatar: String
    )
}
