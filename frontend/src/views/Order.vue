<template>
  <div class="order">
    <div class="card">
      <h2>创建订单</h2>

      <div class="selected-chef" v-if="chef">
        <h3>已选厨师</h3>
        <div class="chef-card">
          <strong>{{ chef.name }}</strong>
          <span>擅长：{{ chef.skill }}</span>
          <span class="price">¥{{ chef.price }}</span>
        </div>
      </div>

      <div class="selected-dishes" v-if="selectedItems.length > 0">
        <h3>已选菜品</h3>
        <div class="dishes-list">
          <span v-for="item in selectedItems" :key="item.id" class="dish-tag">
            {{ item.name }} x {{ item.quantity }}
          </span>
        </div>
        <div class="dish-total-price">
          <span>菜品总价：</span>
          <span class="price-text">¥{{ dishPrice.toFixed(2) }}</span>
        </div>
        <button @click="$router.push('/cart')" class="edit-cart-btn">修改菜品</button>
      </div>
      <div v-else class="no-dishes">
        <button @click="$router.push('/cart')" class="select-dishes-btn">去购物车选择菜品</button>
      </div>

      <div class="form-item">
        <label>用户ID：</label>
        <input type="text" v-model="userId" readonly>
      </div>
      <div class="form-item">
        <label>厨师ID：</label>
        <input type="text" v-model="chefId" readonly>
        <button @click="$router.push('/chefs')" class="select-chef-btn">选择厨师</button>
      </div>

      <!-- 日期固定为查询空闲厨师时选择的日期 -->
      <div class="form-item">
        <label>服务日期：</label>
        <input type="text" v-model="selectedDate" readonly class="date-input">
      </div>

      <!-- 时间可以修改，默认中午12点 -->
      <div class="form-item">
        <label>服务时间：</label>
        <input type="time" v-model="cookTime" class="time-input">
      </div>

      <div class="form-item">
        <label>地址：</label>
        <input type="text" v-model="address" placeholder="请输入服务地址">
      </div>
      <div class="form-item">
        <label>服务费：</label>
        <span class="price-text">¥{{ (dishPrice + (chef ? chef.price : 200)).toFixed(2) }}</span>
      </div>

      <button @click="createOrder" :disabled="loading || !canSubmit" class="submit-btn">
        {{ loading ? '创建中...' : '创建订单' }}
      </button>

      <div :class="['result', result.type]" v-if="result.message">
        {{ result.message }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Order',
  data() {
    return {
      userId: '',
      chefId: '',
      chef: null,
      cartItems: [],
      selectedDate: '',  // 服务日期（固定）
      cookTime: '12:00',  // 服务时间（可修改，默认中午12点）
      address: '',
      loading: false,
      result: { type: '', message: '' }
    }
  },
  computed: {
    canSubmit() {
      return this.userId && this.chefId && this.selectedItems.length > 0 && this.selectedDate && this.cookTime && this.address
    },
    selectedItems() {
      return this.cartItems.filter(item => item.selected)
    },
    dishPrice() {
      return this.selectedItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    }
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    if (!user.id) {
      this.$router.push('/login')
      return
    }
    this.userId = user.id

    const chef = JSON.parse(localStorage.getItem('selectedChef') || '{}')
    if (chef.id) {
      this.chef = chef
      this.chefId = chef.id
    }

    this.cartItems = JSON.parse(localStorage.getItem('cart') || '[]')

    // 获取查询空闲厨师时选择的日期
    const selectedDate = localStorage.getItem('selectedDate')
    if (selectedDate) {
      this.selectedDate = selectedDate
    } else {
      // 默认明天
      const tomorrow = new Date()
      tomorrow.setDate(tomorrow.getDate() + 1)
      this.selectedDate = tomorrow.toISOString().split('T')[0]
    }

    // 设置默认时间为中午12点
    this.cookTime = '12:00'
  },
  methods: {
    async createOrder() {
      if (!this.canSubmit) {
        this.result = { type: 'error', message: '请填写完整信息' }
        return
      }

      this.loading = true
      this.result = { type: '', message: '' }

      // 组合日期和时间
      const cookingTime = `${this.selectedDate} ${this.cookTime}:00`

      const requestData = {
        userId: Number(this.userId),
        chefId: Number(this.chefId),
        cookingTime: cookingTime,
        address: this.address,
        price: this.dishPrice + (this.chef ? this.chef.price : 200),
        dishes: this.selectedItems.map(item => ({
          dishId: item.id,
          dishName: item.name,
          price: item.price,
          quantity: item.quantity
        }))
      }

      try {
        const res = await this.$axios.post('/order/create', requestData)

        if (res.data.code === 200) {
          this.result = { type: 'success', message: '✅ 订单创建成功！' }
          localStorage.removeItem('selectedChef')
          localStorage.removeItem('selectedDate')
          setTimeout(() => {
            this.$router.push('/orders')
          }, 1500)
        } else {
          this.result = { type: 'error', message: `❌ 订单创建失败：${res.data.msg}` }
        }
      } catch (err) {
        this.result = { type: 'error', message: `❌ 订单创建异常：${err.message}` }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.order {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.selected-chef {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.selected-dishes {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.dishes-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.dish-tag {
  background: #e8f4ff;
  color: #409eff;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
}

.dish-total-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-top: 1px solid #eee;
  margin-bottom: 10px;
}

.dish-total-price .price-text {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.edit-cart-btn,
.select-dishes-btn {
  padding: 8px 16px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.no-dishes {
  text-align: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.chef-card {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chef-card .price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.form-item label {
  width: 80px;
  flex-shrink: 0;
}

.form-item input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-item input[readonly] {
  background: #f5f7fa;
}

/* 日期输入框样式 */
.date-input {
  background: #f0f9ff !important;
  color: #409eff;
  font-weight: bold;
}

/* 时间输入框样式 */
.time-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.select-chef-btn {
  margin-left: 10px;
  padding: 6px 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.price-text {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

button.submit-btn {
  width: 100%;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 10px;
}

button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.result {
  margin-top: 15px;
  padding: 12px;
  border-radius: 6px;
  text-align: center;
}

.result.success {
  background: #f0f9ff;
  color: #409eff;
}

.result.error {
  background: #fef0f0;
  color: #f56c6c;
}
</style>