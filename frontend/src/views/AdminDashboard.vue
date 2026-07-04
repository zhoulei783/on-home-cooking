<template>
  <div class="admin-dashboard">
    <!-- 新订单通知横幅 -->
    <div v-if="newOrderNotification" class="notification-banner">
      <div class="notification-content">
        <span class="notification-icon">🔔</span>
        <span class="notification-text">有新的订单变化，请快速审核</span>
      </div>
      <div class="notification-actions">
        <button @click="dismissNotification" class="dismiss-btn">知道了</button>
      </div>
    </div>

    <div class="card">
      <h2>管理员后台</h2>
      <div class="tabs">
        <button :class="{ active: activeTab === 'orders' }" @click="activeTab = 'orders'; loadOrders()">订单管理</button>
        <button :class="{ active: activeTab === 'recharges' }" @click="loadRecharges()">充值管理</button>
        <button :class="{ active: activeTab === 'chef-earnings' }" @click="activeTab = 'chef-earnings'; loadChefs()">厨师收入</button>
        <button :class="{ active: activeTab === 'menu' }" @click="activeTab = 'menu'; loadDishes()">菜单管理</button>
      </div>

      <!-- 订单管理 -->
      <div v-if="activeTab === 'orders'" class="tab-content">
        <div class="filter-section">
          <h3>订单管理</h3>
          <div class="filter-controls">
            <select v-model="selectedChefId" @change="onChefChange" class="chef-select">
              <option value="">所有厨师</option>
              <option v-for="chef in chefs" :key="chef.id" :value="chef.id">
                {{ chef.name }}
              </option>
            </select>
            <button @click="loadOrders" :disabled="loading">刷新订单</button>
          </div>
        </div>

        <div v-if="selectedChef && chefInfo" class="chef-info-card">
          <h4>厨师信息</h4>
          <div class="chef-info">
            <div>姓名：{{ chefInfo.name }}</div>
            <div>手机号：{{ chefInfo.phone }}</div>
            <div>总收入：¥{{ chefInfo.totalEarnings || 0 }}</div>
            <div>已发放：¥{{ chefInfo.paidAmount || 0 }}</div>
            <div>剩余：¥{{ chefInfo.remainingAmount || 0 }}</div>
          </div>
        </div>

        <div v-if="orders.length > 0" class="list">
          <div v-for="order in orders" :key="order.id" class="list-item">
            <div class="order-header">
              <div class="order-no">订单号：{{ order.orderNo }}</div>
              <div :class="['status-badge', 'status-' + order.status]">{{ getStatusText(order.status) }}</div>
            </div>
            <div class="order-info">
              <div>用户：{{ order.username }}</div>
              <div>用户电话：{{ order.userPhone }}</div>
              <div>厨师：{{ order.chefName }}</div>
              <div>厨师电话：{{ order.chefPhone }}</div>
              <div>时间：{{ formatTime(order.cookingTime) }}</div>
              <div>地址：{{ order.address }}</div>
              <div>金额：¥{{ order.price }}</div>
            </div>
            <div v-if="order.dishes && order.dishes.length > 0" class="order-dishes">
              <div class="dishes-title">菜品：</div>
              <div v-for="dish in order.dishes" :key="dish.id" class="dish-item">
                <span class="dish-name">{{ dish.dishName }}</span>
                <span class="dish-quantity">x{{ dish.quantity }}</span>
                <span class="dish-price">¥{{ dish.price }}</span>
              </div>
            </div>
            <!-- 评价信息 -->
            <div v-if="order.review" class="order-review">
              <div class="review-title">评价：</div>
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
            <!-- 审核操作按钮 -->
            <div v-if="order.status === 1" class="order-actions">
              <button @click="handleOrderDispatch(order.id)" class="action-btn approve-btn">审核通过（派遣厨师）</button>
              <button @click="handleOrderReject(order.id)" class="action-btn reject-btn">拒绝（余额回退）</button>
            </div>
            <!-- 取消订单待审核状态按钮 -->
            <div v-if="order.status === 6" class="order-actions">
              <button @click="handleApproveCancel(order.id)" class="action-btn approve-btn">同意取消</button>
              <button @click="handleRejectCancel(order.id)" class="action-btn reject-btn">拒绝取消</button>
            </div>
          </div>
        </div>
        <div v-else class="empty">暂无订单</div>
      </div>

      <!-- 充值管理 -->
      <div v-if="activeTab === 'recharges'" class="tab-content">
        <h3>充值管理</h3>
        <button @click="loadRecharges" :disabled="loading">刷新充值记录</button>

        <div v-if="recharges.length > 0" class="list">
          <div v-for="recharge in recharges" :key="recharge.id" class="list-item">
            <div class="recharge-header">
              <div class="recharge-user">用户：{{ recharge.userName }}</div>
              <div :class="['status-badge', getRechargeStatusClass(recharge.status)]">{{ getRechargeStatusText(recharge.status) }}</div>
            </div>
            <div class="recharge-info">
              <div>充值金额：¥{{ recharge.amount }}</div>
              <div>用户ID：{{ recharge.userId }}</div>
              <div>用户电话：{{ recharge.userPhone }}</div>
              <div>充值时间：{{ formatTime(recharge.createTime) }}</div>
            </div>
            <div v-if="recharge.status === 0" class="recharge-actions">
              <button @click="confirmRecharge(recharge)" class="confirm-btn">确认充值</button>
              <button @click="rejectRecharge(recharge)" class="reject-btn">拒绝</button>
            </div>
          </div>
        </div>
        <div v-else class="empty">暂无充值记录</div>
      </div>

      <!-- 厨师收入 -->
      <div v-if="activeTab === 'chef-earnings'" class="tab-content">
        <h3>厨师收入管理</h3>
        <button @click="loadChefs" :disabled="loading">刷新厨师列表</button>
        <div v-if="chefs.length > 0" class="list">
          <div v-for="chef in chefs" :key="chef.id" class="list-item">
            <div class="chef-header">
              <div class="chef-name">{{ chef.name }}</div>
              <div class="chef-phone">{{ chef.phone }}</div>
            </div>
            <div class="chef-earnings-info">
              <div>总收入：¥{{ chef.totalEarnings || 0 }}</div>
              <div>已发放：¥{{ chef.paidAmount || 0 }}</div>
              <div>剩余：¥{{ chef.remainingAmount || 0 }}</div>
            </div>
            <div class="chef-actions">
              <button @click="payChef(chef)" class="pay-btn">发放收入</button>
            </div>
          </div>
        </div>
        <div v-else class="empty">暂无厨师</div>
      </div>

      <!-- 菜单管理 -->
      <div v-if="activeTab === 'menu'" class="tab-content">
        <h2>菜品目录</h2>
        <div class="menu-actions">
          <button @click="addDish" class="add-dish-btn">添加菜品</button>
        </div>
        <div class="filters">
          <button @click="loadDishes" :class="{active: !selectedCuisine}">全部</button>
          <button v-for="cuisine in ['川菜', '粤菜', '淮扬菜', '湘菜']" :key="cuisine"
                  @click="filterByCuisine(cuisine)"
                  :class="{active: selectedCuisine === cuisine}">
            {{ cuisine }}
          </button>
        </div>
        <div class="dish-grid">
          <div v-for="dish in dishes" :key="dish.id" class="dish-card">
            <img
                :src="dish.image || '/src/assets/dish-default.png'"
                :alt="dish.name"
                class="dish-img"
            />
            <h3>{{ dish.name }}</h3>
            <p class="description">{{ dish.description }}</p>
            <p class="ingredients">🥘 配方：{{ dish.ingredients || '暂无配方信息' }}</p>
            <div class="info">
              <span class="price">¥{{ dish.price }}</span>
              <span class="cuisine">{{ dish.cuisine }}</span>
              <span class="spiciness">{{ '🌶'.repeat(dish.spiciness) || '不辣' }}</span>
            </div>
            <div class="dish-actions">
              <button @click="editDish(dish)" class="edit-btn">修改菜品</button>
              <button @click="deleteDish(dish)" class="delete-btn">删除菜品</button>
            </div>
          </div>
        </div>
      </div>

      <button @click="logout" class="logout-btn">退出登录</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminDashboard',
  data() {
    return {
      activeTab: 'orders',
      orders: [],
      reviews: [],
      loading: false,
      chefs: [],
      selectedChefId: '',
      chefInfo: null,
      dishes: [],
      selectedCuisine: '',
      recharges: [],
      newOrderNotification: null,
      notificationInterval: null
    }
  },
  mounted() {
    const admin = JSON.parse(localStorage.getItem('admin') || '{}')
    if (!admin.id) {
      this.$router.push('/admin-login-page')
      return
    }
    this.loadChefs()
    this.loadOrders()
    this.checkNewOrders()
    this.notificationInterval = setInterval(() => this.checkNewOrders(), 10000)
  },
  beforeUnmount() {
    if (this.notificationInterval) {
      clearInterval(this.notificationInterval)
    }
  },
  methods: {
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
    async loadChefs() {
      try {
        const res = await this.$axios.get('/admin/chefs')
        if (res.data.code === 200) {
          this.chefs = res.data.data || []
        }
      } catch (err) {
        console.error('加载厨师列表失败：', err)
      }
    },
    async loadDishes() {
      this.loading = true
      try {
        const res = await this.$axios.get('/dish/list')
        if (res.data.code === 200) {
          this.dishes = res.data.data || []
          this.selectedCuisine = ''
        }
      } catch (err) {
        alert('加载菜品失败：' + err.message)
      } finally {
        this.loading = false
      }
    },
    async filterByCuisine(cuisine) {
      this.selectedCuisine = cuisine
      try {
        const res = await this.$axios.get(`/dish/cuisine/${cuisine}`)
        if (res.data.code === 200) {
          this.dishes = res.data.data || []
        }
      } catch (err) {
        alert('加载菜品失败：' + err.message)
      }
    },
    async loadOrders() {
      if (this.loading) return

      this.loading = true
      try {
        let url = '/admin/orders/paid'
        if (this.selectedChefId) {
          url = `/admin/orders/chef/${this.selectedChefId}`
        }
        const res = await this.$axios.get(url)
        if (res.data.code === 200) {
          if (this.selectedChefId) {
            this.chefInfo = res.data.data.chef
            this.orders = res.data.data.orders || []
          } else {
            this.orders = res.data.data || []
            this.chefInfo = null
          }

          for (let order of this.orders) {
            if (order.id) {
              try {
                const reviewRes = await this.$axios.get('/review/by-order/' + order.id)
                if (reviewRes.data.code === 200 && reviewRes.data.data) {
                  order.review = reviewRes.data.data
                }
              } catch (err) {
                console.error('加载评价失败：', err)
                order.review = null
              }
            }
          }
        }
      } catch (err) {
        console.error('加载订单失败：', err)
        if (err.message.includes('Network Error')) {
          alert('无法连接到后端服务，请检查服务是否启动')
        } else {
          alert('加载订单失败：' + err.message)
        }
      } finally {
        this.loading = false
      }
    },
    onChefChange() {
      this.loadOrders()
    },
    async payChef(chef) {
      const amount = prompt('请输入发放金额：')
      if (!amount || amount <= 0) {
        alert('请输入有效的金额')
        return
      }

      if (!confirm(`确认给厨师 ${chef.name} 发放 ¥${amount} 吗？`)) {
        return
      }

      try {
        const res = await this.$axios.post('/chef/pay', {
          chefId: chef.id,
          amount: parseFloat(amount)
        })
        if (res.data.code === 200) {
          alert('发放成功')
          this.loadChefs()
        } else {
          alert('发放失败：' + res.data.msg)
        }
      } catch (err) {
        alert('发放失败：' + err.message)
      }
    },
    async loadRecharges() {
      this.loading = true
      this.activeTab = 'recharges'
      try {
        const res = await this.$axios.get('/admin/recharges')
        if (res.data.code === 200) {
          this.recharges = res.data.data || []
        }
      } catch (err) {
        console.error('加载充值记录失败：', err)
      } finally {
        this.loading = false
      }
    },
    getRechargeStatusText(status) {
      const map = {0: '待确认', 1: '已确认', 2: '已拒绝'}
      return map[status] || status
    },
    getRechargeStatusClass(status) {
      const map = {0: 'status-pending', 1: 'status-confirmed', 2: 'status-rejected'}
      return map[status] || ''
    },
    async confirmRecharge(recharge) {
      if (!confirm(`确认给用户 ${recharge.userName} 充值 ¥${recharge.amount} 吗？`)) {
        return
      }
      try {
        const res = await this.$axios.post(`/admin/recharge/${recharge.id}/confirm`)
        if (res.data.code === 200) {
          alert('确认成功，用户余额已更新')
          this.loadRecharges()
        } else {
          alert('确认失败：' + res.data.msg)
        }
      } catch (err) {
        alert('确认失败：' + err.message)
      }
    },
    async rejectRecharge(recharge) {
      if (!confirm(`确认拒绝用户 ${recharge.userName} 的充值申请吗？`)) {
        return
      }
      try {
        const res = await this.$axios.post(`/admin/recharge/${recharge.id}/reject`)
        if (res.data.code === 200) {
          alert('已拒绝充值申请')
          this.loadRecharges()
        } else {
          alert('拒绝失败：' + res.data.msg)
        }
      } catch (err) {
        alert('拒绝失败：' + err.message)
      }
    },
    async editDish(dish) {
      const newName = prompt('菜品名称：', dish.name)
      if (newName === null) return

      const newPrice = prompt('菜品价格：', dish.price)
      if (newPrice === null) return

      const newDescription = prompt('菜品描述：', dish.description)
      if (newDescription === null) return

      const newIngredients = prompt('菜品配方：', dish.ingredients)
      if (newIngredients === null) return

      const newCuisine = prompt('菜系（川菜/粤菜/淮扬菜/湘菜）：', dish.cuisine)
      if (newCuisine === null) return

      const newSpiciness = prompt('辣度（0-5）：', dish.spiciness)
      if (newSpiciness === null) return

      const newImage = prompt('菜品图片路径：', dish.image)
      if (newImage === null) return

      if (!confirm('确认修改菜品信息吗？')) {
        return
      }

      try {
        const res = await this.$axios.put(`/dish/${dish.id}`, {
          name: newName,
          price: parseFloat(newPrice),
          description: newDescription,
          ingredients: newIngredients,
          cuisine: newCuisine,
          spiciness: parseInt(newSpiciness),
          image: newImage
        })
        if (res.data.code === 200) {
          alert('修改成功')
          this.loadDishes()
        } else {
          alert('修改失败：' + res.data.msg)
        }
      } catch (err) {
        alert('修改失败：' + err.message)
      }
    },
    async addDish() {
      const name = prompt('菜品名称：')
      if (!name) return

      const price = prompt('菜品价格：')
      if (!price) return

      const description = prompt('菜品描述：')
      if (!description) return

      const ingredients = prompt('菜品配方：')
      if (!ingredients) return

      const cuisine = prompt('菜系（川菜/粤菜/淮扬菜/湘菜）：')
      if (!cuisine) return

      const spiciness = prompt('辣度（0-5）：')
      if (!spiciness) return

      const image = prompt('菜品图片路径：')
      if (!image) return

      if (!confirm('确认添加菜品吗？')) {
        return
      }

      try {
        const res = await this.$axios.post('/dish/add', {
          name: name,
          price: parseFloat(price),
          description: description,
          ingredients: ingredients,
          cuisine: cuisine,
          spiciness: parseInt(spiciness),
          image: image
        })
        if (res.data.code === 200) {
          alert('添加成功')
          this.loadDishes()
        } else {
          alert('添加失败：' + res.data.msg)
        }
      } catch (err) {
        alert('添加失败：' + err.message)
      }
    },
    async deleteDish(dish) {
      if (!confirm(`确认删除菜品“${dish.name}”吗？`)) {
        return
      }

      try {
        const res = await this.$axios.delete(`/dish/${dish.id}`)
        if (res.data.code === 200) {
          alert('删除成功')
          this.loadDishes()
        } else {
          alert('删除失败：' + res.data.msg)
        }
      } catch (err) {
        alert('删除失败：' + err.message)
      }
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
      localStorage.removeItem('admin')
      this.$router.push('/')
    },
    async checkNewOrders() {
      try {
        const res = await this.$axios.get('/notification/admin/unread')
        if (res.data.code === 200 && res.data.data.length > 0) {
          this.newOrderNotification = res.data.data[0]
        }
      } catch (err) {
        console.error('检查新订单失败：', err)
      }
    },
    // 关闭通知
    async dismissNotification() {
      if (!this.newOrderNotification) return
      try {
        await this.$axios.put(`/notification/${this.newOrderNotification.id}/read`)
        this.newOrderNotification = null
      } catch (err) {
        console.error('关闭通知失败：', err)
      }
    },
    async handleOrderDispatch(orderId) {
      if (!confirm('确认审核通过并派遣厨师吗？')) {
        return
      }
      try {
        const res = await this.$axios.post(`/admin/order/${orderId}/dispatch`)
        if (res.data.code === 200) {
          alert('审核通过，已派遣厨师')
          this.loadOrders()
        } else {
          alert('操作失败：' + (res.data.msg || '未知错误'))
        }
      } catch (err) {
        alert('操作失败：网络错误')
      }
    },
    async handleOrderReject(orderId) {
      if (!confirm('确认拒绝此订单并退回用户余额吗？')) {
        return
      }
      try {
        const res = await this.$axios.post(`/admin/order/${orderId}/reject`)
        if (res.data.code === 200) {
          alert('拒绝成功，余额已退回')
          this.loadOrders()
        } else {
          alert('操作失败：' + (res.data.msg || '未知错误'))
        }
      } catch (err) {
        alert('操作失败：网络错误')
      }
    },
    // 同意取消订单
    async handleApproveCancel(orderId) {
      if (!confirm('确认同意取消订单吗？订单金额将退回用户账户')) {
        return
      }
      try {
        const res = await this.$axios.post(`/admin/order/${orderId}/approve-cancel`)
        if (res.data.code === 200) {
          alert('已同意取消，余额已退回')
          this.loadOrders()
        } else {
          alert('操作失败：' + res.data.msg)
        }
      } catch (err) {
        alert('操作失败：' + err.message)
      }
    },
    // 拒绝取消订单
    async handleRejectCancel(orderId) {
      if (!confirm('确认拒绝取消订单吗？订单将继续服务中')) {
        return
      }
      try {
        const res = await this.$axios.post(`/admin/order/${orderId}/reject-cancel`)
        if (res.data.code === 200) {
          alert('已拒绝取消，订单继续服务中')
          this.loadOrders()
        } else {
          alert('操作失败：' + res.data.msg)
        }
      } catch (err) {
        alert('操作失败：' + err.message)
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.notification-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }
  50% {
    box-shadow: 0 4px 20px rgba(102, 126, 234, 0.6);
  }
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
}

.notification-icon {
  font-size: 24px;
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.notification-text {
  font-size: 16px;
  font-weight: 500;
}

.notification-actions {
  display: flex;
  gap: 10px;
}

.dismiss-btn {
  padding: 10px 20px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.3s;
}

.dismiss-btn:hover {
  background: #66b1ff;
}

.card {
  max-width: 1200px;
  margin: 0 auto;
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

.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.tabs button {
  flex: 1;
  padding: 12px;
  background: #f5f7fa;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #000;
  font-weight: 500;
  transition: all 0.3s;
}

.tabs button:hover {
  background: #e8f4fd;
  color: #333;
}

.tabs button.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.tab-content {
  min-height: 300px;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-section h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
}

.filter-controls {
  display: flex;
  gap: 15px;
  align-items: center;
}

.chef-select {
  padding: 8px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  min-width: 180px;
}

.chef-info-card {
  background: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
}

.chef-info-card h4 {
  margin: 0 0 12px 0;
  font-size: 15px;
  color: #333;
}

.chef-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  font-size: 14px;
  color: #555;
}

.list {
  margin-top: 10px;
}

.list-item {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  transition: box-shadow 0.3s;
}

.list-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.order-header, .chef-header, .recharge-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no, .chef-name, .recharge-user {
  font-weight: bold;
  font-size: 15px;
  color: #333;
}

.chef-phone {
  font-size: 14px;
  color: #666;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-0 {
  background: #909399;
  color: #fff;
}

.status-1 {
  background: #e6a23c;
  color: #fff;
}

.status-2 {
  background: #409eff;
  color: #fff;
}

.status-3 {
  background: #67c23a;
  color: #fff;
}

.status-4 {
  background: #10b981;
  color: #fff;
}

.status-5 {
  background: #f56c6c;
  color: #fff;
}

.status-6 {
  background: #f09819;
  color: #fff;
}

.status-pending {
  background: #e6a23c;
  color: #fff;
}

.status-confirmed {
  background: #67c23a;
  color: #fff;
}

.status-rejected {
  background: #f56c6c;
  color: #fff;
}

.order-actions {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
}

.approve-btn {
  background: #67c23a;
  color: #fff;
}

.approve-btn:hover {
  background: #85ce61;
}

.order-info, .recharge-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
  font-size: 14px;
  color: #555;
}

.order-dishes {
  background: #f9fafb;
  padding: 12px;
  border-radius: 6px;
  margin-top: 12px;
}

.dishes-title {
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
  font-size: 14px;
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
  margin: 0 15px;
}

.dish-price {
  color: #f56c6c;
  font-weight: bold;
}

.order-review {
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 6px;
  padding: 12px;
  margin-top: 12px;
}

.review-title {
  font-weight: bold;
  color: #fa8c16;
  margin-bottom: 8px;
  font-size: 14px;
}

.review-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
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
  line-height: 1.5;
}

.review-time {
  font-size: 12px;
  color: #999;
}

.chef-earnings-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 6px;
  margin-bottom: 12px;
}

.chef-earnings-info div {
  font-size: 14px;
  color: #555;
}

.chef-actions, .recharge-actions {
  text-align: right;
  margin-top: 12px;
}

.pay-btn, .confirm-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
  background: #67c23a;
  color: #fff;
}

.pay-btn:hover, .confirm-btn:hover {
  background: #5daf34;
}

.recharge-actions {
  display: flex;
  gap: 12px;
}

.recharge-actions button {
  flex: 1;
  padding: 10px;
}

.menu-actions {
  margin-bottom: 20px;
}

.add-dish-btn {
  padding: 10px 25px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.add-dish-btn:hover {
  background: #66b1ff;
}

.filters {
  margin-bottom: 20px;
}

.filters button {
  margin-right: 10px;
  padding: 8px 20px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
}

.filters button.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.dish-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.dish-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.dish-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  background: #f56c6c;
}
</style>