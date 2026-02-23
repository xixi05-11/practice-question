<template>
  <div class="question-comment">
    <a-card :bordered="false" class="comment-card">
      <a-space direction="vertical" :size="16" style="width: 100%">
        <div class="comment-header">
          <a-typography-title :level="4" class="comment-title">评论 ({{ totalCount }})</a-typography-title>
        </div>

        <div class="comment-input">
          <a-textarea
            v-model:value="commentContent"
            placeholder="写下你的评论..."
            :auto-size="{ minRows: 3, maxRows: 5 }"
            class="comment-textarea"
          />
          <a-button type="primary" @click="handleAddComment" :loading="loading" class="comment-btn">
            发表评论
          </a-button>
        </div>

        <a-spin :spinning="loading">
          <a-list :data-source="commentList" class="comment-list">
            <template #renderItem="{ item }">
              <a-list-item class="comment-item">
                <CommentItem
                  :comment="item"
                  :question-id="questionId"
                  @reply="handleReply"
                  @refresh="loadComments"
                />
              </a-list-item>
            </template>
          </a-list>

          <div v-if="commentList.length === 0 && !loading" class="empty-comment">
            <a-empty description="暂无评论，快来发表第一条评论吧！" />
          </div>

          <div v-if="hasMore" class="load-more">
            <a-button @click="loadMore" :loading="loadingMore">加载更多</a-button>
          </div>
        </a-spin>
      </a-space>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { addComment, getCommentList } from '@/api/questionCommentController'
import CommentItem from './CommentItem.vue'

interface Props {
  questionId: number
}

const props = defineProps<Props>()

const commentList = ref<API.QuestionCommentVO[]>([])
const commentContent = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalCount = ref(0)
const hasMore = computed(() => commentList.value.length < totalCount.value)

const loadComments = async (reset = true) => {
  if (reset) {
    currentPage.value = 1
    commentList.value = []
  }

  loading.value = true
  try {
    const res = await getCommentList({
      questionId: props.questionId,
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    })
    if (res.data.code === 0 && res.data.data) {
      const newData = res.data.data.records || []
      if (reset) {
        commentList.value = newData
      } else {
        commentList.value = [...commentList.value, ...newData]
      }
      totalCount.value = res.data.data.total || 0
    }
  } catch (error) {
    message.error('加载评论失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const loadMore = () => {
  currentPage.value++
  loadingMore.value = true
  loadComments(false)
}

const handleAddComment = async () => {
  if (!commentContent.value.trim()) {
    message.warning('请输入评论内容')
    return
  }

  loading.value = true
  try {
    const res = await addComment({
      questionId: props.questionId,
      content: commentContent.value.trim(),
    })
    if (res.data.code === 0) {
      message.success('评论发表成功')
      commentContent.value = ''
      loadComments()
    } else {
      message.error(res.data.message || '评论发表失败')
    }
  } catch (error) {
    message.error('评论发表失败')
  } finally {
    loading.value = false
  }
}

const handleReply = () => {
  loadComments()
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.question-comment {
  margin-top: 24px;
}

.comment-card {
  background-color: #fafafa;
}

.comment-header {
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
}

.comment-title {
  margin: 0;
}

.comment-input {
  padding: 16px 0;
  border-bottom: 1px solid #e8e8e8;
}

.comment-textarea {
  margin-bottom: 12px;
  border-radius: 8px;
}

.comment-btn {
  float: right;
}

.comment-list {
  padding: 16px 0;
}

.comment-item {
  padding: 12px 0;
  border-bottom: none;
}

.comment-item :deep(.ant-list-item) {
  border-bottom: none;
}

.empty-comment {
  padding: 40px 0;
  text-align: center;
}

.load-more {
  padding: 16px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .comment-title {
    font-size: 16px;
  }

  .comment-textarea {
    font-size: 14px;
  }
}
</style>
