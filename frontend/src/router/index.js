import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'

// 前台系统组件
import SystemLayout from '../views/system/SystemLayout.vue'
import GuJieYiWen from '../views/system/GuJieYiWen.vue'
import ZhongShengWanXiang from '../views/system/ZhongShengWanXiang.vue'
import BaoHuangTian from '../views/system/BaoHuangTian.vue'
import TianXiaMingGu from '../views/system/TianXiaMingGu.vue'
import AiChat from '../views/system/AiChat.vue'
import UserProfile from '../views/system/UserProfile.vue'
import ArticlePublish from '../views/system/ArticlePublish.vue'
import ArticleDetail from '../views/system/ArticleDetail.vue'
import VideoDetail from '../views/system/VideoDetail.vue'
import InsectDetail from '../views/system/InsectDetail.vue'
import VideoPublish from '../views/system/VideoPublish.vue'

// 后台管理组件
import AdminLayout from '../views/admin/AdminLayout.vue'
import UserManage from '../views/admin/UserManage.vue'
import InsectManage from '../views/admin/InsectManage.vue'

const routes = [
  // === 登录注册 ===
  { path: '/', name: 'LoginPage', component: LoginPage },
  { path: '/register', name: 'RegisterPage', component: RegisterPage },

  // === 前台系统 ===
  {
    path: '/system',
    component: SystemLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/system/gujie' }, 
      { path: 'gujie', name: 'GuJieYiWen', component: GuJieYiWen, meta: { requiresAuth: true } },
      { path: 'zhongsheng', name: 'ZhongShengWanXiang', component: ZhongShengWanXiang, meta: { requiresAuth: true } },
      { path: 'baohuang', name: 'BaoHuangTian', component: BaoHuangTian, meta: { requiresAuth: true } },
      { path: 'tianxia', name: 'TianXiaMingGu', component: TianXiaMingGu, meta: { requiresAuth: true } },
      { path: 'ai-chat', name: 'AiChat', component: AiChat, meta: { requiresAuth: true } },
      { path: 'user-profile', name: 'UserProfile', component: UserProfile, meta: { requiresAuth: true } },
      { path: 'article-publish', name: 'ArticlePublish', component: ArticlePublish, meta: { requiresAuth: true } },
      { path: 'article/:id', name: 'ArticleDetail', component: ArticleDetail, meta: { requiresAuth: true } },
      { path: 'video/:id', name: 'VideoDetail', component: VideoDetail, meta: { requiresAuth: true } },
      { path: 'insect/:id', name: 'InsectDetail', component: InsectDetail, meta: { requiresAuth: true } },
      { path: 'video-publish', name: 'VideoPublish', component: VideoPublish, meta: { requiresAuth: true } }
    ]
  },

  // === 后台管理系统 ===
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/admin/user' },
      { 
        path: 'user', 
        name: 'UserManage', 
        component: UserManage, 
        meta: { requiresAuth: true, title: '用户账号管理' } 
      },
      { 
        path: 'insect', 
        name: 'InsectManage', 
        component: InsectManage, 
        meta: { requiresAuth: true, title: '蛊虫信息管理' } 
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// 路由守卫:只有带了 user_token 且 isLoggedIn 为 true 才能进入系统或后台
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('user_token')
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'

  // 只要是 requiresAuth 的路由,都需要检查登录状态
  if (to.meta.requiresAuth && (!token || !isLoggedIn)) {
    // 没登录则跳转回登录页
    next({ path: '/' })
  } else {
    next()
  }
})

export default router
