// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /questionBank/get/id */
export async function getQuestionBankById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionBankByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionBankVO>('/questionBank/get/id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /questionBank/get/list */
export async function getQuestionBankList(
  body: API.QuestionBankQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionBankVO>('/questionBank/get/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
