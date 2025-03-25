package app.core.validation

import java.util.regex.Pattern

object IdValidator: Validator<String> {
    private var pattern: Pattern =
        Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$")

    override fun validate(obj: String?): Boolean {
        return obj != null && "" != obj && pattern.matcher(obj).matches()
    }


}