package me.m123.image.data

data class UserInfoToken(
        var id: Int,
        var token: String,
        var username: String,
        var last_name: String,
        var first_name: String,
        var email: String,
        var gender: String,
        var avatar: String,
        var date_joined: String,
        var last_login: String
)