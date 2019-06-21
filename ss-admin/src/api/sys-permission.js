import { axios } from '@/utils/request'

const JELLY_SERVER = '/SS-JELLY-SERVER'

// 获取权限表格数据
export function pageList (data) {
  return axios({
    url: `${JELLY_SERVER}/permission/pageList`,
    method: 'post',
    data: data
  })
}
