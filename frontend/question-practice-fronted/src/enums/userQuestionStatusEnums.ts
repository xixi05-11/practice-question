export const UserQuestionStatusEnum = {
  NOT_DONE: { text: '未做', value: 0 },
  MASTERED: { text: '已掌握', value: 1 },
  LATER: { text: '稍后再刷', value: 2 },
  ERROR_PRONE: { text: '易错', value: 3 },
} as const
