// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /question/get/id */
export async function getQuestionVoById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionVOByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionVO>('/question/get/id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /question/get/list/vo */
export async function getQuestionList(
  body: API.QuestionQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionVO>('/question/get/list/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /question/update/status */
export async function editQuestionStatus(
  body: API.EditQuestionStatusRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/question/update/status', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
