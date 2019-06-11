import { asyncRouterMap, constantRouterMap } from '@/config/router.config'

/**
 * 过滤账户是否拥有某一个权限，并将菜单从加载列表移除
 *
 * @param menus
 * @param route
 * @returns {boolean}
 */
function hasPermission (menus, route) {
  if (route.meta && route.meta.permission) {
    let flag = false;
    for (let i = 0, len = menus.length; i < len; i++) {
      flag = route.meta.permission.includes(menus[i]);
      if (flag) {
        return true
      }
    }
    return false
  }
  return true
}

/**
 * 单账户多角色时，使用该方法可过滤角色不存在的菜单
 *
 * @param roles
 * @param route
 * @returns {*}
 */
// eslint-disable-next-line
function hasRole(roles, route) {
  if (route.meta && route.meta.roles) {
    return route.meta.roles.includes(roles.id)
  } else {
    return true
  }
}

/**
 * 异步过滤路由
 */
function filterAsyncRouter (routerMap, menus) {
  return routerMap.filter(route => {
    if (hasPermission(menus, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, menus)
      }
      return true
    }
    return false
  })
}

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers;
      state.routers = constantRouterMap.concat(routers)
    }
  },
  actions: {
    GenerateRoutes ({ commit }, data) {
      return new Promise(resolve => {
        const { menus } = data;
        // 过滤出拥有权限的路由
        const accessedRouters = filterAsyncRouter(asyncRouterMap, menus);
        commit('SET_ROUTERS', accessedRouters);
        // 完成路由过滤
        commit('SET_ROUTE_GENERATION', true);
        resolve()
      })
    }
  }
};

export default permission
