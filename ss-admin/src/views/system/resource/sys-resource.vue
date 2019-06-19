<template>
  <div>
    <a-row>
      <a-col :span="6">
        <a-card title="资源管理">
          <el-tree :data="resTeeData" node-key="resId" default-expand-all
                   @node-click="treeNodeClick">
        <span class="ss-tree" slot-scope="{ node, data }">
          <span v-bind:style="{ color: (data.state!==1 ? 'red' : (data.meta.hideHeader?'#bbbebb': '')) }">
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
        </a-card>
      </a-col>
      <a-col :span="18">
        <a-card title="权限管理">
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
              <el-table stripe border highlight-current-row size="mini" element-loading-text="拼命加载中"
                        :data="resListData" height="350px">
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
      </a-col>

    </a-row>
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
        resListData: [],
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
            this.resListData = result && result.list
          } else {
            this.resTeeData = []
            this.resListData = []
          }
        })

      },
      // 树节点被点击
      treeNodeClick (data) {
        console.log("树节点被点击.....", data)
      },
      // 测试方法
      test (data) {
        console.log("操作按钮被点击.....", data)
      }
    },
  }
</script>

<style scoped lang="less">

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