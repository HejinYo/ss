import { axios } from '@/utils/request'

export function getOperateTree () {
  return axios({
    url: '/resource/operateTree',
    method: 'get'
  })
}
