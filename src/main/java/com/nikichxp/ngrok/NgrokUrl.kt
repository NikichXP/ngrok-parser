package com.nikichxp.ngrok

import com.mashape.unirest.http.Unirest

object NgrokUrl {
    val url = Unirest
            .get("http://localhost:4040/api/tunnels")
            .asJson().body.`object`
            .getJSONArray("tunnels")
            .getJSONObject(0)
            .get("public_url")
            .toString()
            .replace("http:", "https:")

//    fun getUrl() = url
}