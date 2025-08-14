import { createRouter, createWebHistory } from 'vue-router'
import { authGuard } from '../utils/authGuard'

// Import views
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'
import DashboardPage from '../views/DashboardPage.vue'
import FlashcardDecksPage from '../views/FlashcardDecksPage.vue'
import FlashcardDetailPage from '../views/FlashcardDetailPage.vue'
import QuizPage from '../views/QuizPage.vue'
import QuizResultPage from '../views/QuizResultPage.vue'
import LeaderboardPage from '../views/LeaderboardPage.vue'
import AchievementsPage from '../views/AchievementsPage.vue'
import NotFoundPage from '../views/NotFoundPage.vue'
import Home from '../views/Home.vue'
import Words from '../views/Words.vue'
import WordDetail from '../components/WordCard.vue'
import WordEdit from '../components/WordEdit.vue'
import CreateWord from '../components/CreateWord.vue'
import FlashcardDeckOfOwner from '../views/FlashcardDeckOfOwner.vue'
import FlashcardView from '../views/FlashcardView.vue'

const routes = [
    { path: '/dashboard', component: DashboardPage, beforeEnter: authGuard },
    { path: '/login', component: LoginPage },
    { path: '/register', component: RegisterPage },
    { path: '/flashcards', component: FlashcardDecksPage, beforeEnter: authGuard },
    { path: '/flashcards/:id', component: FlashcardDetailPage, beforeEnter: authGuard },
    { path: '/quiz', component: QuizPage, beforeEnter: authGuard },
    { path: '/quiz-result', component: QuizResultPage, beforeEnter: authGuard },
    { path: '/leaderboard', component: LeaderboardPage, beforeEnter: authGuard },
    { path: '/achievements', component: AchievementsPage, beforeEnter: authGuard },
    { path: '/', component: Home },
    { path: '/words', component: Words },
    { path: '/word/:id', component: WordDetail },
    { path: '/word/:id/edit', component: WordEdit, name: 'WordEdit', beforeEnter: authGuard, props: true },
    { path: '/word/create', component: CreateWord, beforeEnter: authGuard },
    { path: '/flashcards/view/:id', component: FlashcardView, beforeEnter: authGuard },
    { path: '/users/:userId/decks', component: FlashcardDeckOfOwner, beforeEnter: authGuard },
    { path: '/:pathMatch(.*)*', component: NotFoundPage },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

export default router
