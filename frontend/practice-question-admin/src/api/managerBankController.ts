// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /admin/bank/add */
export async function addQuestionBank(
  body: API.QuestionBankAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/admin/bank/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/bank/delete */
export async function deleteQuestionBank(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.deleteQuestionBankParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/admin/bank/delete', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/bank/edit */
export async function editQuestionBank(
  body: API.QuestionBankEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/admin/bank/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /admin/bank/get/id */
export async function getQuestionBankById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionBankByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionBankVO>('/admin/bank/get/id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/bank/get/list */
export async function getQuestionBankList(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBankVO>('/admin/bank/get/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /admin/bank/upload */
export async function uploadQuestionBank(body: FormData, options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/admin/bank/upload', {
    method: 'POST',
    data: body,
    ...(options || {}),
  })
}
