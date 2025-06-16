package ru.baklykov.app.core.exception

import java.util.*


class WrongInputException(vararg data: String?) :
    RuntimeException("Wrong inputs $data")