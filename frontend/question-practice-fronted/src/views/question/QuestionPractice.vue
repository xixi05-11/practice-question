<template>
  <div class="practice-container">
    <a-empty v-if="!questionPracticeList || questionPracticeList.length === 0" description="暂无练习题" />
    <div v-else>
      <div v-for="(practice) in questionPracticeList" :key="practice.id" class="practice-item">
        <div class="practice-header">
          <a-typography-title :level="4" class="practice-title">
            <a-tag :color="getTypeColor(practice.type)" class="type-tag">{{ getTypeName(practice.type) }}</a-tag>
            {{ practice.title }}
          </a-typography-title>
        </div>
        <a-typography-paragraph class="practice-content">
          {{ practice.content }}
        </a-typography-paragraph>
        <div v-if="practice.options" class="options-container">
          <div v-for="(option, optKey) in parseOptions(practice.options)" :key="optKey" class="option-item" :class="getOptionClass(practice.id, optKey, practice.type)" @click="handleOptionClick(practice.id, practice.type, optKey)">
            <a-radio v-if="practice.type === 0" class="option-radio" :checked="userAnswers[practice.id] === optKey">{{ optKey }}. {{ option }}</a-radio>
            <a-checkbox v-else-if="practice.type === 1" class="option-checkbox" :checked="Array.isArray(userAnswers[practice.id]) && (userAnswers[practice.id] as string[]).includes(optKey)">{{ optKey }}. {{ option }}</a-checkbox>
            <a-radio v-else-if="practice.type === 2" class="option-radio" :checked="userAnswers[practice.id] === optKey">{{ optKey }}. {{ option }}</a-radio>
          </div>
        </div>
        <div class="practice-footer">
          <a-button v-if="!submittedPractices[String(practice.id)]" type="primary" @click="handleSubmitAnswer(practice.id, practice.type)">提交答案</a-button>
          <a-tag v-else :color="submittedPractices[String(practice.id)].isCorrect ? 'success' : 'error'" class="result-tag">{{ submittedPractices[String(practice.id)].isCorrect ? '回答正确' : '回答错误' }}</a-tag>
        </div>
        <div v-if="submittedPractices[String(practice.id)]?.explanation" class="explanation-section">
          <div class="explanation-title">题目解析</div>
          <a-typography-paragraph class="explanation-content">
            {{ submittedPractices[String(practice.id)].explanation }}
          </a-typography-paragraph>
        </div>
      </div>
      <div v-if="questionPracticeList && questionPracticeList.length > 0" class="reset-section">
        <a-button type="default" @click="handleReset" :disabled="Object.keys(submittedPractices).length === 0">再测一次</a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { checkAnswer } from '@/api/questionPracticeController.ts'

interface Props {
  questionPracticeList: API.QuestionPracticeVO[]
}

defineProps<Props>()
const userAnswers = ref<Record<string, string | string[]>>({})
const submittedPractices = ref<Record<string, API.QuestionPracticeCheckResultVO>>({})

const getTypeName = (type: number) => {
  const typeMap = {
    0: '单选题',
    1: '多选题',
    2: '判断题'
  }
  return typeMap[type] || '未知类型'
}

const getTypeColor = (type: number) => {
  const colorMap = {
    0: 'blue',
    1: 'orange',
    2: 'green'
  }
  return colorMap[type] || 'default'
}

const parseOptions = (options: string) => {
  try {
    const parsed = JSON.parse(options)
    if (Array.isArray(parsed)) {
      const result: Record<string, string> = {}
      parsed.forEach((item: any) => {
        if (item.key && item.text) {
          result[item.key] = item.text
        }
      })
      return result
    }
    return parsed
  } catch (error) {
    console.error('解析options失败:', error)
    return {}
  }
}

const handleOptionClick = (practiceId: number, type: number, optionKey: string) => {
  const practiceIdStr = String(practiceId)
  if (submittedPractices.value[practiceIdStr]) return
  if (type === 0 || type === 2) {
    const currentAnswer = userAnswers.value[practiceIdStr] as string
    if (currentAnswer === optionKey) {
      userAnswers.value[practiceIdStr] = ''
    } else {
      userAnswers.value[practiceIdStr] = optionKey
    }
  } else if (type === 1) {
    const currentAnswers = userAnswers.value[practiceIdStr] as string[] || []
    const index = currentAnswers.indexOf(optionKey)
    if (index > -1) {
      userAnswers.value[practiceIdStr] = currentAnswers.filter((_, i) => i !== index)
    } else {
      userAnswers.value[practiceIdStr] = [...currentAnswers, optionKey]
    }
  }
}

const getOptionClass = (practiceId: number, optionKey: string, type: number) => {
  const practiceIdStr = String(practiceId)
  const submitted = submittedPractices.value[practiceIdStr]
  if (!submitted) return ''

  const correctOptions = submitted.correctAnswer.split(',').map((opt: string) => opt.trim())
  const isCorrectOption = correctOptions.includes(optionKey)

  if (type === 0 || type === 2) {
    const userAnswer = userAnswers.value[practiceIdStr] as string
    if (submitted.isCorrect && isCorrectOption) {
      return 'option-correct'
    } else if (!submitted.isCorrect && userAnswer === optionKey) {
      return 'option-wrong'
    } else if (!submitted.isCorrect && isCorrectOption) {
      return 'option-correct'
    }
  } else if (type === 1) {
    const currentUserAnswers = userAnswers.value[practiceIdStr] as string[] || []
    if (submitted.isCorrect) {
      if (isCorrectOption) {
        return 'option-correct'
      }
    } else if (!submitted.isCorrect) {
      if (currentUserAnswers.includes(optionKey) && !isCorrectOption) {
        return 'option-wrong'
      } else if (isCorrectOption) {
        return 'option-correct'
      }
    }
  }
  return ''
}

const handleSubmitAnswer = async (practiceId: number, type: number) => {
  const practiceIdStr = String(practiceId)
  const userAnswer = userAnswers.value[practiceIdStr]
  if (!userAnswer || (Array.isArray(userAnswer) && userAnswer.length === 0)) {
    message.warning('请先选择答案')
    return
  }

  const submitData: API.QuestionPracticeSubmitRequest = {
    id: practiceId,
    type: type,
    userAnswer: Array.isArray(userAnswer) ? userAnswer.join(',') : userAnswer
  }

  const res = await checkAnswer(submitData)
  if (res.data.code === 0 && res.data.data) {
    submittedPractices.value = {
      ...submittedPractices.value,
      [practiceIdStr]: res.data.data
    }
    if (res.data.data.isCorrect) {
      message.success('回答正确')
    } else {
      message.error('回答错误')
    }
  } else {
    message.error(res.data.message || '提交失败')
  }
}

const handleReset = () => {
  userAnswers.value = {}
  submittedPractices.value = {}
  message.info('已重置，请重新作答')
}
</script>

<style scoped>
.practice-container {
  min-height: 200px;
  padding: 16px 0;
}

.practice-item {
  background-color: #fafafa;
  padding: 16px;
  margin-bottom: 16px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.practice-item:last-child {
  margin-bottom: 0;
}

.practice-header {
  margin-bottom: 12px;
}

.practice-title {
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.type-tag {
  flex-shrink: 0;
}

.practice-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 16px;
}

.options-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background-color: #fff;
  border-radius: 6px;
  border: 1px solid #d9d9d9;
  transition: all 0.3s;
}

.option-item:hover {
  border-color: #1890ff;
  background-color: #f0f7ff;
  cursor: pointer;
}

.option-item.option-correct {
  border-color: #52c41a;
  background-color: #f6ffed;
}

.option-item.option-correct .option-radio,
.option-item.option-correct .option-checkbox {
  color: #52c41a;
}

.option-item.option-correct .option-radio :deep(.ant-radio-wrapper),
.option-item.option-correct .option-checkbox :deep(.ant-checkbox-wrapper) {
  color: #52c41a;
}

.option-item.option-wrong {
  border-color: #ff4d4f;
  background-color: #fff1f0;
}

.option-item.option-wrong .option-radio,
.option-item.option-wrong .option-checkbox {
  color: #ff4d4f;
}

.option-item.option-wrong .option-radio :deep(.ant-radio-wrapper),
.option-item.option-wrong .option-checkbox :deep(.ant-checkbox-wrapper) {
  color: #ff4d4f;
}

.option-radio,
.option-checkbox {
  margin: 0;
}

.practice-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.result-tag {
  font-size: 16px;
  padding: 4px 12px;
}

.reset-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  justify-content: center;
}

.explanation-section {
  margin-top: 16px;
  padding: 16px;
  background-color: #f0f9ff;
  border-left: 4px solid #1890ff;
  border-radius: 4px;
}

.explanation-title {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 8px;
}

.explanation-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 0;
}
</style>
