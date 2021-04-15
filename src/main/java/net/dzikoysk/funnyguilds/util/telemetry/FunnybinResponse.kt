package net.dzikoysk.funnyguilds.util.telemetry

import java.util.*

class FunnybinResponse {
    var fullUrl: String? = null
    var shortUrl: String? = null
    var uuid: String? = null

    constructor() {}
    constructor(fullUrl: String?, shortUrl: String?, uuid: String?) {
        this.fullUrl = fullUrl
        this.shortUrl = shortUrl
        this.uuid = uuid
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || this.javaClass != o.javaClass) {
            return false
        }
        val that = o as FunnybinResponse
        return fullUrl == that.fullUrl &&
                shortUrl == that.shortUrl &&
                uuid == that.uuid
    }

    override fun hashCode(): Int {
        return Objects.hash(fullUrl, shortUrl, uuid)
    }

    override fun toString(): String {
        return "FunnybinResponse{" +
                "fullUrl='" + fullUrl + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                ", uuid='" + uuid + '\'' +
                '}'
    }
}