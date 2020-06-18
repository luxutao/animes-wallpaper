package me.m123.image.data

data class ImageList(
    var count: Int,
    var `results`: List<ImageListData>,
    var next: String,
    var previous: String
    )

data class ImageListData(
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

