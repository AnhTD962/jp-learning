import apiClient from './apiClient'

export const searchService = {
  async globalSearch(keyword) {
    const res = await apiClient.get('/search', {
      params: { keyword }
    })
    // 🔒 Chuẩn hóa dữ liệu tránh undefined
    return {
      words: res.data.words || [],
      decks: res.data.decks || []
    }
  }
}