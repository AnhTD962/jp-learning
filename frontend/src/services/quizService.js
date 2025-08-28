import apiClient from "./apiClient";

export default {
  async generateQuiz(deckId, numberOfQuestions = 0) {
    const res = await apiClient.post("/quizzes/generate", {
      deckId,
      numberOfQuestions,
    });
    return res.data;
  },

  async startAttempt(quizId) {
    const res = await apiClient.post(
      `/attempts/start/${encodeURIComponent(quizId)}`
    );
    return res.data; // { attemptId, quizId, deckId, questionsSnapshot }
  },

  async submitAttempt(attemptId, answers) {
    // Đừng wrap thêm "answers" nữa
    const res = await apiClient.post(
      `/attempts/${encodeURIComponent(attemptId)}/submit`,
      { answers } // đúng chuẩn DTO
    );
    return res.data;
  },

  // Get attempt result (owner or deck owner)
  async getAttemptResult(attemptId) {
    const res = await apiClient.get(
      `/attempts/${encodeURIComponent(attemptId)}/result`
    );
    return res.data;
  },

  // List current user's attempts
  async getMyAttempts() {
    const res = await apiClient.get("/attempts/me");
    return res.data;
  },

  // Deck owner: list all attempts for a deck
  async getDeckAttempts(deckId) {
    const res = await apiClient.get(
      `/attempts/deck/${encodeURIComponent(deckId)}`
    );
    return res.data;
  },
};
