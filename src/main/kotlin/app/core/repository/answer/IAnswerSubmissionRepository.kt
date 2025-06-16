package app.core.repository.answer

import app.core.model.game.AnswerSubmission

interface IAnswerSubmissionRepository {
    fun save(submission: AnswerSubmission): Int
}