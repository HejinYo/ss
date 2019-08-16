<template>
  <div>
    <a-card ref="permInstance">
      <span slot="title">
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
            <a-select v-model="queryModel.key" defaultValue="permName" style="width: 20%">
              <a-select-option value="permName">名称</a-select-option>
              <a-select-option value="permCode">权限</a-select-option>
              <a-select-option value="code">编码</a-select-option>
            </a-select>
            <a-input-search v-model="queryModel.value" @search="loadPermissionData" style="width: 80%"
                            laceholder="查询..." :maxlength="32">
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
                    v-loading="listLoading"
                    :data="permListData" :height="clientHeight - differenceHigh">
            <el-table-column prop="permId" label="编号" sortable="custom" align="center" width="70"></el-table-column>
            <el-table-column prop="permName" label="名称" sortable="custom" align="center" width="150"></el-table-column>
            <el-table-column prop="permCode" label="权限" sortable="custom" align="center" width="80"></el-table-column>
            <el-table-column prop="code" label="编码" sortable="custom" align="code" min-width="150"></el-table-column>
            <el-table-column prop="state" label="状态" sortable="custom" align="center" width="90">
              <template slot-scope="scope">
                <a-tag :color="scope.row.state === 1 ? '#2db7f5': '#bbbbbb' ">
                  {{scope.row.state === 1 ? '正常' : '禁用'}}
                </a-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" sortable="custom" label="创建日期" align="center"
                             width="230"></el-table-column>
          </el-table>
        </div>

      </div>
      <br/>
      <!--翻页工具条-->
      <a-pagination showSizeChanger size="small"
                    @change="pageNumChange"
                    @showSizeChange="pageSizeChange"
                    :total="pageParam.total"
                    :pageSize="pageParam.pageSize">
      </a-pagination>
    </a-card>
  </div>

</template>

<script>
import { pageList } from '@/api/sys-permission'

export default {
  name: 'sys-permission',
  components: {},
  computed: {},
  props: {
    clientHeight: {
      type: Number
    },
    treeNodeResId: {
      type: Number
    }
  },
  data () {
    return {
      // 差额高
      differenceHigh: 0,
      // 表格加载
      listLoading: false,
      // 表格数据
      permListData: [],
      // 分页查询参数
      pageParam: {
        total: 0,
        pageNum: 1,
        pageSize: 10,
        sidx: null,
        sort: null
      },
      // 查询参数
      queryModel: { key: 'permName', value: null }
    }
  },
  watch: {
    treeNodeResId (data) {
      this.loadPermissionData()
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
      this.loadPermissionData()
    })
  },
  methods: {
    // 加载权限表格数据
    loadPermissionData () {
      this.listLoading = true
      this.pageParam.queryParam = { [this.queryModel.key]: this.queryModel.value, resId: this.treeNodeResId }
      const param = { ...this.pageParam }
      pageList(param).then(res => {
        let { result } = res
        this.permListData = result.list
        this.pageParam.total = result.total
        setTimeout(() => {
          this.listLoading = false
        }, 100)
      })
    },
    // 翻页
    pageNumChange (page, pageSize) {
      this.pageParam.pageNum = page
      this.loadPermissionData()
    },
    // 分页大小
    pageSizeChange (current, size) {
      this.pageParam.pageSize = size
      this.loadPermissionData()
    }
  }
}
</script>

<style scoped>

  .ss-permission-page {
    position: absolute;
    right: 2%;
    bottom: 10px;
  }
</style>
