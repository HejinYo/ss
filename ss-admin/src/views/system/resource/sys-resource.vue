<template>
  <div>
    <a-card title="资源树" :bordered="false" style="width: 300px">
      <el-tree :data="resTeeData" node-key="resId" default-expand-all>
        <span class="ss-tree-title" slot-scope="{ node, data }">
          <a-icon :type="data.icon"/>
          <span>{{data.resName}} </span>
          <span class="ss-tree-optional">
            <a-icon type="ellipsis" spin/>
          </span>
        </span>
      </el-tree>
    </a-card>
  </div>
</template>

<script>
  import { getOperateTree } from '@/api/sys-resource'

  export default {
    name: 'sys-resource',
    components: {},
    computed: {},
    data () {
      return {
        resTeeData: [],
        defaultProps: {
          children: 'children',
          label: 'resName'
        }
      }
    },
    mounted () {
      this.$nextTick(function () {
        // 保证完全挂载
        this.loadResTreeData()
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
    },
  }
</script>

<style scoped lang="less">

  .ss-tree-title:hover {
    .ss-tree-optional {
      display: inline;
    }
  }

  .ss-tree-optional {
    display: none;
  }
</style>