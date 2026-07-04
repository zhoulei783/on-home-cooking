import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import AdminLogin from '../views/AdminLogin.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import ChefList from '../views/ChefList.vue'
import DishList from '../views/DishList.vue'
import Cart from '../views/Cart.vue'
import Order from '../views/Order.vue'
import OrderList from '../views/OrderList.vue'
import Register from '../views/Register.vue'
import ChefLogin from '../views/ChefLogin.vue'
import ChefDashboard from '../views/ChefDashboard.vue'
import Wallet from '../views/Wallet.vue'

const routes = [
    { path: '/', name: 'Home', component: Home },
    { path: '/login', name: 'Login', component: Login },
    { path: '/admin-login-page', name: 'AdminLogin', component: AdminLogin },
    { path: '/admin-dashboard', name: 'AdminDashboard', component: AdminDashboard },
    { path: '/chefs', name: 'ChefList', component: ChefList },
    { path: '/dishes', name: 'DishList', component: DishList },
    { path: '/cart', name: 'Cart', component: Cart },
    { path: '/order', name: 'Order', component: Order },
    { path: '/orders', name: 'OrderList', component: OrderList },
    { path: '/wallet', name: 'Wallet', component: Wallet },
    { path: '/register', name: 'Register', component: Register },
    { path: '/chef-login', name: 'ChefLogin', component: ChefLogin },
    { path: '/chef-dashboard', name: 'ChefDashboard', component: ChefDashboard },
]

const router = createRouter({
    history: createWebHistory(),
    routes
})
export default router
