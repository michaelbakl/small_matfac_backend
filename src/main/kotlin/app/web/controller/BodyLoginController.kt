package app.web.controller

import app.core.exception.SignUpException
import app.core.service.login.LoginService
import app.core.service.user.IUserService
import app.core.util.CommonResponse
import app.core.validation.IdValidator
import app.web.model.Login
import app.web.model.request.login.RefreshTokensRequest
import app.web.model.request.login.SignInRequest
import app.web.model.request.login.SignUpRegistrationRequest
import app.web.model.request.login.SignUpRequest
import app.web.model.response.login.SignInResponse
import app.web.security.AuthRolesRequired
import app.web.security.JwtTokenService
import app.web.security.UserCredentials
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import ru.baklykov.app.core.model.User
import ru.baklykov.app.core.validation.ValidUUID
import java.util.*


@Tag(
    name = "Аутентификация и управление пользователями",
    description = "Контроллер для входа в систему, регистрации и управления привилегиями пользователей"
)
@RestController
@RequestMapping("/api")
class BodyLoginController(
    private val loginService: LoginService,
    private val tokenService: JwtTokenService,
    private val userService: IUserService
) {

    /**
     * logins user on server
     * @param login - sign in request
     * @return token
     */
    @Operation(
        summary = "Авторизация пользователя",
        description = "Позволяет пользователю войти в систему с использованием email и пароля.",
        responses = [
            ApiResponse(responseCode = "200", description = "Успешная авторизация"),
            ApiResponse(responseCode = "401", description = "Неверные учетные данные")
        ]
    )
    @PostMapping(value = ["/signin"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun signIn(@RequestBody login: SignInRequest): ResponseEntity<CommonResponse<SignInResponse>> {
        val user = loginService.login(Login(login.email, login.password))
        val token = tokenService.createToken(user)
        val refreshToken = tokenService.generateRefreshToken(user.userId.toString())
        return ResponseEntity
            .ok()
            .body(CommonResponse(response = SignInResponse(token, refreshToken)))
    }

    @Operation(
        summary = "Запрос на регистрацию",
        description = "Отправляет запрос на регистрацию нового пользователя для последующей модерации.",
        responses = [
            ApiResponse(responseCode = "200", description = "Запрос успешно отправлен"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации или другой сбой")
        ]
    )
    @PostMapping(value = ["/signup-request"])
    @ResponseBody
    fun submitSignupRequest(@RequestBody req: SignUpRegistrationRequest): ResponseEntity<CommonResponse<String>> {
        return try {
            loginService.submitRequest(req)
            return ResponseEntity.ok(CommonResponse(response = "Request submitted for review."))
        } catch (e: SignUpException) {
            ResponseEntity<CommonResponse<String>>.badRequest()
                .body(CommonResponse(isError = true, error = HttpStatus.BAD_REQUEST.name))
        }
    }

    /**
     * create user on server
     * @param login - sign up request
     * @return String
     */
    @Operation(
        summary = "Регистрация пользователя (только для админов)",
        description = "Создаёт пользователя в системе с правами по умолчанию. Доступно только администраторам.",
        responses = [
            ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            ApiResponse(responseCode = "400", description = "Ошибка создания пользователя")
        ]
    )
    @PostMapping(value = ["/signup"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired(value = ["ADMIN"])
    fun signUp(@RequestBody login: SignUpRequest): ResponseEntity<String> {
        return try {
            loginService.create(Login(login.email, login.password), listOf("USER"))
            ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("Signed up successfully!")
        } catch (e: SignUpException) {
            ResponseEntity<String>(HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * refreshes a pair of tokens (access and refresh) for user
     * @param request - refresh tokens dto request
     * @return token
     */
    @Operation(
        summary = "Обновление JWT-токенов",
        description = "Позволяет получить новую пару access/refresh токенов по действующему refresh токену.",
        responses = [
            ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            ApiResponse(responseCode = "400", description = "Невалидный токен или ошибка обновления")
        ]
    )
    @PostMapping(value = ["/refreshTokens"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun refreshTokens(@RequestBody request: RefreshTokensRequest): ResponseEntity<CommonResponse<SignInResponse>> {
        try {
            val userId: String? = tokenService.getUserIdByToken(request.refreshToken)
            val user: User? = userId?.let { userService.findById(UUID.fromString(userId)) }
            val accessToken: String? = user?.let { tokenService.createToken(user) }
            val refreshToken: String = tokenService.generateRefreshToken(user?.userId.toString())
            if (accessToken != null) {
                return ResponseEntity
                    .ok()
                    .body(CommonResponse(response = SignInResponse(accessToken, refreshToken)))
            }
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, "Null tokens", ""))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Назначение ролей пользователю",
        description = "Администратор может назначить пользователю одну или несколько ролей.",
        responses = [
            ApiResponse(responseCode = "200", description = "Роли успешно назначены"),
            ApiResponse(responseCode = "400", description = "Ошибка валидации данных")
        ]
    )
    @PostMapping(value = ["/give_privileges_to_user"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired(value = ["ADMIN"])
    fun grantPrivileges(
        @RequestParam("userId") @ValidUUID userId: String,
        @RequestParam("roles") roles: List<String>,
        userCredentials: UserCredentials,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<Int>> {
        try {
            if (bindingResult.hasErrors() || !IdValidator.validate(userId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            val response = userService.grantUserPrivileges(UUID.fromString(userId), roles)
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Обновление ролей пользователя",
        description = "Позволяет заменить список ролей пользователя. Только для администраторов.",
        responses = [
            ApiResponse(responseCode = "200", description = "Роли обновлены"),
            ApiResponse(responseCode = "400", description = "Ошибка обработки")
        ]
    )
    @PostMapping(value = ["/update_user_privileges"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired(value = ["ADMIN"])
    fun updatePrivileges(
        @RequestParam("userId") userId: String,
        @RequestParam("roles") roles: List<String>,
        userCredentials: UserCredentials,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<Int>> {
        try {
            if (bindingResult.hasErrors() || !IdValidator.validate(userId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }
            val response = userService.updateUserPrivileges(UUID.fromString(userId), roles)
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Удаление всех ролей пользователя",
        description = "Удаляет все роли у пользователя. Только для администраторов.",
        responses = [
            ApiResponse(responseCode = "200", description = "Привилегии удалены"),
            ApiResponse(responseCode = "400", description = "Ошибка удаления")
        ]
    )
    @PostMapping(value = ["/remove_privileges_from_user"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired(value = ["ADMIN"])
    fun removePrivileges(
        @RequestParam("userId") userId: String,
        userCredentials: UserCredentials,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<Int>> {
        try {
            if (bindingResult.hasErrors() || !IdValidator.validate(userId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }

            // TODO(save id of the admin who granted permissions)

            val response = userService.removeAllUserPrivileges(UUID.fromString(userId))
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

    @Operation(
        summary = "Удаление одной роли у пользователя",
        description = "Удаляет конкретную роль у пользователя. Только для администраторов.",
        responses = [
            ApiResponse(responseCode = "200", description = "Роль удалена"),
            ApiResponse(responseCode = "400", description = "Ошибка удаления роли")
        ]
    )
    @PostMapping(value = ["/remove_privilege_from_user"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    @AuthRolesRequired(value = ["ADMIN"])
    fun removeOnePrivilege(
        @RequestParam("userId") userId: String,
        @RequestParam("role") privilege: String,
        userCredentials: UserCredentials,
        bindingResult: BindingResult
    ): ResponseEntity<CommonResponse<Int>> {
        try {
            if (bindingResult.hasErrors() || !IdValidator.validate(userId)) {
                return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(CommonResponse(true, bindingResult.toString(), ""))
            }

            // TODO(save id of the admin who granted permissions)

            val response = userService.removeUserPrivilege(UUID.fromString(userId), privilege)
            return ResponseEntity
                .ok()
                .body(CommonResponse(response = response))
        } catch (e: Exception) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CommonResponse(true, e.toString(), ""))
        }
    }

}

