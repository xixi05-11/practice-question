declare namespace API {
  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageQuestionBankVO = {
    code?: number
    data?: PageQuestionBankVO
    message?: string
  }

  type BaseResponsePageQuestionVO = {
    code?: number
    data?: PageQuestionVO
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponseQuestionBankVO = {
    code?: number
    data?: QuestionBankVO
    message?: string
  }

  type BaseResponseQuestionVO = {
    code?: number
    data?: QuestionVO
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type deleteQuestionBankParams = {
    id: number
  }

  type DeleteRequest = {
    id?: number
  }

  type getQuestionBankByIdParams = {
    id: number
  }

  type getQuestionByIdParams = {
    id: number
  }

  type getUserByIdParams = {
    id: number
  }

  type loginParams = {
    text: string
    password: string
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type PageQuestionBankVO = {
    records?: QuestionBankVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageQuestionBankVO
    searchCount?: PageQuestionBankVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageQuestionVO = {
    records?: QuestionVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageQuestionVO
    searchCount?: PageQuestionVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageUserVO = {
    records?: UserVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageUserVO
    searchCount?: PageUserVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type QuestionAddRequest = {
    title?: string
    content?: string
    tagList?: string[]
    answer?: string
    difficulty?: number
    questionBankIdList?: number[]
  }

  type QuestionBankAddRequest = {
    title?: string
    description?: string
    coverUrl?: string
  }

  type QuestionBankEditRequest = {
    id?: number
    title?: string
    description?: string
    coverUrl?: string
  }

  type QuestionBankQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    searchText?: string
    id?: number
    title?: string
    description?: string
    coverUrl?: string
    userId?: number
    startEditTime?: string
    endEditTime?: string
    startCreateTime?: string
    endCreateTime?: string
  }

  type QuestionBankVO = {
    id?: number
    title?: string
    description?: string
    coverUrl?: string
    userVO?: UserVO
    questionVOList?: QuestionVO[]
    createTime?: string
    updateTime?: string
  }

  type QuestionEditRequest = {
    id?: number
    title?: string
    content?: string
    tagList?: string[]
    answer?: string
    difficulty?: number
    questionBankIdList?: number[]
  }

  type QuestionQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    searchText?: string
    id?: number
    title?: string
    content?: string
    tagList?: string[]
    difficulty?: number
    thumbNum?: number
    userId?: number
    startEditTime?: string
    endEditTime?: string
    startCreateTime?: string
    endCreateTime?: string
    questionBankId?: number
  }

  type QuestionVO = {
    id?: number
    title?: string
    content?: string
    tagList?: string[]
    answer?: string
    difficulty?: number
    thumbNum?: number
    userVO?: UserVO
    status?: number
    questionBank?: string[]
    hasThumb?: boolean
    createTime?: string
    updateTime?: string
  }

  type resetPasswordParams = {
    id: number
  }

  type User = {
    id?: number
    userAccount?: string
    password?: string
    userName?: string
    email?: string
    avatar?: string
    profile?: string
    role?: string
    editTime?: string
    createTime?: string
    updateTime?: string
    isDelete?: number
  }

  type UserQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    searchText?: string
    id?: number
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
    email?: string
    startCreateTime?: string
    endCreateTime?: string
    startEditTime?: string
    endEditTime?: string
  }

  type UserUpdateRequest = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    email?: string
    userName?: string
    avatar?: string
    profile?: string
    role?: string
    createTime?: string
  }
}
