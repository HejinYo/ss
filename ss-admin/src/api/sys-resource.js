import { axios } from '@/utils/request'

const JELLY_SERVER = '/SS-JELLY-SERVER'

// 获取资源操作树数据
export function getOperateTree () {
  return axios({
    url: `${JELLY_SERVER}/resource/operateTree`,
    method: 'get'
  })
}

// 添加資源
export function saveResource (data) {
  return axios({
    url: `${JELLY_SERVER}/resource`,
    method: 'post',
    data: data
  })
}

// 修改資源
export function updateResource (resId, data) {
  return axios({
    url: `${JELLY_SERVER}/resource/${resId}`,
    method: 'put',
    data: data
  })
}

// 删除資源
export function deleteResource (resId) {
  return axios({
    url: `${JELLY_SERVER}/resource/${resId}`,
    method: 'delete'
  })
}
