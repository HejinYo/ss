// eslint-disable-next-line
import { BasicLayout, BlankLayout, PageView, RouteView, UserLayout } from '@/layouts'

/**
 * 基础路由
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Login')
      },
      {
        path: 'register',
        name: 'register',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/Register')
      },
      {
        path: 'register-result',
        name: 'registerResult',
        component: () => import(/* webpackChunkName: "user" */ '@/views/user/RegisterResult')
      }
    ]
  },
  {
    path: '/test',
    component: BlankLayout,
    redirect: '/test/home',
    children: [
      {
        path: 'home',
        name: 'TestHome',
        component: () => import('@/views/Home')
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }

]

// 前端路由表
const constantRouterComponents = {
  // 基础页面 layout 必须引入
  BasicLayout: BasicLayout,
  BlankLayout: BlankLayout,
  RouteView: RouteView,
  PageView: PageView,
  // 外部链接
  monitor: () => import('@/views/dashboard/Monitor'),
  // 分析页
  dashboardAnalysis: () => import('@/views/dashboard/Analysis'),
  // 工作台
  dashboardWorkplace: () => import('@/views/dashboard/Workplace'),
  // 个人中心
  accountCenter: () => import('@/views/account/center/Index'),
  // 个人设置
  accountSettings: () => import('@/views/account/settings/Index'),
  // 基本设置
  accountSettingsBase: () => import('@/views/account/settings/BaseSetting'),
  // 安全设置
  accountSettingsSecurity: () => import('@/views/account/settings/Security'),
  // 个性化设置
  accountSettingsCustom: () => import('@/views/account/settings/Custom'),
  // 账户绑定
  accountSettingsBinding: () => import('@/views/account/settings/Binding'),
  // 新消息通知
  accountSettingsNotification: () => import('@/views/account/settings/Notification'),
  // IconSelector
  otherIconSelector: () => import('@/views/other/IconSelectorView'),
  // 资源管理
  systemResource: () => import('@/views/system/resource')
}

/**
 * 获取路由菜单信息
 *
 * 1. 调用 getRouterByUser() 访问后端接口获得路由结构数组
 *   https://github.com/sendya/ant-design-pro-vue/blob/feature/dynamic-menu/public/dynamic-menu.json
 */
export const generatorDynamicRouter = (data) => {
  return new Promise((resolve, reject) => {
    const routers = generator(data)
    // 前端未找到页面路由（固定不用改）
    routers.push({ path: '*', redirect: '/404', hidden: true })
    resolve(routers)
  })
}

/**
 * 格式化 后端 结构信息并递归生成层级路由表
 */
export const generator = (routerMap, parent) => {
  return routerMap.map(item => {
    let meta = item.meta || {}
    const currentRouter = {
      // 路由地址 动态拼接生成如 /dashboard/workplace
      path: meta.path || '',
      // 路由名称，建议唯一
      name: item.resCode || '',
      // 该路由对应页面的 组件
      component: constantRouterComponents[meta.component] || null,
      // 隐藏子菜单
      hideChildrenInMenu: meta.hideChildrenInMenu || false,
      // meta
      meta: {
        // 页面标题
        title: item.resName,
        // 菜单图标
        icon: item.icon || undefined,
        // 页面权限(供指令权限用，可去掉)
        permission: (item.code && [item.code]) || null,
        // 外部链接
        target: (meta.target) || null,
        // 隐藏菜单
        hideHeader: (meta.hideHeader) || false
      }
    }
    // 为了防止出现后端返回结果不规范，处理有可能出现拼接出两个 反斜杠
    !meta.target && (currentRouter.path = currentRouter.path.replace('//', '/'))
    // 重定向
    meta.redirect && (currentRouter.redirect = meta.redirect)
    // 是否有子菜单，并递归处理
    if (item.children && item.children.length > 0) {
      // Recursion
      currentRouter.children = generator(item.children, currentRouter)
    }
    return currentRouter
  })
}
