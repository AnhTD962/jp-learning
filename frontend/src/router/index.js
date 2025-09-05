import { createRouter, createWebHistory } from "vue-router";
import { authGuard } from "../utils/authGuard";

// Import views
import LoginPage from "../views/LoginPage.vue";
import RegisterPage from "../views/RegisterPage.vue";
import FlashcardDecksPage from "../views/FlashcardDecksPage.vue";
import FlashcardDetailPage from "../views/FlashcardDetailPage.vue";
import QuizView from "../views/QuizView.vue";
import QuizResult from "../views/QuizResult.vue";
import NotFoundPage from "../views/NotFoundPage.vue";
import Home from "../views/Home.vue";
import Words from "../views/Words.vue";
import WordDetail from "../components/WordCard.vue";
import WordEdit from "../components/WordEdit.vue";
import CreateWord from "../components/CreateWord.vue";
import FlashcardDeckOfOwner from "../views/FlashcardDeckOfOwner.vue";
import FlashcardView from "../views/FlashcardView.vue";
import MyAttempts from "../views/MyAttempts.vue";
import NotificationView from "../views/NotificationView.vue";
import ForgotPassword from "../views/ForgotPassword.vue";
import Profile from "../views/Profile.vue";
import ChangePassword from "../views/ChangePassword.vue";

const routes = [
  { path: "/login", component: LoginPage },
  { path: "/register", component: RegisterPage },
  {
    path: "/flashcards",
    component: FlashcardDecksPage,
    beforeEnter: authGuard,
  },
  {
    path: "/flashcards/:id",
    component: FlashcardDetailPage,
    beforeEnter: authGuard,
  },
  {
    path: "/quiz/:quizId",
    component: QuizView,
    name: "QuizView",
    beforeEnter: authGuard,
    props: true,
  },
  {
    path: "/attempts/:attemptId/result",
    component: QuizResult,
    beforeEnter: authGuard,
  },
  { path: "/attempts", component: MyAttempts, beforeEnter: authGuard },
  { path: "/", component: Home },
  { path: "/words", component: Words },
  { path: "/word/:id", component: WordDetail },
  {
    path: "/word/:id/edit",
    component: WordEdit,
    name: "WordEdit",
    beforeEnter: authGuard,
    props: true,
  },
  { path: "/word/create", component: CreateWord, beforeEnter: authGuard },
  { path: "/flashcards/view/:id", component: FlashcardView },
  {
    path: "/users/:userId/decks",
    component: FlashcardDeckOfOwner,
    beforeEnter: authGuard,
  },
  { path: "/:pathMatch(.*)*", component: NotFoundPage },
  {
    path: "/notifications",
    name: "Notification",
    component: NotificationView,
    beforeEnter: authGuard,
  },
  { path: "/forgot-password", component: ForgotPassword },
  {
    path: "/profile",
    component: Profile,
    name: "Profile",
    beforeEnter: authGuard,
  },
  {
    path: "/change-password",
    component: ChangePassword,
    name: "ChangePassword",
    beforeEnter: authGuard,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
