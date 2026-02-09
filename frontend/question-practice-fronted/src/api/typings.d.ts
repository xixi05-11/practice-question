declare namespace API {
  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseListQuestionPracticeVO = {
    code?: number
    data?: QuestionPracticeVO[]
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

  type BaseResponsePageQuestionPracticeVO = {
    code?: number
    data?: PageQuestionPracticeVO
    message?: string
  }

  type BaseResponsePageQuestionVO = {
    code?: number
    data?: PageQuestionVO
    message?: string
  }

  type BaseResponseQuestionBankVO = {
    code?: number
    data?: QuestionBankVO
    message?: string
  }

  type BaseResponseQuestionPracticeCheckResultVO = {
    code?: number
    data?: QuestionPracticeCheckResultVO
    message?: string
  }

  type BaseResponseQuestionPracticeVO = {
    code?: number
    data?: QuestionPracticeVO
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

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type BaseResponseVoid = {
    code?: number
    data?: Record<string, any>
    message?: string
  }

  type cancelThumbParams = {
    questionId: number
  }

  type changeEmailParams = {
    newEmail: string
  }

  type doThumbParams = {
    questionId: number
  }

  type EditQuestionStatusRequest = {
    questionId?: number
    status?: number
  }

  type EmailRequest = {
    email?: string
    emailCode?: string
    password?: string
    checkPassword?: string
  }

  type getQuestionBankByIdParams = {
    id: number
  }

  type getQuestionPracticeByIdParams = {
    id: number
  }

  type getQuestionPracticeListByQuestionIdParams = {
    questionId: number
  }

  type getQuestionVOByIdParams = {
    id: number
  }

  type getUserVOByIdParams = {
    id: number
  }

  type isThumbParams = {
    questionId: number
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

  type PageQuestionPracticeVO = {
    records?: QuestionPracticeVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageQuestionPracticeVO
    searchCount?: PageQuestionPracticeVO
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

  type QuestionPracticeCheckResultVO = {
    questionPracticeId?: number
    isCorrect?: boolean
    correctAnswer?: string
    userAnswer?: string
    explanation?: string
  }

  type QuestionPracticeQueryRequest = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    searchText?: string
    id?: number
    questionId?: number
    type?: number
    content?: string
    startCreateTime?: string
    endCreateTime?: string
  }

  type QuestionPracticeSubmitRequest = {
    id?: number
    userAnswer?: string
  }

  type QuestionPracticeVO = {
    id?: number
    questionId?: number
    type?: number
    content?: string
    options?: string
    createTime?: string
    updateTime?: string
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
    difficulty?: string
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
    hasThumb?: boolean
    createTime?: string
    updateTime?: string
  }

  type sendEmailParams = {
    email: string
    operation: string
  }

  type UserChangePwdRequest = {
    id?: number
    oldPassword?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserEditRequest = {
    userName?: string
    profile?: string
    avatar?: string
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
