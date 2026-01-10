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

  type changeEmailParams = {
    newEmail: string
  }

  type EmailRequest = {
    email?: string
    emailCode?: string
    password?: string
    checkPassword?: string
  }

  type getUserVOByIdParams = {
    id: number
  }

  type loginParams = {
    text: string
    password: string
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
