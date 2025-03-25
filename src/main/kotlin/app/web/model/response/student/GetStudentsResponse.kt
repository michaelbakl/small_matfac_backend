package app.web.model.response.student

import app.web.model.response.person.GetStudentInfoResponse
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class GetStudentsResponse(

    @JsonProperty
    val students: Map<UUID, List<GetStudentInfoResponse>>
)
