<template>
  <a-tree
    :treeData="resTeeData"
    autoExpandParent
    showLine
    defaultExpandAll
    :defaultSelectedKeys="['0-0-0']"
  >
    <template slot="title" slot-scope="{title}">
      1{title}
    </template>
    <a-icon slot="smile" type="smile-o"/>
    <a-icon slot="meh" type="smile-o"/>
    <template slot="custom" slot-scope="{selected}">
      <a-icon :type="selected ? 'frown':'frown-o'"/>
    </template>
  </a-tree>
</template>

<script>
  import { getOperateTree } from '@/api/sys-resource'


  export default {
    name: 'sys-resource',
    components: {},
    computed: {},
    data () {
      return {
        resTeeData: []
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
          console.log(res)
          const { result, code, msg } = res
          if (code === 1) {
            console.log(result)
            this.resTeeData = result && result.tree
          } else {
            this.resTeeData = [{
              title: 'parent 1',
              key: '0-0',
              slots: {
                icon: 'smile',
              },
              children: [
                { title: 'leaf', key: '0-0-0', slots: { icon: 'meh' } },
                { title: 'leaf', key: '0-0-1', scopedSlots: { icon: 'custom' } }],
            }]
          }
        })

      },
      onSelect (selectedKeys, info) {
        console.log('selected', selectedKeys, info)
      },
      onCheck (checkedKeys, info) {
        console.log('onCheck', checkedKeys, info)
      },
    },
  }
</script>
