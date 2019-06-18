import { axios } from '@/utils/request'

const JELLY_SERVER = '/SS-JELLY-SERVER'

// 获取资源操作树数据
export function getOperateTree () {
  return axios({
    url: `${JELLY_SERVER}/resource/operateTree`,
    method: 'get'
  })
}
