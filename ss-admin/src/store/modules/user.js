import Vue from 'vue'
import { getInfo, getMenus, login, logout } from '@/api/login'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { welcome } from '@/utils/util'
import Cookies from 'js-cookie'

const user = {
  state: {
    token: '',
    name: '',
    welcome: '',
    avatar: '',
    roles: [],
    info: {},
    // 用户拥有的菜单编码 ['system','user']
    menus: []
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, { name, welcome }) => {
      state.name = name
      state.welcome = welcome
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_INFO: (state, info) => {
      state.info = info
    },
    SET_MENUS: (state, menus) => {
      state.menus = menus
    }
  },

  actions: {
    // 登录
    Login ({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(response => {
          const { code } = response
          if (code === 1) {
            const toke = Cookies.get('x-access-token')
            Vue.ls.set(ACCESS_TOKEN, toke, 7 * 24 * 60 * 60 * 1000)
            commit('SET_TOKEN', toke)
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo ({ commit }) {
      return new Promise((resolve, reject) => {
        getInfo().then(data => {
          let { result } = data
          commit('SET_INFO', result)
          commit('SET_NAME', { name: result.nickName, welcome: welcome() })
          commit('SET_AVATAR', result.avatar)
          resolve(result)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户菜單
    GetMenus ({ commit }) {
      return new Promise((resolve, reject) => {
        getMenus().then(data => {
          let { result } = data
          commit('SET_ROLES', result.role)
          commit('SET_MENUS', result)
          resolve(result)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 登出
    Logout ({ commit, state }) {
      return new Promise((resolve) => {
        commit('SET_TOKEN', '')
        commit('SET_ROLES', [])
        Vue.ls.remove(ACCESS_TOKEN)
        // 移除cookie
        Cookies.remove('x-access-token')

        logout(state.token).then(() => {
          resolve()
        }).catch(() => {
          resolve()
        })
      })
    }

  }
}

export default user
