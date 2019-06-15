import { constantRouterMap, generatorDynamicRouter } from '@/config/router.config'
import { getOperateTree } from '@/api/sys-resource'

const dd = [{
  'resId': 2,
  'parentId': 1,
  'type': 1,
  'resName': '首页',
  'resCode': 'index',
  'icon': 'home',
  'meta': {
    'path': '/',
    'redirect': '/dashboard/workplace',
    'target': null,
    'component': 'BasicLayout',
    'keepAlive': null,
    'hideHeader': null,
    'hideChildrenInMenu': null
  },
  'seq': 1,
  'state': 1,
  'children': [{
    'resId': 3,
    'parentId': 2,
    'type': 1,
    'resName': '系统管理',
    'resCode': 'system',
    'icon': 'setting',
    'meta': {
      'path': '/system',
      'redirect': null,
      'target': null,
      'component': 'PageView',
      'keepAlive': false,
      'hideHeader': null,
      'hideChildrenInMenu': null
    },
    'seq': 1,
    'state': 1,
    'children': [{
      'resId': 7,
      'parentId': 3,
      'type': 1,
      'resName': '资源管理',
      'resCode': 'system_resource',
      'icon': 'tool',
      'meta': {
        'path': '/system/resource',
        'redirect': null,
        'target': null,
        'component': 'systemResource',
        'keepAlive': true,
        'hideHeader': null,
        'hideChildrenInMenu': null
      },
      'seq': 1,
      'state': 1,
      'children': []
    }]
  }, {
    'resId': 4,
    'parentId': 2,
    'type': 1,
    'resName': '仪表盘',
    'resCode': 'dashboard',
    'icon': 'dashboard',
    'meta': {
      'path': '/dashboard',
      'redirect': null,
      'target': null,
      'component': 'RouteView',
      'keepAlive': false,
      'hideHeader': null,
      'hideChildrenInMenu': null
    },
    'seq': 2,
    'state': 1,
    'children': [{
      'resId': 8,
      'parentId': 4,
      'type': 1,
      'resName': '分析页',
      'resCode': 'dashboard_analysis',
      'icon': 'bars',
      'meta': {
        'path': '/dashboard/analysis',
        'redirect': null,
        'target': null,
        'component': 'dashboardAnalysis',
        'keepAlive': true,
        'hideHeader': null,
        'hideChildrenInMenu': null
      },
      'seq': 1,
      'state': 1,
      'children': []
    }, {
      'resId': 9,
      'parentId': 4,
      'type': 1,
      'resName': '监控页',
      'resCode': 'monitor',
      'icon': 'tool',
      'meta': {
        'path': 'https://www.hejinyo.cn/',
        'redirect': null,
        'target': '_blank',
        'component': null,
        'keepAlive': false,
        'hideHeader': null,
        'hideChildrenInMenu': null
      },
      'seq': 2,
      'state': 1,
      'children': []
    }, {
      'resId': 10,
      'parentId': 4,
      'type': 1,
      'resName': '工作台',
      'resCode': 'dashboard_workplace',
      'icon': 'tool',
      'meta': {
        'path': '/dashboard/workplace',
        'redirect': null,
        'target': null,
        'component': 'dashboardWorkplace',
        'keepAlive': true,
        'hideHeader': null,
        'hideChildrenInMenu': null
      },
      'seq': 3,
      'state': 1,
      'children': []
    }]
  }, {
    'resId': 5,
    'parentId': 2,
    'type': 1,
    'resName': '个人页',
    'resCode': 'account',
    'icon': 'user',
    'meta': {
      'path': '/account',
      'redirect': null,
      'target': null,
      'component': 'RouteView',
      'keepAlive': true,
      'hideHeader': null,
      'hideChildrenInMenu': null
    },
    'seq': 3,
    'state': 1,
    'children': [{
      'resId': 11,
      'parentId': 5,
      'type': 1,
      'resName': '个人中心',
      'resCode': 'account_center',
      'icon': 'bars',
      'meta': {
        'path': '/account/center',
        'redirect': null,
        'target': null,
        'component': 'accountCenter',
        'keepAlive': true,
        'hideHeader': null,
        'hideChildrenInMenu': null
      },
      'seq': 1,
      'state': 1,
      'children': []
    }, {
      'resId': 12,
      'parentId': 5,
      'type': 1,
      'resName': '个人设置',
      'resCode': 'account_settings',
      'icon': 'tool',
      'meta': {
        'path': '/account/settings',
        'redirect': null,
        'target': null,
        'component': 'accountSettings',
        'keepAlive': false,
        'hideHeader': true,
        'hideChildrenInMenu': true
      },
      'seq': 2,
      'state': 1,
      'children': [{
        'resId': 14,
        'parentId': 12,
        'type': 1,
        'resName': '基本设置',
        'resCode': 'account_settings_base',
        'icon': 'tool',
        'meta': {
          'path': '/account/settings/base',
          'redirect': null,
          'target': null,
          'component': 'accountSettingsBase',
          'keepAlive': true,
          'hideHeader': null,
          'hideChildrenInMenu': null
        },
        'seq': 1,
        'state': 1,
        'children': []
      }, {
        'resId': 15,
        'parentId': 12,
        'type': 1,
        'resName': '安全设置',
        'resCode': 'account_settings_security',
        'icon': 'tool',
        'meta': {
          'path': '/account/settings/security',
          'redirect': null,
          'target': null,
          'component': 'accountSettingsSecurity',
          'keepAlive': true,
          'hideHeader': null,
          'hideChildrenInMenu': null
        },
        'seq': 2,
        'state': 1,
        'children': []
      }, {
        'resId': 16,
        'parentId': 12,
        'type': 1,
        'resName': '个性化设置',
        'resCode': 'account_settings_custom',
        'icon': 'tool',
        'meta': {
          'path': '/account/settings/custom',
          'redirect': null,
          'target': null,
          'component': 'accountSettingsCustom',
          'keepAlive': true,
          'hideHeader': null,
          'hideChildrenInMenu': null
        },
        'seq': 3,
        'state': 1,
        'children': []
      }, {
        'resId': 17,
        'parentId': 12,
        'type': 1,
        'resName': '账户绑定',
        'resCode': 'account_settings_binding',
        'icon': 'tool',
        'meta': {
          'path': '/account/settings/binding',
          'redirect': null,
          'target': null,
          'component': 'accountSettingsBinding',
          'keepAlive': true,
          'hideHeader': null,
          'hideChildrenInMenu': null
        },
        'seq': 4,
        'state': 1,
        'children': []
      }, {
        'resId': 18,
        'parentId': 12,
        'type': 1,
        'resName': '新消息通知',
        'resCode': 'account_settings_notification',
        'icon': 'tool',
        'meta': {
          'path': '/account/settings/notification',
          'redirect': null,
          'target': null,
          'component': 'accountSettingsNotification',
          'keepAlive': true,
          'hideHeader': null,
          'hideChildrenInMenu': null
        },
        'seq': 5,
        'state': 1,
        'children': []
      }]
    }]
  }, {
    'resId': 6,
    'parentId': 2,
    'type': 1,
    'resName': '其他组件',
    'resCode': 'other',
    'icon': 'slack',
    'meta': {
      'path': '/other',
      'redirect': '/other/icon-selector',
      'target': null,
      'component': 'RouteView',
      'keepAlive': true,
      'hideHeader': null,
      'hideChildrenInMenu': null
    },
    'seq': 4,
    'state': 1,
    'children': [{
      'resId': 13,
      'parentId': 6,
      'type': 1,
      'resName': 'IconSelector',
      'resCode': 'other_icon_selector',
      'icon': 'tool',
      'meta': {
        'path': '/other/icon-selector',
        'redirect': null,
        'target': null,
        'component': 'otherIconSelector',
        'keepAlive': true,
        'hideHeader': null,
        'hideChildrenInMenu': null
      },
      'seq': 1,
      'state': 1,
      'children': []
    }]
  }]
}]
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
        getOperateTree().then((res) => {
          generatorDynamicRouter(res.result.tree).then(routers => {
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
