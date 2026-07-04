<template>
  <div class="dish-list">
    <h2>菜品目录</h2>
    <div class="filters">
      <button @click="loadDishes" :class="{active: !selectedCuisine}">全部</button>
      <button v-for="cuisine in cuisines" :key="cuisine"
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
        <button @click="addToCart(dish)" class="add-btn">加入购物车</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DishList',
  data() {
    return {
      dishes: [],
      cuisines: ['川菜', '粤菜', '淮扬菜', '湘菜'],
      selectedCuisine: ''
    }
  },
  mounted() {
    this.loadDishes()
  },
  methods: {
    async loadDishes() {
      this.selectedCuisine = ''
      const res = await this.$axios.get('/dish/list')
      if (res.data.code === 200) {
        this.dishes = res.data.data
      }
    },
    async filterByCuisine(cuisine) {
      this.selectedCuisine = cuisine
      const res = await this.$axios.get(`/dish/cuisine/${cuisine}`)
      if (res.data.code === 200) {
        this.dishes = res.data.data
      }
    },
    addToCart(dish) {
      let cart = JSON.parse(localStorage.getItem('cart') || '[]')
      const existingItem = cart.find(item => item.id === dish.id)
      if (existingItem) {
        existingItem.quantity += 1
      } else {
        cart.push({
          id: dish.id,
          name: dish.name,
          price: dish.price,
          quantity: 1
        })
      }
      localStorage.setItem('cart', JSON.stringify(cart))
      alert(`${dish.name} 已加入购物车！`)
    }
  }
}
</script>

<style scoped>
.dish-list { padding: 20px; }
.filters { margin-bottom: 20px; }
.filters button { margin-right: 10px; padding: 8px 16px; border: 1px solid #ddd; background: #fff; border-radius: 4px; cursor: pointer; color: #333; }
.filters button.active { background: #409eff; color: white; border-color: #409eff; }
.dish-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 20px; }
.dish-card { border: 1px solid #ddd; padding: 15px; border-radius: 8px; background: #fff; overflow: hidden; transition: transform 0.3s; }
.dish-card:hover { transform: translateY(-5px); box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15); }
.dish-img { width: 100%; height: 180px; object-fit: cover; border-radius: 4px; margin-bottom: 10px; background: #f5f7fa; }
.dish-card h3 { margin: 10px 0; }
.description { color: #666; font-size: 14px; margin-bottom: 10px; }
.ingredients { color: #606266; font-size: 13px; margin-bottom: 10px; padding: 8px; background: #f9fafb; border-radius: 4px; border-left: 3px solid #409eff; }
.info { display: flex; justify-content: space-between; margin: 10px 0; font-size: 14px; }
.price { color: #f56c6c; font-weight: bold; font-size: 18px; }
.cuisine { color: #409eff; }
.spiciness { color: #e6a23c; }
.add-btn { width: 100%; padding: 10px; background: #409eff; color: white; border: none; border-radius: 4px; cursor: pointer; }
.add-btn:hover { background: #66b1ff; }
</style>