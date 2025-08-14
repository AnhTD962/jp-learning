import apiClient from './apiClient'

export default {
    async getWords() {
        const res = await apiClient.get('/words')
        return res.data
    },

    async getWordById(id) {
        const res = await apiClient.get(`/words/${id}`)
        return res.data
    },

    async getWordByType(type) {
        const res = await apiClient.get(`/words/type/${type}`)
        return res.data
    },

    async getWordByTypeAndSubType(type, subType) {
        const res = await apiClient.get(`/words/type/${type}/subtype/${subType}`)
        return res.data
    },

    async searchWords(query) {
        const res = await apiClient.get(`/words/search?q=${query}`)
        return res.data
    },

    async createWord(word) {
        const res = await apiClient.post('/words', word)
        return res.data
    },

    async updateWord(id, word) {
        const res = await apiClient.put(`/words/${id}`, word)
        return res.data
    },

    async deleteWord(id) {
        const res = await apiClient.delete(`/words/${id}`)
        return res.data
    },
}
