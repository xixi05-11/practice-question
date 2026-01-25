<template>
  <div id="home">
    <div class="home">
      <a-list
        :data-source="questionBankList"
        :grid="{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 4,
          xl: 5,
          xxl: 6,
        }"
      >
        <template #renderItem="{ item: bank }">
          <a-list-item>
            <RouterLink :to="`/bank/${bank.id}`">
              <a-card
                hoverable
                :bordered="false"
                style="width: 260px; height: 120px; overflow: hidden"
              >
                <a-row>
                  <!-- 左侧图片 -->
                  <a-col :span="8">
                    <a-image
                      :src="bank.coverUrl || '/default-cover.jpg'"
                      style="width: 100%; border-radius: 4px"
                      :preview="false"
                    />
                  </a-col>

                  <!-- 右侧文本 -->
                  <a-col :span="16" style="padding-left: 12px">
                    <p class="title">{{ bank.title }}</p>
                    <p class="desc">{{ bank.description }}</p>
                  </a-col>
                </a-row>
              </a-card>
            </RouterLink>
          </a-list-item>
        </template>
      </a-list>
    </div>
  </div>
</template>
<script setup lang="ts">
//获取题库列表
import { onMounted, reactive, ref } from 'vue'
import { getQuestionBankList } from '@/api/questionBankController.ts'
import { message } from 'ant-design-vue'

const questionBankList = ref<API.QuestionBankVO[]>([])

const formState = reactive<API.QuestionBankQueryRequest>({
  pageNum: 1,
  pageSize: 10,
  sortOrder: 'asc',
  sortField: 'createTime',
})

const getQuestionBank = async () => {
  const res = await getQuestionBankList(formState)
  if (res.data.code === 0 && res.data.data && res.data.data.records) {
    questionBankList.value = res.data.data.records
  } else {
    message.error('数据加载失败')
  }
}

onMounted(() => {
  getQuestionBank()
})
</script>
