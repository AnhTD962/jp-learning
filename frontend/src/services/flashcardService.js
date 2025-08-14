import apiClient from './apiClient'

export default {
    async getCurrentDecks() {
        const res = await apiClient.get('/decks/my-decks')
        return res.data
    },

    async getDecks() {
        const res = await apiClient.get('/decks')
        return res.data
    },

    async getDecksByUserId(userId) {
        const res = await apiClient.get(`/decks/user/${userId}`)
        return res.data
    },

    async getDeck(id) {
        const res = await apiClient.get(`/decks/${id}`)
        return res.data
    },

    async createDeck(deck) {
        const res = await apiClient.post('/decks', deck)
        return res.data
    },

    async updateDeck(id, deck) {
        const res = await apiClient.put(`/decks/${id}`, deck)
        return res.data
    },

    async deleteDeck(id) {
        const res = await apiClient.delete(`/decks/${id}`)
        return res.data
    },

    async addFlashcard(deckId, flashcard) {
        const res = await apiClient.post(`/decks/${deckId}/flashcards`, flashcard)
        return res.data
    },

    async updateFlashcard(deckId, cardId, flashcard) {
        const res = await apiClient.put(`/decks/${deckId}/flashcards/${cardId}`, flashcard)
        return res.data
    },

    async deleteFlashcard(deckId, cardId) {
        const res = await apiClient.delete(`/decks/${deckId}/flashcards/${cardId}`)
        return res.data
    },
}
