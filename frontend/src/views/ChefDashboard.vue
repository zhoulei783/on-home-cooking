<template>
  <div class="chef-dashboard">
    <div class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-left">
          <h1>👨‍🍳 厨师后台</h1>
          <p>欢迎，{{ chef.name }}</p>
        </div>
        <div class="welcome-right">
          <div class="balance-card">
            <div class="balance-label">总收入</div>
            <div class="balance-amount">¥{{ chef.totalEarnings || 0 }}</div>
          </div>
          <div class="balance-card" style="margin-top: 10px;">
            <div class="balance-label">已发放</div>
            <div class="balance-amount">¥{{ chef.paidAmount || 0 }}</div>
          </div>
          <div class="balance-card" style="margin-top: 10px;">
            <div class="balance-label">剩余</div>
            <div class="balance-amount">¥{{ chef.remainingAmount || 0 }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 状态管理卡片 -->
    <div class="card status-card">
      <h3>📅 状态管理</h3>
      <div class="status-form">
        <div class="form-group">
          <label>选择日期：</label>
          <input
              type="date"
              v-model="statusDate"
              :min="today"
          />
        </div>
        <div class="form-group">
          <label>选择状态：</label>
          <select v-model="statusValue">
            <option :value="1">🔴 忙碌</option>
            <option :value="0">🟢 空闲</option>
          </select>
        </div>
        <button
            @click="updateStatus"
            :disabled="statusLoading"
            class="update-status-btn"
        >
          {{ statusLoading ? '保存中...' : '保存状态' }}
        </button>
      </div>
      <div v-if="statusMessage" :class="['status-message', statusMessageType]">
        {{ statusMessage }}
      </div>
    </div>

    <div class="card">
      <h3>我的订单</h3>
      <div v-if="orders.length === 0" class="empty">暂无订单</div>
      <div v-else class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-item">
          <div class="order-header">
            <span class="order-id">订单 #{{ order.id }}</span>
            <span :class="['status-badge', 'status-' + order.status]">{{ getStatusText(order.status) }}</span>
            <span class="order-no">编号：{{ order.orderNo }}</span>
          </div>
          <div class="order-detail">
            <p>📍 服务地址：{{ order.address }}</p>
            <p>⏰ 烹饪时间：{{ formatTime(order.cookingTime) }}</p>
            <p>👤 客户：{{ order.username }}</p>
            <p>📞 联系方式：{{ order.userPhone }}</p>
            <p>💰 服务费：¥{{ order.price }}</p>
          </div>
          <div v-if="order.dishes && order.dishes.length > 0" class="order-dishes">
            <h4>🍽 菜品：</h4>
            <div v-for="dish in order.dishes" :key="dish.id" class="dish-item">
              <span class="dish-name">{{ dish.dishName }}</span>
              <span class="dish-quantity">x{{ dish.quantity }}</span>
              <span class="dish-price">¥{{ dish.price }}</span>
            </div>
          </div>
          <!-- 评价信息 -->
          <div v-if="order.review" class="order-review">
            <h4>⭐ 客户评价：</h4>
            <div class="review-content">
              <div class="review-rating">
                <span
                    v-for="i in 5"
                    :key="i"
                    :class="['star', { active: i <= order.review.rating }]"
                >★</span>
              </div>
              <div v-if="order.review.content" class="review-text">{{ order.review.content }}</div>
              <div class="review-time">评价时间：{{ formatTime(order.review.createTime) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新订单通知弹窗 -->
    <div v-if="showNewOrderModal" class="notification-modal">
      <div class="modal-content">
        <div class="modal-icon">🔔</div>
        <div class="modal-title">您有新的订单！！</div>
        <div class="modal-order-info">
          <p>订单号：{{ newOrderNotification.orderNo }}</p>
          <p>服务地址：{{ newOrderNotification.address }}</p>
          <p>服务时间：{{ formatTime(newOrderNotification.cookingTime) }}</p>
        </div>
        <button @click="handleNewOrder" class="modal-btn">查看订单</button>
      </div>
    </div>

    <button @click="loadOrders" class="refresh-btn">🔄 刷新订单</button>

    <button @click="logout" class="logout-btn">退出登录</button>
  </div>
</template>

<script>
export default {
  name: 'ChefDashboard',
  data() {
    return {
      chef: {},
      orders: [],
      statusDate: '',
      statusValue: 1,
      statusLoading: false,
      statusMessage: '',
      statusMessageType: 'success',
      showNewOrderModal: false,
      newOrderNotification: {},
      notificationInterval: null
    }
  },
  computed: {
    completedOrdersTotal() {
      const total = this.orders
          .filter(order => order.status === 2)
          .reduce((total, order) => {
            const price = parseFloat(order.price) || 0
            return total + price
          }, 0)
      return (total * 0.9).toFixed(2)
    },
    today() {
      return new Date().toISOString().split('T')[0]
    }
  },
  mounted() {
    const chef = JSON.parse(localStorage.getItem('chef') || '{}')
    if (!chef.id) {
      this.$router.push('/chef-login')
      return
    }
    this.chef = chef
    this.statusDate = this.today
    this.loadOrders()
    // 定时检查新订单通知
    this.checkNewOrders()
    this.notificationInterval = setInterval(() => this.checkNewOrders(), 5000)
  },
  beforeUnmount() {
    if (this.notificationInterval) {
      clearInterval(this.notificationInterval)
    }
  },
  methods: {
    async loadOrders() {
      try {
        const chefRes = await this.$axios.get('/chef/' + this.chef.id)
        if (chefRes.data.code === 200) {
          this.chef = chefRes.data.data
        }

        const res = await this.$axios.get('/order/chef/' + this.chef.id)
        if (res.data.code === 200) {
          this.orders = res.data.data || []
          for (let order of this.orders) {
            if (order.id) {
              // 加载菜品信息
              try {
                const dishRes = await this.$axios.get('/order/items/' + order.id)
                if (dishRes.data.code === 200) {
                  order.dishes = dishRes.data.data || []
                }
              } catch (err) {
                console.error('加载菜品信息失败：', err)
                order.dishes = []
              }

              // 加载评价信息
              try {
                const reviewRes = await this.$axios.get('/review/by-order/' + order.id)
                if (reviewRes.data.code === 200 && reviewRes.data.data) {
                  order.review = reviewRes.data.data
                }
              } catch (err) {
                console.error('加载评价信息失败：', err)
                order.review = null
              }
            }
          }
        }
      } catch (err) {
        console.error(err)
      }
    },
    async updateStatus() {
      if (!this.statusDate || !this.chef.id) {
        this.showStatusMessage('请选择日期', 'error')
        return
      }

      this.statusLoading = true
      this.statusMessage = ''

      try {
        const res = await this.$axios.put(
            `/chef/status/${this.chef.id}/${this.statusDate}/${this.statusValue}`
        )

        if (res.data.code === 200) {
          this.showStatusMessage('状态更新成功！', 'success')
        } else {
          this.showStatusMessage('更新失败：' + (res.data.message || '未知错误'), 'error')
        }
      } catch (err) {
        console.error('更新状态失败：', err)
        this.showStatusMessage('更新失败：网络错误', 'error')
      } finally {
        this.statusLoading = false
      }
    },
    showStatusMessage(message, type) {
      this.statusMessage = message
      this.statusMessageType = type
      setTimeout(() => {
        this.statusMessage = ''
      }, 3000)
    },
    getStatusText(status) {
      const map = {
        0: '待支付',
        1: '待审核',
        2: '服务中',
        3: '待评价',
        4: '已完成',
        5: '已取消或审核不通过'
      }
      return map[status] || status
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    },
    logout() {
      localStorage.removeItem('chef')
      this.$router.push('/')
    },
    // 检查新订单通知
    async checkNewOrders() {
      try {
        const res = await this.$axios.get('/notification/chef/' + this.chef.id + '/unread')
        if (res.data.code === 200 && res.data.data.length > 0) {
          const notification = res.data.data[0]
          // 获取订单详情
          const orderRes = await this.$axios.get('/order/' + notification.orderId)
          if (orderRes.data.code === 200) {
            this.newOrderNotification = {
              orderNo: orderRes.data.data.orderNo,
              address: orderRes.data.data.address,
              cookingTime: orderRes.data.data.cookingTime,
              notificationId: notification.id
            }
            this.showNewOrderModal = true
          }
        }
      } catch (err) {
        console.error('检查新订单失败：', err)
      }
    },
    // 处理新订单通知
    async handleNewOrder() {
      this.showNewOrderModal = false
      // 标记通知为已读
      try {
        await this.$axios.put('/notification/' + this.newOrderNotification.notificationId + '/read')
      } catch (err) {
        console.error('标记通知已读失败：', err)
      }
      // 刷新订单列表
      this.loadOrders()
    }
  }
}
</script>

<style scoped>
.chef-dashboard {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.welcome-card {
  text-align: center;
  padding: 20px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  border-radius: 12px;
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-left {
  text-align: left;
}

.welcome-left h1 {
  font-size: 22px;
  margin-bottom: 8px;
}

.welcome-left p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-right {
  text-align: right;
}

.balance-card {
  background: rgba(255, 255, 255, 0.3);
  padding: 15px 25px;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, 0.4);
}

.balance-label {
  font-size: 12px;
  margin-bottom: 5px;
  opacity: 0.9;
}

.balance-amount {
  font-size: 24px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card h3 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #333;
}

.status-card {
  margin-bottom: 20px;
}

.status-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: center;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-group input[type="date"],
.form-group select {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  min-width: 180px;
}

.update-status-btn {
  padding: 10px 24px;
  background: #67c23a;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  align-self: flex-end;
}

.update-status-btn:hover:not(:disabled) {
  background: #85ce61;
}

.update-status-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.status-message {
  margin-top: 15px;
  padding: 12px;
  border-radius: 6px;
  font-size: 14px;
  text-align: center;
}

.status-message.success {
  background: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
}

.status-message.error {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ffcdd2;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40px 0;
}

.order-list {
  margin-top: 10px;
}

.order-item {
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-weight: bold;
  font-size: 16px;
  color: #333;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-detail {
  margin-bottom: 12px;
}

.order-detail p {
  margin: 6px 0;
  color: #555;
  font-size: 14px;
  line-height: 1.6;
}

.order-dishes {
  background: #f9fafb;
  padding: 12px;
  border-radius: 6px;
  margin-top: 12px;
}

.order-dishes h4 {
  margin: 0 0 10px 0;
  font-size: 15px;
  color: #333;
}

.dish-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
}

.dish-item:last-child {
  border-bottom: none;
}

.dish-name {
  font-weight: 500;
  color: #333;
}

.dish-quantity {
  color: #409eff;
  font-weight: bold;
  margin: 0 10px;
}

.dish-price {
  color: #f56c6c;
  font-weight: bold;
}

/* 评价样式 */
.order-review {
  background: #fff7e6;
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
  border-left: 4px solid #f59e0b;
}

.order-review h4 {
  margin: 0 0 12px 0;
  font-size: 15px;
  color: #333;
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.review-rating {
  display: flex;
  gap: 2px;
}

.review-rating .star {
  font-size: 24px;
  color: #ddd;
  transition: color 0.2s;
}

.review-rating .star.active {
  color: #f56c6c;
}

.review-text {
  font-size: 14px;
  color: #555;
  line-height: 1.6;
  padding: 10px;
  background: #fff;
  border-radius: 4px;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.status-badge {
  padding: 3px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.status-0 { background: #909399; color: #fff; }
.status-1 { background: #e6a23c; color: #fff; }
.status-2 { background: #409eff; color: #fff; }
.status-3 { background: #67c23a; color: #fff; }
.status-4 { background: #10b981; color: #fff; }
.status-5 { background: #f56c6c; color: #fff; }

.refresh-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 15px;
  font-size: 14px;
}

.refresh-btn:hover {
  background: #66b1ff;
}

.logout-btn {
  width: 100%;
  padding: 12px;
  background: #f56c6c;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 15px;
  font-size: 14px;
}

.logout-btn:hover {
  background: #f78989;
}

/* 新订单通知弹窗 */
.notification-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  animation: slideIn 0.3s ease;
}

@keyframes slideIn {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-icon {
  font-size: 48px;
  margin-bottom: 15px;
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.modal-title {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 20px;
}

.modal-order-info {
  background: #f9fafb;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  text-align: left;
}

.modal-order-info p {
  margin: 8px 0;
  color: #555;
  font-size: 14px;
}

.modal-btn {
  padding: 12px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: all 0.3s;
}

.modal-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}
</style>