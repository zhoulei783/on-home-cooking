<template>
  <div class="cart">
    <div class="card">
      <h2>🛒 购物车</h2>
      
      <div v-if="cartItems.length === 0" class="empty-cart">
        <p>购物车是空的</p>
        <button @click="$router.push('/dishes')" class="browse-btn">
          去浏览菜品
        </button>
      </div>

      <div v-else class="cart-content">
        <div class="cart-list">
          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="item-checkbox">
              <input type="checkbox" v-model="item.selected" @change="saveCart">
            </div>
            <div class="item-info">
              <h3>{{ item.name }}</h3>
              <span class="price">¥{{ item.price }}</span>
            </div>
            <div class="item-actions">
              <button @click="decreaseQuantity(item)" class="qty-btn">-</button>
              <span class="quantity">{{ item.quantity }}</span>
              <button @click="increaseQuantity(item)" class="qty-btn">+</button>
              <span class="subtotal">小计: ¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              <button @click="removeItem(item)" class="remove-btn">删除</button>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <div class="summary-row">
            <span>已选菜品:</span>
            <span>{{ selectedQuantity }} 份</span>
          </div>
          <div class="summary-row total">
            <span>选中总计:</span>
            <span class="total-price">¥{{ selectedPrice.toFixed(2) }}</span>
          </div>
          <div class="summary-actions">
            <button @click="clearCart" class="clear-btn">清空购物车</button>
            <button @click="selectAll" class="select-btn">全选</button>
            <button @click="goToOrder" class="checkout-btn">
              去下单
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Cart',
  data() {
    return {
      cartItems: []
    }
  },
  computed: {
    totalQuantity() {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    totalPrice() {
      return this.cartItems.reduce((sum, item) => sum + (item.price * item.quantity), 0)
    },
    selectedQuantity() {
      return this.cartItems.filter(item => item.selected).reduce((sum, item) => sum + item.quantity, 0)
    },
    selectedPrice() {
      return this.cartItems.filter(item => item.selected).reduce((sum, item) => sum + (item.price * item.quantity), 0)
    }
  },
  mounted() {
    this.loadCart()
  },
  methods: {
    loadCart() {
      this.cartItems = JSON.parse(localStorage.getItem('cart') || '[]')
    },
    increaseQuantity(item) {
      item.quantity++
      this.saveCart()
    },
    decreaseQuantity(item) {
      if (item.quantity > 1) {
        item.quantity--
      }
      this.saveCart()
    },
    removeItem(item) {
      this.cartItems = this.cartItems.filter(i => i.id !== item.id)
      this.saveCart()
    },
    clearCart() {
      if (confirm('确定要清空购物车吗？')) {
        this.cartItems = []
        localStorage.removeItem('cart')
      }
    },
    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.cartItems))
    },
    selectAll() {
      const allSelected = this.cartItems.every(item => item.selected)
      this.cartItems.forEach(item => {
        item.selected = !allSelected
      })
      this.saveCart()
    },
    goToOrder() {
      this.$router.push('/order')
    }
  }
}
</script>

<style scoped>
.cart {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
}

.empty-cart p {
  color: #666;
  font-size: 18px;
  margin-bottom: 20px;
}

.browse-btn {
  padding: 12px 30px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.cart-list {
  margin-bottom: 20px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.item-checkbox {
  margin-right: 15px;
}

.item-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
}

.item-info .price {
  color: #f56c6c;
  font-weight: bold;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.qty-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  color: #333;
}

.qty-btn:hover {
  background: #f5f7fa;
}

.quantity {
  min-width: 30px;
  text-align: center;
  font-size: 16px;
}

.subtotal {
  color: #666;
  margin: 0 15px;
}

.remove-btn {
  padding: 6px 12px;
  background: #f56c6c;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.cart-summary {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  font-size: 16px;
}

.summary-row.total {
  border-top: 1px solid #ddd;
  margin-top: 10px;
  padding-top: 15px;
  font-size: 18px;
  font-weight: bold;
}

.total-price {
  color: #f56c6c;
  font-size: 24px;
}

.summary-actions {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.clear-btn {
  flex: 1;
  padding: 12px;
  background: #fff;
  border: 1px solid #f56c6c;
  color: #f56c6c;
  border-radius: 6px;
  cursor: pointer;
}

.select-btn {
  flex: 1;
  padding: 12px;
  background: #fff;
  border: 1px solid #409eff;
  color: #409eff;
  border-radius: 6px;
  cursor: pointer;
}

.checkout-btn {
  flex: 2;
  padding: 12px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
}

.checkout-btn:hover {
  background: #66b1ff;
}
</style>