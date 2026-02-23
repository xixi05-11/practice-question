// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 POST /question/comment/add */
export async function addComment(
  body: API.QuestionCommentAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong>('/question/comment/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /question/comment/get */
export async function getComment(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommentParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseQuestionCommentVO>('/question/comment/get', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /question/comment/get/children/list */
export async function getChildrenList(
  body: API.QuestionCommentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionCommentVO>('/question/comment/get/children/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /question/comment/get/list/vo */
export async function getCommentList(
  body: API.QuestionCommentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageQuestionCommentVO>('/question/comment/get/list/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
