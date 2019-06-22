<template>
  <div>
    <a-row>
      <a-col :xs="12" :sm="10" :md="8" :lg="7" :xl="6" :xxl="5">
        <a-card title="资源管理" :bodyStyle="bodyStyle">
          <template class="ant-card-actions" slot="extra">
            <a>
              <a-icon type="ellipsis"></a-icon>
            </a>
          </template>
          <div :style="{ height: `${clientHeight - differenceHigh}px`,overflowY: 'auto'}">
            <el-tree ref="resTree" :data="resTeeData" node-key="resId" highlight-current :expand-on-click-node="false"
                     default-expand-all @node-click="treeNodeClick">
            <span class="ss-tree" slot-scope="{ node, data }">
              <span
                v-bind:style="{ color: (data.state!==1 ? '#e2b9b9' : (data.meta.hideHeader?'#bbbebb': '#1890ff')) }">
                <a-icon :type="data.icon"/>
                <span class="ss-tree-title">
                  {{data.resName}}
                </span>
              </span>
              <span class="ss-tree-optional">
                <a-dropdown>
                  <a class="btn" @click.stop=""><a-icon type="ellipsis"/></a>
                  <a-menu slot="overlay">
                    <a-menu-item key="add" @click="openModal(optTypeEnum.insert,data)">新增</a-menu-item>
                    <a-menu-item key="edit" @click="openModal(optTypeEnum.update,data)">修改</a-menu-item>
                    <a-menu-item key="delete" @click="openModal(optTypeEnum.delete,data)">删除</a-menu-item>
                  </a-menu>
                </a-dropdown>
              </span>
            </span>
            </el-tree>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="12" :sm="14" :md="16" :lg="17" :xl="18" :xxl="19">
        <sys-permission :client-height="clientHeight" :tree-node-res-id="treeNodeResId"></sys-permission>
      </a-col>
    </a-row>

    <a-modal :title="operationTitile" v-model="resourceVisible" destroyOnClose>
      <a-form>

        <a-form-item label="名称" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
          <a-input v-model="resModel.resName"/>
        </a-form-item>

        <a-form-item label="类型" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
          <a-input v-model="resModel.type"/>
        </a-form-item>

        <a-form-item label="编码" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
          <a-input v-model="resModel.resCode"/>
        </a-form-item>

        <a-form-item label="图标" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
          <a-input v-model="resModel.icon"/>
        </a-form-item>

        <a-divider>扩展</a-divider>
        <a-form-item v-for="(resMeta,index) in resMetaSelect" :key="index" :label="resMeta.label"
                     :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
          <a-input v-if="resMeta.type === 'string'" v-model="resMetaModel[resMeta.value]"/>
          <a-switch v-else-if="resMeta.type  === 'boolean'" v-model="resMetaModel[resMeta.value]" defaultChecked/>
        </a-form-item>

      </a-form>
    </a-modal>
  </div>
</template>

<script>
  import { getOperateTree } from '@/api/sys-resource'
  import sysPermission from './sys-permission'
  import { mapGetters } from 'vuex'

  export default {
    name: 'sys-resource',
    components: { sysPermission },
    computed: {
      ...mapGetters([
        'clientHeight'
      ]),
      // 操作类型标题
      operationTitile () {
        return this.operationType === this.optTypeEnum.update ? '修改资源' : '添加资源'
      }
    },
    data () {
      return {
        optTypeEnum: {
          insert: 1,
          update: 2,
          delete: 3,
        },
        formItemLayout: {
          labelCol: { span: 4 },
          wrapperCol: { span: 20 },
        },
        resMetaSelect: [
          {
            label: '路径',
            value: 'path',
            type: 'string'
          },
          {
            label: '重定向',
            value: 'redirect',
            type: 'string'
          },
          {
            label: '组件',
            value: 'component',
            type: 'string'
          },
          {
            label: 'target',
            value: 'target',
            type: 'string'
          },
          {
            label: 'keepAlive',
            value: 'keepAlive',
            type: 'boolean'
          },
          {
            label: '隐藏',
            value: 'hideHeader',
            type: 'boolean'
          },
          {
            label: '隐藏子菜单',
            value: 'hideChildrenInMenu',
            type: 'boolean'
          },
        ],
        // 树样式
        bodyStyle: { padding: 0 },
        // 高度差，计算书高度
        differenceHigh: 0,
        // 资源树数据
        resTeeData: [],
        // 点击资源节点的编号
        treeNodeResId: null,
        // 资源编辑弹出层
        resourceVisible: false,
        // 操作类型
        operationType: null,
        // 资源模型
        resModel: {},
        resMetaModel: {}
      }
    },
    mounted () {
      this.$nextTick(function () {
        // 保证完全挂载
        this.loadResTreeData()

        // 滚动条置顶，因为我拿不到滚动条高度
        window.scroll(0, 0)
        let resTree = this.$refs.resTree.$el.getBoundingClientRect()
        this.differenceHigh = resTree.top + 1
      })
    },
    methods: {
      // 加载资源树数据
      loadResTreeData () {
        getOperateTree().then(res => {
          const { result, code, msg } = res
          if (code === 1) {
            console.log(result)
            this.resTeeData = result && result.tree
          } else {
            this.resTeeData = []
          }
        })

      },
      // 树节点被点击
      treeNodeClick (data, node) {
        if (node.id !== 1) {
          this.treeNodeResId = data.resId
        } else {
          this.treeNodeResId = null
        }
      },
      // 打开弹出层
      openModal (type, data) {
        this.operationType = type
        switch (this.operationType) {
          case this.optTypeEnum.insert:
            this.resMetaModel = {}
            this.resModel = {}
            this.resourceVisible = true
            break
          case this.optTypeEnum.update:
            this.resMetaModel = { ...data.meta }
            this.resModel = { ...data }
            this.resourceVisible = true
            break
          case this.optTypeEnum.delete:
            break
          default:
        }
      }
    },
  }
</script>

<style scoped lang="less">
  .testcss {

  }

  .el-tree-node__content:hover {
    .ss-tree-optional {
      display: inline;
      position: absolute;
      right: 10px;
    }
  }

  .ss-tree-title {
    margin-left: 5px;
  }

  .ss-tree-optional {
    display: none;
  }
</style>