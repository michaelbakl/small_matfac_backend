package app.web.model.response.subject

import com.fasterxml.jackson.annotation.JsonProperty

data class GetSubjectsResponse (
    @JsonProperty
    var response: Array<GetSubjectResponse>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GetSubjectsResponse

        return response.contentEquals(other.response)
    }

    override fun hashCode(): Int {
        return response.contentHashCode()
    }
}