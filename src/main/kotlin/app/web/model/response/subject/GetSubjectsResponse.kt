package app.web.model.response.subject

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Ответ с массивом предметов")
data class GetSubjectsResponse(

    @JsonProperty
    @field:Schema(description = "Массив предметов")
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