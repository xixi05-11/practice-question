// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /admin/user/delete */
export async function deleteUser(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/admin/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /admin/user/get */
export async function getUserById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUser>('/admin/user/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /admin/user/get/login */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserVO>('/admin/user/get/login', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/user/list/page/vo */
export async function listUserVoByPage(
  body: API.UserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUserVO>('/admin/user/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/user/login */
export async function login(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.loginParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserVO>('/admin/user/login', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/user/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/admin/user/logout', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/user/resetPwd */
export async function resetPassword(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.resetPasswordParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/admin/user/resetPwd', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/user/update */
export async function updateUser(body: API.UserUpdateRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/admin/user/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
