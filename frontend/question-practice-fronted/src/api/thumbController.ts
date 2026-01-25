// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /thumb/cancelThumb */
export async function cancelThumb(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.cancelThumbParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/thumb/cancelThumb', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /thumb/doThumb */
export async function doThumb(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.doThumbParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/thumb/doThumb', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /thumb/isThumb */
export async function isThumb(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isThumbParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/thumb/isThumb', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
