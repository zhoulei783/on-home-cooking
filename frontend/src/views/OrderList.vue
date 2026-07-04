<template>
  <div class="orders">
    <div class="card">
      <h2>我的订单</h2>
      <button @click="getOrders" :disabled="loading">
        {{ loading ? '加载中...' : '刷新订单' }}
      </button>

      <div v-if="orderList.length > 0" class="order-list">
        <div v-for="order in orderList" :key="order.id" class="list-item">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span :class="['status-badge', 'status-' + getStatusClass(order.status)]">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          <div class="order-body">
            <div>厨师：{{ order.chefName }}</div>
            <div v-if="order.chefPhone">厨师电话：{{ order.chefPhone }}</div>
            <div>烹饪时间：{{ order.cookingTime }}</div>
            <div>地址：{{ order.address }}</div>
            <div v-if="order.dishes && order.dishes.length > 0" class="order-dishes">
              <div class="dishes-label">菜品：</div>
              <div class="dishes-content">
                <span v-for="dish in order.dishes" :key="dish.id" class="dish-item">
                  {{ dish.dishName }} x {{ dish.quantity }}
                </span>
              </div>
            </div>
            <div>服务费：¥{{ order.price }}</div>
            <div v-if="order.review" class="review-info">
              <div>我的评价：{{ '★'.repeat(order.review.rating) }}</div>
              <div>{{ order.review.content }}</div>
            </div>
          </div>
          <div class="order-actions">
            <!-- 待支付状态：显示支付和取消按钮 -->
            <div v-if="order.status === 0" class="pay-buttons">
              <button @click="confirmPay(order)" class="pay-btn confirm">
                确定支付
              </button>
              <button @click="cancelOrder(order)" class="cancel-btn">
                取消订单
              </button>
            </div>
            <!-- 服务中状态：显示确认完成和取消订单按钮 -->
            <div v-if="order.status === 2" class="service-buttons">
              <button @click="completeOrder(order)" class="complete-btn">
                确认完成
              </button>
              <button @click="cancelOrderInService(order)" class="cancel-service-btn">
                取消订单
              </button>
            </div>
            <!-- 取消订单待审核状态：显示提示 -->
            <div v-if="order.status === 6" class="pending-cancel">
              <span class="pending-text">取消申请已提交，等待管理员审核</span>
            </div>
            <!-- 待评价状态：显示去评价按钮 -->
            <button
                v-if="order.status === 3 && !order.review"
                @click="openReviewDialog(order)"
                class="review-btn"
            >
              去评价
            </button>
          </div>
        </div>
      </div>
      <div v-else-if="!loading" class="empty">
        暂无订单记录
      </div>
    </div>

    <ReviewDialog
        v-if="reviewDialogVisible"
        :visible="reviewDialogVisible"
        :orderId="currentOrderId"
        @close="reviewDialogVisible = false"
        @success="getOrders"
    />
  </div>
</template>

<script>
import ReviewDialog from '../components/ReviewDialog.vue'

export default {
  name: 'OrderList',
  components: {
    ReviewDialog
  },
  data() {
    return {
      orderList: [],
      loading: false,
      reviewDialogVisible: false,
      currentOrderId: null,
      userId: null
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      this.$router.push('/login')
      return
    }
    this.userId = user.id
    this.getOrders()
  },
  methods: {
    async getOrders() {
      this.loading = true

      try {
        const res = await this.$axios.get(`/order/user/${this.userId}`)

        if (res.data.code === 200) {
          const orders = res.data.data || []
          for (const order of orders) {
            const reviewRes = await this.$axios.get(`/review/by-order/${order.id}`)
            if (reviewRes.data.code === 200 && reviewRes.data.data) {
              order.review = reviewRes.data.data
            }
          }
          this.orderList = orders
        }
      } catch (err) {
        console.error('获取订单失败:', err)
      } finally {
        this.loading = false
      }
    },
    getStatusClass(status) {
      const map = {
        0: 'pending',
        1: 'reviewing',
        2: 'cooking',
        3: 'wait-review',
        4: 'completed',
        5: 'cancelled',
        6: 'cancel-pending'
      }
      return map[status] || 'pending'
    },
    getStatusText(status) {
      const map = {
        0: '待支付',
        1: '待审核',
        2: '服务中',
        3: '待评价',
        4: '已完成',
        5: '已取消或审核不通过',
        6: '取消订单且待审核'
      }
      return map[status] || status
    },
    openReviewDialog(order) {
      this.currentOrderId = order.id
      this.reviewDialogVisible = true
    },
    async completeOrder(order) {
      if (!confirm('确认服务已完成吗？')) return

      try {
        const res = await this.$axios.put(`/order/complete/${order.orderNo}`)
        if (res.data.code === 200) {
          alert('订单已完成！欢迎进行评价')
          this.getOrders()
        } else {
          alert('操作失败：' + res.data.msg)
        }
      } catch (err) {
        alert('操作异常：' + err.message)
      }
    },
    async cancelOrder(order) {
      if (!confirm('确定要取消该订单吗？')) return

      try {
        const res = await this.$axios.put(`/order/cancel/${order.orderNo}`)
        if (res.data.code === 200) {
          alert('订单已取消')
          this.getOrders()
        } else {
          alert('取消失败：' + res.data.msg)
        }
      } catch (err) {
        alert('取消异常：' + err.message)
      }
    },
    async cancelOrderInService(order) {
      if (!confirm('确定要取消该订单吗？取消后需等待管理员审核')) return

      try {
        const res = await this.$axios.put(`/order/cancel-in-service/${order.orderNo}`)
        if (res.data.code === 200) {
          alert('取消申请已提交，等待管理员审核')
          this.getOrders()
        } else {
          alert('取消失败：' + res.data.msg)
        }
      } catch (err) {
        alert('取消异常：' + err.message)
      }
    },
    async confirmPay(order) {
      if (!confirm(`确定支付订单 ¥${order.price} 吗？`)) return

      try {
        const res = await this.$axios.put(`/order/pay/${order.orderNo}`)
        if (res.data.code === 200) {
          alert('支付成功！请等待管理员审核')
          this.getOrders()
        } else {
          alert('支付失败：' + res.data.msg)
        }
      } catch (err) {
        alert('支付异常：' + err.message)
      }
    },
  }
}
</script>

<style scoped>
.orders {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.card {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.card h2 {
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-list {
  margin-top: 15px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.order-no {
  font-size: 12px;
  color: #999;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

/* 状态样式 */
.status-pending {
  background: #909399;
  color: #fff;
}

/* 待支付 - 灰色 */
.status-reviewing {
  background: #e6a23c;
  color: #fff;
}

/* 待审核 - 橙色 */
.status-cooking {
  background: #409eff;
  color: #fff;
}

/* 服务中 - 蓝色 */
.status-wait-review {
  background: #67c23a;
  color: #fff;
}

/* 待评价 - 绿色 */
.status-completed {
  background: #10b981;
  color: #fff;
}

/* 已完成 - 深绿色 */
.status-cancelled {
  background: #f56c6c;
  color: #fff;
}

/* 已取消 - 红色 */
.status-cancel-pending {
  background: #f09819;
  color: #fff;
}

/* 取消订单待审核 - 橙黄色 */

.order-body {
  color: #666;
  font-size: 14px;
  line-height: 1.8;
}

.order-dishes {
  margin: 10px 0;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.dishes-label {
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.dishes-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.dish-item {
  background: #e8f4ff;
  color: #409eff;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 13px;
}

.review-info {
  margin-top: 10px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
}

.order-actions {
  margin-top: 10px;
  text-align: right;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.cancel-btn, .review-btn, .complete-btn, .pay-btn, .cancel-service-btn {
  width: auto;
  padding: 6px 15px;
  font-size: 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cancel-btn {
  background: #f56c6c;
  color: #fff;
}

.complete-btn {
  background: #409eff;
  color: #fff;
}

.pay-btn {
  background: #67c23a;
  color: #fff;
}

.review-btn {
  background: #67c23a;
  color: #fff;
}

.cancel-service-btn {
  background: #e6a23c;
  color: #fff;
}

.pending-cancel {
  padding: 6px 15px;
  font-size: 12px;
  color: #e6a23c;
  background: #fffbe6;
  border-radius: 4px;
}

.cancel-btn:hover {
  background: #f78989;
}

.complete-btn:hover {
  background: #66b1ff;
}

.pay-btn:hover {
  background: #85ce61;
}

.review-btn:hover {
  background: #85ce61;
}

.cancel-service-btn:hover {
  background: #f0c78a;
}

.empty {
  text-align: center;
  padding: 60px;
  color: #999;
  font-size: 16px;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>