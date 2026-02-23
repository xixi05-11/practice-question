<template>
  <div class="question-detail">
    <a-card :bordered="false">
      <a-space direction="vertical" :size="24" style="width: 100%">
        <div class="question-header">
          <a-space class="title-with-icon">
            <a-typography-title :level="2" class="question-title">
              {{ question?.title }}
              <CheckCircleOutlined
                v-if="question?.status === 1"
                style="color: green; font-size: 30px"
              />
              <QuestionCircleOutlined
                v-else-if="question?.status === 2"
                style="color: #c8a125; font-size: 30px"
              />
              <CloseCircleOutlined
                v-else-if="question?.status === 3"
                style="color: red; font-size: 30px"
              />
            </a-typography-title>
          </a-space>
          <a-space class="question-tags">
            <a-tag v-if="question?.difficulty === 0" color="green">简单</a-tag>
            <a-tag v-else-if="question?.difficulty === 1" color="orange">中等</a-tag>
            <a-tag v-else-if="question?.difficulty === 2" color="red">困难</a-tag>
            <a-tag v-for="(tag, index) in question?.tagList" :key="`tag-${index}`" color="blue">
              {{ tag }}
            </a-tag>
          </a-space>
        </div>

        <div class="question-opration">
          <a-space>
            <a-dropdown>
              <a class="mark-dropdown-btn" @click.prevent> <TagOutlined />标记</a>
              <template #overlay>
                <a-menu :selectedKeys="selectedQuestionStatus" @click="handleMenuClick">
                  <a-menu-item key="1">
                    <a><CheckCircleOutlined style="color: green; margin-right: 5px" /> 已掌握</a>
                  </a-menu-item>
                  <a-menu-item key="2">
                    <a
                      ><QuestionCircleOutlined
                        style="color: #c8a125; margin-right: 3px"
                      />稍后再刷</a
                    >
                  </a-menu-item>
                  <a-menu-item key="3">
                    <a><CloseCircleOutlined style="color: red; margin-right: 5px" />易错，未掌握</a>
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
        </div>
        <a-card :bordered="true" class="description-card">
          <a-typography-paragraph class="content-text">
            {{ question?.content }}
          </a-typography-paragraph>
        </a-card>

        <div class="tabs-header">
          <a-tabs v-model:activeKey="activeTab" class="answer-tabs">
            <a-tab-pane key="answer" tab="推荐答案" />
            <a-tab-pane key="test" tab="测试一下">
              <QuestionPractice :questionPracticeList="questionPracticeList" />
            </a-tab-pane>
          </a-tabs>
          <a-button v-if="activeTab === 'answer'" type="text" class="toggle-btn" @click="toggleAnswer">
            <template #icon>
              <EyeInvisibleOutlined v-if="answerVisible" />
              <EyeOutlined v-else />
            </template>
            {{ answerVisible ? '隐藏答案' : '显示答案' }}
          </a-button>
        </div>

        <a-card v-if="activeTab === 'answer' && answerVisible" :bordered="true" class="content-card">
          <a-typography-paragraph class="content-text">
            {{ question?.answer }}
          </a-typography-paragraph>
        </a-card>
        <a-card v-else-if="activeTab === 'answer' && !answerVisible" :bordered="true" class="content-card hidden-card">
          <a-empty description="答案已隐藏，点击右上角显示答案" />
        </a-card>

        <QuestionComment v-if="question?.id" :question-id="question.id" />
      </a-space>
    </a-card>
  </div>
</template>
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { editQuestionStatus, getQuestionVoById } from '@/api/questionController.ts'
import { getQuestionPracticeListByQuestionId } from '@/api/questionPracticeController.ts'
import { computed, onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  EyeOutlined,
  EyeInvisibleOutlined,
  TagOutlined,
  QuestionCircleOutlined,
  CloseCircleOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons-vue'
import QuestionPractice from './QuestionPractice.vue'
import QuestionComment from './QuestionComment.vue'

const route = useRoute()
const questionId = route.path.split('/')[2]
const question = ref<API.QuestionVO>()
const activeTab = ref('answer')
const answerVisible = ref(false)
const questionPracticeList = ref<API.QuestionPracticeVO[]>([])

const selectedQuestionStatus = computed(() => {
  if (!question.value) return []
  const status = question.value.status
  if (status === 1) return ['1']
  if (status === 2) return ['2']
  if (status === 3) return ['3']
  return []
})

const toggleAnswer = () => {
  answerVisible.value = !answerVisible.value
}

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

const getQuestionPracticeList = async () => {
  const res = await getQuestionPracticeListByQuestionId({
    questionId: Number(questionId),
  })
  if (res.data.code === 0 && res.data.data) {
    questionPracticeList.value = res.data.data
  } else {
    message.error(res.data.message)
  }
}

const handleMenuClick = async ({ key }: { key: string }) => {
  if (!question.value) return

  const clickedStatus = Number(key)
  const currentStatus = question.value.status

  const newStatus = clickedStatus === currentStatus ? 0 : clickedStatus

  const res = await editQuestionStatus({
    questionId: question.value.id,
    status: newStatus,
  })

  if (res.data.code === 0) {
    question.value.status = newStatus
    message.success(newStatus === 0 ? '已取消标记' : '标记已更新')
  } else {
    message.error(res.data.message)
  }
}
onMounted(() => {
  getQuestionDetail()
  getQuestionPracticeList()
})
</script>
<style scoped>
.question-detail {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
  background: #ffffff;
  min-height: 100vh;
  font-family:
    'PingFang SC',
    'Microsoft YaHei',
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    'Helvetica Neue',
    Arial,
    sans-serif;
}

.question-header {
  padding: 16px 0;
}

.title-with-icon {
  align-items: center;
}

.question-title {
  margin-bottom: 16px;
  margin-right: 12px;
}

.question-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.description-card {
  background-color: #fafafa;
  padding: 12px 16px;
}

.description-card :deep(.ant-card-head) {
  padding: 8px 0;
  min-height: auto;
}

.description-card :deep(.ant-card-head-title) {
  padding: 0;
}

.description-card :deep(.ant-card-body) {
  padding: 12px 0 0 0;
}

.description-card .content-text {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
  margin-bottom: 0;
}

.tabs-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.answer-tabs {
  flex: 1;
}

.answer-tabs :deep(.ant-tabs-nav) {
  margin-bottom: 0;
}

.toggle-btn {
  flex-shrink: 0;
  margin-left: 16px;
}

.content-card {
  background-color: #fafafa;
}

.content-card .content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 0;
}

.hidden-card {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-detail {
    padding: 16px;
  }

  .question-title {
    font-size: 20px;
  }

  .description-card .content-text {
    font-size: 13px;
  }

  .tabs-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .toggle-btn {
    margin-left: 0;
  }
}

.mark-dropdown-btn {
  color: #666;
  transition: color 0.3s;
}

.mark-dropdown-btn:hover {
  color: #1890ff;
}
</style>
