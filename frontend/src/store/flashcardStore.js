import { defineStore } from 'pinia'
import flashcardService from '../services/flashcardService'

export const useFlashcardStore = defineStore('flashcards', {
    state: () => ({
        decks: [],
        currentDeck: null,
        isLoading: false,
        error: null,
    }),
    actions: {
        async fetchDecks() {
            this.decks = await flashcardService.getDecks()
        },
        async fetchCurrentDecks() {
            this.decks = await flashcardService.getCurrentDecks()
        },
        async fetchDeck(id) {
            this.currentDeck = await flashcardService.getDeck(id)
        },
        async createDeck(deck) {
            await flashcardService.createDeck(deck)
            await this.fetchDecks()
        },
        async updateDeck(id, deck) {
            await flashcardService.updateDeck(id, deck)
            await this.fetchDecks()
        },
        async deleteDeck(id) {
            await flashcardService.deleteDeck(id)
            this.decks = this.decks.filter(d => d.id !== id)
        },
        async addFlashcard(deckId, flashcard) {
            await flashcardService.addFlashcard(deckId, flashcard)
            await this.fetchDeck(deckId)
        },
        async updateFlashcard(deckId, flashcardId, flashcard) {
            await flashcardService.updateFlashcard(deckId, flashcardId, flashcard)
            await this.fetchDeck(deckId)
        },
        async deleteFlashcard(deckId, flashcardId) {
            await flashcardService.deleteFlashcard(deckId, flashcardId)
            await this.fetchDeck(deckId)
        },
    },
})
