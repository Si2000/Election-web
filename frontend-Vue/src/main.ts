/**
 * Main entry point for the Vue application.
 */
import { createApp } from 'vue'
import App from './App.vue'
import {createRouter, createWebHistory} from "vue-router"
import {createPinia} from "pinia";
import { userInfo } from "@/storages/userStorage";

// CSS Imports
import "@/styles/candidates.css"
import "@/styles/navbar.css";
import "@/styles/auth.css"
import "@/styles/account.css";
import "@/styles/forum.css";
import "@/styles/createTopic.css";
import "@/styles/topicDetail.css";
import "@/styles/verifyAccount.css";
import "@/styles/ElectionResult.css"

// Components & Views
import NewsPage from "@/components/NewsPage.vue";
import QuizPage from "@/components/QuizPage.vue";
import NewsDetailPage from "@/components/NewsDetailPage.vue";
import NewDetaiPage2 from "@/components/NewDetaiPage2.vue";

const routes = [
  { path: '/', redirect: '/home'},
  {path: '/home', component: () => import('@/components/HomePage.vue')},
  { path: '/yasser', component: () => import('@/views/YasserPage.vue') },
  { path: '/map', component: () => import('@/views/ElectionMapView.vue') },
  { path: '/mapProvinces', component: () => import('@/views/ProvincesMapView.vue') },
  {path: '/Candidates', component: () => import('@/views/CandidatesView.vue')},
  {path: '/ElectionResult', component: () => import('@/views/ElectionResult.vue')},
  {path: '/login', component: () => import('@/views/LoginView.vue')},
  {path: '/register', component: () => import('@/views/RegisterView.vue')},
  { path: '/forgot-password', component: () => import('@/views/ForgotPasswordView.vue') },
  { path: '/reset-password', component: () => import('@/views/ResetPasswordView.vue') },
  { path: '/compare', component: () => import('@/views/CompareElection.vue') },

  {
    path: '/verify-account',
    component: () => import('@/views/VerifyAccountView.vue')
  },

  {
    path: '/confirm-email-change',
    component: () => import('@/views/ConfirmEmailChange.vue')
  },

  // Beveiligde routes
  {
    path: '/account',
    component: () => import('@/views/AccountView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: "/profile/:username",
    name: "user-profile",
    component: () => import('@/views/PublicProfileView.vue')
  },
  {
    path: '/adminPanel',
    component: () => import('@/views/AdminPanel.vue'),
    meta: { requiresAuth: true, adminOnly: true }
  },

  // Forum routes
  { path: '/forum', component: () => import('@/views/ForumView.vue') },
  {
    path: '/create-topic',
    component: () => import('@/views/CreateTopicView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/forum/:id',
    component: () => import('@/views/TopicDetailView.vue'),
    props: true
  },
  {
    path: '/municipality/:year/:municipalityId',
    name: 'MunicipalityResultView',
    component: () => import('@/components/MunicipalityResultView.vue'),
    props: true
  },
  {
    path: "/member/:id",
    name: "MemberDetail",
    component: () => import("@/views/MemberDetail.vue"),
    props: true,
  },
  {path: '/news', component: NewsPage,
    children: [
      {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/LoginView.vue')
      },
      {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/RegisterView.vue')
      },
      {
        path: '/ElectionResult',
        name: 'ElectionResult',
        component: () => import('@/views/ElectionResult.vue')
      },
      {
        path: 'candidates',
        name: 'candidatesPage',
        component: () => import('@/views/CandidatesView.vue'),
      },
      {
        path: 'newsDetails',
        component: NewsDetailPage,
      },
      {
        name: 'News',
        path: 'newsDetails2/:id/:title?/:content?',
        component: NewDetaiPage2,
      },
    ]},
  {path: '/quiz', component: QuizPage},
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation Guard (voor beveiligde pagina's)
router.beforeEach((to, from, next) => {
  const store = userInfo();
  store.checkToken();

  // 1. Check login
  if (to.meta.requiresAuth && !store.isLoggedIn) {
    return next('/login');
  }

  // 2. Check admin
  if (to.meta.adminOnly) {
    if (store.role !== "ROLE_ADMIN") {
      alert("Je hebt geen toegang tot deze pagina.");
      return next("/home");
    }
  }

  next();
});

const pinia = createPinia()
const app = createApp(App)

app.use(pinia)
app.use(router)
app.mount('#app')
