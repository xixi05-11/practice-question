<template>
  <div class="question-detail">
    <h1 class="question-title">{{ question?.title }}</h1>
    <div class="question-tag">
      <a-tag v-for="(tag, index) in question?.tagList" :key="`tag-${index}`">
        {{ tag }}
      </a-tag>
    </div>
    <div class="question-content">
      {{ question?.content }}
    </div>

    <div class="question-answer">
      <div class="answer-label">参考答案</div>
      <div class="answer-content">
        {{ question?.answer }}
      </div>

      <div class="answer-author">
        作者：
        <a-avatar :src="question?.userVO?.avatar || defaultAvatar" />
        {{ question?.userVO?.userName }}
      </div>
      <div class="answer-time">最后更新时间：{{ formatTime(question?.updateTime) }}</div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { getQuestionVoById } from '@/api/questionController.ts'
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import defaultAvatar from '@/assets/default-avatar.png'

// 格式化时间函数
const formatTime = (timestamp: string | Date): string => {
  // 添加参数验证
  if (!timestamp) {
    return '未知时间'
  }
  const date = new Date(timestamp)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}
const route = useRoute()
const questionId = route.path.split('/')[2]
const question = ref<API.QuestionVO>()
const getQuestionDetail = async () => {
  const res = await getQuestionVoById({
    id: Number(questionId),
  })
  if (res.data.code === 0 && res.data.data) {
    question.value = res.data.data
  } else {
    message.error(res.data.message)
  }
}

onMounted(() => {
  getQuestionDetail()
})
</script>
<style scoped>
.question-detail {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
}

/* 标题 */
.question-title {
  font-size: 26px;
  font-weight: 600;
  color: #1f1f1f;
  margin-bottom: 20px;
}

/* 题目内容 */
.question-content {
  padding: 10px;
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap; /* 保留换行 */
  margin-bottom: 28px;
}

/* 答案区域 */
.question-answer {
  padding: 16px 20px;
  background-color: #f6f8fa;
  border-left: 4px solid #1677ff;
  border-radius: 4px;
}

.answer-label {
  font-size: 14px;
  font-weight: 500;
  color: #1677ff;
  margin-bottom: 8px;
}

.answer-content {
  font-size: 15px;
  line-height: 1.7;
  color: #262626;
  white-space: pre-wrap;
}
</style>
