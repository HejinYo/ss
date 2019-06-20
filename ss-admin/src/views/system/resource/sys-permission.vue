<template>
  <div>
    <a-card ref="permInstance">
      <span slot="title">
        <a @click="$emit('sider-click', !collapsed)">
          <a-icon :type="collapsed?'right-circle':'left-circle'" theme="twoTone" twoToneColor="#eb2f96"/>
        </a>
        <span>权限管理</span>
      </span>
      <!--操作工具条-->
      <a-row>
        <a-col :xs="24" :sm="12" :md="14" :lg="16">
          <a-button type="primary">修改</a-button>
          <a-button type="danger">删除</a-button>
        </a-col>

        <a-col :xs="24" :sm="12" :md="10" :lg="8">
          <a-input-group compact>
            <a-select defaultValue="Option1" style="width: 30%">
              <a-select-option value="Option1">Option1</a-select-option>
              <a-select-option value="Option2">Option2</a-select-option>
            </a-select>
            <a-input-search style="width: 70%" laceholder="查询..." :maxlength="32">
              <a-icon slot="enterButton" type="search"/>
            </a-input-search>
          </a-input-group>
        </a-col>
      </a-row>
      <br/>
      <div>
        <div>
          <!-- 权限表格 -->
          <el-table ref="permTable" stripe border highlight-current-row size="mini" element-loading-text="拼命加载中"
                    :data="resListData" :height="clientHeight - 330">
            <el-table-column prop="resId" label="编号" sortable="custom" align="center" width="70"></el-table-column>
            <el-table-column prop="resName" label="资源名称" sortable="custom" align="center" min-width="150"></el-table-column>
            <el-table-column prop="permName" label="权限名称" sortable="custom" align="center" min-width="150"></el-table-column>
            <el-table-column prop="resCode" label="权限编码" sortable="custom" align="center" min-width="150"></el-table-column>
            <el-table-column prop="state" sortable="custom" label="状态" align="center" width="90">
              <template slot-scope="scope">
                <a-tag :color="scope.row.state === 1 ? '#2db7f5': '#bbbbbb' ">
                  {{scope.row.state === 1 ? '正常' : '禁用'}}
                </a-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>

      </div>
      <br/>
      <!--翻页工具条-->
      <a-pagination hideOnSinglePage showSizeChanger size="small"
                    :total="1000"
                    :pageSize="10">
      </a-pagination>
    </a-card>
  </div>

</template>

<script>
  export default {
    name: 'sys-permission',
    components: {},
    computed: {},
    props: {
      clientHeight: {
        type: Number
      },
      collapsed: {
        type: Boolean
      }
    },
    data () {
      return {
        // 差额高
        differenceHigh: 0,
        resListData: [
          { "resId": 1, "parentId": 0, "type": 1, "resName": "系统资源", "resCode": "root", "icon": "home", "meta": {}, "seq": 1, "state": 1 }, {
            "resId": 2,
            "parentId": 1,
            "type": 1,
            "resName": "首页",
            "resCode": "index",
            "icon": "home",
            "meta": { "path": "/", "redirect": "/dashboard/workplace", "component": "BasicLayout" },
            "seq": 1,
            "state": 1,
            "children": [{
              "resId": 3,
              "parentId": 2,
              "type": 1,
              "resName": "系统管理",
              "resCode": "system",
              "icon": "setting",
              "meta": { "path": "/system", "component": "PageView", "keepAlive": false },
              "seq": 1,
              "state": 1,
              "children": [{
                "resId": 7,
                "parentId": 3,
                "type": 1,
                "resName": "资源管理",
                "resCode": "system_resource",
                "icon": "tool",
                "meta": { "path": "/system/resource", "component": "systemResource", "keepAlive": true },
                "seq": 1,
                "state": 1,
                "children": []
              }]
            }, {
              "resId": 4,
              "parentId": 2,
              "type": 1,
              "resName": "仪表盘",
              "resCode": "dashboard",
              "icon": "dashboard",
              "meta": { "path": "/dashboard", "redirect": "/dashboard/analysis", "component": "RouteView", "keepAlive": false },
              "seq": 2,
              "state": 1,
              "children": [{
                "resId": 8,
                "parentId": 4,
                "type": 1,
                "resName": "分析页",
                "resCode": "dashboard_analysis",
                "icon": "bars",
                "meta": { "path": "/dashboard/analysis", "component": "dashboardAnalysis", "keepAlive": true },
                "seq": 1,
                "state": 1,
                "children": []
              }, {
                "resId": 9,
                "parentId": 4,
                "type": 1,
                "resName": "监控页",
                "resCode": "monitor",
                "icon": "tool",
                "meta": { "path": "https://www.hejinyo.cn/", "target": "_blank", "keepAlive": false },
                "seq": 2,
                "state": 1,
                "children": []
              }, {
                "resId": 10,
                "parentId": 4,
                "type": 1,
                "resName": "工作台",
                "resCode": "dashboard_workplace",
                "icon": "tool",
                "meta": { "path": "/dashboard/workplace", "component": "dashboardWorkplace", "keepAlive": true },
                "seq": 3,
                "state": 1,
                "children": []
              }]
            }, {
              "resId": 5,
              "parentId": 2,
              "type": 1,
              "resName": "个人页",
              "resCode": "account",
              "icon": "user",
              "meta": { "path": "/account", "component": "RouteView", "keepAlive": true },
              "seq": 3,
              "state": 1,
              "children": [{
                "resId": 11,
                "parentId": 5,
                "type": 1,
                "resName": "个人中心",
                "resCode": "account_center",
                "icon": "bars",
                "meta": { "path": "/account/center", "component": "accountCenter", "keepAlive": true },
                "seq": 1,
                "state": 1,
                "children": []
              }, {
                "resId": 12,
                "parentId": 5,
                "type": 1,
                "resName": "个人设置",
                "resCode": "account_settings",
                "icon": "tool",
                "meta": { "path": "/account/settings", "component": "accountSettings", "keepAlive": false, "hideHeader": true, "hideChildrenInMenu": true },
                "seq": 2,
                "state": 1,
                "children": [{
                  "resId": 14,
                  "parentId": 12,
                  "type": 1,
                  "resName": "基本设置",
                  "resCode": "account_settings_base",
                  "icon": "tool",
                  "meta": { "path": "/account/settings/base", "component": "accountSettingsBase", "keepAlive": true },
                  "seq": 1,
                  "state": 1,
                  "children": []
                }, {
                  "resId": 15,
                  "parentId": 12,
                  "type": 1,
                  "resName": "安全设置",
                  "resCode": "account_settings_security",
                  "icon": "tool",
                  "meta": { "path": "/account/settings/security", "component": "accountSettingsSecurity", "keepAlive": true },
                  "seq": 2,
                  "state": 1,
                  "children": []
                }, {
                  "resId": 16,
                  "parentId": 12,
                  "type": 1,
                  "resName": "个性化设置",
                  "resCode": "account_settings_custom",
                  "icon": "tool",
                  "meta": { "path": "/account/settings/custom", "component": "accountSettingsCustom", "keepAlive": true },
                  "seq": 3,
                  "state": 1,
                  "children": []
                }, {
                  "resId": 17,
                  "parentId": 12,
                  "type": 1,
                  "resName": "账户绑定",
                  "resCode": "account_settings_binding",
                  "icon": "tool",
                  "meta": { "path": "/account/settings/binding", "component": "accountSettingsBinding", "keepAlive": true },
                  "seq": 4,
                  "state": 1,
                  "children": []
                }, {
                  "resId": 18,
                  "parentId": 12,
                  "type": 1,
                  "resName": "新消息通知",
                  "resCode": "account_settings_notification",
                  "icon": "tool",
                  "meta": { "path": "/account/settings/notification", "component": "accountSettingsNotification", "keepAlive": true },
                  "seq": 5,
                  "state": 0,
                  "children": []
                }]
              }]
            }, {
              "resId": 6,
              "parentId": 2,
              "type": 1,
              "resName": "其他组件",
              "resCode": "other",
              "icon": "slack",
              "meta": { "path": "/other", "redirect": "/other/icon-selector", "component": "RouteView", "keepAlive": true },
              "seq": 4,
              "state": 1,
              "children": [{
                "resId": 13,
                "parentId": 6,
                "type": 1,
                "resName": "图标选择",
                "resCode": "other_icon_selector",
                "icon": "tool",
                "meta": { "path": "/other/icon-selector", "component": "otherIconSelector", "keepAlive": true },
                "seq": 1,
                "state": 1,
                "children": []
              }]
            }]
          }, {
            "resId": 3,
            "parentId": 2,
            "type": 1,
            "resName": "系统管理",
            "resCode": "system",
            "icon": "setting",
            "meta": { "path": "/system", "component": "PageView", "keepAlive": false },
            "seq": 1,
            "state": 1,
            "children": [{
              "resId": 7,
              "parentId": 3,
              "type": 1,
              "resName": "资源管理",
              "resCode": "system_resource",
              "icon": "tool",
              "meta": { "path": "/system/resource", "component": "systemResource", "keepAlive": true },
              "seq": 1,
              "state": 1,
              "children": []
            }]
          }, {
            "resId": 4,
            "parentId": 2,
            "type": 1,
            "resName": "仪表盘",
            "resCode": "dashboard",
            "icon": "dashboard",
            "meta": { "path": "/dashboard", "redirect": "/dashboard/analysis", "component": "RouteView", "keepAlive": false },
            "seq": 2,
            "state": 1,
            "children": [{
              "resId": 8,
              "parentId": 4,
              "type": 1,
              "resName": "分析页",
              "resCode": "dashboard_analysis",
              "icon": "bars",
              "meta": { "path": "/dashboard/analysis", "component": "dashboardAnalysis", "keepAlive": true },
              "seq": 1,
              "state": 1,
              "children": []
            }, {
              "resId": 9,
              "parentId": 4,
              "type": 1,
              "resName": "监控页",
              "resCode": "monitor",
              "icon": "tool",
              "meta": { "path": "https://www.hejinyo.cn/", "target": "_blank", "keepAlive": false },
              "seq": 2,
              "state": 1,
              "children": []
            }, {
              "resId": 10,
              "parentId": 4,
              "type": 1,
              "resName": "工作台",
              "resCode": "dashboard_workplace",
              "icon": "tool",
              "meta": { "path": "/dashboard/workplace", "component": "dashboardWorkplace", "keepAlive": true },
              "seq": 3,
              "state": 1,
              "children": []
            }]
          }, {
            "resId": 5,
            "parentId": 2,
            "type": 1,
            "resName": "个人页",
            "resCode": "account",
            "icon": "user",
            "meta": { "path": "/account", "component": "RouteView", "keepAlive": true },
            "seq": 3,
            "state": 1,
            "children": [{
              "resId": 11,
              "parentId": 5,
              "type": 1,
              "resName": "个人中心",
              "resCode": "account_center",
              "icon": "bars",
              "meta": { "path": "/account/center", "component": "accountCenter", "keepAlive": true },
              "seq": 1,
              "state": 1,
              "children": []
            }, {
              "resId": 12,
              "parentId": 5,
              "type": 1,
              "resName": "个人设置",
              "resCode": "account_settings",
              "icon": "tool",
              "meta": { "path": "/account/settings", "component": "accountSettings", "keepAlive": false, "hideHeader": true, "hideChildrenInMenu": true },
              "seq": 2,
              "state": 1,
              "children": [{
                "resId": 14,
                "parentId": 12,
                "type": 1,
                "resName": "基本设置",
                "resCode": "account_settings_base",
                "icon": "tool",
                "meta": { "path": "/account/settings/base", "component": "accountSettingsBase", "keepAlive": true },
                "seq": 1,
                "state": 1,
                "children": []
              }, {
                "resId": 15,
                "parentId": 12,
                "type": 1,
                "resName": "安全设置",
                "resCode": "account_settings_security",
                "icon": "tool",
                "meta": { "path": "/account/settings/security", "component": "accountSettingsSecurity", "keepAlive": true },
                "seq": 2,
                "state": 1,
                "children": []
              }, {
                "resId": 16,
                "parentId": 12,
                "type": 1,
                "resName": "个性化设置",
                "resCode": "account_settings_custom",
                "icon": "tool",
                "meta": { "path": "/account/settings/custom", "component": "accountSettingsCustom", "keepAlive": true },
                "seq": 3,
                "state": 1,
                "children": []
              }, {
                "resId": 17,
                "parentId": 12,
                "type": 1,
                "resName": "账户绑定",
                "resCode": "account_settings_binding",
                "icon": "tool",
                "meta": { "path": "/account/settings/binding", "component": "accountSettingsBinding", "keepAlive": true },
                "seq": 4,
                "state": 1,
                "children": []
              }, {
                "resId": 18,
                "parentId": 12,
                "type": 1,
                "resName": "新消息通知",
                "resCode": "account_settings_notification",
                "icon": "tool",
                "meta": { "path": "/account/settings/notification", "component": "accountSettingsNotification", "keepAlive": true },
                "seq": 5,
                "state": 0,
                "children": []
              }]
            }]
          }, {
            "resId": 6,
            "parentId": 2,
            "type": 1,
            "resName": "其他组件",
            "resCode": "other",
            "icon": "slack",
            "meta": { "path": "/other", "redirect": "/other/icon-selector", "component": "RouteView", "keepAlive": true },
            "seq": 4,
            "state": 1,
            "children": [{
              "resId": 13,
              "parentId": 6,
              "type": 1,
              "resName": "图标选择",
              "resCode": "other_icon_selector",
              "icon": "tool",
              "meta": { "path": "/other/icon-selector", "component": "otherIconSelector", "keepAlive": true },
              "seq": 1,
              "state": 1,
              "children": []
            }]
          }, {
            "resId": 7,
            "parentId": 3,
            "type": 1,
            "resName": "资源管理",
            "resCode": "system_resource",
            "icon": "tool",
            "meta": { "path": "/system/resource", "component": "systemResource", "keepAlive": true },
            "seq": 1,
            "state": 1,
            "children": []
          }, {
            "resId": 8,
            "parentId": 4,
            "type": 1,
            "resName": "分析页",
            "resCode": "dashboard_analysis",
            "icon": "bars",
            "meta": { "path": "/dashboard/analysis", "component": "dashboardAnalysis", "keepAlive": true },
            "seq": 1,
            "state": 1,
            "children": []
          }, {
            "resId": 9,
            "parentId": 4,
            "type": 1,
            "resName": "监控页",
            "resCode": "monitor",
            "icon": "tool",
            "meta": { "path": "https://www.hejinyo.cn/", "target": "_blank", "keepAlive": false },
            "seq": 2,
            "state": 1,
            "children": []
          }, {
            "resId": 10,
            "parentId": 4,
            "type": 1,
            "resName": "工作台",
            "resCode": "dashboard_workplace",
            "icon": "tool",
            "meta": { "path": "/dashboard/workplace", "component": "dashboardWorkplace", "keepAlive": true },
            "seq": 3,
            "state": 1,
            "children": []
          }, {
            "resId": 11,
            "parentId": 5,
            "type": 1,
            "resName": "个人中心",
            "resCode": "account_center",
            "icon": "bars",
            "meta": { "path": "/account/center", "component": "accountCenter", "keepAlive": true },
            "seq": 1,
            "state": 1,
            "children": []
          }, {
            "resId": 12,
            "parentId": 5,
            "type": 1,
            "resName": "个人设置",
            "resCode": "account_settings",
            "icon": "tool",
            "meta": { "path": "/account/settings", "component": "accountSettings", "keepAlive": false, "hideHeader": true, "hideChildrenInMenu": true },
            "seq": 2,
            "state": 1,
            "children": [{
              "resId": 14,
              "parentId": 12,
              "type": 1,
              "resName": "基本设置",
              "resCode": "account_settings_base",
              "icon": "tool",
              "meta": { "path": "/account/settings/base", "component": "accountSettingsBase", "keepAlive": true },
              "seq": 1,
              "state": 1,
              "children": []
            }, {
              "resId": 15,
              "parentId": 12,
              "type": 1,
              "resName": "安全设置",
              "resCode": "account_settings_security",
              "icon": "tool",
              "meta": { "path": "/account/settings/security", "component": "accountSettingsSecurity", "keepAlive": true },
              "seq": 2,
              "state": 1,
              "children": []
            }, {
              "resId": 16,
              "parentId": 12,
              "type": 1,
              "resName": "个性化设置",
              "resCode": "account_settings_custom",
              "icon": "tool",
              "meta": { "path": "/account/settings/custom", "component": "accountSettingsCustom", "keepAlive": true },
              "seq": 3,
              "state": 1,
              "children": []
            }, {
              "resId": 17,
              "parentId": 12,
              "type": 1,
              "resName": "账户绑定",
              "resCode": "account_settings_binding",
              "icon": "tool",
              "meta": { "path": "/account/settings/binding", "component": "accountSettingsBinding", "keepAlive": true },
              "seq": 4,
              "state": 1,
              "children": []
            }, {
              "resId": 18,
              "parentId": 12,
              "type": 1,
              "resName": "新消息通知",
              "resCode": "account_settings_notification",
              "icon": "tool",
              "meta": { "path": "/account/settings/notification", "component": "accountSettingsNotification", "keepAlive": true },
              "seq": 5,
              "state": 0,
              "children": []
            }]
          }, {
            "resId": 13,
            "parentId": 6,
            "type": 1,
            "resName": "图标选择",
            "resCode": "other_icon_selector",
            "icon": "tool",
            "meta": { "path": "/other/icon-selector", "component": "otherIconSelector", "keepAlive": true },
            "seq": 1,
            "state": 1,
            "children": []
          }, {
            "resId": 14,
            "parentId": 12,
            "type": 1,
            "resName": "基本设置",
            "resCode": "account_settings_base",
            "icon": "tool",
            "meta": { "path": "/account/settings/base", "component": "accountSettingsBase", "keepAlive": true },
            "seq": 1,
            "state": 1,
            "children": []
          }, {
            "resId": 15,
            "parentId": 12,
            "type": 1,
            "resName": "安全设置",
            "resCode": "account_settings_security",
            "icon": "tool",
            "meta": { "path": "/account/settings/security", "component": "accountSettingsSecurity", "keepAlive": true },
            "seq": 2,
            "state": 1,
            "children": []
          }, {
            "resId": 16,
            "parentId": 12,
            "type": 1,
            "resName": "个性化设置",
            "resCode": "account_settings_custom",
            "icon": "tool",
            "meta": { "path": "/account/settings/custom", "component": "accountSettingsCustom", "keepAlive": true },
            "seq": 3,
            "state": 1,
            "children": []
          }, {
            "resId": 17,
            "parentId": 12,
            "type": 1,
            "resName": "账户绑定",
            "resCode": "account_settings_binding",
            "icon": "tool",
            "meta": { "path": "/account/settings/binding", "component": "accountSettingsBinding", "keepAlive": true },
            "seq": 4,
            "state": 1,
            "children": []
          }, {
            "resId": 18,
            "parentId": 12,
            "type": 1,
            "resName": "新消息通知",
            "resCode": "account_settings_notification",
            "icon": "tool",
            "meta": { "path": "/account/settings/notification", "component": "accountSettingsNotification", "keepAlive": true },
            "seq": 5,
            "state": 0,
            "children": []
          }]
      }
    },
    mounted () {
      // 保证完全挂载
      this.$nextTick(function () {
        // 滚动条置顶，因为我拿不到滚动条高度
        window.scroll(0, 0)
        let permInstance = this.$refs.permInstance.$el.getBoundingClientRect()
        let permTable = this.$refs.permTable.$el.getBoundingClientRect()
        this.differenceHigh = permTable.top + permInstance.bottom - permTable.bottom
      })
    },
    methods: {},
  }
</script>