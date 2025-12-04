import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import SignupView from '../views/SignupView.vue'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'login',
            component: LoginView
        },
        {
            path: '/signup',
            name: 'signup',
            component: SignupView
        },
        {
            path: '/home',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login/success',
            name: 'login-success',
            component: () => import('../views/LoginSuccess.vue')
        },
        {
            path: '/login/failure',
            name: 'login-failure',
            component: () => import('../views/LoginFailure.vue')
        }
    ]
})

export default router
