import { constantRouterMap, generatorDynamicRouter } from '@/config/router.config'
import { getUserMenus } from '@/api/login'

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: []
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    }
  },
  actions: {
    GenerateRoutes ({ commit }, data) {
      return new Promise(resolve => {
        getUserMenus().then((res) => {
          generatorDynamicRouter(res.result).then(routers => {
            commit('SET_ROUTERS', routers)
            // 完成路由过滤
            commit('SET_ROUTE_GENERATION', true)
            resolve()
          })

        })
      })
    }
  }
}

export default permission
