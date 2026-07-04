<template>
  <div class="chefs">
    <div class="card">
      <h2>查询空闲厨师</h2>

      <!-- 日期选择器 -->
      <div class="date-selector">
        <label>选择日期：</label>
        <input
            type="date"
            v-model="selectedDate"
            @change="getChefs"
            :min="today"
        />
      </div>

      <button @click="getChefs" :disabled="loading">
        {{ loading ? '加载中...' : '查询空闲厨师' }}
      </button>

      <div v-if="chefList.length > 0" class="chef-list">
        <div
            v-for="chef in chefList"
            :key="chef.id"
            :class="['list-item', { active: selectedChef && selectedChef.id === chef.id }]"
            @click="selectChef(chef)"
        >
          <div class="chef-info">
            <strong>{{ chef.name }}</strong>
            <span class="skill">{{ chef.skill }}</span>
          </div>
          <div class="chef-price">
            <span class="price">¥{{ chef.price }}</span>
            <span class="unit">/次</span>
          </div>
        </div>
      </div>
      <div v-else-if="!loading" class="empty">
        暂无空闲厨师
      </div>

      <button v-if="selectedChef" @click="goToOrder" class="next-btn">
        选择该厨师，去下单 →
      </button>

      <button v-if="selectedChef" @click="showReviews" class="review-btn">
        查看该厨师评价
      </button>
    </div>
  </div>

  <!-- 评价弹窗 -->
  <div v-if="showReviewModal" class="modal" @click.self="closeReviewModal">
    <div class="modal-content">
      <div class="modal-header">
        <h3>{{ selectedChef?.name }} 的评价</h3>
        <span class="close" @click="closeReviewModal">&times;</span>
      </div>
      <div class="modal-body">
        <div v-if="reviewLoading" class="loading">加载中...</div>
        <div v-else-if="reviewList.length === 0" class="empty-review">暂无评价</div>
        <div v-else class="review-list">
          <div v-for="review in reviewList" :key="review.id" class="review-item">
            <div class="review-rating">
              <span v-for="i in 5" :key="i" :class="['star', { filled: i <= review.rating }]">★</span>
            </div>
            <div class="review-content">{{ review.content || '暂无评价内容' }}</div>
            <div class="review-time">{{ review.createTime || '-' }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChefList',
  data() {
    return {
      chefList: [],
      selectedChef: null,
      loading: false,
      selectedDate: '',  // 新增日期字段
      showReviewModal: false,
      reviewList: [],
      reviewLoading: false
    }
  },
  computed: {
    today() {
      // 返回今天的日期（格式：YYYY-MM-DD）
      return new Date().toISOString().split('T')[0]
    }
  },
  mounted() {
    // 初始化选中日期为今天
    this.selectedDate = this.today
    this.getChefs()
  },
  methods: {
    async getChefs() {
      this.loading = true

      try {
        // 根据选择的日期查询空闲厨师
        const date = this.selectedDate || this.today
        const res = await this.$axios.get(`/chef/idle/${date}`)
        this.logContent += `\n响应数据：${JSON.stringify(res.data, null, 2)}`

        if (res.data.code === 200) {
          this.chefList = res.data.data || []
          if (this.chefList.length > 0) {
            this.selectedChef = this.chefList[0]
          }
          // 后端已返回包含等级和技能的完整信息，无需额外请求
        }
      } catch (err) {
        console.error('获取厨师列表失败:', err)
      } finally {
        this.loading = false
      }
    },

    selectChef(chef) {
      this.selectedChef = chef
    },
    goToOrder() {
      if (this.selectedChef) {
        // 同时保存选中的日期
        localStorage.setItem('selectedChef', JSON.stringify(this.selectedChef))
        localStorage.setItem('selectedDate', this.selectedDate)
        this.$router.push('/order')
      }
    },

    async showReviews() {
      if (!this.selectedChef) return

      this.reviewLoading = true
      this.showReviewModal = true

      try {
        const res = await this.$axios.get(`/chef/${this.selectedChef.id}/reviews`)
        if (res.data.code === 200) {
          this.reviewList = res.data.data || []
        }
      } catch (err) {
        console.error('获取评价失败:', err)
        this.reviewList = []
      } finally {
        this.reviewLoading = false
      }
    },

    closeReviewModal() {
      this.showReviewModal = false
    },
    getAvatarUrl(avatar) {
      if (!avatar || avatar === '') return '/src/assets/chef-default.png'
      if (avatar.startsWith('http')) {
        return avatar
      }
      return `http://localhost:8080${avatar}`
    },
    handleAvatarError(event) {
      event.target.src = '/src/assets/chef-default.png'
    }
  }
}
</script>

<style scoped>
.chef-list {
  margin-top: 15px;
}
.chef-avatar {
  width: 80px;
  height: 80px;
  margin-bottom: 10px;
  border-radius: 50%;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}
.chef-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.chef-info {
  margin-bottom: 8px;
}
.chef-info strong {
  font-size: 16px;
  color: #333;
}
.skill {
  margin-left: 10px;
  color: #666;
  font-size: 13px;
}
.chef-price {
  text-align: right;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.unit {
  color: #999;
  font-size: 12px;
}
.next-btn {
  margin-top: 20px;
  background: #67c23a;
}
.next-btn:hover {
  background: #85ce61;
}
.review-btn {
  margin-top: 10px;
  background: #409eff;
}
.review-btn:hover {
  background: #66b1ff;
}
/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow: hidden;
}
.modal-header {
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h3 {
  margin: 0;
  color: #333;
}
.modal-header .close {
  font-size: 24px;
  cursor: pointer;
  color: #999;
}
.modal-body {
  padding: 20px;
  max-height: 60vh;
  overflow-y: auto;
}
.loading {
  text-align: center;
  color: #999;
}
.empty-review {
  text-align: center;
  color: #999;
  padding: 20px;
}
.review-list {
  margin-top: 10px;
}
.review-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}
.review-item:last-child {
  border-bottom: none;
}
.review-rating {
  margin-bottom: 10px;
}
.review-rating .star {
  font-size: 18px;
  color: #ddd;
}
.review-rating .star.filled {
  color: #ff9800;
}
.review-content {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}
.review-time {
  font-size: 12px;
  color: #999;
}
/* 日期选择器样式 */
.date-selector {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.date-selector label {
  font-size: 14px;
  color: #333;
}
.date-selector input[type="date"] {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background: #fff;
  cursor: pointer;
}
.date-selector input[type="date"]:focus {
  outline: none;
  border-color: #67c23a;
}
</style>