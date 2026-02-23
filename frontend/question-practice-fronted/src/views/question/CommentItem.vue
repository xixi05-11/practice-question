<template>
  <div class="comment-item-container">
    <div class="comment-main">
      <a-avatar :size="40" :src="comment.userVO?.avatar" class="comment-avatar">
        {{ comment.userVO?.userName?.charAt(0) || 'U' }}
      </a-avatar>
      <div class="comment-content-wrapper">
        <div class="comment-user">
          <span class="user-name">{{ comment.userVO?.userName || '匿名用户' }}</span>
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
        </div>
        <div class="comment-text">{{ comment.content }}</div>
        <div class="comment-actions">
          <a-button type="text" size="small" @click="handleThumb" :class="{ 'has-thumb': comment.hasThumb }">
            <template #icon>
              <LikeOutlined />
            </template>
            {{ comment.thumbNum || 0 }}
          </a-button>
          <a-button type="text" size="small" @click="handleReply">
            <template #icon>
              <MessageOutlined />
            </template>
            回复
          </a-button>
        </div>
      </div>
    </div>

    <div v-if="comment.children && comment.children.length > 0" class="comment-children">
      <div v-for="(child, index) in displayChildren" :key="child.id || index" class="child-comment">
        <a-avatar :size="32" :src="child.userVO?.avatar" class="child-avatar">
          {{ child.userVO?.userName?.charAt(0) || 'U' }}
        </a-avatar>
        <div class="child-content-wrapper">
          <div class="child-user">
            <span class="user-name">{{ child.userVO?.userName || '匿名用户' }}</span>
            <span v-if="child.replyUserVO" class="reply-info">
              回复 <span class="reply-user">{{ child.replyUserVO?.userName || '匿名用户' }}</span>
            </span>
            <span class="comment-time">{{ formatTime(child.createTime) }}</span>
          </div>
          <div class="child-text">{{ child.content }}</div>
          <div class="child-actions">
            <a-button type="text" size="small" @click="handleChildThumb(child)">
              <template #icon>
                <LikeOutlined />
              </template>
              {{ child.thumbNum || 0 }}
            </a-button>
            <a-button type="text" size="small" @click="handleReplyChild(child)">
              <template #icon>
                <MessageOutlined />
              </template>
              回复
            </a-button>
          </div>
        </div>
      </div>

      <div
        v-if="comment.childrenCount && comment.childrenCount > (comment.children?.length || 0)"
        class="load-more-children"
      >
        <a-button type="link" @click="handleLoadMoreChildren">
          查看全部 {{ comment.childrenCount }} 条回复
        </a-button>
      </div>
    </div>

    <a-modal
      v-model:open="replyVisible"
      title="发表回复"
      @ok="handleReplySubmit"
      @cancel="replyVisible = false"
      :confirm-loading="loading"
    >
      <a-textarea
        v-model:value="replyContent"
        :placeholder="replyPlaceholder"
        :auto-size="{ minRows: 4, maxRows: 6 }"
        show-count
        :maxlength="500"
      />
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { message } from 'ant-design-vue'
import { LikeOutlined, MessageOutlined } from '@ant-design/icons-vue'
import { addComment, getChildrenList } from '@/api/questionCommentController'

interface Props {
  comment: API.QuestionCommentVO
  questionId: number
}

const props = defineProps<Props>()

const emit = defineEmits(['reply', 'refresh'])

const replyVisible = ref(false)
const replyContent = ref('')
const loading = ref(false)
const replyingTo = ref<API.QuestionCommentVO | null>(null)

const displayChildren = computed(() => {
  return props.comment.children?.slice(0, 3) || []
})

const replyPlaceholder = computed(() => {
  if (replyingTo.value) {
    return `回复 ${replyingTo.value.userVO?.userName || '用户'}:`
  }
  return '发表回复:'
})

const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString()
}

const handleThumb = () => {
  message.info('点赞功能开发中')
}

const handleChildThumb = (child: API.QuestionCommentVO) => {
  message.info('点赞功能开发中')
}

const handleReply = () => {
  replyingTo.value = null
  replyContent.value = ''
  replyVisible.value = true
}

const handleReplyChild = (child: API.QuestionCommentVO) => {
  replyingTo.value = child
  replyContent.value = ''
  replyVisible.value = true
}

const handleReplySubmit = async () => {
  if (!replyContent.value.trim()) {
    message.warning('请输入回复内容')
    return
  }

  loading.value = true
  try {
    const res = await addComment({
      questionId: props.questionId,
      content: replyContent.value.trim(),
      parentId: props.comment.id,
      replyUserId: replyingTo.value?.userVO?.id,
    })
    if (res.data.code === 0) {
      message.success('回复发表成功')
      replyVisible.value = false
      replyContent.value = ''
      emit('refresh')
    } else {
      message.error(res.data.message || '回复发表失败')
    }
  } catch (error) {
    message.error('回复发表失败')
  } finally {
    loading.value = false
  }
}

const handleLoadMoreChildren = async () => {
  if (!props.comment.id) return

  loading.value = true
  try {
    const res = await getChildrenList({
      questionId: props.questionId,
      rootId: props.comment.id,
      childrenLimit: 100,
    })
    if (res.data.code === 0 && res.data.data) {
      props.comment.children = res.data.data.records || []
    } else {
      message.error('加载更多回复失败')
    }
  } catch (error) {
    message.error('加载更多回复失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.comment-item-container {
  padding: 12px 0;
}

.comment-main {
  display: flex;
  gap: 12px;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content-wrapper {
  flex: 1;
  min-width: 0;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  color: #333;
  line-height: 1.6;
  margin-bottom: 8px;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.comment-actions .ant-btn {
  padding: 0 8px;
  height: 28px;
  color: #666;
}

.comment-actions .ant-btn:hover {
  color: #1890ff;
}

.comment-actions .has-thumb {
  color: #1890ff;
}

.comment-children {
  margin-top: 12px;
  margin-left: 52px;
  padding: 12px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.child-comment {
  display: flex;
  gap: 10px;
  padding: 8px 0;
}

.child-comment:not(:last-child) {
  border-bottom: 1px solid #e8e8e8;
}

.child-avatar {
  flex-shrink: 0;
}

.child-content-wrapper {
  flex: 1;
  min-width: 0;
}

.child-user {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 4px;
  flex-wrap: wrap;
}

.reply-info {
  font-size: 13px;
  color: #666;
}

.reply-user {
  color: #1890ff;
  font-weight: 500;
}

.child-text {
  color: #555;
  line-height: 1.5;
  margin-bottom: 6px;
  font-size: 14px;
  word-break: break-word;
}

.child-actions {
  display: flex;
  gap: 8px;
}

.child-actions .ant-btn {
  padding: 0 6px;
  height: 24px;
  font-size: 12px;
  color: #999;
}

.child-actions .ant-btn:hover {
  color: #1890ff;
}

.load-more-children {
  padding: 8px 0 4px 0;
  text-align: center;
}

@media (max-width: 768px) {
  .comment-children {
    margin-left: 24px;
    padding: 8px;
  }

  .child-avatar {
    width: 28px;
    height: 28px;
    font-size: 12px;
  }

  .child-text {
    font-size: 13px;
  }
}
</style>
