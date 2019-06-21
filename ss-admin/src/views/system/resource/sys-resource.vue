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
                    <a-menu-item key="add" @click="test(data)">新增</a-menu-item>
                    <a-menu-item key="edit" @click="test(data)">修改</a-menu-item>
                    <a-menu-item key="delete" @click="test(data)">移除</a-menu-item>
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
      ])
    },
    data () {
      return {
        bodyStyle: { padding: 0 },
        differenceHigh: 0,
        resTeeData: [],
        treeNodeResId: null
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
      // 测试方法
      test (data) {
        console.log('操作按钮被点击.....', data)
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