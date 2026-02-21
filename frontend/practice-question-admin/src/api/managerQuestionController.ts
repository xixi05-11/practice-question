// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /admin/question/add */
export async function addQuestion(body: API.QuestionAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/admin/question/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/question/delete */
export async function deleteQuestion(body: API.DeleteRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean>('/admin/question/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /admin/question/get */
export async function getQuestionById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionVO>('/admin/question/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/question/list/page/vo */
export async function listQuestionVoByPage(
  body: API.QuestionQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionVO>('/admin/question/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/question/update */
export async function updateQuestion(
  body: API.QuestionEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/admin/question/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
