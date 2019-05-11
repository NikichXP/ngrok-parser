package com.nikichxp.ngrok

import com.mashape.unirest.http.Unirest
import java.lang.Exception

class NgrokUrl {

    private var executableLocation: String? = null
    var ngrokAddress: String? = null
        get() {
            if (field != null) {
                return field
            }
            val ret = loadNgrokAddr()
            if (ret != null) {
                field = ret
            }
            return ret
        }
    
    private var process: Process? = null

    fun location(location: String) = apply {
        this.executableLocation = location
    }

    private fun loadNgrokAddr(): String? = try {
        Unirest.get("http://localhost:4040/api/tunnels")
                .asJson().body.`object`
                .getJSONArray("tunnels")
                .getJSONObject(0)
                .get("public_url")
                .toString()
                .replace("http:", "https:")
    } catch (e: Exception) {
        null
    }

    fun tryLaunch(port: Int): String? = executableLocation?.let {
        if (loadNgrokAddr() == null) {
            val builder = ProcessBuilder(
                    if (it.startsWith('~')) {
                        System.getProperty("user.home") + it.substring(1)
                    } else {
                        it
                    }.let {
                        if (it.endsWith('/')) {
                            "$it/ngrok"
                        } else {
                            it
                        }
                    }, "http", port.toString())
            builder.redirectErrorStream(true)
            process = builder.start()
        }
        (0..10).forEach {
            val response = loadNgrokAddr()
            if (response != null) {
                return response
            }
            Thread.sleep(100)
        }
        return null
    }

    fun stopProcess() {
        process?.destroy()
    }
}

