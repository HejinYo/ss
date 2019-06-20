// ie polyfill
import '@babel/polyfill'

import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store/'
import { VueAxios } from './utils/request'
// mock
import './mock'

import bootstrap from './core/bootstrap'
import './core/use'
import './permission' // permission control
import './utils/filter' // global filter
import { Tree, Checkbox, Table, TableColumn, Loading } from 'element-ui'

Vue.config.productionTip = false

// mount axios Vue.$http and this.$http
Vue.use(VueAxios)
Vue.use(Tree)
Vue.use(Checkbox)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Loading.directive)

new Vue({
  router,
  store,
  created () {
    bootstrap()
  },
  mounted () {
    this.$nextTick(function () {
      // 保证完全挂载
      window.onresize = function temp () {
        let height = document.documentElement.clientHeight
        store.commit('SET_CLIENT_HEIGHT', height - 5)
      }
    })
  },
  render: h => h(App)
}).$mount('#app')
