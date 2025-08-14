import { defineStore } from 'pinia'
import wordService from '../services/wordService'

export const useWordStore = defineStore('words', {
    state: () => ({
        words: [],
        searchResults: [],
        isLoading: false,
        error: null,
    }),
    actions: {
        async fetchWords() {
            this.isLoading = true
            try {
                this.words = await wordService.getWords()
            } catch (e) {
                this.error = e
            } finally {
                this.isLoading = false
            }
        },
        async searchWords(query) {
            this.searchResults = await wordService.searchWords(query)
        },
        async addWord(word) {
            await wordService.createWord(word)
            await this.fetchWords()
        },
        async updateWord(id, word) {
            await wordService.updateWord(id, word)
            await this.fetchWords()
        },
        async deleteWord(id) {
            await wordService.deleteWord(id)
            await this.fetchWords()
        },
    },
})
