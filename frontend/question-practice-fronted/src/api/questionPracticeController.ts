// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /questionPractice/check/answer */
export async function checkAnswer(
  body: API.QuestionPracticeSubmitRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionPracticeCheckResultVO>('/questionPractice/check/answer', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /questionPractice/get/id */
export async function getQuestionPracticeById(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionPracticeByIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionPracticeVO>('/questionPractice/get/id', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /questionPractice/get/list/vo */
export async function getQuestionPracticeList(
  body: API.QuestionPracticeQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionPracticeVO>('/questionPractice/get/list/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /questionPractice/list/by/question */
export async function getQuestionPracticeListByQuestionId(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getQuestionPracticeListByQuestionIdParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListQuestionPracticeVO>('/questionPractice/list/by/question', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
