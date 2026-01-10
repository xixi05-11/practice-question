// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /user/avatar/upload */
export async function uploadAvatar(body: {}, options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/user/avatar/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/edit */
export async function editUser(body: API.UserEditRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/email/change */
export async function changeEmail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.changeEmailParams,
  body: API.EmailRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/user/email/change', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/email/login */
export async function loginByEmailCode(body: API.EmailRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseUserVO>('/user/email/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/email/resetPwd */
export async function resetPwdByEmailCode(
  body: API.EmailRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/user/email/resetPwd', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/email/send */
export async function sendEmail(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.sendEmailParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseVoid>('/user/email/send', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/get/login */
export async function getLoginUser(options?: { [key: string]: any }) {
  return request<API.BaseResponseUserVO>('/user/get/login', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /user/get/vo */
export async function getUserVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserVO>('/user/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/login */
export async function login(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.loginParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUserVO>('/user/login', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/logout', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/register */
export async function register(body: API.EmailRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /user/resetPwd */
export async function resetPwd(body: API.UserChangePwdRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/user/resetPwd', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
